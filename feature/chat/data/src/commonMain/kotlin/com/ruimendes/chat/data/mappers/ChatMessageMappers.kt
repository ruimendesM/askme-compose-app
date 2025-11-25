package com.ruimendes.chat.data.mappers

import com.ruimendes.chat.data.dto.ChatMessageDto
import com.ruimendes.chat.domain.models.ChatMessage
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = Instant.parse(createdAt),
        senderId = senderId,
    )
}