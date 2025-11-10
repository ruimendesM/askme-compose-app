package com.ruimendes.core.designsystem.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ruimendes.core.designsystem.components.brand.AppBrandLogo
import com.ruimendes.core.designsystem.theme.AppTheme
import com.ruimendes.core.presentation.util.DeviceConfiguration
import com.ruimendes.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppAdaptativeResultLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    deviceConfiguration: DeviceConfiguration = currentDeviceConfiguration()
) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        if (deviceConfiguration == DeviceConfiguration.MOBILE_PORTRAIT) {
            AppSurface(
                modifier = Modifier.padding(innerPadding),
                header = {
                    Spacer(modifier = Modifier.height(24.dp))
                    AppBrandLogo()
                    Spacer(modifier = Modifier.height(24.dp))
                },
                content = content
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (deviceConfiguration != DeviceConfiguration.MOBILE_LANDSCAPE) {
                    AppBrandLogo()
                }
                Column(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
@Preview
private fun LightPreview() {
    AppAdaptativeResultLayoutPreview(
        darkTheme = false,
        deviceConfiguration = DeviceConfiguration.MOBILE_PORTRAIT
    )
}

@Composable
@Preview
private fun DarkPreview() {
    AppAdaptativeResultLayoutPreview(
        darkTheme = true,
        deviceConfiguration = DeviceConfiguration.MOBILE_PORTRAIT
    )
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun LightMobileLandscapePreview() {
    AppAdaptativeResultLayoutPreview(
        darkTheme = false,
        deviceConfiguration = DeviceConfiguration.MOBILE_LANDSCAPE
    )
}

@Composable
@Preview(widthDp = 840, heightDp = 481)
private fun DarkMobileLandscapePreview() {
    AppAdaptativeResultLayoutPreview(
        darkTheme = true,
        deviceConfiguration = DeviceConfiguration.MOBILE_LANDSCAPE
    )
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun LightTabletPreview() {
    AppAdaptativeResultLayoutPreview(
        darkTheme = false,
        deviceConfiguration = DeviceConfiguration.TABLET_LANDSCAPE
    )
}

@Composable
@Preview(widthDp = 1000, heightDp = 600)
private fun DarkTabletPreview() {
    AppAdaptativeResultLayoutPreview(
        darkTheme = true,
        deviceConfiguration = DeviceConfiguration.TABLET_LANDSCAPE
    )
}

@Composable
private fun AppAdaptativeResultLayoutPreview(
    darkTheme: Boolean,
    deviceConfiguration: DeviceConfiguration
) {
    AppTheme(darkTheme = darkTheme) {
        AppAdaptativeResultLayout(deviceConfiguration = deviceConfiguration, content = {
            Text(
                text = "Registration Successful!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        })
    }
}