package ru.sokomishalov.memeory.dto

import ru.sokomishalov.memeory.enums.AttachmentType
import ru.sokomishalov.memeory.enums.AttachmentType.NONE
import ru.sokomishalov.memeory.util.EMPTY

data class AttachmentDTO(
        var url: String? = EMPTY,
        var type: AttachmentType? = NONE
)
