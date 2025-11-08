package com.ruimendes.core.designsystem.components.brand

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import askme.core.designsystem.generated.resources.Res
import askme.core.designsystem.generated.resources.app_logo
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AppBrandLogo(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.app_logo),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}