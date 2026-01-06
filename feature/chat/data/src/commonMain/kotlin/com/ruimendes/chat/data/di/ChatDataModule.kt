package com.ruimendes.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ruimendes.chat.data.chat.KtorChatParticipantService
import com.ruimendes.chat.data.chat.KtorChatService
import com.ruimendes.chat.data.chat.OfflineFirstChatRepository
import com.ruimendes.chat.data.chat.WebSocketChatConnectionClient
import com.ruimendes.chat.data.message.OfflineFirstMessageRepository
import com.ruimendes.chat.data.network.KtorWebSocketConnector
import com.ruimendes.chat.database.DatabaseFactory
import com.ruimendes.chat.domain.chat.ChatConnectionClient
import com.ruimendes.chat.domain.chat.ChatParticipantService
import com.ruimendes.chat.domain.chat.ChatRepository
import com.ruimendes.chat.domain.chat.ChatService
import com.ruimendes.chat.domain.message.MessageRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module
val chatDataModule = module {
    includes(platformChatDataModule)

    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    singleOf(::OfflineFirstChatRepository) bind ChatRepository::class
    singleOf(::OfflineFirstMessageRepository) bind MessageRepository::class
    singleOf(::WebSocketChatConnectionClient) bind ChatConnectionClient::class
    singleOf(::KtorWebSocketConnector)
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}