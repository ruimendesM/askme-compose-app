package com.ruimendes.chat.data.dto.websocket

import kotlinx.serialization.Serializable

enum class IncomingWebSocketType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED,
    NEW_ANONYMOUS_MESSAGE
}

@Serializable
sealed interface IncomingWebSocketDto {

    @Serializable
    data class NewMessageDto(
        val id: String,
        val chatId: String,
        val content: String,
        val senderId: String,
        val createdAt: String,
        private val type: IncomingWebSocketType = IncomingWebSocketType.NEW_MESSAGE
    ) : IncomingWebSocketDto

    @Serializable
    data class MessageDeletedDto(
        val messageId: String,
        val chatId: String,
        private val type: IncomingWebSocketType = IncomingWebSocketType.MESSAGE_DELETED
    ) : IncomingWebSocketDto

    @Serializable
    data class ProfilePictureUpdatedDto(
        val userId: String,
        val newUrl: String?,
        private val type: IncomingWebSocketType = IncomingWebSocketType.PROFILE_PICTURE_UPDATED
    ) : IncomingWebSocketDto

    @Serializable
    data class ChatParticipantsChangedDto(
        val chatId: String,
        private val type: IncomingWebSocketType = IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED
    ) : IncomingWebSocketDto

    @Serializable
    data class NewAnonymousMessageDto(
        val id: String,
        val senderEmail: String,
        val content: String,
        val createdAt: String,
        private val type: IncomingWebSocketType = IncomingWebSocketType.NEW_ANONYMOUS_MESSAGE
    ) : IncomingWebSocketDto
}

