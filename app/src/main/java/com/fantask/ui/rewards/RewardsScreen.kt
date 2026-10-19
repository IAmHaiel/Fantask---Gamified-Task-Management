package com.fantask.ui.rewards

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Repeat
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantask.data.GamificationSummary
import com.fantask.data.model.RewardClaimEntity
import com.fantask.data.model.RewardEntity
import com.fantask.theme.DuoGold
import com.fantask.theme.DuoGoldShadow
import com.fantask.theme.DuoGreen
import com.fantask.theme.DuoGreenShadow
import com.fantask.ui.components.DuoButton
import com.fantask.ui.components.DuoCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    summary: GamificationSummary,
    rewards: List<RewardEntity>,
    claims: List<RewardClaimEntity>,
    onClaimReward: (RewardEntity) -> Unit,
    onEditReward: (RewardEntity) -> Unit,
    onDeleteReward: (RewardEntity) -> Unit,
    onNewRewardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClaimsHistory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("REWARDS SHOP", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp) },
                actions = {
                    IconButton(onClick = { showClaimsHistory = !showClaimsHistory }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Claim History",
                            tint = if (showClaimsHistory) DuoGold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewRewardClick,
                containerColor = DuoGold,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Reward")
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
            // Duolingo Points Vault Card
            item {
                DuoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DuoGold.copy(alpha = 0.6f),
                    backgroundColor = DuoGold.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL POINTS BALANCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = DuoGold,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${summary.currentPoints}",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " PTS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = DuoGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = "Earned: ${summary.totalPointsEarned} pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Spent: ${summary.totalPointsSpent} pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (showClaimsHistory) {
                item {
                    Text(
                        text = "PURCHASE HISTORY",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                if (claims.isEmpty()) {
                    item {
                        Text(
                            text = "No rewards purchased yet. Complete tasks to earn points and reward yourself!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(claims, key = { it.id }) { claim ->
                        ClaimHistoryTile(claim = claim)
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YOUR REWARDS",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${rewards.size} available",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (rewards.isEmpty()) {
                    item {
                        DuoCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = DuoGold,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No rewards created yet",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Create custom rewards (gaming time, coffee, movie) that you can buy with your task points!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(rewards, key = { it.id }) { reward ->
                        val claimCount = claims.count { it.rewardId == reward.id }
                        val isClaimable = summary.currentPoints >= reward.pointsRequired && (reward.repeatable || claimCount == 0)

                        RewardTile(
                            reward = reward,
                            isClaimable = isClaimable,
                            claimCount = claimCount,
                            onClaim = { onClaimReward(reward) },
                            onEdit = { onEditReward(reward) },
                            onDelete = { onDeleteReward(reward) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardTile(
    reward: RewardEntity,
    isClaimable: Boolean,
    claimCount: Int,
    onClaim: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    DuoCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isClaimable) DuoGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reward.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    if (reward.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = reward.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cost Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DuoGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${reward.pointsRequired} PTS",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Duolingo 3D Claim Button
                DuoButton(
                    text = if (!reward.repeatable && claimCount > 0) "CLAIMED" else "CLAIM",
                    onClick = onClaim,
                    enabled = isClaimable,
                    color = DuoGreen,
                    shadowColor = DuoGreenShadow,
                    height = 38.dp,
                    modifier = Modifier.width(110.dp)
                )
            }
        }
    }
}

@Composable
private fun ClaimHistoryTile(claim: RewardClaimEntity) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()) }
    val formattedDate = remember(claim.claimedAt) { dateFormat.format(Date(claim.claimedAt)) }

    DuoCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = claim.rewardName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = formattedDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "-${claim.pointsSpent} PTS",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}
