package com.example.speedguard.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

fun createNotification(
    context: Context,
    content: String,
    channelID: String,
    channelName: String,
    contentTitle: String,
    smallIcon: Int
): Notification {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelID, channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(context, channelID)
        .setContentTitle(contentTitle)
        .setContentText(content)
        .setSmallIcon(smallIcon)
        .build()
}