package com.fantask.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fantask.data.db.FantaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DUE_REMINDER = "com.fantask.ACTION_DUE_REMINDER"
        const val ACTION_SCHEDULE_REMINDER = "com.fantask.ACTION_SCHEDULE_REMINDER"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_NAME = "extra_task_name"
        const val EXTRA_TASK_DIFFICULTY = "extra_task_difficulty"
        const val EXTRA_TASK_POINTS = "extra_task_points"
        const val EXTRA_START_TIME = "extra_start_time"
        const val EXTRA_END_TIME = "extra_end_time"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val taskName = intent.getStringExtra(EXTRA_TASK_NAME) ?: "Your Task"
        val points = intent.getIntExtra(EXTRA_TASK_POINTS, 5)

        when (intent.action) {
            ACTION_DUE_REMINDER -> {
                val difficulty = intent.getStringExtra(EXTRA_TASK_DIFFICULTY) ?: "Normal"
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = taskId.toInt(),
                    channelId = NotificationHelper.CHANNEL_DUE_DATES,
                    title = "Task Due: $taskName",
                    content = "$difficulty Task worth +$points pts. Time to check it off!"
                )
            }
            ACTION_SCHEDULE_REMINDER -> {
                val startTime = intent.getStringExtra(EXTRA_START_TIME) ?: ""
                val endTime = intent.getStringExtra(EXTRA_END_TIME) ?: ""
                val timeWindow = if (endTime.isNotEmpty()) "$startTime - $endTime" else startTime

                NotificationHelper.showNotification(
                    context = context,
                    notificationId = (taskId + 5000).toInt(),
                    channelId = NotificationHelper.CHANNEL_SCHEDULES,
                    title = "Scheduled Task: $taskName",
                    content = "It's time ($timeWindow)! Worth +$points pts. Complete it today to keep your streak alive."
                )

                // Reschedule next occurrence for recurring schedule
                if (taskId != -1L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val db = FantaskDatabase.getInstance(context)
                            val task = db.taskDao().getTaskById(taskId)
                            if (task != null && task.scheduleEnabled) {
                                val scheduler = AlarmScheduler(context)
                                scheduler.scheduleTask(task)
                            }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }
        }
    }
}
