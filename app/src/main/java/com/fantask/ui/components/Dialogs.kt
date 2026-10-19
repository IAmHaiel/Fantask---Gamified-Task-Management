package com.fantask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantask.data.model.BadgeConditionType
import com.fantask.data.model.BadgeEntity
import com.fantask.data.model.RecurrenceType
import com.fantask.data.model.RewardEntity
import com.fantask.data.model.TaskDifficulty
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskPriority
import com.fantask.data.model.TaskStatus
import com.fantask.theme.EasyGreen
import com.fantask.theme.HardPurple
import com.fantask.theme.MediumBlue
import com.fantask.theme.PointsGold
import com.fantask.theme.StreakFlame
import com.fantask.theme.SuperHardRed

/**
 * Mandatory Task Confirmation Dialog:
 * Asks the user if the task is genuinely completed and highlights points earned.
 */
@Composable
fun TaskConfirmationDialog(
    task: TaskEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EasyGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirm Completion",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Is this task really done?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = task.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (task.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Reward & Streak Callout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PointsGold.copy(alpha = 0.15f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = PointsGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You will earn +${task.difficulty.points} points!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StreakFlame.copy(alpha = 0.12f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = StreakFlame,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "This will advance your daily streak!",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = EasyGreen)
            ) {
                Text("Yes, It's Done!", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Not Yet")
            }
        }
    )
}

/**
 * Celebration Overlay when task completes and badges unlock
 */
@Composable
fun CelebrationDialog(
    pointsEarned: Int,
    unlockedBadges: List<BadgeEntity>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = PointsGold,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Awesome Job!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "+$pointsEarned Points",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PointsGold
                )
                Text(
                    text = "Points added to your balance & daily streak maintained!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (unlockedBadges.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "New Achievement Unlocked!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    unlockedBadges.forEach { badge ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = PointsGold,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = badge.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = badge.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Continue", fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Task Creation and Edit Dialog
 */
@Composable
fun TaskCreateEditDialog(
    initialTask: TaskEntity? = null,
    onSave: (TaskEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialTask?.name ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var difficulty by remember { mutableStateOf(initialTask?.difficulty ?: TaskDifficulty.EASY) }
    var priority by remember { mutableStateOf(initialTask?.priority ?: TaskPriority.MEDIUM) }
    var status by remember { mutableStateOf(initialTask?.status ?: TaskStatus.TODO) }

    // Offline scheduling
    var scheduleEnabled by remember { mutableStateOf(initialTask?.scheduleEnabled ?: false) }
    var startTime by remember { mutableStateOf(initialTask?.scheduledStartTime ?: "08:00") }
    var endTime by remember { mutableStateOf(initialTask?.scheduledEndTime ?: "10:00") }
    var recurrenceType by remember { mutableStateOf(initialTask?.recurrenceType ?: RecurrenceType.DAILY) }

    // Due date (stored as epoch millis)
    var hasDueDate by remember { mutableStateOf(initialTask?.dueDate != null) }
    var dueDateMillis by remember { mutableStateOf(initialTask?.dueDate ?: (System.currentTimeMillis() + 86400000L)) }

    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTask == null) "New Task" else "Edit Task",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Task Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Task Name *") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        text = "Task name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Difficulty Selector with Points
                Text("Difficulty & Points Reward:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskDifficulty.values().forEach { diff ->
                        val isSelected = difficulty == diff
                        val (chipBg, chipBorder) = when (diff) {
                            TaskDifficulty.EASY -> EasyGreen to EasyGreen
                            TaskDifficulty.MEDIUM -> MediumBlue to MediumBlue
                            TaskDifficulty.HARD -> HardPurple to HardPurple
                            TaskDifficulty.SUPER_HARD -> SuperHardRed to SuperHardRed
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) chipBg.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) chipBorder else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { difficulty = diff }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = diff.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = "+${diff.points} pts",
                                    fontSize = 10.sp,
                                    color = chipBg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Priority Selector
                Text("Priority:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { prio ->
                        FilterChip(
                            selected = priority == prio,
                            onClick = { priority = prio },
                            label = { Text(prio.displayName, fontSize = 12.sp) }
                        )
                    }
                }

                // Status Selector (if editing)
                if (initialTask != null) {
                    Text("Column Status:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TaskStatus.values().forEach { stat ->
                            FilterChip(
                                selected = status == stat,
                                onClick = { status = stat },
                                label = { Text(stat.displayName, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Due Date Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = hasDueDate,
                        onCheckedChange = { hasDueDate = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Set Due Date Reminder", style = MaterialTheme.typography.bodyMedium)
                }

                // Offline Scheduled Window Section (e.g. 8AM - 10AM)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Offline Schedule Reminder",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = scheduleEnabled,
                                onCheckedChange = { scheduleEnabled = it }
                            )
                        }

                        if (scheduleEnabled) {
                            Text(
                                "e.g. Daily task reminder at 8:00 AM - 10:00 AM",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = startTime,
                                    onValueChange = { startTime = it },
                                    label = { Text("Start Time") },
                                    placeholder = { Text("08:00") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = endTime,
                                    onValueChange = { endTime = it },
                                    label = { Text("End Time") },
                                    placeholder = { Text("10:00") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Recurrence Type
                            Text("Recurrence:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(RecurrenceType.DAILY, RecurrenceType.WEEKDAYS, RecurrenceType.WEEKENDS).forEach { rec ->
                                    FilterChip(
                                        selected = recurrenceType == rec,
                                        onClick = { recurrenceType = rec },
                                        label = { Text(rec.displayName, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val taskToSave = (initialTask ?: TaskEntity(name = name.trim())).copy(
                        name = name.trim(),
                        description = description.trim(),
                        difficulty = difficulty,
                        priority = priority,
                        status = status,
                        dueDate = if (hasDueDate) dueDateMillis else null,
                        scheduleEnabled = scheduleEnabled,
                        scheduledStartTime = if (scheduleEnabled) startTime else null,
                        scheduledEndTime = if (scheduleEnabled) endTime else null,
                        recurrenceType = if (scheduleEnabled) recurrenceType else RecurrenceType.NONE
                    )
                    onSave(taskToSave)
                }
            ) {
                Text(if (initialTask == null) "Create Task" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Reward Creation Dialog
 */
@Composable
fun RewardCreateDialog(
    initialReward: RewardEntity? = null,
    onSave: (RewardEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialReward?.name ?: "") }
    var description by remember { mutableStateOf(initialReward?.description ?: "") }
    var pointsText by remember { mutableStateOf(initialReward?.pointsRequired?.toString() ?: "20") }
    var repeatable by remember { mutableStateOf(initialReward?.repeatable ?: true) }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialReward == null) "Create New Reward" else "Edit Reward",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Reward Name *") },
                    placeholder = { Text("e.g. 1 Hour Video Game, Coffee break") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { pointsText = it },
                    label = { Text("Points Required *") },
                    placeholder = { Text("e.g. 25") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Switch(
                        checked = repeatable,
                        onCheckedChange = { repeatable = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (repeatable) "Repeatable Reward" else "One-time Reward",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (repeatable) "Can be claimed multiple times" else "Can only be claimed once",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (error) {
                    Text(
                        text = "Please enter a valid name and points amount (> 0)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val points = pointsText.toIntOrNull()
                    if (name.isBlank() || points == null || points <= 0) {
                        error = true
                        return@Button
                    }
                    val reward = (initialReward ?: RewardEntity(name = name.trim(), pointsRequired = points)).copy(
                        name = name.trim(),
                        description = description.trim(),
                        pointsRequired = points,
                        repeatable = repeatable
                    )
                    onSave(reward)
                }
            ) {
                Text(if (initialReward == null) "Create Reward" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Custom Badge Creation Dialog (Configurable by user)
 */
@Composable
fun BadgeCreateDialog(
    onSave: (BadgeEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var conditionType by remember { mutableStateOf(BadgeConditionType.TASKS_COMPLETED_TOTAL) }
    var thresholdText by remember { mutableStateOf("10") }
    var selectedIcon by remember { mutableStateOf("Star") }
    var error by remember { mutableStateOf(false) }

    val iconOptions = listOf("Star", "EmojiEvents", "LocalFireDepartment", "MonetizationOn", "Shield")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Badge", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Badge Name *") },
                    placeholder = { Text("e.g. Productivity Beast") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Complete 50 tasks with focus") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Achievement Condition:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                BadgeConditionType.values().forEach { cond ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { conditionType = cond }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = conditionType == cond,
                            onCheckedChange = { conditionType = cond }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(cond.displayName, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                OutlinedTextField(
                    value = thresholdText,
                    onValueChange = { thresholdText = it },
                    label = { Text("Goal / Threshold *") },
                    placeholder = { Text("e.g. 10") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Choose Icon:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    iconOptions.forEach { iconName ->
                        val isSelected = selectedIcon == iconName
                        val iconVector = when (iconName) {
                            "EmojiEvents" -> Icons.Default.EmojiEvents
                            "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
                            "MonetizationOn" -> Icons.Default.MonetizationOn
                            else -> Icons.Default.Star
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (error) {
                    Text(
                        text = "Please provide a valid name and goal threshold (> 0)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val threshold = thresholdText.toIntOrNull()
                    if (name.isBlank() || threshold == null || threshold <= 0) {
                        error = true
                        return@Button
                    }
                    val badge = BadgeEntity(
                        name = name.trim(),
                        description = description.trim(),
                        iconName = selectedIcon,
                        conditionType = conditionType,
                        threshold = threshold,
                        isPreset = false
                    )
                    onSave(badge)
                }
            ) {
                Text("Create Badge")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
