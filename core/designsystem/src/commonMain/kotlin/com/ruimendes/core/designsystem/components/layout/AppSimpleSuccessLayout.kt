package com.ruimendes.core.designsystem.components.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ruimendes.core.designsystem.components.brand.AppSuccessIcon
import com.ruimendes.core.designsystem.components.buttons.AppButton
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import com.ruimendes.core.presentation.util.DeviceConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppSimpleSuccessLayout(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    primaryButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    secondaryButton: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-25).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            primaryButton()

            if (secondaryButton != null) {
                Spacer(modifier = Modifier.height(8.dp))
                secondaryButton()
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Previews

@Composable
@Preview(showBackground = true)
private fun LightPreview() {
    AppSimpleSuccessLayoutPreview(
        darkTheme = false
    )
}

@Composable
@Preview
private fun DarkPreview() {
    AppSimpleSuccessLayoutPreview(
        darkTheme = true
    )
}

@Composable
private fun AppSimpleSuccessLayoutPreview(
    darkTheme: Boolean,
) {
    AppTheme(darkTheme = darkTheme) {
        AppSimpleSuccessLayout(
            title = "Hello world!",
            description = "Test description",
            icon = {
                AppSuccessIcon()
            },
            primaryButton = {
                AppButton(
                    text = "Log In",
                    onClick = {}
                )
            },
            secondaryButton = {
                AppButton(
                    text = "Resend verification email",
                    onClick = {},
                )
            }
        )
    }
}