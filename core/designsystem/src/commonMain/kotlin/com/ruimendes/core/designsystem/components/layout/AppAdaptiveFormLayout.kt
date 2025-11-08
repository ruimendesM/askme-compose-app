package com.ruimendes.core.designsystem.components.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ruimendes.core.designsystem.components.brand.AppBrandLogo
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.designsystem.theme.extended
import com.ruimendes.core.presentation.util.DeviceConfiguration
import com.ruimendes.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppAdaptiveFormLayout(
    headerText: String,
    errorText: String? = null,
    logo: @Composable () -> Unit,
    formContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    deviceConfiguration: DeviceConfiguration = currentDeviceConfiguration()
) {
    val headerColor = if (deviceConfiguration == DeviceConfiguration.MOBILE_LANDSCAPE) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.extended.textPrimary
    }

    when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            AppSurface(
                modifier = modifier
                    .consumeWindowInsets(WindowInsets.navigationBars)
                    .consumeWindowInsets(WindowInsets.displayCutout),
                header = {
                    Spacer(modifier = Modifier.height(24.dp))
                    logo()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                AuthHeaderSection(
                    headerText = headerText,
                    headerColor = headerColor,
                    errorText = errorText
                )
                Spacer(modifier = Modifier.height(24.dp))
                formContent()
            }
        }

        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .consumeWindowInsets(WindowInsets.displayCutout)
            ) {
                Column(
                    modifier = modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    logo()
                    AuthHeaderSection(
                        headerText = headerText,
                        headerColor = headerColor,
                        errorText = errorText,
                        textAlign = TextAlign.Start
                    )
                }
                AppSurface(modifier = modifier.weight(1f).padding(top = 16.dp, end = 16.dp)) {
                    formContent()
                }
            }
        }

        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                logo()
                Column(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthHeaderSection(
                        headerText = headerText,
                        headerColor = headerColor,
                        errorText = errorText
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    formContent()
                }
            }
        }
    }
}

@Composable
fun AuthHeaderSection(
    headerText: String,
    headerColor: Color,
    errorText: String? = null,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = headerText,
        style = MaterialTheme.typography.titleLarge,
        color = headerColor,
        textAlign = textAlign,
        modifier = Modifier.fillMaxWidth()
    )
    AnimatedVisibility(
        visible = errorText != null
    ) {
        errorText?.let {
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = textAlign
            )
        }
    }
}

// Previews

@Composable
@Preview
private fun LightAppAdaptiveFormLayoutPreview() {
    AppAdaptiveFormLayoutPreview(
        darkTheme = false,
        deviceConfiguration = DeviceConfiguration.MOBILE_PORTRAIT
    )
}

@Composable
@Preview
private fun DarkAppAdaptiveFormLayoutPreview() {
    AppAdaptiveFormLayoutPreview(
        darkTheme = true,
        deviceConfiguration = DeviceConfiguration.MOBILE_PORTRAIT
    )
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun LightMobileLandscapeAppAdaptiveFormLayoutPreview() {
    AppAdaptiveFormLayoutPreview(
        darkTheme = false,
        deviceConfiguration = DeviceConfiguration.MOBILE_LANDSCAPE
    )
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun DarkMobileLandscapeAppAdaptiveFormLayoutPreview() {
    AppAdaptiveFormLayoutPreview(
        darkTheme = true,
        deviceConfiguration = DeviceConfiguration.MOBILE_LANDSCAPE
    )
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun LightTabletAppAdaptiveFormLayoutPreview() {
    AppAdaptiveFormLayoutPreview(
        darkTheme = false,
        deviceConfiguration = DeviceConfiguration.TABLET_LANDSCAPE
    )
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun DarkTabletAppAdaptiveFormLayoutPreview() {
    AppAdaptiveFormLayoutPreview(
        darkTheme = true,
        deviceConfiguration = DeviceConfiguration.TABLET_LANDSCAPE
    )
}

@Composable
fun AppAdaptiveFormLayoutPreview(
    darkTheme: Boolean,
    deviceConfiguration: DeviceConfiguration
) {
    AppTheme(darkTheme = darkTheme) {
        AppAdaptiveFormLayout(
            headerText = "Welcome to AskMe!",
            errorText = "Login failed!",
            logo = { AppBrandLogo() },
            formContent = {
                Text(
                    text = "Sample form title",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sample form title 2",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            deviceConfiguration = deviceConfiguration
        )
    }
}