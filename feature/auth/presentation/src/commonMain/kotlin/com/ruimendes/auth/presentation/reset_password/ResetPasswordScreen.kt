package com.ruimendes.auth.presentation.reset_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import askme.feature.auth.presentation.generated.resources.Res
import askme.feature.auth.presentation.generated.resources.forgot_password_email_sent_successfully
import askme.feature.auth.presentation.generated.resources.new_password
import askme.feature.auth.presentation.generated.resources.password
import askme.feature.auth.presentation.generated.resources.password_hint
import askme.feature.auth.presentation.generated.resources.set_new_password
import askme.feature.auth.presentation.generated.resources.submit
import com.ruimendes.core.designsystem.components.brand.AppBrandLogo
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.components.layout.AppAdaptiveFormLayout
import com.ruimendes.core.designsystem.components.layout.AppSnackbarScaffold
import com.ruimendes.core.designsystem.components.textfields.AppPasswordTextField
import com.ruimendes.core.designsystem.components.textfields.AppTextField
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoot(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    AppSnackbarScaffold {
        AppAdaptiveFormLayout(
            headerText = stringResource(Res.string.set_new_password),
            errorText = state.errorText?.asString(),
            logo = {
                AppBrandLogo()
            }
        ) {
            AppPasswordTextField(
                state = state.passwordTextState,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.new_password),
                supportingText = stringResource(Res.string.password_hint),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ResetPasswordAction.OnTogglePasswordVisibilityClick)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = stringResource(Res.string.submit),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(ResetPasswordAction.OnSubmitClick)
                },
                enabled = !state.isLoading && state.canSubmit,
                isLoading = state.isLoading
            )
            if (state.isResetSuccessful) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.forgot_password_email_sent_successfully),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.success,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center

                )
            }
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

@Preview
@Composable
private fun Preview(darkTheme: Boolean) {
    AppTheme(darkTheme = darkTheme) {
        ResetPasswordScreen(
            state = ResetPasswordState(),
            onAction = {}
        )
    }
}