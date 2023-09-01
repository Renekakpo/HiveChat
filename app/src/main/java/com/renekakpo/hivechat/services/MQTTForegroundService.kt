package com.renekakpo.hivechat.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.renekakpo.hivechat.helpers.HiveChatClientHelper

class MQTTForegroundService : Service() {
    companion object {
        private const val CHANNEL_ID = "MQTTForegroundServiceChannel"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Perform your MQTT client connection setup here
        val hiveClient = HiveChatClientHelper.getClient()

        Log.e("onStartCommand", "${hiveClient.config.state.isConnected}")

        // Create a notification channel for the foreground service
        createNotificationChannel()

        // Build the notification
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MQTT Service")
            .setContentText("MQTT client is connected")
//            .setSmallIcon(R.drawable.notification_icon)
            .build()

        // Start the service in the foreground with the notification
        startForeground(1, notification)

        // Return START_STICKY to ensure the service is automatically restarted if it's killed by the system
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        // Disconnect and clean up your MQTT client resources here
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MQTT Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}