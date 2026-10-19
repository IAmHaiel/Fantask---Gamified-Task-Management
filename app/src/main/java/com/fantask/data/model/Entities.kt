package com.fantask.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class TaskRequirement(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isDone: Boolean = false
)

@Serializable
@Entity(
    tableName = "tasks",
    indices = [
        Index("status"),
        Index("dueDate"),
        Index("scheduleEnabled")
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val requirementsJson: String = "[]",
    val dueDate: Long? = null,
    val difficulty: TaskDifficulty = TaskDifficulty.EASY,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val completedAt: Long? = null,
    val pointsAwarded: Int = 0,
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val recurrenceDays: Int = 0, // Bitmask: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
    val scheduledStartTime: String? = null, // e.g. "08:00 AM"
    val scheduledEndTime: String? = null,   // e.g. "10:00 AM"
    val scheduleEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun TaskEntity.getRequirements(): List<TaskRequirement> {
    return try {
        if (requirementsJson.isBlank()) emptyList()
        else kotlinx.serialization.json.Json.decodeFromString(requirementsJson)
    } catch (_: Exception) {
        emptyList()
    }
}

fun TaskEntity.withRequirements(requirements: List<TaskRequirement>): TaskEntity {
    val jsonString = kotlinx.serialization.json.Json.encodeToString(requirements)
    return this.copy(requirementsJson = jsonString)
}

fun TaskEntity.areAllRequirementsDone(): Boolean {
    val list = getRequirements()
    return list.isEmpty() || list.all { it.isDone }
}

@Serializable
@Entity(
    tableName = "completion_logs",
    indices = [
        Index("taskId"),
        Index("completedDate")
    ]
)
data class CompletionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val completedDate: String, // "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis(),
    val pointsEarned: Int
)

@Serializable
@Entity(
    tableName = "rewards",
    indices = [
        Index("pointsRequired")
    ]
)
data class RewardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val pointsRequired: Int,
    val repeatable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "reward_claims",
    indices = [
        Index("rewardId")
    ]
)
data class RewardClaimEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rewardId: Long,
    val rewardName: String,
    val pointsSpent: Int,
    val claimedAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "badges",
    indices = [
        Index("conditionType")
    ]
)
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val iconName: String = "Star",
    val conditionType: BadgeConditionType,
    val threshold: Int,
    val isPreset: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "badge_unlocks",
    primaryKeys = ["badgeId"]
)
data class BadgeUnlockEntity(
    val badgeId: Long,
    val unlockedAt: Long = System.currentTimeMillis()
)
