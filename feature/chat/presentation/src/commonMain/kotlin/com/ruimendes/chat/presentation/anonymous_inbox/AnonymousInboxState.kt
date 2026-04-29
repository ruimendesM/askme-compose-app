package com.ruimendes.chat.presentation.anonymous_inbox

import com.ruimendes.chat.domain.models.ConnectionState
import com.ruimendes.chat.presentation.chat_detail.BannerState
import com.ruimendes.chat.presentation.model.MessageUI
import com.ruimendes.core.presentation.util.UiText

data class AnonymousInboxState(
    val messages: List<MessageUI> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val isPaginationLoading: Boolean = false,
    val paginationError: UiText? = null,
    val endReached: Boolean = false,
    val isNearBottom: Boolean = true,
    val bannerState: BannerState = BannerState()
)
