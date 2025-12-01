package com.ruimendes.chat.presentation.chat_list

import com.ruimendes.chat.presentation.model.ChatUI

sealed interface ChatListAction {
    data object OnUserAvatarClick: ChatListAction
    data object OnDismissUserMenu: ChatListAction
    data object OnLogoutClick: ChatListAction
    data object OnConfirmLogout: ChatListAction
    data object OnDismissLogoutDialog: ChatListAction
    data object OnCreateChatClick: ChatListAction
    data object OnProfileSettingsClick: ChatListAction
    data class OnChatClick(val chat: ChatUI): ChatListAction
}