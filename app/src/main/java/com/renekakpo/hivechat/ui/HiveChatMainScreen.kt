package com.renekakpo.hivechat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.renekakpo.hivechat.navigation.HiveChatNavGraph
import com.renekakpo.hivechat.ui.theme.HiveChatTheme

@Composable
fun HiveChatMainScreen(modifier: Modifier = Modifier) {
    val navHostController = rememberNavController()
    HiveChatNavGraph(modifier = modifier, navController = navHostController)
}

@Preview()
@Composable
fun HiveChatMainScreenPreview() {
    HiveChatTheme {
        HiveChatMainScreen()
    }
}