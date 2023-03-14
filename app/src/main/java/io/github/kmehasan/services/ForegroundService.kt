package io.github.kmehasan.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.github.kmehasan.R


class ForegroundService : Service() {
    private fun createNotification() {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sms_channel", "SMS Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create a notification and set its properties
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "sms_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("SMS Receiver")
            .setContentText("Listening for incoming SMS messages")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification as the foreground notification
        startForeground(1, builder.build())
    }

    override fun onCreate() {
        super.onCreate()
        createNotification();
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}
