package com.fantask.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.fantask.data.db.FantaskDatabase
import com.fantask.data.model.RecurrenceType
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleTask(task: TaskEntity, leadTimeMinutes: Int = 0) {
        if (task.status == TaskStatus.DONE) return

        // 1. Due date reminder
        if (task.dueDate != null) {
            val triggerTime = task.dueDate - (leadTimeMinutes * 60_000L)
            if (triggerTime > System.currentTimeMillis()) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    action = AlarmReceiver.ACTION_DUE_REMINDER
                    putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
                    putExtra(AlarmReceiver.EXTRA_TASK_NAME, task.name)
                    putExtra(AlarmReceiver.EXTRA_TASK_DIFFICULTY, task.difficulty.displayName)
                    putExtra(AlarmReceiver.EXTRA_TASK_POINTS, task.difficulty.points)
                }
                val requestCode = (task.id * 10).toInt() + 1
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setExactAlarm(triggerTime, pendingIntent)
            }
        }

        // 2. Scheduled Time Window (e.g. 8:00 AM - 10:00 AM)
        if (task.scheduleEnabled && !task.scheduledStartTime.isNullOrEmpty()) {
            val nextWindowStartTime = calculateNextOccurrence(
                task.scheduledStartTime,
                task.recurrenceType,
                task.recurrenceDays
            )
            if (nextWindowStartTime != null) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    action = AlarmReceiver.ACTION_SCHEDULE_REMINDER
                    putExtra(AlarmReceiver.EXTRA_TASK_ID, task.id)
                    putExtra(AlarmReceiver.EXTRA_TASK_NAME, task.name)
                    putExtra(AlarmReceiver.EXTRA_START_TIME, task.scheduledStartTime)
                    putExtra(AlarmReceiver.EXTRA_END_TIME, task.scheduledEndTime ?: "")
                    putExtra(AlarmReceiver.EXTRA_TASK_POINTS, task.difficulty.points)
                }
                val requestCode = (task.id * 10).toInt() + 2
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setExactAlarm(nextWindowStartTime, pendingIntent)
            }
        }
    }

    fun cancelTaskAlarms(taskId: Long) {
        // Cancel due alarm
        val dueIntent = Intent(context, AlarmReceiver::class.java)
        val duePending = PendingIntent.getBroadcast(
            context,
            (taskId * 10).toInt() + 1,
            dueIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        duePending?.let { alarmManager.cancel(it) }

        // Cancel schedule alarm
        val schedIntent = Intent(context, AlarmReceiver::class.java)
        val schedPending = PendingIntent.getBroadcast(
            context,
            (taskId * 10).toInt() + 2,
            schedIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        schedPending?.let { alarmManager.cancel(it) }
    }

    private fun setExactAlarm(triggerTimeMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }

    companion object {
        fun parseTimeString(timeString: String): Pair<Int, Int>? {
            val trimmed = timeString.trim()
            val isPm = trimmed.endsWith("PM", ignoreCase = true)
            val isAm = trimmed.endsWith("AM", ignoreCase = true)
            val cleanTime = trimmed.replace("AM", "", ignoreCase = true)
                .replace("PM", "", ignoreCase = true)
                .trim()

            val parts = cleanTime.split(":")
            if (parts.size != 2) return null
            var hour = parts[0].trim().toIntOrNull() ?: return null
            val minute = parts[1].trim().toIntOrNull() ?: return null

            if (isPm && hour < 12) hour += 12
            if (isAm && hour == 12) hour = 0

            if (hour !in 0..23 || minute !in 0..59) return null
            return hour to minute
        }
    }

    fun calculateNextOccurrence(
        timeString: String,
        recurrenceType: RecurrenceType,
        customDaysBitmask: Int
    ): Long? {
        val (hour, minute) = parseTimeString(timeString) ?: return null

        val now = LocalDateTime.now()
        var candidate = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute, 0))

        if (candidate.isBefore(now) || candidate.isEqual(now)) {
            candidate = candidate.plusDays(1)
        }

        // Check recurrence rules
        for (i in 0..14) { // Look ahead up to 2 weeks
            if (isDayMatching(candidate.dayOfWeek, recurrenceType, customDaysBitmask)) {
                return candidate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
            candidate = candidate.plusDays(1)
        }

        return null
    }

    private fun isDayMatching(
        dayOfWeek: DayOfWeek,
        recurrenceType: RecurrenceType,
        customDaysBitmask: Int
    ): Boolean {
        return when (recurrenceType) {
            RecurrenceType.NONE, RecurrenceType.DAILY -> true
            RecurrenceType.WEEKDAYS -> dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
            RecurrenceType.WEEKENDS -> dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
            RecurrenceType.CUSTOM -> {
                // Bitmask: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
                val mask = 1 shl (dayOfWeek.value - 1)
                (customDaysBitmask and mask) != 0
            }
        }
    }

    suspend fun rescheduleAllAlarms(database: FantaskDatabase) {
        val scheduledTasks = database.taskDao().getScheduledTasks()
        for (task in scheduledTasks) {
            scheduleTask(task)
        }
        val upcomingDueTasks = database.taskDao().getUpcomingDueTasks()
        for (task in upcomingDueTasks) {
            scheduleTask(task)
        }
    }
}
