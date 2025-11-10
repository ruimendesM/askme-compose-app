package com.ruimendes.askme

import androidx.compose.runtime.Composable
import com.ruimendes.auth.presentation.register.RegisterRoot
import com.ruimendes.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        RegisterRoot()
    }
}