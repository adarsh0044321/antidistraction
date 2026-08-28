package com.adarshsingh.antidistraction.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.adarshsingh.antidistraction.MainActivity
import com.adarshsingh.antidistraction.R
import com.adarshsingh.antidistraction.domain.engine.FocusSessionEngine
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusForegroundService : Service() {

    @Inject
    lateinit var sessionEngine: FocusSessionEngine

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var isStarted = false

    companion object {
        private const val CHANNEL_ID = "focus_session_channel"
        private const val NOTIFICATION_ID = 1001

        fun startService(context: Context) {
            val intent = Intent(context, FocusForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FocusForegroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observeSessionState()
        Logger.i("FocusForegroundService", "Foreground service created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            val notification = createNotification("Protection Active", "Focus session in progress")
            startForeground(NOTIFICATION_ID, notification)
            isStarted = true
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun observeSessionState() {
        serviceScope.launch {
            sessionEngine.sessionState.collectLatest { session ->
                when (session.state) {
                    FocusState.FOCUS_ACTIVE, FocusState.RESUMED -> {
                        val mins = session.remainingSeconds / 60
                        val secs = session.remainingSeconds % 60
                        val timeStr = String.format("%02d:%02d remaining", mins, secs)
                        updateNotification("${session.mode.name.replace("_", " ")} Active", timeStr)
                    }
                    FocusState.PAUSED -> {
                        updateNotification("Focus Session Paused", "Tap to resume focus")
                    }
                    FocusState.IDLE, FocusState.FOCUS_COMPLETED, FocusState.FOCUS_ABANDONED -> {
                        stopForeground(true)
                        stopSelf()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateNotification(title: String, content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(title, content))
    }

    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Active Focus Session Protection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active focus timer and session status"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
