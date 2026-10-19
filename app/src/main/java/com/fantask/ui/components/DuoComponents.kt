package com.fantask.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantask.theme.DuoBlue
import com.fantask.theme.DuoBlueShadow
import com.fantask.theme.DuoGreen
import com.fantask.theme.DuoGreenShadow
import java.util.Locale

/**
 * Duolingo 3D Tactile Button:
 * Features a solid bottom shadow bevel that pushes down when tapped.
 */
@Composable
fun DuoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = DuoGreen,
    shadowColor: Color = DuoGreenShadow,
    textColor: Color = Color.White,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pushOffset by animateDpAsState(targetValue = if (isPressed && enabled) 3.dp else 0.dp, label = "push")
    val bottomBorderHeight by animateDpAsState(targetValue = if (isPressed && enabled) 1.dp else 4.dp, label = "border")

    val activeColor = if (enabled) color else MaterialTheme.colorScheme.surfaceVariant
    val activeShadow = if (enabled) shadowColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val activeTextColor = if (enabled) textColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .height(height + 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Shadow base (gives the 3D bottom bevel)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(activeShadow)
        )

        // Raised button body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = pushOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(activeColor),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = activeTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = activeTextColor,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Duolingo 3D Card Tile:
 * Rounded corners with 2dp border and bottom depth.
 */
@Composable
fun DuoCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    cornerRadius: Dp = 18.dp,
    bottomDepth: Dp = 3.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(borderColor)
            .padding(bottom = bottomDepth)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(cornerRadius))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

/**
 * Duolingo Top-Bar Stat Badge (Streak flame, Gems/Points)
 */
@Composable
fun DuoStatBadge(
    icon: ImageVector,
    iconTint: Color,
    valueText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = valueText,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 12-Hour AM/PM Time Picker Modal Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuoTimePickerDialog(
    title: String,
    initialHour: Int = 8,
    initialMinute: Int = 0,
    onTimeSelected: (String) -> Unit, // returns formatted e.g. "08:00 AM"
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false // Explicit 12-hour mode with AM/PM toggle!
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = DuoBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timePickerState)
            }
        },
        confirmButton = {
            DuoButton(
                text = "Set Time",
                onClick = {
                    val hour24 = timePickerState.hour
                    val minute = timePickerState.minute
                    val isPm = hour24 >= 12
                    val hour12 = when {
                        hour24 == 0 -> 12
                        hour24 > 12 -> hour24 - 12
                        else -> hour24
                    }
                    val amPm = if (isPm) "PM" else "AM"
                    val formatted = String.format(Locale.US, "%02d:%02d %s", hour12, minute, amPm)
                    onTimeSelected(formatted)
                },
                color = DuoBlue,
                shadowColor = DuoBlueShadow,
                height = 42.dp,
                modifier = Modifier.width(130.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    )
}
