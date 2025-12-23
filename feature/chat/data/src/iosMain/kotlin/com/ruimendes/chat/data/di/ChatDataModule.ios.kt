package com.ruimendes.chat.data.di

import com.ruimendes.chat.data.lifecycle.AppLifecycleObserver
import com.ruimendes.chat.data.network.ConnectivityObserver
import com.ruimendes.chat.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory() }
    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
}