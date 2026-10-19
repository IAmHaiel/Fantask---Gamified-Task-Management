package com.fantask.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.BadgeUnlockEntity
import com.fantask.data.model.CompletionLogEntity
import com.fantask.data.model.RewardClaimEntity
import com.fantask.data.model.RewardEntity
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY priority DESC, dueDate ASC, createdAt DESC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity): Int

    @Delete
    suspend fun deleteTask(task: TaskEntity): Int

    @Query("SELECT * FROM tasks WHERE scheduleEnabled = 1")
    suspend fun getScheduledTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND status != 'DONE'")
    suspend fun getUpcomingDueTasks(): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int
}

@Dao
interface CompletionLogDao {
    @Query("SELECT * FROM completion_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CompletionLogEntity>>

    @Query("SELECT * FROM completion_logs WHERE taskId = :taskId")
    suspend fun getLogsForTask(taskId: Long): List<CompletionLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CompletionLogEntity): Long

    @Query("DELETE FROM completion_logs WHERE taskId = :taskId")
    suspend fun deleteLogsForTask(taskId: Long): Int

    @Query("SELECT COALESCE(SUM(pointsEarned), 0) FROM completion_logs")
    fun getTotalPointsEarned(): Flow<Int>

    @Query("SELECT DISTINCT completedDate FROM completion_logs ORDER BY completedDate DESC")
    suspend fun getDistinctCompletionDates(): List<String>

    @Query("SELECT COUNT(*) FROM completion_logs")
    fun getTotalTasksCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT taskId) FROM completion_logs")
    suspend fun getUniqueTasksCompletedCount(): Int
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY pointsRequired ASC, name ASC")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Query("SELECT * FROM rewards WHERE id = :id LIMIT 1")
    suspend fun getRewardById(id: Long): RewardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardEntity): Long

    @Update
    suspend fun updateReward(reward: RewardEntity): Int

    @Delete
    suspend fun deleteReward(reward: RewardEntity): Int

    @Query("SELECT COUNT(*) FROM rewards")
    suspend fun getRewardCount(): Int
}

@Dao
interface RewardClaimDao {
    @Query("SELECT * FROM reward_claims ORDER BY claimedAt DESC")
    fun getAllClaims(): Flow<List<RewardClaimEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(claim: RewardClaimEntity): Long

    @Query("SELECT COALESCE(SUM(pointsSpent), 0) FROM reward_claims")
    fun getTotalPointsSpent(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reward_claims")
    fun getClaimCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reward_claims WHERE rewardId = :rewardId")
    suspend fun getClaimCountForReward(rewardId: Long): Int
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY isPreset DESC, threshold ASC")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE id = :id LIMIT 1")
    suspend fun getBadgeById(id: Long): BadgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Update
    suspend fun updateBadge(badge: BadgeEntity): Int

    @Delete
    suspend fun deleteBadge(badge: BadgeEntity): Int

    @Query("SELECT * FROM badge_unlocks ORDER BY unlockedAt DESC")
    fun getAllUnlocks(): Flow<List<BadgeUnlockEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnlock(unlock: BadgeUnlockEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM badge_unlocks WHERE badgeId = :badgeId)")
    suspend fun isBadgeUnlocked(badgeId: Long): Boolean

    @Query("SELECT COUNT(*) FROM badges")
    suspend fun getBadgeCount(): Int
}
