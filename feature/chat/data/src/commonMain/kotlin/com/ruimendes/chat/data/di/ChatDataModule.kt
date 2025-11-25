package com.ruimendes.chat.data.di

import com.ruimendes.chat.data.chat.KtorChatParticipantService
import com.ruimendes.chat.data.chat.KtorChatService
import com.ruimendes.chat.domain.chat.ChatParticipantService
import com.ruimendes.chat.domain.chat.ChatService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatDataModule = module {
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
}