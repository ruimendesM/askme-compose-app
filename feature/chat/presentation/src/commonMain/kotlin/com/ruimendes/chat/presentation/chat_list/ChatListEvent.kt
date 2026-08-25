package com.ruimendes.chat.presentation.chat_list

import com.ruimendes.core.presentation.util.UiText

interface ChatListEvent {
    data object OnLogoutSuccess: ChatListEvent
    data class OnLogoutError(val error: UiText): ChatListEvent
}