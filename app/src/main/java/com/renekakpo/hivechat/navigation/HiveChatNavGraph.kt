package com.renekakpo.hivechat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.renekakpo.hivechat.ui.screens.ChatListScreen
import com.renekakpo.hivechat.ui.screens.ChatScreen
import com.renekakpo.hivechat.ui.screens.SignInScreen
import com.renekakpo.hivechat.ui.screens.SplashScreen

@Composable
fun HiveChatNavGraph(modifier: Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SplashScreen.route,
        modifier = modifier
    ) {
        composable(route = SplashScreen.route) {
            SplashScreen(navController = navController)
        }

        composable(route = SignInScreen.route) {
            SignInScreen(navController = navController)
        }

        composable(route = ChatListScreen.route) {
            ChatListScreen(navController = navController)
        }

        composable(route = ChatScreen.route) {
            ChatScreen(navController = navController)
        }
    }
}