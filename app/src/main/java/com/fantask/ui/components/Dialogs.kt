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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.fantask.data.model.TaskRequirement
import com.fantask.data.model.TaskStatus
import com.fantask.data.model.areAllRequirementsDone
import com.fantask.data.model.getRequirements
import com.fantask.data.model.withRequirements
import com.fantask.theme.DuoBlue
import com.fantask.theme.DuoBlueShadow
import com.fantask.theme.DuoFlame
import com.fantask.theme.DuoFlameShadow
import com.fantask.theme.DuoGold
import com.fantask.theme.DuoGoldShadow
import com.fantask.theme.DuoGreen
import com.fantask.theme.DuoGreenShadow
import com.fantask.theme.DuoPurple
import com.fantask.theme.DuoPurpleShadow
import com.fantask.theme.DuoRed
import com.fantask.theme.DuoRedShadow

/**
 * Task Confirmation Dialog with Anti-Abuse Requirements Checklist:
 * Displays required sub-tasks that must all be checked before claiming points.
 */
@Composable
fun TaskConfirmationDialog(
    task: TaskEntity,
    onConfirm: (TaskEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val initialRequirements = remember { task.getRequirements() }
    val requirements = remember { mutableStateListOf<TaskRequirement>().apply { addAll(initialRequirements) } }

    val allChecked = requirements.isEmpty() || requirements.all { it.isDone }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DuoGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = DuoGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Confirm Completion",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Did you complete all requirements for this task?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Task Summary Tile
                DuoCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = task.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
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

                // Anti-Abuse Requirements Checklist
                if (requirements.isNotEmpty()) {
                    Text(
                        text = "Task Requirements (${requirements.count { it.isDone }}/${requirements.size} completed):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        requirements.forEachIndexed { index, req ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        requirements[index] = req.copy(isDone = !req.isDone)
                                    }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            ) {
                                Checkbox(
                                    checked = req.isDone,
                                    onCheckedChange = { checked ->
                                        requirements[index] = req.copy(isDone = checked)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = DuoGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = req.text,
                                    fontWeight = if (req.isDone) FontWeight.Normal else FontWeight.SemiBold,
                                    color = if (req.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (!allChecked) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DuoRed.copy(alpha = 0.12f))
                                .padding(10.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DuoRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Please complete and check all requirements to claim points!",
                                color = DuoRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Reward & Streak Callout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DuoGold.copy(alpha = 0.15f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = DuoGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Reward: +${task.difficulty.points} Points!",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Maintains your streak & updates achievement progress",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            DuoButton(
                text = "CLAIM +${task.difficulty.points} PTS!",
                onClick = {
                    val updatedTask = task.withRequirements(requirements.toList())
                    onConfirm(updatedTask)
                },
                color = DuoGreen,
                shadowColor = DuoGreenShadow,
                enabled = allChecked,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Not Yet", fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Celebration Overlay when task completes
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DuoGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = DuoGold,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "TASK COMPLETED!",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "+$pointsEarned PTS",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = DuoGold
                )
                Text(
                    text = "Streak maintained! Keep up the momentum tomorrow.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (unlockedBadges.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "NEW BADGES UNLOCKED!",
                        fontWeight = FontWeight.Black,
                        color = DuoBlue,
                        fontSize = 14.sp
                    )
                    unlockedBadges.forEach { badge ->
                        DuoCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = DuoGold,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = badge.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = badge.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            DuoButton(
                text = "CONTINUE",
                onClick = onDismiss,
                color = DuoGreen,
                shadowColor = DuoGreenShadow,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

/**
 * Task Creation & Edit Dialog with Requirements Checklist & AM/PM Time Pickers
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

    // Requirements Checklist State
    val requirements = remember {
        mutableStateListOf<TaskRequirement>().apply {
            addAll(initialTask?.getRequirements() ?: emptyList())
        }
    }
    var newRequirementText by remember { mutableStateOf("") }

    // Offline scheduling
    var scheduleEnabled by remember { mutableStateOf(initialTask?.scheduleEnabled ?: false) }
    var startTime by remember { mutableStateOf(initialTask?.scheduledStartTime ?: "08:00 AM") }
    var endTime by remember { mutableStateOf(initialTask?.scheduledEndTime ?: "10:00 AM") }
    var recurrenceType by remember { mutableStateOf(initialTask?.recurrenceType ?: RecurrenceType.DAILY) }

    // Time Picker Modals
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTask == null) "New Task" else "Edit Task",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
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
                    Text("Task name is required", color = DuoRed, style = MaterialTheme.typography.bodySmall)
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Requirements Checklist Section (Anti-Abuse)
                Text(
                    text = "Task Requirements Checklist:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Add sub-tasks that must be verified before completing this task.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newRequirementText,
                        onValueChange = { newRequirementText = it },
                        placeholder = { Text("e.g. Read 20 pages, Write tests") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newRequirementText.isNotBlank()) {
                                requirements.add(TaskRequirement(text = newRequirementText.trim()))
                                newRequirementText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DuoBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Requirement", tint = Color.White)
                    }
                }

                // List of current requirements
                if (requirements.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        requirements.forEachIndexed { index, req ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "• ${req.text}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { requirements.removeAt(index) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = DuoRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Difficulty Selector with Points
                Text("Difficulty & Rewards:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskDifficulty.values().forEach { diff ->
                        val isSelected = difficulty == diff
                        val (cardColor, shadowColor) = when (diff) {
                            TaskDifficulty.EASY -> DuoGreen to DuoGreenShadow
                            TaskDifficulty.MEDIUM -> DuoBlue to DuoBlueShadow
                            TaskDifficulty.HARD -> DuoPurple to DuoPurpleShadow
                            TaskDifficulty.SUPER_HARD -> DuoRed to DuoRedShadow
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) cardColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) cardColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { difficulty = diff }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = diff.displayName,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = "+${diff.points} pts",
                                    fontSize = 10.sp,
                                    color = cardColor,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Priority Selector
                Text("Priority:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

                // Offline Scheduled Window Section (with 12h AM/PM Modals!)
                DuoCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = DuoFlame)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Offline Schedule Reminder",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = scheduleEnabled,
                            onCheckedChange = { scheduleEnabled = it }
                        )
                    }

                    if (scheduleEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tap to choose start and end times (with AM/PM):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Interactive Time Chips (Opens Modal!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Start Time Chip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.5.dp, DuoBlue, RoundedCornerShape(12.dp))
                                    .clickable { showStartTimePicker = true }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Start Time", fontSize = 11.sp, color = DuoBlue, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = DuoBlue)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(startTime, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    }
                                }
                            }

                            // End Time Chip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.5.dp, DuoBlue, RoundedCornerShape(12.dp))
                                    .clickable { showEndTimePicker = true }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("End Time", fontSize = 11.sp, color = DuoBlue, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = DuoBlue)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(endTime, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Recurrence Selection
                        Text("Repeat:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        },
        confirmButton = {
            DuoButton(
                text = if (initialTask == null) "CREATE TASK" else "SAVE CHANGES",
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@DuoButton
                    }
                    val base = initialTask ?: TaskEntity(name = name.trim())
                    val updated = base.copy(
                        name = name.trim(),
                        description = description.trim(),
                        difficulty = difficulty,
                        priority = priority,
                        status = status,
                        scheduleEnabled = scheduleEnabled,
                        scheduledStartTime = if (scheduleEnabled) startTime else null,
                        scheduledEndTime = if (scheduleEnabled) endTime else null,
                        recurrenceType = if (scheduleEnabled) recurrenceType else RecurrenceType.NONE
                    ).withRequirements(requirements.toList())

                    onSave(updated)
                },
                color = DuoGreen,
                shadowColor = DuoGreenShadow,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    )

    // Time Picker Modals
    if (showStartTimePicker) {
        DuoTimePickerDialog(
            title = "Set Start Time",
            onTimeSelected = { timeStr ->
                startTime = timeStr
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        DuoTimePickerDialog(
            title = "Set End Time",
            onTimeSelected = { timeStr ->
                endTime = timeStr
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

/**
 * Reward Creation Dialog with Duolingo 3D Button Styling
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
                text = if (initialReward == null) "Create Reward" else "Edit Reward",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Reward Name *") },
                    placeholder = { Text("e.g. 1 Hour Video Game, Ice Cream") },
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
                    label = { Text("Points Cost *") },
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
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (repeatable) "Repeatable Reward" else "One-Time Reward",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (repeatable) "Can be purchased anytime you have points" else "Can only be purchased once",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (error) {
                    Text("Please enter a valid reward name and positive points cost", color = DuoRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            DuoButton(
                text = if (initialReward == null) "SAVE REWARD" else "UPDATE",
                onClick = {
                    val points = pointsText.toIntOrNull()
                    if (name.isBlank() || points == null || points <= 0) {
                        error = true
                        return@DuoButton
                    }
                    val reward = (initialReward ?: RewardEntity(name = name.trim(), pointsRequired = points)).copy(
                        name = name.trim(),
                        description = description.trim(),
                        pointsRequired = points,
                        repeatable = repeatable
                    )
                    onSave(reward)
                },
                color = DuoGold,
                shadowColor = DuoGoldShadow,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Custom Badge Dialog with Duolingo 3D Button Styling
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

    val iconOptions = listOf("Star", "EmojiEvents", "LocalFireDepartment", "MonetizationOn")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Badge", fontWeight = FontWeight.Black, fontSize = 20.sp) },
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
                    placeholder = { Text("e.g. Focus Master") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Complete 50 tasks with perfection") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Unlock Condition:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                            onCheckedChange = { conditionType = cond },
                            colors = CheckboxDefaults.colors(checkedColor = DuoBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(cond.displayName, fontSize = 13.sp)
                    }
                }

                OutlinedTextField(
                    value = thresholdText,
                    onValueChange = { thresholdText = it },
                    label = { Text("Goal Threshold *") },
                    placeholder = { Text("e.g. 10") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Icon:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                .background(if (isSelected) DuoGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) DuoGold else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = if (isSelected) DuoGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (error) {
                    Text("Please provide a valid badge name and goal number (> 0)", color = DuoRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            DuoButton(
                text = "CREATE BADGE",
                onClick = {
                    val threshold = thresholdText.toIntOrNull()
                    if (name.isBlank() || threshold == null || threshold <= 0) {
                        error = true
                        return@DuoButton
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
                },
                color = DuoBlue,
                shadowColor = DuoBlueShadow,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    )
}
