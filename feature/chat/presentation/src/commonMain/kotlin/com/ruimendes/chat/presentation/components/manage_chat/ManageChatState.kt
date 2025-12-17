package com.ruimendes.chat.presentation.components.manage_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.ruimendes.core.designsystem.components.avatar.ChatParticipantUI
import com.ruimendes.core.presentation.util.UiText

data class ManageChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val existingChatParticipants: List<ChatParticipantUI> = emptyList(),
    val selectedChatParticipants: List<ChatParticipantUI> = emptyList(),
    val isSearching: Boolean = false,
    val canAddParticipant: Boolean = false,
    val currentSearchResult: ChatParticipantUI? = null,
    val searchError: UiText? = null,
    val isSubmitting: Boolean = false,
    val submitError: UiText? = null
)