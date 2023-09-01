package com.renekakpo.hivechat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.renekakpo.hivechat.ui.screens.*

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

        composable(route = ContactsScreen.route) {
            ContactsScreen(navController = navController)
        }

        composable(
            route = ChatScreen.routeWithArgs,
            arguments = listOf(
                navArgument(name = ChatScreen.itemToJsonArg) {
                    type = NavType.StringType
                },
                navArgument(name = ChatScreen.itemUUIDArg) {
                    type = NavType.StringType
                },
                navArgument(name = ChatScreen.itemExistsArg) {
                    type = NavType.BoolType
                }
            )
        ) {
            ChatScreen(navController = navController)
        }
    }
}