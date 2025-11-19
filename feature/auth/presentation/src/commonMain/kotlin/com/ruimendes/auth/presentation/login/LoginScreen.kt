package com.ruimendes.auth.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import askme.feature.auth.presentation.generated.resources.Res
import askme.feature.auth.presentation.generated.resources.create_account
import askme.feature.auth.presentation.generated.resources.email
import askme.feature.auth.presentation.generated.resources.email_placeholder
import askme.feature.auth.presentation.generated.resources.forgot_password
import askme.feature.auth.presentation.generated.resources.login
import askme.feature.auth.presentation.generated.resources.password
import askme.feature.auth.presentation.generated.resources.welcome_back
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
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LoginEvent.Success -> onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                LoginAction.OnForgotPasswordClick -> onForgotPasswordClick()
                LoginAction.OnSignUpClick -> onCreateAccountClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    AppSnackbarScaffold {
        AppAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_back),
            errorText = state.error?.asString(),
            logo = {
                AppBrandLogo()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            AppTextField(
                state = state.emailTextFieldState,
                placeholder = stringResource(Res.string.email_placeholder),
                keyboardType = KeyboardType.Email,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppPasswordTextField(
                state = state.passwordTextFieldState,
                placeholder = stringResource(Res.string.password),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(LoginAction.OnTogglePasswordVisibility)
                },
                title = stringResource(Res.string.password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.forgot_password),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.align(Alignment.End)
                    .clickable {
                        onAction(LoginAction.OnForgotPasswordClick)
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
                enabled = state.canLogin,
                isLoading = state.isLoggingIn,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                text = stringResource(Res.string.create_account),
                onClick = {
                    onAction(LoginAction.OnSignUpClick)
                },
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
private fun Preview(darkTheme: Boolean) {
    AppTheme(darkTheme = darkTheme) {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}