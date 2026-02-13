package com.focusclinic.app.platform

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

actual class TimerNotificationManager : TimerNotification {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    init {
        requestPermission()
    }

    actual fun onSessionStarted(durationMinutes: Int) {
        scheduleCompletionNotification(durationMinutes)
    }

    actual fun onSessionCompleted(earnedXp: Long, earnedCoins: Long) {
        cancelScheduledNotification()
        showImmediateNotification(
            title = "Session Complete!",
            body = "Earned +$earnedXp XP and +$earnedCoins Coins",
        )
    }

    actual fun onSessionStopped() {
        cancelScheduledNotification()
    }

    private fun requestPermission() {
        notificationCenter.requestAuthorizationWithOptions(
            options = 0x04u or 0x02u or 0x01u, // alert | sound | badge
            completionHandler = { _, _ -> },
        )
    }

    private fun scheduleCompletionNotification(durationMinutes: Int) {
        val content = UNMutableNotificationContent().apply {
            setTitle("Session Complete!")
            setBody("Your $durationMinutes min focus session is done. Check your rewards!")
            setSound(UNNotificationSound.defaultSound())
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = durationMinutes * 60.0,
            repeats = false,
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = SCHEDULED_NOTIFICATION_ID,
            content = content,
            trigger = trigger,
        )

        notificationCenter.addNotificationRequest(request, withCompletionHandler = null)
    }

    private fun showImmediateNotification(title: String, body: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound())
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 1.0,
            repeats = false,
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = IMMEDIATE_NOTIFICATION_ID,
            content = content,
            trigger = trigger,
        )

        notificationCenter.addNotificationRequest(request, withCompletionHandler = null)
    }

    private fun cancelScheduledNotification() {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf(SCHEDULED_NOTIFICATION_ID),
        )
    }

    companion object {
        private const val SCHEDULED_NOTIFICATION_ID = "focus_timer_scheduled"
        private const val IMMEDIATE_NOTIFICATION_ID = "focus_timer_immediate"
    }
}
