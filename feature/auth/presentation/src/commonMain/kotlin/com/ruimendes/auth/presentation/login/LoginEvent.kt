package com.ruimendes.auth.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
}