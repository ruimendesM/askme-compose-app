package com.ruimendes.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ruimendes.chat.data.chat.KtorChatParticipantService
import com.ruimendes.chat.data.chat.KtorChatService
import com.ruimendes.chat.database.DatabaseFactory
import com.ruimendes.chat.domain.chat.ChatParticipantService
import com.ruimendes.chat.domain.chat.ChatService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module
val chatDataModule = module {
    includes(platformChatDataModule)

    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}