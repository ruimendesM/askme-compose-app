package com.ruimendes.chat.presentation.anonymous_inbox

sealed interface AnonymousInboxAction {
    data object OnScrollToTop : AnonymousInboxAction
    data object OnRetryPaginationClick : AnonymousInboxAction
    data class OnFirstVisibleIndexChanged(val index: Int) : AnonymousInboxAction
    data class OnTopVisibleIndexChanged(val topVisibleIndex: Int) : AnonymousInboxAction
    data object OnHideBanner : AnonymousInboxAction
}
