package com.fantask.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantask.data.BadgeWithStatus
import com.fantask.data.GamificationSummary
import com.fantask.data.model.TaskDifficulty
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskStatus
import com.fantask.data.model.getRequirements
import com.fantask.theme.DuoBlue
import com.fantask.theme.DuoBlueShadow
import com.fantask.theme.DuoFlame
import com.fantask.theme.DuoGold
import com.fantask.theme.DuoGoldShadow
import com.fantask.theme.DuoGreen
import com.fantask.theme.DuoGreenShadow
import com.fantask.theme.DuoPurple
import com.fantask.theme.DuoPurpleShadow
import com.fantask.theme.DuoRed
import com.fantask.theme.DuoRedShadow
import com.fantask.ui.components.DuoButton
import com.fantask.ui.components.DuoCard
import com.fantask.ui.components.DuoStatBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    summary: GamificationSummary,
    tasks: List<TaskEntity>,
    badges: List<BadgeWithStatus>,
    onNewTaskClick: () -> Unit,
    onNavigateKanban: () -> Unit,
    onNavigateRewards: () -> Unit,
    onNavigateBadges: () -> Unit,
    onNavigateSettings: () -> Unit,
    onCompleteTaskClick: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingOrScheduledTasks = tasks.filter { it.status != TaskStatus.DONE }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FANTASK",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        fontSize = 22.sp,
                        color = DuoGreen
                    )
                },
                actions = {
                    // Duolingo Top Stat Badges
                    DuoStatBadge(
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = DuoFlame,
                        valueText = "${summary.currentStreak}"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DuoStatBadge(
                        icon = Icons.Default.MonetizationOn,
                        iconTint = DuoGold,
                        valueText = "${summary.currentPoints}",
                        onClick = onNavigateRewards
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewTaskClick,
                containerColor = DuoGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Duolingo Streak Banner
            item {
                DuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DuoFlame.copy(alpha = 0.5f),
                    backgroundColor = DuoFlame.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(DuoFlame.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = DuoFlame,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${summary.currentStreak} DAY STREAK!",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (summary.currentStreak > 0) "Complete a task today to keep it lit!" else "Complete your first task to ignite!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Quick Nav DuoButtons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DuoButton(
                        text = "KANBAN",
                        onClick = onNavigateKanban,
                        icon = Icons.Default.ViewKanban,
                        color = DuoBlue,
                        shadowColor = DuoBlueShadow,
                        modifier = Modifier.weight(1f)
                    )
                    DuoButton(
                        text = "REWARDS",
                        onClick = onNavigateRewards,
                        icon = Icons.Default.CardGiftcard,
                        color = DuoGold,
                        shadowColor = DuoGoldShadow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Active Tasks Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S TASKS",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${pendingOrScheduledTasks.size} tasks left",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (pendingOrScheduledTasks.isEmpty()) {
                item {
                    DuoCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DuoGreen,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All tasks cleared!",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Add another quest to build your streak and earn more points.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(pendingOrScheduledTasks, key = { it.id }) { task ->
                    HomeTaskTile(task = task, onComplete = { onCompleteTaskClick(task) })
                }
            }

            // Achievements Row
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACHIEVEMENTS",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodySmall,
                        color = DuoBlue,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.clickable(onClick = onNavigateBadges)
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(badges, key = { it.badge.id }) { item ->
                        DuoCard(
                            modifier = Modifier
                                .width(135.dp)
                                .clickable(onClick = onNavigateBadges),
                            borderColor = if (item.isUnlocked) DuoGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            backgroundColor = if (item.isUnlocked) DuoGold.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (item.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (item.isUnlocked) DuoGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.badge.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (item.isUnlocked) "UNLOCKED" else "${item.currentProgress}/${item.badge.threshold}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (item.isUnlocked) DuoGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTaskTile(task: TaskEntity, onComplete: () -> Unit) {
    val (diffColor, diffBg) = when (task.difficulty) {
        TaskDifficulty.EASY -> DuoGreen to DuoGreen.copy(alpha = 0.12f)
        TaskDifficulty.MEDIUM -> DuoBlue to DuoBlue.copy(alpha = 0.12f)
        TaskDifficulty.HARD -> DuoPurple to DuoPurple.copy(alpha = 0.12f)
        TaskDifficulty.SUPER_HARD -> DuoRed to DuoRed.copy(alpha = 0.12f)
    }

    val requirements = remember(task.requirementsJson) { task.getRequirements() }

    DuoCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onComplete,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DuoGreen.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = DuoGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = diffBg
                    ) {
                        Text(
                            text = "+${task.difficulty.points} pts",
                            color = diffColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (requirements.isNotEmpty()) {
                        val done = requirements.count { it.isDone }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "Reqs: $done/${requirements.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (task.scheduleEnabled && !task.scheduledStartTime.isNullOrEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = task.scheduledStartTime ?: "",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
