package ru.sokomishalov.memeory.db.mongo.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.sokomishalov.commons.core.consts.EMPTY
import java.util.*

@Document
data class Meme(
        @Id
        var id: String = EMPTY,
        var caption: String? = null,
        var channelId: String? = null,
        var attachments: List<Attachment> = emptyList(),
        var publishedAt: Date = Date(),
        @CreatedDate
        var createdAt: Date = Date()
)
