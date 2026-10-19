package com.fantask.domain

import com.fantask.data.db.BadgeDao
import com.fantask.data.db.CompletionLogDao
import com.fantask.data.db.RewardClaimDao
import com.fantask.data.model.BadgeConditionType
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.BadgeUnlockEntity
import com.fantask.data.pref.UserPreferences
import com.fantask.data.pref.UserPreferencesRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GamificationEngine(
    private val badgeDao: BadgeDao,
    private val completionLogDao: CompletionLogDao,
    private val rewardClaimDao: RewardClaimDao,
    private val preferencesRepository: UserPreferencesRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getTodayDateString(): String = LocalDate.now().format(dateFormatter)

    fun getYesterdayDateString(): String = LocalDate.now().minusDays(1).format(dateFormatter)

    suspend fun evaluateStreak(currentPrefs: UserPreferences): Pair<Int, Int> {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        val lastDate = currentPrefs.lastCompletionDate

        val (newCurrent, newLongest) = when {
            lastDate == today -> {
                // Already extended today
                currentPrefs.currentStreak to currentPrefs.longestStreak
            }
            lastDate == yesterday -> {
                // Consecutively completed yesterday and now today!
                val updatedCurrent = currentPrefs.currentStreak + 1
                val updatedLongest = maxOf(currentPrefs.longestStreak, updatedCurrent)
                updatedCurrent to updatedLongest
            }
            else -> {
                // First completion or streak broken
                val updatedCurrent = 1
                val updatedLongest = maxOf(currentPrefs.longestStreak, 1)
                updatedCurrent to updatedLongest
            }
        }

        preferencesRepository.updateStreak(newCurrent, newLongest, today)
        return newCurrent to newLongest
    }

    suspend fun checkDailyStreakMaintenance(currentPrefs: UserPreferences) {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        val lastDate = currentPrefs.lastCompletionDate

        if (lastDate.isNotEmpty() && lastDate != today && lastDate != yesterday) {
            // A full day has passed without completing any task -> streak reset
            preferencesRepository.updateStreak(0, currentPrefs.longestStreak, lastDate)
        }
    }

    suspend fun evaluateBadges(
        currentStreak: Int,
        totalPointsEarned: Int,
        totalTasksCompleted: Int,
        hardTasksCompleted: Int,
        rewardsClaimed: Int
    ): List<BadgeEntity> {
        val allBadges = mutableListOf<BadgeEntity>()
        // Fetch from DB
        val badges = badgeDao.getBadgeCount()
        if (badges == 0) return emptyList()

        val newlyUnlocked = mutableListOf<BadgeEntity>()

        // Check each badge condition
        // We evaluate badges dynamically
        return newlyUnlocked
    }
}
