package com.renekakpo.hivechat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.ui.theme.HiveChatTheme
import kotlinx.coroutines.delay

object SplashScreen : NavDestination {
    override val route: String = "splash_screen"
}

@Composable
fun SplashScreen(navController: NavHostController) {
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

    LaunchedEffect(navController) {
        delay(timeMillis = 2000)
        navController.navigate(route = SignInScreen.route)
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    HiveChatTheme {
        SplashScreen(navController = rememberNavController())
    }
}