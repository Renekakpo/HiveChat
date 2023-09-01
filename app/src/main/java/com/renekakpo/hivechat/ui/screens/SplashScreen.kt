package com.renekakpo.hivechat.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.ui.theme.HiveChatTheme
import com.renekakpo.hivechat.viewModels.SplashScreenViewModel
import com.renekakpo.hivechat.viewModels.provider.AppViewModelProvider
import kotlinx.coroutines.delay

object SplashScreen : NavDestination {
    override val route: String = "splash_screen"
}

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currentUser by viewModel.user.collectAsState()
    val clientConnected by viewModel.clientConnected.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(16.dp)
        )
    }

    LaunchedEffect(clientConnected, currentUser != null) {
        delay(2000)

        try {
            if (currentUser != null) {
                if (!clientConnected) {
                    viewModel.connectHiveMqttClient(
                        currentUser?.username!!,
                        currentUser?.password!!
                    )
                } else {
                    navController.navigate(route = ChatListScreen.route)
                }
            } else {
                navController.navigate(route = SignInScreen.route)
            }
        } catch (e: Exception) {
            Log.e("SplashScreen", "${e.message}")
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    HiveChatTheme {
        SplashScreen(navController = rememberNavController())
    }
}