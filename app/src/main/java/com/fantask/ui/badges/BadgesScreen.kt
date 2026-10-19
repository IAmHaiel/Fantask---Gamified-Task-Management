package com.fantask.ui.badges

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantask.data.BadgeWithStatus
import com.fantask.data.model.BadgeEntity
import com.fantask.theme.DuoBlue
import com.fantask.theme.DuoGold
import com.fantask.theme.DuoGreen
import com.fantask.ui.components.DuoCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    badges: List<BadgeWithStatus>,
    onNewBadgeClick: () -> Unit,
    onDeleteBadge: (BadgeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedCount = badges.count { it.isUnlocked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ACHIEVEMENTS", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewBadgeClick,
                containerColor = DuoBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Badge")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Duolingo Mastery Trophy Banner
            item {
                DuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DuoGold.copy(alpha = 0.6f),
                    backgroundColor = DuoGold.copy(alpha = 0.12f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(DuoGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = DuoGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$unlockedCount / ${badges.size} BADGES UNLOCKED",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                            val progressFloat = if (badges.isNotEmpty()) unlockedCount.toFloat() / badges.size.toFloat() else 0f
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressFloat },
                                color = DuoGold,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            // Badges List
            items(badges, key = { it.badge.id }) { item ->
                BadgeTile(badgeWithStatus = item, onDelete = { onDeleteBadge(item.badge) })
            }
        }
    }
}

@Composable
private fun BadgeTile(badgeWithStatus: BadgeWithStatus, onDelete: () -> Unit) {
    val badge = badgeWithStatus.badge
    val isUnlocked = badgeWithStatus.isUnlocked
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    val iconVector: ImageVector = when (badge.iconName) {
        "EmojiEvents" -> Icons.Default.EmojiEvents
        "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
        "MonetizationOn" -> Icons.Default.MonetizationOn
        "Shield" -> Icons.Default.Shield
        else -> Icons.Default.Star
    }

    DuoCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isUnlocked) DuoGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        backgroundColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) DuoGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) iconVector else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) DuoGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = badge.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (!badge.isPreset) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DuoBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "CUSTOM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = DuoBlue,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (isUnlocked) {
                    val dateStr = badgeWithStatus.unlockedAt?.let { dateFormat.format(Date(it)) } ?: "Completed"
                    Text(
                        text = "Unlocked on $dateStr",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuoGold
                    )
                } else {
                    val current = badgeWithStatus.currentProgress
                    val target = badge.threshold
                    val frac = if (target > 0) minOf(1f, current.toFloat() / target.toFloat()) else 0f
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { frac },
                            color = DuoGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$current / $target",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!badge.isPreset) {
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
