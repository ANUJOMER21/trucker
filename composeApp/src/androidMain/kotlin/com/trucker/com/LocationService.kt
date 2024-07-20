package com.trucker.com

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class LocationService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val interval: Long = 15 * 60 * 1000 // 5 minutes in milliseconds

    private val runnable = object : Runnable {
        override fun run() {
            // Enqueue the LocationWorker
            val workRequest = OneTimeWorkRequestBuilder<LocationWorker>()
                .build()
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
            handler.postDelayed(this, interval)
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val channelId = "location_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}
