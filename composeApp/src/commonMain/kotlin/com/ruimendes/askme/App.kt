package com.ruimendes.askme

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ruimendes.askme.navigation.DeepLinkListener
import com.ruimendes.askme.navigation.NavigationRoot
import com.ruimendes.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    DeepLinkListener(navController)

    AppTheme {
        NavigationRoot(navController)
    }
}