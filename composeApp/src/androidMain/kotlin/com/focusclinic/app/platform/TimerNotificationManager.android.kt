package com.focusclinic.app.platform

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.focusclinic.app.MainActivity

actual class TimerNotificationManager(private val context: Context) : TimerNotification {

    private var wakeLock: PowerManager.WakeLock? = null

    actual override fun onSessionStarted(durationMinutes: Int) {
        acquireWakeLock(durationMinutes)

        val intent = Intent(context, FocusTimerService::class.java).apply {
            putExtra(FocusTimerService.EXTRA_DURATION, durationMinutes)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    actual override fun onSessionCompleted(earnedXp: Long, earnedCoins: Long) {
        stopService()
        releaseWakeLock()
        showCompletionNotification(earnedXp, earnedCoins)
    }

    actual override fun onSessionStopped() {
        stopService()
        releaseWakeLock()
    }

    private fun stopService() {
        context.stopService(Intent(context, FocusTimerService::class.java))
    }

    private fun acquireWakeLock(durationMinutes: Int) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "FocusClinic::TimerWakeLock",
        ).apply {
            val timeoutMs = (durationMinutes + 1) * 60L * 1000L
            acquire(timeoutMs)
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
    }

    private fun showCompletionNotification(earnedXp: Long, earnedCoins: Long) {
        createCompletionChannel()

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(context, COMPLETION_CHANNEL_ID)
            .setContentTitle("Session Complete!")
            .setContentText("Earned +$earnedXp XP and +$earnedCoins Coins")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    private fun createCompletionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                COMPLETION_CHANNEL_ID,
                COMPLETION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Notifies when a focus session is completed"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val COMPLETION_CHANNEL_ID = "focus_completion_channel"
        private const val COMPLETION_CHANNEL_NAME = "Session Complete"
        private const val COMPLETION_NOTIFICATION_ID = 1002
    }
}
