package com.ruimendes.core.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import com.ruimendes.core.presentation.util.currentDeviceConfiguration

@Composable
fun AppAdaptativeDialogSheetLayout(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = currentDeviceConfiguration()
    if(configuration.isMobile) {
        AppBottomSheet(
            onDismiss = onDismiss,
            content = content
        )
    } else {
        AppDialogContent(
            onDismiss = onDismiss,
            content = content
        )
    }
}