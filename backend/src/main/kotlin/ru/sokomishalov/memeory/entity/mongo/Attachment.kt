package ru.sokomishalov.memeory.entity.mongo

import ru.sokomishalov.commons.core.consts.EMPTY
import ru.sokomishalov.memeory.enums.AttachmentType
import ru.sokomishalov.memeory.enums.AttachmentType.NONE

data class Attachment(
        var url: String? = EMPTY,
        var type: AttachmentType? = NONE,
        var aspectRatio: Double? = null
)
