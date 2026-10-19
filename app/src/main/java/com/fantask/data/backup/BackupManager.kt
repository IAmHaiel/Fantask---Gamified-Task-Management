package com.fantask.data.backup

import androidx.room.withTransaction
import com.fantask.data.db.FantaskDatabase
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.BadgeUnlockEntity
import com.fantask.data.model.CompletionLogEntity
import com.fantask.data.model.RewardClaimEntity
import com.fantask.data.model.RewardEntity
import com.fantask.data.model.TaskEntity
import com.fantask.data.pref.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class FantaskBackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val tasks: List<TaskEntity> = emptyList(),
    val completionLogs: List<CompletionLogEntity> = emptyList(),
    val rewards: List<RewardEntity> = emptyList(),
    val rewardClaims: List<RewardClaimEntity> = emptyList(),
    val badges: List<BadgeEntity> = emptyList(),
    val badgeUnlocks: List<BadgeUnlockEntity> = emptyList(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletionDate: String = ""
)

object BackupManager {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun createBackupJson(
        database: FantaskDatabase,
        preferencesRepository: UserPreferencesRepository
    ): String {
        val tasks = database.taskDao().getAllTasks().first()
        val logs = database.completionLogDao().getAllLogs().first()
        val rewards = database.rewardDao().getAllRewards().first()
        val claims = database.rewardClaimDao().getAllClaims().first()
        val badges = database.badgeDao().getAllBadges().first()
        val unlocks = database.badgeDao().getAllUnlocks().first()
        val prefs = preferencesRepository.preferencesFlow.first()

        val backupData = FantaskBackupData(
            version = 1,
            exportedAt = System.currentTimeMillis(),
            tasks = tasks,
            completionLogs = logs,
            rewards = rewards,
            rewardClaims = claims,
            badges = badges,
            badgeUnlocks = unlocks,
            currentStreak = prefs.currentStreak,
            longestStreak = prefs.longestStreak,
            lastCompletionDate = prefs.lastCompletionDate
        )

        return json.encodeToString(backupData)
    }

    suspend fun restoreFromJson(
        jsonString: String,
        database: FantaskDatabase,
        preferencesRepository: UserPreferencesRepository
    ): Result<Int> {
        return try {
            val backupData = json.decodeFromString<FantaskBackupData>(jsonString)

            database.withTransaction {
                // Restore tasks
                for (task in backupData.tasks) {
                    database.taskDao().insertTask(task)
                }
                // Restore completion logs
                for (log in backupData.completionLogs) {
                    database.completionLogDao().insertLog(log)
                }
                // Restore rewards
                for (reward in backupData.rewards) {
                    database.rewardDao().insertReward(reward)
                }
                // Restore claims
                for (claim in backupData.rewardClaims) {
                    database.rewardClaimDao().insertClaim(claim)
                }
                // Restore badges
                for (badge in backupData.badges) {
                    database.badgeDao().insertBadge(badge)
                }
                // Restore unlocks
                for (unlock in backupData.badgeUnlocks) {
                    database.badgeDao().insertUnlock(unlock)
                }
            }

            // Restore streak preferences
            preferencesRepository.updateStreak(
                backupData.currentStreak,
                backupData.longestStreak,
                backupData.lastCompletionDate
            )

            val totalRestored = backupData.tasks.size + backupData.rewards.size + backupData.badges.size
            Result.success(totalRestored)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
