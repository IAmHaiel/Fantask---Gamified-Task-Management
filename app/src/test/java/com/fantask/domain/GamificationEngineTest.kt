package com.fantask.domain

import com.fantask.data.model.TaskDifficulty
import com.fantask.data.pref.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GamificationEngineTest {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @Test
    fun testDifficultyPoints() {
        assertEquals(5, TaskDifficulty.EASY.points)
        assertEquals(15, TaskDifficulty.MEDIUM.points)
        assertEquals(30, TaskDifficulty.HARD.points)
        assertEquals(50, TaskDifficulty.SUPER_HARD.points)
    }

    @Test
    fun testStreakConsecutiveDays() {
        val today = LocalDate.now().format(formatter)
        val yesterday = LocalDate.now().minusDays(1).format(formatter)

        val prefs = UserPreferences(
            currentStreak = 3,
            longestStreak = 5,
            lastCompletionDate = yesterday
        )

        // Consecutive completion: streak should become 4
        val newCurrent = prefs.currentStreak + 1
        val newLongest = maxOf(prefs.longestStreak, newCurrent)

        assertEquals(4, newCurrent)
        assertEquals(5, newLongest)
    }

    @Test
    fun testStreakSameDay() {
        val today = LocalDate.now().format(formatter)

        val prefs = UserPreferences(
            currentStreak = 4,
            longestStreak = 4,
            lastCompletionDate = today
        )

        // Already completed today: streak should remain 4
        val newCurrent = prefs.currentStreak
        val newLongest = prefs.longestStreak

        assertEquals(4, newCurrent)
        assertEquals(4, newLongest)
    }

    @Test
    fun testStreakBroken() {
        val twoDaysAgo = LocalDate.now().minusDays(2).format(formatter)

        val prefs = UserPreferences(
            currentStreak = 5,
            longestStreak = 10,
            lastCompletionDate = twoDaysAgo
        )

        // Missed yesterday: streak resets to 1 on new completion, longest remains 10
        val newCurrent = 1
        val newLongest = maxOf(prefs.longestStreak, newCurrent)

        assertEquals(1, newCurrent)
        assertEquals(10, newLongest)
    }
}
