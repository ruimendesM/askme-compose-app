package com.ruimendes.auth.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import askme.feature.auth.presentation.generated.resources.Res
import askme.feature.auth.presentation.generated.resources.email
import askme.feature.auth.presentation.generated.resources.email_placeholder
import askme.feature.auth.presentation.generated.resources.login
import askme.feature.auth.presentation.generated.resources.password
import askme.feature.auth.presentation.generated.resources.password_hint
import askme.feature.auth.presentation.generated.resources.register
import askme.feature.auth.presentation.generated.resources.username
import askme.feature.auth.presentation.generated.resources.username_hint
import askme.feature.auth.presentation.generated.resources.username_placeholder
import askme.feature.auth.presentation.generated.resources.welcome_to_app
import com.ruimendes.core.designsystem.components.brand.AppBrandLogo
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.components.buttons.AppButtonStyle
import com.ruimendes.core.designsystem.components.layout.AppAdaptiveFormLayout
import com.ruimendes.core.designsystem.components.layout.AppSnackbarScaffold
import com.ruimendes.core.designsystem.components.textfields.AppPasswordTextField
import com.ruimendes.core.designsystem.components.textfields.AppTextField
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterEvent.Success -> {
                onRegisterSuccess(event.email)
            }
        }
    }

    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AppSnackbarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        AppAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_to_app),
            errorText = state.registrationError?.asString(),
            logo = { AppBrandLogo() }
        ) {
            with(state.usernameFieldState) {
                AppTextField(
                    state = textState,
                    placeholder = stringResource(Res.string.username_placeholder),
                    title = stringResource(Res.string.username),
                    supportingText = error?.asString() ?: stringResource(Res.string.username_hint),
                    isError = error != null,
                    onFocusChanged = { isFocused ->
                        onAction(RegisterAction.OnInputTextFocusGain)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            with(state.emailFieldState) {
                AppTextField(
                    state = textState,
                    placeholder = stringResource(Res.string.email_placeholder),
                    title = stringResource(Res.string.email),
                    supportingText = error?.asString(),
                    isError = error != null,
                    onFocusChanged = { isFocused ->
                        onAction(RegisterAction.OnInputTextFocusGain)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            with(state.passwordFieldState) {
                AppPasswordTextField(
                    state = textState,
                    placeholder = stringResource(Res.string.password),
                    title = stringResource(Res.string.password),
                    supportingText = error?.asString() ?: stringResource(Res.string.password_hint),
                    isError = error != null,
                    onFocusChanged = { isFocused ->
                        onAction(RegisterAction.OnInputTextFocusGain)
                    },
                    onToggleVisibilityClick = {
                        onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                    },
                    isPasswordVisible = state.isPasswordVisible
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                text = stringResource(Res.string.register),
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
                enabled = state.canRegister,
                isLoading = state.isRegistering,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(RegisterAction.OnLoginClick)
                },
                isLoading = state.isRegistering,
                style = AppButtonStyle.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
private fun LightPreview() {
    Preview(darkTheme = false)
}

@Composable
@Preview
private fun DarkPreview() {
    Preview(darkTheme = true)
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun LightMobilePreview() {
    Preview(darkTheme = false)
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun DarkMobilePreview() {
    Preview(darkTheme = true)
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun LightTabletPreview() {
    Preview(darkTheme = false)
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun DarkTabletPreview() {
    Preview(darkTheme = true)
}

@Composable
private fun Preview(
    darkTheme: Boolean
) {
    AppTheme(darkTheme = darkTheme) {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}