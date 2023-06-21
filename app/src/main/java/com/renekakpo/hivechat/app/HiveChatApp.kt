package com.renekakpo.hivechat.app

import android.app.Application
import android.content.Context

class HiveChatApp: Application() {

    companion object {
        lateinit var appContext: Context
        private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}