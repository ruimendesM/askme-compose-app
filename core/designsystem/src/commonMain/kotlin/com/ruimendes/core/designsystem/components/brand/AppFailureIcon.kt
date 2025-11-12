package com.ruimendes.core.designsystem.components.brand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import askme.core.designsystem.generated.resources.Res
import askme.core.designsystem.generated.resources.success_checkmark
import com.ruimendes.core.designsystem.theme.extended
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AppFailureIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}