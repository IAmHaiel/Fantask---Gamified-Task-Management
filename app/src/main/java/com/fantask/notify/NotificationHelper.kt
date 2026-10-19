package com.fantask.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fantask.MainActivity
import com.fantask.R

object NotificationHelper {
    const val CHANNEL_DUE_DATES = "fantask_due_dates"
    const val CHANNEL_SCHEDULES = "fantask_schedules"
    const val CHANNEL_ACHIEVEMENTS = "fantask_achievements"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val dueChannel = NotificationChannel(
                CHANNEL_DUE_DATES,
                "Task Due Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Offline notifications for tasks approaching their due date"
                enableVibration(true)
            }

            val scheduleChannel = NotificationChannel(
                CHANNEL_SCHEDULES,
                "Daily Task Schedules",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Offline notifications for scheduled task time windows (e.g. 8:00 AM - 10:00 AM)"
                enableVibration(true)
            }

            val achievementChannel = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Achievements & Badges",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Offline notifications when unlocking new badges and milestones"
            }

            notificationManager.createNotificationChannels(listOf(dueChannel, scheduleChannel, achievementChannel))
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        content: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is denied
        }
    }
}
