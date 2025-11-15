package com.ruimendes.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import askme.feature.auth.presentation.generated.resources.Res
import askme.feature.auth.presentation.generated.resources.error_account_exists
import askme.feature.auth.presentation.generated.resources.error_invalid_email
import askme.feature.auth.presentation.generated.resources.error_invalid_password
import askme.feature.auth.presentation.generated.resources.error_invalid_username
import com.ruimendes.auth.domain.EmailValidator
import com.ruimendes.core.domain.auth.AuthService
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.onFailure
import com.ruimendes.core.domain.util.onSuccess
import com.ruimendes.core.domain.validation.PasswordValidator
import com.ruimendes.core.presentation.util.UiText
import com.ruimendes.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    val authService: AuthService
) : ViewModel() {

    private val eventChannel = Channel<RegisterEvent>()
    val events by lazy {
        eventChannel.receiveAsFlow()
    }
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    private val isEmailValidFlow =
        snapshotFlow { state.value.emailFieldState.textState.text.toString() }
            .map { email -> EmailValidator.validate(email) }
            .distinctUntilChanged()

    private val isUsernameValidFlow =
        snapshotFlow { state.value.usernameFieldState.textState.text.toString() }
            .map { username -> username.length in 3..20 }
            .distinctUntilChanged()

    private val isPasswordValidFlow =
        snapshotFlow { state.value.passwordFieldState.textState.text.toString() }
            .map { password -> PasswordValidator.validate(password).isValidPassword }
            .distinctUntilChanged()

    private val isRegisteringFlow = state
        .map { it.isRegistering }
        .distinctUntilChanged()

    private fun observeValidationStates() {
        combine(
            isEmailValidFlow,
            isUsernameValidFlow,
            isPasswordValidFlow,
            isRegisteringFlow
        ) { isEmailValid, isUsernameValid, isPasswordValid, isRegistering ->
            _state.update {
                it.copy(
                    canRegister = !isRegistering && isEmailValid && isUsernameValid && isPasswordValid
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }

            else -> Unit
        }
    }

    private fun register() {
        if (!validateFormInputs()) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRegistering = true
                )
            }

            val email = state.value.emailFieldState.textState.text.toString()
            val username = state.value.usernameFieldState.textState.text.toString()
            val password = state.value.passwordFieldState.textState.text.toString()

            authService
                .register(
                    email = email,
                    username = username,
                    password = password
                )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isRegistering = false
                        )
                    }
                    eventChannel.send(RegisterEvent.Success(email))
                }
                .onFailure { error ->
                    val registrationError = when (error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isRegistering = false,
                            registrationError = registrationError
                        )
                    }
                }
        }
    }

    private fun clearAllTextFieldErrors() {
        _state.update {
            it.copy(
                emailFieldState = it.emailFieldState.copy(error = null),
                usernameFieldState = it.usernameFieldState.copy(error = null),
                passwordFieldState = it.passwordFieldState.copy(error = null),
                registrationError = null
            )
        }
    }

    private fun validateFormInputs(): Boolean {
        clearAllTextFieldErrors()

        val currentState = state.value
        val email = currentState.emailFieldState.textState.text.toString()
        val username = currentState.usernameFieldState.textState.text.toString()
        val password = currentState.passwordFieldState.textState.text.toString()

        val isEmailValid = EmailValidator.validate(email)
        val passwordValidationState = PasswordValidator.validate(password)
        val isUsernameValid = username.length in 3..20

        val emailError = if (!isEmailValid) {
            UiText.Resource(Res.string.error_invalid_email)
        } else null

        val usernameError = if (!isUsernameValid) {
            UiText.Resource(Res.string.error_invalid_username)
        } else null

        val passwordError = if (!passwordValidationState.isValidPassword) {
            UiText.Resource(Res.string.error_invalid_password)
        } else null

        _state.update {
            it.copy(
                emailFieldState = it.emailFieldState.copy(error = emailError),
                usernameFieldState = it.usernameFieldState.copy(error = usernameError),
                passwordFieldState = it.passwordFieldState.copy(error = passwordError),
            )
        }

        return isUsernameValid && isEmailValid && passwordValidationState.isValidPassword
    }

}