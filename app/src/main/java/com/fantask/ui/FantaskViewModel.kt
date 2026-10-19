package com.fantask.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fantask.data.BadgeWithStatus
import com.fantask.data.FantaskRepository
import com.fantask.data.GamificationSummary
import com.fantask.data.backup.BackupManager
import com.fantask.data.db.FantaskDatabase
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.RewardClaimEntity
import com.fantask.data.model.RewardEntity
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskStatus
import com.fantask.data.pref.UserPreferences
import com.fantask.data.pref.UserPreferencesRepository
import com.fantask.notify.AlarmScheduler
import com.fantask.notify.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

data class FantaskUiState(
    val preferences: UserPreferences = UserPreferences(),
    val tasks: List<TaskEntity> = emptyList(),
    val rewards: List<RewardEntity> = emptyList(),
    val claims: List<RewardClaimEntity> = emptyList(),
    val badges: List<BadgeWithStatus> = emptyList(),
    val gamificationSummary: GamificationSummary = GamificationSummary(),
    val taskToConfirm: TaskEntity? = null,
    val taskToEdit: TaskEntity? = null,
    val isCreateTaskOpen: Boolean = false,
    val isCreateRewardOpen: Boolean = false,
    val rewardToEdit: RewardEntity? = null,
    val isCreateBadgeOpen: Boolean = false,
    val celebrationEvent: Pair<Int, List<BadgeEntity>>? = null
)

private data class BaseData(
    val tasks: List<TaskEntity>,
    val rewards: List<RewardEntity>,
    val claims: List<RewardClaimEntity>,
    val badges: List<BadgeWithStatus>,
    val summary: GamificationSummary
)

class FantaskViewModel(
    private val repository: FantaskRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val database: FantaskDatabase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiStateInternal = MutableStateFlow(FantaskUiState())

    private val baseDataFlow = combine(
        repository.allTasksFlow,
        repository.allRewardsFlow,
        repository.allClaimsFlow,
        repository.badgesWithStatusFlow,
        repository.gamificationSummaryFlow
    ) { tasks, rewards, claims, badges, summary ->
        BaseData(tasks, rewards, claims, badges, summary)
    }

    val uiState: StateFlow<FantaskUiState> = combine(
        baseDataFlow,
        repository.userPreferencesFlow,
        _uiStateInternal
    ) { data, prefs, internalState ->
        internalState.copy(
            preferences = prefs,
            tasks = data.tasks,
            rewards = data.rewards,
            claims = data.claims,
            badges = data.badges,
            gamificationSummary = data.summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FantaskUiState()
    )

    fun openCreateTaskDialog() {
        _uiStateInternal.value = _uiStateInternal.value.copy(
            isCreateTaskOpen = true,
            taskToEdit = null
        )
    }

    fun openEditTaskDialog(task: TaskEntity) {
        _uiStateInternal.value = _uiStateInternal.value.copy(
            isCreateTaskOpen = true,
            taskToEdit = task
        )
    }

    fun closeTaskDialog() {
        _uiStateInternal.value = _uiStateInternal.value.copy(
            isCreateTaskOpen = false,
            taskToEdit = null
        )
    }

    fun saveTask(task: TaskEntity) {
        viewModelScope.launch {
            val id = repository.saveTask(task)
            val updated = task.copy(id = id)
            alarmScheduler.scheduleTask(updated, uiState.value.preferences.leadTimeMinutes)
            closeTaskDialog()
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            alarmScheduler.cancelTaskAlarms(task.id)
            repository.deleteTask(task)
        }
    }

    fun moveTask(task: TaskEntity, newStatus: TaskStatus) {
        if (newStatus == TaskStatus.DONE) {
            requestCompleteTask(task)
        } else {
            viewModelScope.launch {
                repository.updateTaskStatus(task, newStatus)
            }
        }
    }

    fun requestCompleteTask(task: TaskEntity) {
        _uiStateInternal.value = _uiStateInternal.value.copy(taskToConfirm = task)
    }

    fun dismissTaskConfirmation() {
        _uiStateInternal.value = _uiStateInternal.value.copy(taskToConfirm = null)
    }

    fun confirmCompleteTask() {
        val task = _uiStateInternal.value.taskToConfirm ?: return
        viewModelScope.launch {
            alarmScheduler.cancelTaskAlarms(task.id)
            val newBadges = repository.completeTask(task)
            _uiStateInternal.value = _uiStateInternal.value.copy(
                taskToConfirm = null,
                celebrationEvent = task.difficulty.points to newBadges
            )
        }
    }

    fun dismissCelebration() {
        _uiStateInternal.value = _uiStateInternal.value.copy(celebrationEvent = null)
    }

    // Rewards
    fun openCreateRewardDialog() {
        _uiStateInternal.value = _uiStateInternal.value.copy(
            isCreateRewardOpen = true,
            rewardToEdit = null
        )
    }

    fun openEditRewardDialog(reward: RewardEntity) {
        _uiStateInternal.value = _uiStateInternal.value.copy(
            isCreateRewardOpen = true,
            rewardToEdit = reward
        )
    }

    fun closeRewardDialog() {
        _uiStateInternal.value = _uiStateInternal.value.copy(
            isCreateRewardOpen = false,
            rewardToEdit = null
        )
    }

    fun saveReward(reward: RewardEntity) {
        viewModelScope.launch {
            repository.saveReward(reward)
            closeRewardDialog()
        }
    }

    fun deleteReward(reward: RewardEntity) {
        viewModelScope.launch {
            repository.deleteReward(reward)
        }
    }

    fun claimReward(reward: RewardEntity) {
        viewModelScope.launch {
            repository.claimReward(reward)
        }
    }

    // Badges
    fun openCreateBadgeDialog() {
        _uiStateInternal.value = _uiStateInternal.value.copy(isCreateBadgeOpen = true)
    }

    fun closeBadgeDialog() {
        _uiStateInternal.value = _uiStateInternal.value.copy(isCreateBadgeOpen = false)
    }

    fun saveBadge(badge: BadgeEntity) {
        viewModelScope.launch {
            repository.saveBadge(badge)
            closeBadgeDialog()
        }
    }

    fun deleteBadge(badge: BadgeEntity) {
        viewModelScope.launch {
            repository.deleteBadge(badge)
        }
    }

    // Settings
    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(true)
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(false)
        }
    }

    fun setLeadTimeMinutes(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.setLeadTimeMinutes(minutes)
        }
    }

    fun sendTestNotification(context: android.content.Context) {
        NotificationHelper.showNotification(
            context = context,
            notificationId = 9999,
            channelId = NotificationHelper.CHANNEL_DUE_DATES,
            title = "Fantask Offline Reminder",
            content = "Offline notifications and exact alarms are active and ready!"
        )
    }

    fun exportBackup(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            val jsonString = BackupManager.createBackupJson(database, preferencesRepository)
            contentResolver.openOutputStream(uri)?.use { os ->
                os.write(jsonString.toByteArray(Charsets.UTF_8))
            }
        }
    }

    fun importBackup(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            val sb = java.lang.StringBuilder()
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        sb.append(line).append('\n')
                        line = reader.readLine()
                    }
                }
            }
            BackupManager.restoreFromJson(sb.toString(), database, preferencesRepository)
            alarmScheduler.rescheduleAllAlarms(database)
        }
    }
}

class FantaskViewModelFactory(
    private val repository: FantaskRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val database: FantaskDatabase,
    private val alarmScheduler: AlarmScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FantaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FantaskViewModel(repository, preferencesRepository, database, alarmScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
