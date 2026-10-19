package com.fantask.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantask.data.pref.UserPreferences
import com.fantask.theme.DuoBlue
import com.fantask.theme.DuoBlueShadow
import com.fantask.theme.DuoGreen
import com.fantask.theme.DuoGreenShadow
import com.fantask.theme.DuoPurple
import com.fantask.theme.DuoPurpleShadow
import com.fantask.ui.components.DuoButton
import com.fantask.ui.components.DuoCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onThemeChange: (String) -> Unit,
    onLeadTimeChange: (Int) -> Unit,
    onTestNotification: () -> Unit,
    onExportBackup: (Uri) -> Unit,
    onImportBackup: (Uri) -> Unit,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            onExportBackup(uri)
            scope.launch { snackbarHostState.showSnackbar("Backup exported successfully!") }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onImportBackup(uri)
            scope.launch { snackbarHostState.showSnackbar("Backup restored successfully!") }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SETTINGS", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section
            item {
                DuoCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "APPEARANCE & THEME", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose your preferred Duolingo style interface mode.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val options = listOf(
                        Triple("SYSTEM", "System Default", Icons.Default.SettingsBrightness),
                        Triple("LIGHT", "Duolingo Light Mode", Icons.Default.Brightness7),
                        Triple("DARK", "Duolingo Night Mode", Icons.Default.Brightness4)
                    )

                    options.forEach { (mode, label, icon) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onThemeChange(mode) }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = preferences.themeMode.equals(mode, ignoreCase = true),
                                onClick = { onThemeChange(mode) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = DuoGreen)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Offline Notification Section
            item {
                DuoCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "OFFLINE REMINDERS & ALARMS", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Task due dates and daily scheduled windows (e.g. 8:00 AM - 10:00 AM) run offline exact alarms.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Reminder Advance Time:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0 to "At Due Time", 15 to "15m before", 60 to "1h before").forEach { (mins, label) ->
                            FilterChip(
                                selected = preferences.leadTimeMinutes == mins,
                                onClick = { onLeadTimeChange(mins) },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DuoButton(
                        text = "TEST OFFLINE NOTIFICATION",
                        onClick = {
                            onTestNotification()
                            scope.launch { snackbarHostState.showSnackbar("Test offline notification posted!") }
                        },
                        icon = Icons.Default.Notifications,
                        color = DuoBlue,
                        shadowColor = DuoBlueShadow,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Local Backup & Restore Section (Offline JSON)
            item {
                DuoCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "OFFLINE BACKUP & RESTORE", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Export tasks, sub-task requirements, streaks, points, rewards, and custom badges to a local JSON file.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DuoButton(
                            text = "EXPORT",
                            onClick = { exportLauncher.launch("fantask_backup.json") },
                            icon = Icons.Default.Download,
                            color = DuoGreen,
                            shadowColor = DuoGreenShadow,
                            modifier = Modifier.weight(1f)
                        )

                        DuoButton(
                            text = "IMPORT",
                            onClick = { importLauncher.launch(arrayOf("application/json")) },
                            icon = Icons.Default.Upload,
                            color = DuoPurple,
                            shadowColor = DuoPurpleShadow,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // About Section
            item {
                DuoCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.OfflinePin, contentDescription = null, tint = DuoGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "100% PRIVATE & OFFLINE", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fantask has zero network code and zero internet permission. Your data never leaves your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DuoButton(
                        text = "RE-OPEN FIRST-RUN SETUP",
                        onClick = { showResetDialog = true },
                        icon = Icons.Default.RestartAlt,
                        color = DuoBlue,
                        shadowColor = DuoBlueShadow,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Re-run Setup", fontWeight = FontWeight.Black) },
                text = { Text("Would you like to revisit the initial setup screen? Your existing tasks and points will remain safe.") },
                confirmButton = {
                    DuoButton(
                        text = "RE-OPEN",
                        onClick = {
                            showResetDialog = false
                            onResetOnboarding()
                        },
                        color = DuoBlue,
                        shadowColor = DuoBlueShadow,
                        modifier = Modifier.width(110.dp),
                        height = 40.dp
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
