package com.ruimendes.chat.presentation.create_chat

import com.ruimendes.chat.domain.models.Chat

sealed interface CreateChatEvent {
    data class OnChatCreated(val chat: Chat): CreateChatEvent
}