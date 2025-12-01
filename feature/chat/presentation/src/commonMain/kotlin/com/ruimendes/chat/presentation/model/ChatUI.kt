package com.ruimendes.chat.presentation.model

import com.ruimendes.chat.domain.models.ChatMessage
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI

data class ChatUI(
    val id: String,
    val localParticipant: ChatParticipantUI,
    val otherParticipants: List<ChatParticipantUI>,
    val lastMessage: ChatMessage?,
    val lastMessageSenderUsername: String?
)
