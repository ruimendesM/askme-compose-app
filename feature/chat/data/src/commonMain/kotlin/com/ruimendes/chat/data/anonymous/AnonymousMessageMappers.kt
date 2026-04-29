package com.ruimendes.chat.data.anonymous

import com.ruimendes.chat.database.entities.AnonymousMessageEntity
import com.ruimendes.chat.domain.anonymous.AnonymousMessage
import kotlin.time.Instant

fun AnonymousMessageDto.toDomain(): AnonymousMessage {
    return AnonymousMessage(
        id = id,
        senderEmail = senderEmail,
        content = content,
        createdAt = Instant.parse(createdAt)
    )
}

fun AnonymousMessageEntity.toDomain(): AnonymousMessage {
    return AnonymousMessage(
        id = id,
        senderEmail = senderEmail,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(createdAt)
    )
}

fun AnonymousMessage.toEntity(): AnonymousMessageEntity {
    return AnonymousMessageEntity(
        id = id,
        senderEmail = senderEmail,
        content = content,
        createdAt = createdAt.toEpochMilliseconds()
    )
}
