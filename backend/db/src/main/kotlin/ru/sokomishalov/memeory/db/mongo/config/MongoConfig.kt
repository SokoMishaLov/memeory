package ru.sokomishalov.memeory.db.mongo.config

import com.mongodb.MongoClientOptions
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import ru.sokomishalov.memeory.core.util.consts.MONGO_KEY_DOT_REPLACEMENT
import java.time.Duration.ofSeconds


/**
 * @author sokomishalov
 */
@Configuration
@EnableReactiveMongoRepositories("ru.sokomishalov.memeory.db.mongo.repository")
@EnableConfigurationProperties(MongoProperties::class)
class MongoConfig {

    @Bean
    @Primary
    fun mongoOptions(): MongoClientOptions {
        return MongoClientOptions
                .builder()
                .retryWrites(false)
                .connectTimeout(ofSeconds(10).toMillis().toInt())
                .socketTimeout(ofSeconds(10).toMillis().toInt())
                .build()
    }

    @Bean
    @Primary
    fun mongoConverter(mongoFactory: MongoDbFactory, mongoMappingContext: MongoMappingContext): MappingMongoConverter {
        return MappingMongoConverter(DefaultDbRefResolver(mongoFactory), mongoMappingContext).apply {
            setMapKeyDotReplacement(MONGO_KEY_DOT_REPLACEMENT)
        }
    }
}
