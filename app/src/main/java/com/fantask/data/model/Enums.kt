package com.fantask.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskDifficulty(val points: Int, val displayName: String) {
    EASY(5, "Easy"),
    MEDIUM(15, "Medium"),
    HARD(30, "Hard"),
    SUPER_HARD(50, "Super Hard")
}

@Serializable
enum class TaskPriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

@Serializable
enum class TaskStatus(val displayName: String) {
    PENDING("Pending"),
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done")
}

@Serializable
enum class RecurrenceType(val displayName: String) {
    NONE("One-time"),
    DAILY("Every day"),
    WEEKDAYS("Mon - Fri"),
    WEEKENDS("Sat - Sun"),
    CUSTOM("Custom days")
}

@Serializable
enum class BadgeConditionType(val displayName: String) {
    TASKS_COMPLETED_TOTAL("Total Tasks Completed"),
    STREAK_DAYS("Daily Streak Days"),
    POINTS_EARNED_TOTAL("Total Points Earned"),
    REWARDS_CLAIMED("Rewards Claimed"),
    HARD_TASKS_COMPLETED("Hard & Super Hard Tasks")
}
