package com.example.speedguard.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

class SpeedTrackingService : Service() {
    private val channelID = "speed_tracking"
    private val channelName = "Speed Tracking Channel"
    private val notificationContentTitle = "Speed Guard"
    private val notificationIcon = android.R.drawable.ic_menu_mylocation




    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


}