package com.ruimendes.chat.data.dto.websocket

import kotlinx.serialization.Serializable

enum class OutgoingWebSocketType {
    NEW_MESSAGE
}

@Serializable
sealed interface OutgoingWebSocketDto {

    @Serializable
    data class NewMessage(
        val chatId: String,
        val messageId: String,
        val content: String, val type: OutgoingWebSocketType = OutgoingWebSocketType.NEW_MESSAGE
    ) : OutgoingWebSocketDto
}