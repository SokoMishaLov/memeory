package ru.sokomishalov.memeory.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.sokomishalov.memeory.util.EMPTY
import java.util.*

@Document
data class Meme(
        @Id
        var id: String = EMPTY,
        var caption: String? = null,
        var channelId: String? = null,
        var channelName: String? = null,
        var attachments: List<Attachment>? = null,
        var publishedAt: Date = Date(),
        var createdAt: Date = Date()
)
