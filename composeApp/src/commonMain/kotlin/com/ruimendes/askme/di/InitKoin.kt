package com.ruimendes.askme.di

import com.ruimendes.auth.presentation.di.authPresentationModule
import com.ruimendes.chat.data.di.chatDataModule
import com.ruimendes.chat.presentation.di.chatPresentationModule
import com.ruimendes.core.data.di.coreDataModule
import com.ruimendes.core.presentation.di.corePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            appModule,
            coreDataModule,
            corePresentationModule,
            authPresentationModule,
            chatPresentationModule,
            chatDataModule
        )
    }
}