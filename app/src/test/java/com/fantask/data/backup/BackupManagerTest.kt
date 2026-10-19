package com.fantask.data.backup

import com.fantask.data.model.BadgeConditionType
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.RewardEntity
import com.fantask.data.model.TaskDifficulty
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskPriority
import com.fantask.data.model.TaskStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupManagerTest {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Test
    fun testBackupSerializationAndDeserialization() {
        val sampleTask = TaskEntity(
            id = 1,
            name = "Test Offline Alarm Task",
            description = "Must run at 8AM-10AM",
            difficulty = TaskDifficulty.HARD,
            priority = TaskPriority.HIGH,
            status = TaskStatus.TODO,
            scheduleEnabled = true,
            scheduledStartTime = "08:00",
            scheduledEndTime = "10:00"
        )

        val sampleReward = RewardEntity(
            id = 1,
            name = "1h Video Game Break",
            pointsRequired = 30,
            repeatable = true
        )

        val sampleBadge = BadgeEntity(
            id = 1,
            name = "Test Badge",
            description = "Desc",
            conditionType = BadgeConditionType.TASKS_COMPLETED_TOTAL,
            threshold = 5,
            isPreset = false
        )

        val backupData = FantaskBackupData(
            version = 1,
            tasks = listOf(sampleTask),
            rewards = listOf(sampleReward),
            badges = listOf(sampleBadge),
            currentStreak = 7,
            longestStreak = 14,
            lastCompletionDate = "2026-10-19"
        )

        val encoded = json.encodeToString(backupData)
        val decoded = json.decodeFromString<FantaskBackupData>(encoded)

        assertEquals(1, decoded.tasks.size)
        assertEquals("Test Offline Alarm Task", decoded.tasks[0].name)
        assertEquals(TaskDifficulty.HARD, decoded.tasks[0].difficulty)
        assertEquals(30, decoded.tasks[0].difficulty.points)
        assertEquals(7, decoded.currentStreak)
        assertEquals(14, decoded.longestStreak)
        assertEquals(1, decoded.rewards.size)
        assertEquals("1h Video Game Break", decoded.rewards[0].name)
    }
}
