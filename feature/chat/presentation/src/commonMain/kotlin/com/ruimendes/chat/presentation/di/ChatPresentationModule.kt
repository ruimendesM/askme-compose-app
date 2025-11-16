package com.ruimendes.chat.presentation.di

import com.ruimendes.chat.presentation.chat_list.ChatListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
}



