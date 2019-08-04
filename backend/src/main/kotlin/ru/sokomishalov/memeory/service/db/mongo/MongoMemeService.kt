package ru.sokomishalov.memeory.service.db.mongo

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.domain.Sort.NullHandling.NULLS_LAST
import org.springframework.data.domain.Sort.Order
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.stereotype.Service
import reactor.bool.not
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.empty
import ru.sokomishalov.memeory.config.MemeoryProperties
import ru.sokomishalov.memeory.dto.MemeDTO
import ru.sokomishalov.memeory.entity.mongo.Meme
import ru.sokomishalov.memeory.repository.MemeRepository
import ru.sokomishalov.memeory.service.db.MemeService
import ru.sokomishalov.memeory.service.db.ProfileService
import ru.sokomishalov.memeory.util.EMPTY
import ru.sokomishalov.memeory.util.log.Loggable
import java.time.Duration
import org.springframework.data.domain.PageRequest.of as pageOf
import org.springframework.data.domain.Sort.by as sortBy
import reactor.core.publisher.Flux.fromIterable as fluxFromIterable
import reactor.core.publisher.Mono.justOrEmpty as monoJustOrEmpty
import ru.sokomishalov.memeory.mapper.MemeMapper.Companion.INSTANCE as memeMapper

@Service
class MongoMemeService(
        private val repository: MemeRepository,
        private val profileService: ProfileService,
        private val template: ReactiveMongoTemplate,
        private val props: MemeoryProperties
) : MemeService, Loggable {

    override fun saveMemesIfNotExist(memes: Flux<MemeDTO>): Flux<MemeDTO> {
        return memes
                .filterWhen { !repository.existsById(it.id) }
                .map { memeMapper.toEntity(it) }
                .let { repository.saveAll(it) }
                .map { memeMapper.toDto(it) }

    }

    override fun pageOfMemes(page: Int, count: Int, token: String?): Flux<MemeDTO> {
        return monoJustOrEmpty(token)
                .defaultIfEmpty(EMPTY)
                .flatMap { profileService.findById(it) }
                .flatMapMany {
                    val pageRequest = pageOf(page, count, sortBy(Order(DESC, "publishedAt", NULLS_LAST)))

                    if (it.watchAllChannels) {
                        repository.findAllMemesBy(pageRequest)
                    } else {
                        repository.findAllByChannelIdIn(it?.channels ?: emptyList(), pageRequest)
                    }
                }
                .map { memeMapper.toDto(it) }
                .onErrorResume {
                    logError(it)
                    empty()
                }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun startUp() {
        fluxFromIterable(
                listOf(
                        Index().on("createdAt", DESC).expire(Duration.ofDays(props.memeExpirationDays.toLong())),
                        Index().on("publishedAt", DESC)
                ))
                .flatMap {
                    template
                            .indexOps(Meme::class.java)
                            .ensureIndex(it)
                }
                .subscribe()
    }
}
