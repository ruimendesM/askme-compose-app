package com.ruimendes.auth.presentation.register_success

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import askme.feature.auth.presentation.generated.resources.Res
import askme.feature.auth.presentation.generated.resources.account_successfully_created
import askme.feature.auth.presentation.generated.resources.login
import askme.feature.auth.presentation.generated.resources.resend_verification_email
import askme.feature.auth.presentation.generated.resources.verification_email_sent_to_x
import com.ruimendes.core.designsystem.components.brand.AppBrandLogo
import com.ruimendes.core.designsystem.components.brand.AppSuccessIcon
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.components.buttons.AppButtonStyle
import com.ruimendes.core.designsystem.components.layout.AppAdaptativeResultLayout
import com.ruimendes.core.designsystem.components.layout.AppSimpleSuccessLayout
import com.ruimendes.core.designsystem.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterSuccessRoot(
    viewModel: RegisterSuccessViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterSuccessScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun RegisterSuccessScreen(
    state: RegisterSuccessState,
    onAction: (RegisterSuccessAction) -> Unit,
) {
    AppAdaptativeResultLayout {
        AppSimpleSuccessLayout(
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
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        RegisterSuccessScreen(
            state = RegisterSuccessState(
                registeredEmail = "teste@preview.com"
            ),
            onAction = {}
        )
    }
}