package com.ruimendes.chat.presentation.mappers

import com.ruimendes.chat.domain.models.Chat
import com.ruimendes.chat.presentation.model.ChatUI

fun Chat.toUi(localParticipantId: String): ChatUI {
    val (local, other) = participants.partition { it.userId == localParticipantId }
    return ChatUI(
        id = id,
        localParticipant = local.first().toUi(),
        otherParticipants = other.map { it.toUi() },
        lastMessage = lastMessage,
        lastMessageSenderUsername = participants.find { it.userId == lastMessage?.senderId }?.username
    )
}