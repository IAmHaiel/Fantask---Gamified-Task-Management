package com.fantask.ui.kanban

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fantask.data.model.TaskDifficulty
import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskPriority
import com.fantask.data.model.TaskRequirement
import com.fantask.data.model.TaskStatus
import com.fantask.data.model.getRequirements
import com.fantask.data.model.withRequirements
import com.fantask.theme.DuoBlue
import com.fantask.theme.DuoBlueShadow
import com.fantask.theme.DuoFlame
import com.fantask.theme.DuoGold
import com.fantask.theme.DuoGreen
import com.fantask.theme.DuoGreenShadow
import com.fantask.theme.DuoPurple
import com.fantask.theme.DuoPurpleShadow
import com.fantask.theme.DuoRed
import com.fantask.theme.DuoRedShadow
import com.fantask.ui.components.DuoCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(
    tasks: List<TaskEntity>,
    onMoveTask: (TaskEntity, TaskStatus) -> Unit,
    onRequestCompleteTask: (TaskEntity) -> Unit,
    onSaveTask: (TaskEntity) -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onNewTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statuses = listOf(
        TaskStatus.PENDING,
        TaskStatus.TODO,
        TaskStatus.IN_PROGRESS,
        TaskStatus.DONE
    )

    // Drag-and-drop state
    var draggedTask by remember { mutableStateOf<TaskEntity?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var hoveredStatus by remember { mutableStateOf<TaskStatus?>(null) }

    // Column screen bounds storage for collision detection
    val columnBounds = remember { mutableStateMapOf<TaskStatus, Rect>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Kanban Board",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DuoBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Hold card to drag",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DuoBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontally Scrollable Board containing all 4 columns side by side
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                statuses.forEach { status ->
                    val columnTasks = tasks.filter { it.status == status }
                    val isHovered = hoveredStatus == status && draggedTask != null

                    KanbanColumn(
                        status = status,
                        tasks = columnTasks,
                        isHovered = isHovered,
                        onColumnBounds = { rect -> columnBounds[status] = rect },
                        onStartDrag = { task, touchPos ->
                            draggedTask = task
                            dragPosition = touchPos
                            dragOffset = Offset.Zero
                        },
                        onDrag = { delta ->
                            dragOffset += delta
                            val currentPos = dragPosition + dragOffset
                            // Check which column contains currentPos
                            hoveredStatus = columnBounds.entries.firstOrNull { (_, rect) ->
                                rect.contains(currentPos)
                            }?.key
                        },
                        onEndDrag = {
                            val targetStatus = hoveredStatus
                            val task = draggedTask
                            if (task != null && targetStatus != null && targetStatus != task.status) {
                                if (targetStatus == TaskStatus.DONE) {
                                    onRequestCompleteTask(task)
                                } else {
                                    onMoveTask(task, targetStatus)
                                }
                            }
                            draggedTask = null
                            dragOffset = Offset.Zero
                            hoveredStatus = null
                        },
                        onQuickMove = { task, newStatus ->
                            if (newStatus == TaskStatus.DONE) {
                                onRequestCompleteTask(task)
                            } else {
                                onMoveTask(task, newStatus)
                            }
                        },
                        onToggleRequirement = { task, reqIndex ->
                            val reqs = task.getRequirements().toMutableList()
                            if (reqIndex in reqs.indices) {
                                reqs[reqIndex] = reqs[reqIndex].copy(isDone = !reqs[reqIndex].isDone)
                                onSaveTask(task.withRequirements(reqs))
                            }
                        },
                        onEdit = onEditTask,
                        onDelete = onDeleteTask
                    )
                }
            }

            // Floating drag ghost (tactile lift overlay)
            draggedTask?.let { task ->
                val (cardColor, _) = when (task.difficulty) {
                    TaskDifficulty.EASY -> DuoGreen to DuoGreenShadow
                    TaskDifficulty.MEDIUM -> DuoBlue to DuoBlueShadow
                    TaskDifficulty.HARD -> DuoPurple to DuoPurpleShadow
                    TaskDifficulty.SUPER_HARD -> DuoRed to DuoRedShadow
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
                        .zIndex(100f)
                        .graphicsLayer {
                            scaleX = 1.05f
                            scaleY = 1.05f
                            rotationZ = 3f
                            alpha = 0.95f
                        }
                        .padding(16.dp)
                        .width(280.dp)
                ) {
                    DuoCard(
                        borderColor = cardColor,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.shadow(16.dp, RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = task.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Moving to ${hoveredStatus?.displayName ?: "..."}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = cardColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanColumn(
    status: TaskStatus,
    tasks: List<TaskEntity>,
    isHovered: Boolean,
    onColumnBounds: (Rect) -> Unit,
    onStartDrag: (TaskEntity, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onEndDrag: () -> Unit,
    onQuickMove: (TaskEntity, TaskStatus) -> Unit,
    onToggleRequirement: (TaskEntity, Int) -> Unit,
    onEdit: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit
) {
    val (headerColor, headerBg) = when (status) {
        TaskStatus.PENDING -> DuoPurple to DuoPurple.copy(alpha = 0.12f)
        TaskStatus.TODO -> DuoBlue to DuoBlue.copy(alpha = 0.12f)
        TaskStatus.IN_PROGRESS -> DuoFlame to DuoFlame.copy(alpha = 0.12f)
        TaskStatus.DONE -> DuoGreen to DuoGreen.copy(alpha = 0.12f)
    }

    Box(
        modifier = Modifier
            .width(290.dp)
            .fillMaxHeight()
            .onGloballyPositioned { coordinates ->
                onColumnBounds(coordinates.boundsInRoot())
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isHovered) headerColor.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            )
            .border(
                width = if (isHovered) 2.5.dp else 1.5.dp,
                color = if (isHovered) headerColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Column Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(headerColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = status.displayName,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Task count pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = headerBg
                ) {
                    Text(
                        text = "${tasks.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = headerColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Cards in this Column
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Drop cards here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        KanbanDraggableCard(
                            task = task,
                            onStartDrag = { touchPos -> onStartDrag(task, touchPos) },
                            onDrag = onDrag,
                            onEndDrag = onEndDrag,
                            onQuickMove = { target -> onQuickMove(task, target) },
                            onToggleRequirement = { idx -> onToggleRequirement(task, idx) },
                            onEdit = { onEdit(task) },
                            onDelete = { onDelete(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanDraggableCard(
    task: TaskEntity,
    onStartDrag: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onEndDrag: () -> Unit,
    onQuickMove: (TaskStatus) -> Unit,
    onToggleRequirement: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val requirements = remember(task.requirementsJson) { task.getRequirements() }
    var expandedRequirements by remember { mutableStateOf(false) }

    val (diffColor, diffBg) = when (task.difficulty) {
        TaskDifficulty.EASY -> DuoGreen to DuoGreen.copy(alpha = 0.12f)
        TaskDifficulty.MEDIUM -> DuoBlue to DuoBlue.copy(alpha = 0.12f)
        TaskDifficulty.HARD -> DuoPurple to DuoPurple.copy(alpha = 0.12f)
        TaskDifficulty.SUPER_HARD -> DuoRed to DuoRed.copy(alpha = 0.12f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset -> onStartDrag(offset) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onEndDrag() },
                    onDragCancel = { onEndDrag() }
                )
            }
    ) {
        DuoCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            // Drag grip indicator & Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.DragIndicator,
                        contentDescription = "Hold to drag",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", modifier = Modifier.size(18.dp))
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Task") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Task") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            // Requirements Checklist Preview (Anti-Abuse)
            if (requirements.isNotEmpty()) {
                val doneCount = requirements.count { it.isDone }
                val totalCount = requirements.size

                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedRequirements = !expandedRequirements }
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (doneCount == totalCount) Icons.Default.CheckCircle else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (doneCount == totalCount) DuoGreen else DuoFlame,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Requirements ($doneCount/$totalCount)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (expandedRequirements) "Hide" else "Show",
                                fontSize = 10.sp,
                                color = DuoBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (expandedRequirements) {
                            Spacer(modifier = Modifier.height(4.dp))
                            requirements.forEachIndexed { idx, req ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleRequirement(idx) }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Checkbox(
                                        checked = req.isDone,
                                        onCheckedChange = { onToggleRequirement(idx) },
                                        modifier = Modifier.size(20.dp),
                                        colors = CheckboxDefaults.colors(checkedColor = DuoGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = req.text,
                                        fontSize = 12.sp,
                                        color = if (req.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pills row: Difficulty, Priority, Schedule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty (+pts)
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

                // Priority
                val prioColor = when (task.priority) {
                    TaskPriority.HIGH -> DuoRed
                    TaskPriority.MEDIUM -> DuoFlame
                    TaskPriority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = prioColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = task.priority.displayName,
                        color = prioColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Scheduled Time
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
