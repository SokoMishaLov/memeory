package ru.sokomishalov.memeory.service.db.mongo.coroutine

import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.sokomishalov.memeory.condition.ConditionalOnUsingCoroutines
import ru.sokomishalov.memeory.dto.ProfileDTO
import ru.sokomishalov.memeory.entity.mongo.Profile
import ru.sokomishalov.memeory.repository.ProfileRepository
import ru.sokomishalov.memeory.service.db.ProfileService
import ru.sokomishalov.memeory.util.EMPTY
import ru.sokomishalov.memeory.util.extensions.await
import ru.sokomishalov.memeory.util.extensions.awaitStrict
import ru.sokomishalov.memeory.util.extensions.isNotNullOrEmpty
import ru.sokomishalov.memeory.util.log.Loggable
import java.util.UUID.randomUUID
import org.springframework.data.mongodb.core.query.Criteria.where as criteriaWhere
import ru.sokomishalov.memeory.mapper.ProfileMapper.Companion.INSTANCE as profileMapper


/**
 * @author sokomishalov
 */
@Service
@Primary
@ConditionalOnUsingCoroutines
class CoroutineMongoProfileService(
        private val repository: ProfileRepository,
        private val template: ReactiveMongoTemplate
) : ProfileService, Loggable {

    override fun findById(id: String): Mono<ProfileDTO> = GlobalScope.mono(Unconfined) {
        val profile = repository.findById(id).awaitStrict()
        profileMapper.toDto(profile)
    }

    @Transactional
    override fun saveIfNecessary(profile: ProfileDTO): Mono<ProfileDTO> = GlobalScope.mono(Unconfined) {
        when {
            profile.id.isNullOrBlank() && profile.socialsMap.isNotNullOrEmpty() -> {
                val criteriaList = profile.socialsMap.entries.map {
                    criteriaWhere("socialsMap.${it.key}")
                            .exists(true)
                            .and("socialsMap.${it.key}.id")
                            .`is`(it.value.getOrDefault("id", EMPTY))
                }

                val query = Query(Criteria().orOperator(*criteriaList.toTypedArray()))

                val profiles = template.find(query, Profile::class.java).await()

                when {
                    profiles.isNullOrEmpty() -> saveProfile(profile)
                    else -> profileMapper.toDto(profiles.first())
                }
            }

            else -> saveProfile(profile)
        }
    }

    suspend fun saveProfile(profile: ProfileDTO): ProfileDTO {
        if (profile.id.isNullOrBlank()) {
            profile.id = randomUUID().toString()
        }
        val toSave = profileMapper.toEntity(profile)
        val savedProfile = repository.save(toSave).awaitStrict()
        return profileMapper.toDto(savedProfile)
    }
}