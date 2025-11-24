package com.ruimendes.askme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ruimendes.auth.presentation.navigation.AuthGraphRoutes
import com.ruimendes.auth.presentation.navigation.authGraph
import com.ruimendes.chat.presentation.navigation.ChatGraphRoutes
import com.ruimendes.chat.presentation.navigation.chatGraph


@Composable
fun NavigationRoot(
    navController: NavHostController,
    startDestination: Any
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(ChatGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
        chatGraph(navController)
    }
}