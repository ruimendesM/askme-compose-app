package com.ruimendes.chat.presentation.anonymous_inbox

import com.ruimendes.core.presentation.util.UiText

sealed interface AnonymousInboxEvent {
    data object OnNewMessage : AnonymousInboxEvent
    data class OnError(val error: UiText) : AnonymousInboxEvent
}
