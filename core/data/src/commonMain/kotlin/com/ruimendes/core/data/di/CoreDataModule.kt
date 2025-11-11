package com.ruimendes.core.data.di

import com.ruimendes.core.data.auth.KtorAuthService
import com.ruimendes.core.data.logger.KermitLogger
import com.ruimendes.core.data.networking.HttpClientFactory
import com.ruimendes.core.domain.auth.AuthService
import com.ruimendes.core.domain.logging.AppLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<AppLogger> { KermitLogger }
    single {
        HttpClientFactory(get()).create(get())
    }
    singleOf(::KtorAuthService) bind AuthService::class
}