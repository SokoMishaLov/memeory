package ru.sokomishalov.memeory.db.mongo.repository

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import ru.sokomishalov.memeory.core.enums.Provider
import ru.sokomishalov.memeory.db.mongo.entity.Channel


/**
 * @author sokomishalov
 */
@Repository
interface ChannelRepository : ReactiveMongoRepository<Channel, String> {

    fun findAllByTopicsIn(topics: Iterable<String>): Flux<Channel>

    fun findAllByProvider(providerId: Provider): Flux<Channel>

}
