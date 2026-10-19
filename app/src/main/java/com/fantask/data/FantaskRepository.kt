package com.fantask.data

import com.fantask.data.db.FantaskDatabase
import com.fantask.data.model.BadgeConditionType
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.BadgeUnlockEntity
import com.fantask.data.model.CompletionLogEntity
import com.fantask.data.model.RewardClaimEntity
import com.fantask.data.model.RewardEntity
import com.fantask.data.model.TaskDifficulty
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskStatus
import com.fantask.data.pref.UserPreferences
import com.fantask.data.pref.UserPreferencesRepository
import com.fantask.domain.GamificationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

data class GamificationSummary(
    val currentPoints: Int = 0,
    val totalPointsEarned: Int = 0,
    val totalPointsSpent: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val tasksCompletedCount: Int = 0
)

data class BadgeWithStatus(
    val badge: BadgeEntity,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null,
    val currentProgress: Int = 0
)

class FantaskRepository(
    private val database: FantaskDatabase,
    private val preferencesRepository: UserPreferencesRepository
) {
    private val taskDao = database.taskDao()
    private val completionLogDao = database.completionLogDao()
    private val rewardDao = database.rewardDao()
    private val rewardClaimDao = database.rewardClaimDao()
    private val badgeDao = database.badgeDao()

    val engine = GamificationEngine(badgeDao, completionLogDao, rewardClaimDao, preferencesRepository)

    val userPreferencesFlow: Flow<UserPreferences> = preferencesRepository.preferencesFlow

    val allTasksFlow: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByStatusFlow(status: TaskStatus): Flow<List<TaskEntity>> = taskDao.getTasksByStatus(status)

    val allRewardsFlow: Flow<List<RewardEntity>> = rewardDao.getAllRewards()

    val allClaimsFlow: Flow<List<RewardClaimEntity>> = rewardClaimDao.getAllClaims()

    val allBadgesFlow: Flow<List<BadgeEntity>> = badgeDao.getAllBadges()

    val allUnlocksFlow: Flow<List<BadgeUnlockEntity>> = badgeDao.getAllUnlocks()

    val gamificationSummaryFlow: Flow<GamificationSummary> = combine(
        completionLogDao.getTotalPointsEarned(),
        rewardClaimDao.getTotalPointsSpent(),
        completionLogDao.getTotalTasksCompletedCount(),
        preferencesRepository.preferencesFlow
    ) { earned, spent, count, prefs ->
        GamificationSummary(
            currentPoints = maxOf(0, earned - spent),
            totalPointsEarned = earned,
            totalPointsSpent = spent,
            currentStreak = prefs.currentStreak,
            longestStreak = prefs.longestStreak,
            tasksCompletedCount = count
        )
    }

    val badgesWithStatusFlow: Flow<List<BadgeWithStatus>> = combine(
        badgeDao.getAllBadges(),
        badgeDao.getAllUnlocks(),
        gamificationSummaryFlow
    ) { badges, unlocks, summary ->
        val unlockMap = unlocks.associateBy { it.badgeId }
        badges.map { badge ->
            val unlock = unlockMap[badge.id]
            val progress = when (badge.conditionType) {
                BadgeConditionType.TASKS_COMPLETED_TOTAL -> summary.tasksCompletedCount
                BadgeConditionType.STREAK_DAYS -> summary.currentStreak
                BadgeConditionType.POINTS_EARNED_TOTAL -> summary.totalPointsEarned
                BadgeConditionType.REWARDS_CLAIMED -> summary.totalPointsSpent // or claim count
                BadgeConditionType.HARD_TASKS_COMPLETED -> 0 // evaluated dynamically
            }
            BadgeWithStatus(
                badge = badge,
                isUnlocked = unlock != null,
                unlockedAt = unlock?.unlockedAt,
                currentProgress = minOf(progress, badge.threshold)
            )
        }
    }

    suspend fun saveTask(task: TaskEntity): Long {
        return if (task.id == 0L) {
            taskDao.insertTask(task)
        } else {
            taskDao.updateTask(task)
            task.id
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
        completionLogDao.deleteLogsForTask(task.id)
    }

    suspend fun updateTaskStatus(task: TaskEntity, newStatus: TaskStatus): List<BadgeEntity> {
        if (task.status == newStatus) return emptyList()

        if (newStatus == TaskStatus.DONE) {
            return completeTask(task)
        } else if (task.status == TaskStatus.DONE) {
            // Reopening task from DONE to another status: revert points!
            reopenTask(task, newStatus)
            return emptyList()
        } else {
            taskDao.updateTask(task.copy(status = newStatus))
            return emptyList()
        }
    }

    suspend fun completeTask(task: TaskEntity): List<BadgeEntity> {
        val now = System.currentTimeMillis()
        val points = task.difficulty.points
        val today = engine.getTodayDateString()

        // 1. Update task to DONE
        val updatedTask = task.copy(
            status = TaskStatus.DONE,
            completedAt = now,
            pointsAwarded = points
        )
        taskDao.updateTask(updatedTask)

        // 2. Insert completion log
        completionLogDao.insertLog(
            CompletionLogEntity(
                taskId = task.id,
                completedDate = today,
                timestamp = now,
                pointsEarned = points
            )
        )

        // 3. Update streak
        val currentPrefs = preferencesRepository.preferencesFlow.first()
        val (newCurrentStreak, _) = engine.evaluateStreak(currentPrefs)

        // 4. Evaluate badges
        val totalEarned = completionLogDao.getTotalPointsEarned().first()
        val completedCount = completionLogDao.getTotalTasksCompletedCount().first()
        val claimsCount = rewardClaimDao.getClaimCount().first()

        val newlyUnlockedBadges = mutableListOf<BadgeEntity>()
        val allBadges = badgeDao.getAllBadges().first()
        for (badge in allBadges) {
            if (!badgeDao.isBadgeUnlocked(badge.id)) {
                val meetsCriteria = when (badge.conditionType) {
                    BadgeConditionType.TASKS_COMPLETED_TOTAL -> completedCount >= badge.threshold
                    BadgeConditionType.STREAK_DAYS -> newCurrentStreak >= badge.threshold
                    BadgeConditionType.POINTS_EARNED_TOTAL -> totalEarned >= badge.threshold
                    BadgeConditionType.REWARDS_CLAIMED -> claimsCount >= badge.threshold
                    BadgeConditionType.HARD_TASKS_COMPLETED -> {
                        (task.difficulty == TaskDifficulty.HARD || task.difficulty == TaskDifficulty.SUPER_HARD)
                    }
                }
                if (meetsCriteria) {
                    badgeDao.insertUnlock(BadgeUnlockEntity(badgeId = badge.id, unlockedAt = now))
                    newlyUnlockedBadges.add(badge)
                }
            }
        }

        return newlyUnlockedBadges
    }

    private suspend fun reopenTask(task: TaskEntity, newStatus: TaskStatus) {
        val now = System.currentTimeMillis()
        val pointsToRevert = task.pointsAwarded
        if (pointsToRevert > 0) {
            // Log negative points reversal to prevent complete/reopen farming
            completionLogDao.insertLog(
                CompletionLogEntity(
                    taskId = task.id,
                    completedDate = engine.getTodayDateString(),
                    timestamp = now,
                    pointsEarned = -pointsToRevert
                )
            )
        }
        taskDao.updateTask(
            task.copy(
                status = newStatus,
                completedAt = null,
                pointsAwarded = 0
            )
        )
    }

    suspend fun saveReward(reward: RewardEntity): Long {
        return if (reward.id == 0L) {
            rewardDao.insertReward(reward)
        } else {
            rewardDao.updateReward(reward)
            reward.id
        }
    }

    suspend fun deleteReward(reward: RewardEntity) {
        rewardDao.deleteReward(reward)
    }

    suspend fun claimReward(reward: RewardEntity): Result<List<BadgeEntity>> {
        val summary = gamificationSummaryFlow.first()
        if (summary.currentPoints < reward.pointsRequired) {
            return Result.failure(IllegalStateException("Not enough points! Need ${reward.pointsRequired}, but have ${summary.currentPoints}."))
        }

        if (!reward.repeatable) {
            val count = rewardClaimDao.getClaimCountForReward(reward.id)
            if (count > 0) {
                return Result.failure(IllegalStateException("This reward is not repeatable and has already been claimed."))
            }
        }

        val now = System.currentTimeMillis()
        rewardClaimDao.insertClaim(
            RewardClaimEntity(
                rewardId = reward.id,
                rewardName = reward.name,
                pointsSpent = reward.pointsRequired,
                claimedAt = now
            )
        )

        // Evaluate badge for reward claim
        val updatedClaimsCount = rewardClaimDao.getClaimCount().first()
        val newlyUnlocked = mutableListOf<BadgeEntity>()
        val allBadges = badgeDao.getAllBadges().first()
        for (badge in allBadges) {
            if (badge.conditionType == BadgeConditionType.REWARDS_CLAIMED && !badgeDao.isBadgeUnlocked(badge.id)) {
                if (updatedClaimsCount >= badge.threshold) {
                    badgeDao.insertUnlock(BadgeUnlockEntity(badgeId = badge.id, unlockedAt = now))
                    newlyUnlocked.add(badge)
                }
            }
        }

        return Result.success(newlyUnlocked)
    }

    suspend fun saveBadge(badge: BadgeEntity): Long {
        return if (badge.id == 0L) {
            badgeDao.insertBadge(badge)
        } else {
            badgeDao.updateBadge(badge)
            badge.id
        }
    }

    suspend fun deleteBadge(badge: BadgeEntity) {
        badgeDao.deleteBadge(badge)
    }

    suspend fun checkDailyStreak() {
        val prefs = preferencesRepository.preferencesFlow.first()
        engine.checkDailyStreakMaintenance(prefs)
    }
}
