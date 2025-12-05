package com.ruimendes.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.TextFieldState
import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.chat.presentation.model.ChatUI
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.core.presentation.util.UiText

data class ChatDetailState(
    val chat: ChatUI? = null,
    val isLoading: Boolean = false,
    val messages: List<MessageUI> = emptyList(),
    val error: UiText? = null,
    val messageTextFieldState: TextFieldState = TextFieldState(),
    val canSendMessage: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val paginationError: UiText? = null,
    val endReached: Boolean = false,
    val bannerState: BannerState = BannerState(),
    val isChatOptionsOpen: Boolean = false,
    val isNearBottom: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)

data class BannerState(
    val formattedDate: UiText? = null,
    val isVisible: Boolean = false
)