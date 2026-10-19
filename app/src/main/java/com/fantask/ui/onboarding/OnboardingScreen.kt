package com.fantask.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantask.theme.DuoBlue
import com.fantask.theme.DuoFlame
import com.fantask.theme.DuoGold
import com.fantask.theme.DuoGreen
import com.fantask.theme.DuoGreenShadow
import com.fantask.ui.components.DuoButton
import com.fantask.ui.components.DuoCard

@Composable
fun OnboardingScreen(
    currentTheme: String,
    onThemeSelect: (String) -> Unit,
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    var notificationPermissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationPermissionGranted = isGranted
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Mascot Icon (Flame / Shield in Duolingo style)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(DuoFlame.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = DuoFlame,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "FANTASK",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = DuoGreen
                )

                Text(
                    text = "Gamified Offline Tasks & Daily Streaks",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DuoFeaturePill(icon = Icons.Default.OfflinePin, label = "100% Offline")
                    DuoFeaturePill(icon = Icons.Default.MonetizationOn, label = "Earn Points")
                    DuoFeaturePill(icon = Icons.Default.LocalFireDepartment, label = "Daily Streaks")
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Mandatory Theme Configuration
                Text(
                    text = "CHOOSE YOUR LOOK",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "Select your preferred appearance mode:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(14.dp))

                DuoThemeCard(
                    title = "Duolingo Light Mode",
                    description = "Crisp, bold, high-contrast daytime interface",
                    icon = Icons.Default.Brightness7,
                    isSelected = selectedTheme.equals("LIGHT", ignoreCase = true),
                    onClick = {
                        selectedTheme = "LIGHT"
                        onThemeSelect("LIGHT")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                DuoThemeCard(
                    title = "Duolingo Night Mode",
                    description = "Sleek slate dark aesthetic tailored for evening focus",
                    icon = Icons.Default.Brightness4,
                    isSelected = selectedTheme.equals("DARK", ignoreCase = true),
                    onClick = {
                        selectedTheme = "DARK"
                        onThemeSelect("DARK")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                DuoThemeCard(
                    title = "System Default",
                    description = "Matches your Android system theme automatically",
                    icon = Icons.Default.SettingsBrightness,
                    isSelected = selectedTheme.equals("SYSTEM", ignoreCase = true),
                    onClick = {
                        selectedTheme = "SYSTEM"
                        onThemeSelect("SYSTEM")
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Offline Notification Prompt
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    DuoCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = DuoBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Offline Alarms & Reminders", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "Allow alerts to run your daily scheduled task reminders offline.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (notificationPermissionGranted) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = DuoGreen)
                            } else {
                                OutlinedButton(
                                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                                ) {
                                    Text("Enable", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Duolingo 3D CTA Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                DuoButton(
                    text = "LET'S DO THIS!",
                    onClick = {
                        onThemeSelect(selectedTheme)
                        onCompleteOnboarding()
                    },
                    color = DuoGreen,
                    shadowColor = DuoGreenShadow,
                    height = 54.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DuoFeaturePill(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DuoGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DuoThemeCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DuoCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderColor = if (isSelected) DuoGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        backgroundColor = if (isSelected) DuoGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) DuoGreen else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = DuoGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
