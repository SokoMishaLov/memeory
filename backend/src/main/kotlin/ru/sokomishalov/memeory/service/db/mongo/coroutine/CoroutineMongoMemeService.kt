package ru.sokomishalov.memeory.service.db.mongo.coroutine

import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.flux
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Primary
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.domain.Sort.NullHandling.NULLS_LAST
import org.springframework.data.domain.Sort.Order
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.stereotype.Service
import reactor.bool.not
import reactor.core.publisher.Flux
import ru.sokomishalov.memeory.condition.ConditionalOnUsingCoroutines
import ru.sokomishalov.memeory.config.MemeoryProperties
import ru.sokomishalov.memeory.dto.MemeDTO
import ru.sokomishalov.memeory.entity.mongo.Meme
import ru.sokomishalov.memeory.repository.MemeRepository
import ru.sokomishalov.memeory.service.db.MemeService
import ru.sokomishalov.memeory.service.db.ProfileService
import ru.sokomishalov.memeory.util.EMPTY
import ru.sokomishalov.memeory.util.extensions.*
import java.time.Duration.ofDays
import org.springframework.data.domain.PageRequest.of as pageOf
import org.springframework.data.domain.Sort.by as sortBy
import ru.sokomishalov.memeory.mapper.MemeMapper.Companion.INSTANCE as memeMapper

@Service
@Primary
@ConditionalOnUsingCoroutines
class CoroutineMongoMemeService(
        private val repository: MemeRepository,
        private val profileService: ProfileService,
        private val template: ReactiveMongoTemplate,
        private val props: MemeoryProperties
) : MemeService {

    override fun saveMemesIfNotExist(memes: Flux<MemeDTO>): Flux<MemeDTO> = GlobalScope.flux(Unconfined) {
        val memesToInsert = memes
                .await()
                .coFilter { (repository.existsById(it.id).not()).awaitStrict() }
                .coMap { memeMapper.toEntity(it) }

        val savedMemes = repository
                .saveAll(memesToInsert)
                .await()

        savedMemes
                .coMap { memeMapper.toDto(it) }
                .coForEach { send(it) }
    }

    override fun pageOfMemes(page: Int, count: Int, token: String?): Flux<MemeDTO> = GlobalScope.flux(Unconfined) {
        val id = token ?: EMPTY
        val profile = profileService.findById(id).await()

        val pageRequest = pageOf(page, count, sortBy(Order(DESC, "publishedAt", NULLS_LAST)))

        val foundMemes = when {
            profile == null || profile.watchAllChannels -> repository.findAllMemesBy(pageRequest).await()
            else -> repository.findAllByChannelIdIn(profile.channels ?: emptyList(), pageRequest).await()
        }

        foundMemes
                .coMap { memeMapper.toDto(it) }
                .coForEach { send(it) }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun startUp() {
        GlobalScope.launch(Unconfined) {
            val indexes = listOf(
                    Index().on("createdAt", DESC).expire(ofDays(props.memeExpirationDays.toLong())),
                    Index().on("publishedAt", DESC)
            )
            indexes.coForEach { template.indexOps(Meme::class.java).ensureIndex(it) }
        }
    }
}