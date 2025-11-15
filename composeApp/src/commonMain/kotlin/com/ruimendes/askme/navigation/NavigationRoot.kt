package com.ruimendes.askme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ruimendes.auth.presentation.navigation.AuthGraphRoutes
import com.ruimendes.auth.presentation.navigation.authGraph
import com.ruimendes.chat.presentation.chat_list.ChatListRoot
import com.ruimendes.chat.presentation.chat_list.ChatListRoute


@Composable
fun NavigationRoot(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = AuthGraphRoutes.Graph
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(ChatListRoute) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
        composable<ChatListRoute> {
            ChatListRoot()
        }
    }
}