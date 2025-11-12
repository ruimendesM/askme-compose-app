package com.ruimendes.auth.presentation.register_success

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import askme.feature.auth.presentation.generated.resources.Res
import askme.feature.auth.presentation.generated.resources.account_successfully_created
import askme.feature.auth.presentation.generated.resources.login
import askme.feature.auth.presentation.generated.resources.resend_verification_email
import askme.feature.auth.presentation.generated.resources.resent_verification_email
import askme.feature.auth.presentation.generated.resources.verification_email_sent_to_x
import com.ruimendes.core.designsystem.components.brand.AppSuccessIcon
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.components.buttons.AppButtonStyle
import com.ruimendes.core.designsystem.components.layout.AppAdaptativeResultLayout
import com.ruimendes.core.designsystem.components.layout.AppSimpleResultLayout
import com.ruimendes.core.designsystem.components.layout.AppSnackbarScaffold
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterSuccessRoot(
    viewModel: RegisterSuccessViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterSuccessEvent.ResentVerificationEmailSuccess -> {
                snackbarHostState.showSnackbar(
                    message = getString(Res.string.resent_verification_email)
                )
            }
        }

    }

    RegisterSuccessScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterSuccessScreen(
    state: RegisterSuccessState,
    onAction: (RegisterSuccessAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    AppSnackbarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        AppAdaptativeResultLayout {
            AppSimpleResultLayout(
                title = stringResource(Res.string.account_successfully_created),
                description = stringResource(Res.string.verification_email_sent_to_x, state.registeredEmail),
                icon = {
                    AppSuccessIcon()
                },
                primaryButton = {
                    AppButton(
                        text = stringResource(Res.string.login),
                        onClick = {
                            onAction(RegisterSuccessAction.OnLoginClick)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                secondaryButton = {
                    AppButton(
                        text = stringResource(Res.string.resend_verification_email),
                        onClick = {
                            onAction(RegisterSuccessAction.OnResendVerificationEmailClick)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isResendingVerificationEmail,
                        isLoading = state.isResendingVerificationEmail,
                        style = AppButtonStyle.SECONDARY
                    )
                },
                secondaryError = state.resendVerificationError?.asString()
            )
        }
    }
}

// Previews

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

@Preview
@Composable
private fun Preview(darkTheme: Boolean) {
    AppTheme(darkTheme = darkTheme) {
        RegisterSuccessScreen(
            state = RegisterSuccessState(
                registeredEmail = "teste@preview.com"
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}