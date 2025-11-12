package com.ruimendes.auth.presentation.register_success

sealed interface RegisterSuccessEvent {
    data object ResentVerificationEmailSuccess: RegisterSuccessEvent
}