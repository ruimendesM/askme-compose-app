package com.ruimendes.chat.data.di

import com.ruimendes.chat.data.lifecycle.AppLifecycleObserver
import com.ruimendes.chat.data.network.ConnectionErrorHandler
import com.ruimendes.chat.data.network.ConnectivityObserver
import com.ruimendes.chat.data.notification.FirebasePushNotificationService
import com.ruimendes.chat.database.DatabaseFactory
import com.ruimendes.chat.domain.notifications.PushNotificationService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory() }
    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
    singleOf(::ConnectionErrorHandler)
    singleOf(::FirebasePushNotificationService) bind PushNotificationService::class
}