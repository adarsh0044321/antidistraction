package com.adarshsingh.antidistraction.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.adarshsingh.antidistraction.R
import com.adarshsingh.antidistraction.util.Logger

class WakeAlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "wake_alarm_channel"
        private const val NOTIFICATION_ID = 2001
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Logger.i("WakeAlarmReceiver", "Wake alarm triggered!")
        showWakeNotification(context)
    }

    private fun showWakeNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Wake Up Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily wake up alarm and morning focus reminder"
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("🌅 Good Morning! Time to Wake Up")
            .setContentText("Start your day with clarity. Set today's focus goals!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }
}
