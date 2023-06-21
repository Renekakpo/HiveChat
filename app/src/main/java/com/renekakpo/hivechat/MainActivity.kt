package com.renekakpo.hivechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.renekakpo.hivechat.ui.HiveChatMainScreen
import com.renekakpo.hivechat.ui.theme.HiveChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HiveChatTheme {
                HiveChatMainScreen()
            }
        }
    }
}