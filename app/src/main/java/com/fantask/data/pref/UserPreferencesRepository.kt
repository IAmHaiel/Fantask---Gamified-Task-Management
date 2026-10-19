package com.fantask.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fantask_preferences")

data class UserPreferences(
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val onboardingCompleted: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletionDate: String = "",
    val notificationsEnabled: Boolean = true,
    val leadTimeMinutes: Int = 0
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LONGEST_STREAK = intPreferencesKey("longest_streak")
        val LAST_COMPLETION_DATE = stringPreferencesKey("last_completion_date")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LEAD_TIME_MINUTES = intPreferencesKey("lead_time_minutes")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[PreferencesKeys.THEME_MODE] ?: "SYSTEM",
            onboardingCompleted = prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
            currentStreak = prefs[PreferencesKeys.CURRENT_STREAK] ?: 0,
            longestStreak = prefs[PreferencesKeys.LONGEST_STREAK] ?: 0,
            lastCompletionDate = prefs[PreferencesKeys.LAST_COMPLETION_DATE] ?: "",
            notificationsEnabled = prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            leadTimeMinutes = prefs[PreferencesKeys.LEAD_TIME_MINUTES] ?: 0
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun updateStreak(current: Int, longest: Int, date: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.CURRENT_STREAK] = current
            prefs[PreferencesKeys.LONGEST_STREAK] = longest
            prefs[PreferencesKeys.LAST_COMPLETION_DATE] = date
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setLeadTimeMinutes(minutes: Int) {
        context.dataStore.edit { it[PreferencesKeys.LEAD_TIME_MINUTES] = minutes }
    }
}
