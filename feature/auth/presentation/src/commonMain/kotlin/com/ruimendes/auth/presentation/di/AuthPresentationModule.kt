package com.ruimendes.auth.presentation.di

import com.ruimendes.auth.presentation.email_verification.EmailVerificationViewModel
import com.ruimendes.auth.presentation.login.LoginViewModel
import com.ruimendes.auth.presentation.register.RegisterViewModel
import com.ruimendes.auth.presentation.register_success.RegisterSuccessViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LoginViewModel)
}