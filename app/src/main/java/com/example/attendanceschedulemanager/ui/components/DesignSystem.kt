package com.example.attendanceschedulemanager.ui.components

import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.attendanceschedulemanager.ui.theme.LocalSemanticColors
import com.example.attendanceschedulemanager.ui.theme.LocalThemeController
import com.example.attendanceschedulemanager.ui.theme.LocalUserProfileController
import com.example.attendanceschedulemanager.ui.theme.ThemeMode
import java.util.*

@Composable
fun rememberTopHeaderPadding(): Dp {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    return statusBarHeight + 68.dp
}

@Composable
fun AppTopHeader(
    modifier: Modifier = Modifier,
    title: String = "ClassTrack",
    subtitle: String = "Attendance & Schedule",
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val themeController = LocalThemeController.current
    val userProfileController = LocalUserProfileController.current
    val profile = userProfileController.profile
    val isDark = themeController.themeMode == ThemeMode.DARK

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo + Title + Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // App Logo Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right Action Controls: Notification + Theme Switcher + Profile DP
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(38.dp)
                        .bouncyClickable { onNotificationClick() }
                ) {
                    Box {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                                .align(Alignment.TopEnd)
                        )
                    }
                }

                // Theme Mode Switch Button
                IconButton(
                    onClick = { themeController.toggleTheme() },
                    modifier = Modifier
                        .size(38.dp)
                        .bouncyClickable { themeController.toggleTheme() }
                ) {
                    Icon(
                        if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                        contentDescription = "Switch Theme",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // User DP
                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .bouncyClickable { onProfileClick() },
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    if (profile.profilePictureUri != null) {
                        AsyncImage(
                            model = profile.profilePictureUri,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayPickerStrip(
    selectedDayIndex: Int,
    onSelectDay: (Int) -> Unit,
    activeDaysWithClasses: Set<Int> = emptySet(),
    modifier: Modifier = Modifier
) {
    val dayLabels = remember { listOf("M", "T", "W", "T", "F", "S", "S") }
    // Use a reference week for dates
    val baseCal = remember {
        Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in 0 until 7) {
            val isSelected = (i + 1) == selectedDayIndex
            val hasClasses = activeDaysWithClasses.contains(i + 1)
            val dateNumber = remember(baseCal, i) {
                val dayCal = (baseCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, i) }
                dayCal.get(Calendar.DAY_OF_MONTH)
            }

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow,
                animationSpec = tween(300),
                label = "DayPickerBg"
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(300),
                label = "DayPickerText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .bouncyClickable { onSelectDay(i + 1) }
                    .padding(vertical = 8.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = dayLabels[i],
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = if (isSelected) 0.9f else 0.7f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = "$dateNumber",
                        style = MaterialTheme.typography.titleSmall,
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    // Status dot
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.secondary
                                    hasClasses -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceRingIndicator(
    percentage: Float,
    target: Float = 0.75f,
    size: Dp = 100.dp,
    strokeWidth: Dp = 9.dp,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalSemanticColors.current
    val progressColor = when {
        percentage >= target -> semanticColors.safe
        percentage >= target - 0.05f -> semanticColors.caution
        else -> semanticColors.critical
    }

    val animatedProgress by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        label = "AttendanceRingAnimation"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(size.toPx() - strokePx, size.toPx() - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Progress
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format(Locale.US, "%.1f%%", percentage * 100f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                color = progressColor
            )
            Text(
                text = "Goal ${(target * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusPillChip(
    status: String,
    modifier: Modifier = Modifier,
    text: String
) {
    val semanticColors = LocalSemanticColors.current
    val (bgColor, textColor) = when (status.lowercase()) {
        "present", "attended" -> semanticColors.safeContainer to semanticColors.onSafeContainer
        "absent", "missed" -> semanticColors.criticalContainer to semanticColors.onCriticalContainer
        "excused", "cancelled", "off day" -> semanticColors.cautionContainer to semanticColors.onCautionContainer
        else -> MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = bgColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            val icon = when (status.lowercase()) {
                "present", "attended" -> Icons.Default.CheckCircle
                "absent", "missed" -> Icons.Default.Cancel
                "excused", "off day" -> Icons.Default.EventBusy
                else -> Icons.Default.Info
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun SegmentedHealthBar(
    percentage: Float,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalSemanticColors.current
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "HealthBarAnimation"
    )
    
    val safePct = animatedPercentage
    val remainingPct = 1f - safePct

    val fillColor = if (percentage >= 0.75f) semanticColors.safe else semanticColors.critical

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(1.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(if (safePct > 0f) safePct else 0.001f)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(fillColor)
        )
        if (remainingPct > 0f) {
            Box(
                modifier = Modifier
                    .weight(remainingPct)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            )
        }
    }
}



fun Modifier.bouncyClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "BouncyScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null, 
            enabled = enabled,
            onClick = onClick
        )
}

fun Modifier.liquidGlass(shape: Shape): Modifier = composed {
    clip(shape)
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.06f)
                    )
                )
            )
        }
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.30f),
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.12f)
                )
            ),
            shape = shape
        )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderStroke = BorderStroke(width = 1.dp, color = borderColor)

    if (onClick != null) {
        Surface(
            modifier = modifier
                .liquidGlass(shape)
                .bouncyClickable { onClick() },
            shape = shape,
            color = containerColor,
            border = borderStroke,
            tonalElevation = 1.dp,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    } else {
        Surface(
            modifier = modifier.liquidGlass(shape),
            shape = shape,
            color = containerColor,
            border = borderStroke,
            tonalElevation = 1.dp,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}


