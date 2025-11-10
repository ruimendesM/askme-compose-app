package com.ruimendes.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.ruimendes.core.presentation.util.UiText

data class RegisterState(
    val emailFieldState: RegisterFieldState = RegisterFieldState(),
    val passwordFieldState: RegisterFieldState = RegisterFieldState(),
    val usernameFieldState: RegisterFieldState = RegisterFieldState(),
    val registrationError: UiText? = null,
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false,
    val isPasswordVisible: Boolean = false,
)

data class RegisterFieldState(
    val textState: TextFieldState = TextFieldState(),
    val isValid: Boolean = false,
    val error: UiText? = null
)