package com.focusclinic.app.platform

/**
 * Platform adapter for timer-related system services:
 * - Android: ForegroundService + WakeLock + Notifications
 * - iOS: Scheduled local notifications
 */
interface TimerNotification {
    fun onSessionStarted(durationMinutes: Int)
    fun onSessionCompleted(earnedXp: Long, earnedCoins: Long)
    fun onSessionStopped()
}

expect class TimerNotificationManager : TimerNotification {
    override fun onSessionStarted(durationMinutes: Int)
    override fun onSessionCompleted(earnedXp: Long, earnedCoins: Long)
    override fun onSessionStopped()
}
