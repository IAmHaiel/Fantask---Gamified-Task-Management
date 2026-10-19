package com.fantask

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fantask.ui.FantaskViewModel
import com.fantask.ui.FantaskViewModelFactory
import com.fantask.ui.badges.BadgesScreen
import com.fantask.ui.components.BadgeCreateDialog
import com.fantask.ui.components.CelebrationDialog
import com.fantask.ui.components.RewardCreateDialog
import com.fantask.ui.components.TaskConfirmationDialog
import com.fantask.ui.components.TaskCreateEditDialog
import com.fantask.ui.home.HomeScreen
import com.fantask.ui.kanban.KanbanScreen
import com.fantask.ui.onboarding.OnboardingScreen
import com.fantask.ui.rewards.RewardsScreen
import com.fantask.ui.settings.SettingsScreen

enum class MainNavTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Home", Icons.Default.Home),
    KANBAN("Board", Icons.Default.ViewKanban),
    REWARDS("Rewards", Icons.Default.CardGiftcard),
    BADGES("Badges", Icons.Default.EmojiEvents),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val app = context.applicationContext as FantaskApplication

    val viewModel: FantaskViewModel = viewModel(
        factory = FantaskViewModelFactory(
            repository = app.repository,
            preferencesRepository = app.preferencesRepository,
            database = app.database,
            alarmScheduler = app.alarmScheduler
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(MainNavTab.HOME) }

    // If first run, show Onboarding Screen
    if (!uiState.preferences.onboardingCompleted) {
        OnboardingScreen(
            currentTheme = uiState.preferences.themeMode,
            onThemeSelect = { mode -> viewModel.setThemeMode(mode) },
            onCompleteOnboarding = { viewModel.completeOnboarding() }
        )
        return
    }

    // Main App Shell
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                MainNavTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (selectedTab) {
            MainNavTab.HOME -> {
                HomeScreen(
                    summary = uiState.gamificationSummary,
                    tasks = uiState.tasks,
                    badges = uiState.badges,
                    onNewTaskClick = { viewModel.openCreateTaskDialog() },
                    onNavigateKanban = { selectedTab = MainNavTab.KANBAN },
                    onNavigateRewards = { selectedTab = MainNavTab.REWARDS },
                    onNavigateBadges = { selectedTab = MainNavTab.BADGES },
                    onNavigateSettings = { selectedTab = MainNavTab.SETTINGS },
                    onCompleteTaskClick = { task -> viewModel.requestCompleteTask(task) },
                    modifier = screenModifier
                )
            }
            MainNavTab.KANBAN -> {
                KanbanScreen(
                    tasks = uiState.tasks,
                    onMoveTask = { task, status -> viewModel.moveTask(task, status) },
                    onRequestCompleteTask = { task -> viewModel.requestCompleteTask(task) },
                    onEditTask = { task -> viewModel.openEditTaskDialog(task) },
                    onDeleteTask = { task -> viewModel.deleteTask(task) },
                    onNewTaskClick = { viewModel.openCreateTaskDialog() },
                    modifier = screenModifier
                )
            }
            MainNavTab.REWARDS -> {
                RewardsScreen(
                    summary = uiState.gamificationSummary,
                    rewards = uiState.rewards,
                    claims = uiState.claims,
                    onClaimReward = { reward -> viewModel.claimReward(reward) },
                    onEditReward = { reward -> viewModel.openEditRewardDialog(reward) },
                    onDeleteReward = { reward -> viewModel.deleteReward(reward) },
                    onNewRewardClick = { viewModel.openCreateRewardDialog() },
                    modifier = screenModifier
                )
            }
            MainNavTab.BADGES -> {
                BadgesScreen(
                    badges = uiState.badges,
                    onNewBadgeClick = { viewModel.openCreateBadgeDialog() },
                    onDeleteBadge = { badge -> viewModel.deleteBadge(badge) },
                    modifier = screenModifier
                )
            }
            MainNavTab.SETTINGS -> {
                SettingsScreen(
                    preferences = uiState.preferences,
                    onThemeChange = { mode -> viewModel.setThemeMode(mode) },
                    onLeadTimeChange = { mins -> viewModel.setLeadTimeMinutes(mins) },
                    onTestNotification = { viewModel.sendTestNotification(context) },
                    onExportBackup = { uri -> viewModel.exportBackup(uri, context.contentResolver) },
                    onImportBackup = { uri -> viewModel.importBackup(uri, context.contentResolver) },
                    onResetOnboarding = { viewModel.resetOnboarding() },
                    modifier = screenModifier
                )
            }
        }

        // Dialogs Overlay
        if (uiState.taskToConfirm != null) {
            TaskConfirmationDialog(
                task = uiState.taskToConfirm!!,
                onConfirm = { viewModel.confirmCompleteTask() },
                onDismiss = { viewModel.dismissTaskConfirmation() }
            )
        }

        if (uiState.celebrationEvent != null) {
            val (points, newBadges) = uiState.celebrationEvent!!
            CelebrationDialog(
                pointsEarned = points,
                unlockedBadges = newBadges,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }

        if (uiState.isCreateTaskOpen) {
            TaskCreateEditDialog(
                initialTask = uiState.taskToEdit,
                onSave = { task -> viewModel.saveTask(task) },
                onDismiss = { viewModel.closeTaskDialog() }
            )
        }

        if (uiState.isCreateRewardOpen) {
            RewardCreateDialog(
                initialReward = uiState.rewardToEdit,
                onSave = { reward -> viewModel.saveReward(reward) },
                onDismiss = { viewModel.closeRewardDialog() }
            )
        }

        if (uiState.isCreateBadgeOpen) {
            BadgeCreateDialog(
                onSave = { badge -> viewModel.saveBadge(badge) },
                onDismiss = { viewModel.closeBadgeDialog() }
            )
        }
    }
}
