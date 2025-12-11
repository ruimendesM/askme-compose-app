package com.ruimendes.chat.data.mappers

import com.ruimendes.chat.data.dto.ChatMessageDto
import com.ruimendes.chat.database.entities.ChatMessageEntity
import com.ruimendes.chat.database.view.LastMessageView
import com.ruimendes.chat.domain.models.ChatMessage
import com.ruimendes.chat.domain.models.ChatMessageDeliveryStatus
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = Instant.parse(createdAt),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.SENT,
    )
}

fun LastMessageView.toDomain(): ChatMessage {
    return ChatMessage(
        id = messageId,
        chatId = chatId,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(timestamp),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(this.deliveryStatus),
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = id,
        chatId = chatId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        senderId = senderId,
        deliveryStatus = deliveryStatus.name,
    )
}

fun ChatMessage.toLastMessageView(): LastMessageView {
    return LastMessageView(
        messageId = id,
        chatId = chatId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        senderId = senderId,
        deliveryStatus = deliveryStatus.name,
    )
}