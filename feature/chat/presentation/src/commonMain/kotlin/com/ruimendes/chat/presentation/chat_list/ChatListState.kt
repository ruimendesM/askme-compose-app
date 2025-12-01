package com.ruimendes.chat.presentation.chat_list

import com.ruimendes.chat.presentation.model.ChatUI
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatUI> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUI? = null,
    val isUserMenuOpen: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false
)