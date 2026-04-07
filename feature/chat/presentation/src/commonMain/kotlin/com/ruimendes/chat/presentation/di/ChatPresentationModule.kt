package com.ruimendes.chat.presentation.di

import com.ruimendes.chat.presentation.anonymous_inbox.AnonymousInboxViewModel
import com.ruimendes.chat.presentation.chat_detail.ChatDetailViewModel
import com.ruimendes.chat.presentation.chat_list.ChatListViewModel
import com.ruimendes.chat.presentation.chat_list_detail.ChatListDetailViewModel
import com.ruimendes.chat.presentation.create_chat.CreateChatViewModel
import com.ruimendes.chat.presentation.manage_chat.ManageChatViewModel
import com.ruimendes.chat.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::AnonymousInboxViewModel)
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ManageChatViewModel)
    viewModelOf(::ProfileViewModel)
}



