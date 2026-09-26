package com.example.attendanceschedulemanager.ui.screens.today

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.attendanceschedulemanager.data.entity.AttendanceStatus
import com.example.attendanceschedulemanager.ui.components.*
import com.example.attendanceschedulemanager.ui.components.bouncyClickable
import com.example.attendanceschedulemanager.ui.theme.LocalSemanticColors
import com.example.attendanceschedulemanager.viewmodel.CriticalShortageInfo
import com.example.attendanceschedulemanager.viewmodel.NextClassInfo
import com.example.attendanceschedulemanager.viewmodel.SessionWithSubject
import com.example.attendanceschedulemanager.viewmodel.TodayViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel(),
    onProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val semanticColors = LocalSemanticColors.current

    val (currentDayName, currentDateStr) = remember {
        val cal = Calendar.getInstance()
        val dayNameFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
        dayNameFormat.format(cal.time) to dateFormat.format(cal.time)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setQuickLogOpen(true) },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 68.dp)
                    .bouncyClickable { viewModel.setQuickLogOpen(true) },
                icon = { Icon(Icons.Default.AddTask, contentDescription = null) },
                text = { Text("Quick Log", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = rememberTopHeaderPadding(), bottom = 100.dp)
        ) {
            // 1. Page Header Title & Target badge
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "Current Term",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Text(
                            text = "$currentDayName, $currentDateStr",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Target badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(semanticColors.safe)
                            )
                            Text(
                                text = "Target: 75% Min",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 2. 7-Day Day Picker Strip
            item {
                DayPickerStrip(
                    selectedDayIndex = uiState.selectedDayIndex,
                    onSelectDay = { viewModel.selectDay(it) },
                    activeDaysWithClasses = uiState.activeDaysWithClasses
                )
            }

            // 3. Aggregate Standing Bento Card
            item {
                val emoji = remember(uiState.aggregateAttendance) { getAttendanceEmoji(uiState.aggregateAttendance) }
                AggregateStandingBentoCard(
                    percentage = uiState.aggregateAttendance,
                    totalAttended = uiState.totalAttended,
                    totalConducted = uiState.totalConducted,
                    bufferClasses = uiState.bufferClasses,
                    isBufferPositive = uiState.isBufferPositive,
                    emoji = emoji
                )
            }

            // 4. Critical Shortage Alert Banner (conditional)
            uiState.criticalShortage?.let { shortage ->
                item {
                    CriticalShortageBanner(
                        shortage = shortage
                    )
                }
            }

            // 5. Next Upcoming Class Banner
            uiState.nextClass?.let { next ->
                item {
                    NextClassBanner(
                        next = next
                    )
                }
            }

            // 6. Today's Lectures Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Today's Lectures",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = "${uiState.todaySessions.size} Sessions",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 7. Lecture Cards Stream
            if (uiState.todaySessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Outlined.EventBusy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No classes scheduled for this day!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Use this free time to review materials in the Vault.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = uiState.todaySessions,
                    key = { "${it.slot.id}_${it.record?.id ?: "no_rec"}" }
                ) { session ->
                    TodayLectureCard(
                        session = session,
                        modifier = Modifier.animateItem(),
                        onMark = { status ->
                            viewModel.markAttendance(session.subject.id, session.slot.id, status)
                        }
                    )
                }
            }
        }

        // Quick Log Modal Bottom Sheet
        if (uiState.isQuickLogOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.setQuickLogOpen(false) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                QuickLogSheetContent(
                    subjects = uiState.subjects,
                    todaySessions = uiState.todaySessions,
                    onLog = { subjectId, status ->
                        viewModel.quickLogAttendance(subjectId, status)
                    },
                    onAddSubjectAndLog = { name, code, status ->
                        viewModel.addSubjectAndLog(name, code, status)
                    },
                    onClose = { viewModel.setQuickLogOpen(false) }
                )
            }
        }
    }
}

fun getAttendanceEmoji(percentage: Float): String {
    return when {
        percentage >= 0.90f -> "😎" // Elite
        percentage >= 0.80f -> "😊" // Good
        percentage >= 0.75f -> "😐" // Borderline
        percentage >= 0.65f -> "😟" // Warning
        percentage >= 0.50f -> "😰" // Risk
        else -> "💀" // Critical
    }
}

@Composable
fun AggregateStandingBentoCard(
    percentage: Float,
    totalAttended: Int,
    totalConducted: Int,
    bufferClasses: Int,
    isBufferPositive: Boolean,
    emoji: String,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalSemanticColors.current
    val isSafe = percentage >= 0.75f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .liquidGlass(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // ... existing row content ...
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "AGGREGATE STANDING",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = CircleShape,
                            color = if (isSafe) semanticColors.safeContainer else semanticColors.criticalContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    if (isSafe) Icons.Default.Verified else Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = if (isSafe) semanticColors.onSafeContainer else semanticColors.onCriticalContainer
                                )
                                Text(
                                    text = if (isSafe) "Safe Zone" else "Risk State",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSafe) semanticColors.onSafeContainer else semanticColors.onCriticalContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%.1f%%", percentage * 100f),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                            color = if (isSafe) MaterialTheme.colorScheme.primary else semanticColors.critical
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "/ 75.0% min required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                // Dynamic Emoji based on attendance
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 44.sp),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Health Bar
            SegmentedHealthBar(percentage = percentage)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isBufferPositive) semanticColors.safe else semanticColors.critical
                    )
                    Text(
                        text = if (isBufferPositive) "+$bufferClasses classes buffer before risk" else "$bufferClasses classes shortfall to recover",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isBufferPositive) semanticColors.safe else semanticColors.critical
                    )
                }

                Text(
                    text = "$totalAttended attended / $totalConducted total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CriticalShortageBanner(
    shortage: CriticalShortageInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Critical Shortage: ${shortage.subject.name}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f%%", shortage.percentage * 100f),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = "${shortage.shortfallClasses} lecture below required line. Attend today's class to recover safe standing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun NextClassBanner(
    next: NextClassInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "NEXT CLASS • IN ${next.minutesRemaining} MIN",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${next.session.subject.name} in ${next.session.slot.room ?: "Hall"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.AlarmOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Alert Set",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TodayLectureCard(
    session: SessionWithSubject,
    modifier: Modifier = Modifier,
    onMark: (AttendanceStatus) -> Unit
) {
    val semanticColors = LocalSemanticColors.current
    val isShortageRisk = session.subjectPercentage < 0.75f && session.subjectConducted > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .liquidGlass(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Time & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${session.slot.startTime} – ${session.slot.endTime}",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Text(
                        text = session.slot.sessionType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (session.record != null) {
                    val statusLabel = when (session.record.status) {
                        AttendanceStatus.PRESENT -> "Attended"
                        AttendanceStatus.ABSENT -> "Missed"
                        AttendanceStatus.EXCUSED -> "Excused"
                        AttendanceStatus.CANCELLED -> "Off Day"
                    }
                    StatusPillChip(
                        status = statusLabel,
                        text = statusLabel
                    )
                } else if (isShortageRisk) {
                    Surface(
                        shape = CircleShape,
                        color = semanticColors.criticalContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = semanticColors.onCriticalContainer
                            )
                            Text(
                                text = "Risk State",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = semanticColors.onCriticalContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subject title & Details
            Text(
                text = session.subject.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                session.subject.professor?.let { prof ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = prof,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(text = "•", color = MaterialTheme.colorScheme.outlineVariant)
                }

                session.slot.room?.let { room ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            Icons.Default.MeetingRoom,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = room,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subject Metric Running Footer
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Course Metric: ${session.subjectAttended}/${session.subjectConducted} classes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f%% %s", session.subjectPercentage * 100f, if (session.subjectPercentage >= 0.75f) "Safe" else "Risk"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (session.subjectPercentage >= 0.75f) semanticColors.safe else semanticColors.critical
                    )
                }
            }

            // Quick Mark Action Controls
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onMark(AttendanceStatus.PRESENT) },
                    modifier = Modifier
                        .weight(1f)
                        .bouncyClickable { onMark(AttendanceStatus.PRESENT) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (session.record?.status == AttendanceStatus.PRESENT) semanticColors.safe else MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (session.record?.status == AttendanceStatus.PRESENT) "Present ✓" else "Present (+1)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                FilledTonalButton(
                    onClick = { onMark(AttendanceStatus.ABSENT) },
                    modifier = Modifier
                        .weight(1f)
                        .bouncyClickable { onMark(AttendanceStatus.ABSENT) },
                    shape = CircleShape,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (session.record?.status == AttendanceStatus.ABSENT) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = if (session.record?.status == AttendanceStatus.ABSENT) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (session.record?.status == AttendanceStatus.ABSENT) "Missed ✕" else "Missed",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { onMark(AttendanceStatus.CANCELLED) },
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Off",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun QuickLogSheetContent(
    subjects: List<com.example.attendanceschedulemanager.data.entity.Subject>,
    todaySessions: List<SessionWithSubject>,
    onLog: (Long, AttendanceStatus) -> Unit,
    onAddSubjectAndLog: (String, String, AttendanceStatus) -> Unit,
    onClose: () -> Unit
) {
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: -1L) }
    var newSubjectName by remember { mutableStateOf("") }
    var newSubjectCode by remember { mutableStateOf("") }
    var isAddingNew by remember { mutableStateOf(subjects.isEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Record Attendance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Instant session update",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isAddingNew) "New Course Details" else "Select Course",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (subjects.isNotEmpty()) {
                TextButton(
                    onClick = { isAddingNew = !isAddingNew },
                    modifier = Modifier.bouncyClickable { isAddingNew = !isAddingNew }
                ) {
                    Text(if (isAddingNew) "Select Existing" else "Add New Subject")
                }
            }
        }

        if (isAddingNew) {
            OutlinedTextField(
                value = newSubjectName,
                onValueChange = { newSubjectName = it },
                label = { Text("Subject Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newSubjectCode,
                onValueChange = { newSubjectCode = it },
                label = { Text("Subject Code") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                subjects.forEach { subject ->
                    val isSelected = subject.id == selectedSubjectId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSubjectId = subject.id }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = subject.code,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (isAddingNew) onAddSubjectAndLog(newSubjectName, newSubjectCode, AttendanceStatus.PRESENT)
                    else onLog(selectedSubjectId, AttendanceStatus.PRESENT)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .bouncyClickable(enabled = if (isAddingNew) newSubjectName.isNotBlank() && newSubjectCode.isNotBlank() else selectedSubjectId != -1L) {
                        if (isAddingNew) onAddSubjectAndLog(newSubjectName, newSubjectCode, AttendanceStatus.PRESENT)
                        else onLog(selectedSubjectId, AttendanceStatus.PRESENT)
                    },
                enabled = if (isAddingNew) newSubjectName.isNotBlank() && newSubjectCode.isNotBlank() else selectedSubjectId != -1L,
                shape = CircleShape
            ) {
                Text("Present", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (isAddingNew) onAddSubjectAndLog(newSubjectName, newSubjectCode, AttendanceStatus.ABSENT)
                    else onLog(selectedSubjectId, AttendanceStatus.ABSENT)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .bouncyClickable(enabled = if (isAddingNew) newSubjectName.isNotBlank() && newSubjectCode.isNotBlank() else selectedSubjectId != -1L) {
                        if (isAddingNew) onAddSubjectAndLog(newSubjectName, newSubjectCode, AttendanceStatus.ABSENT)
                        else onLog(selectedSubjectId, AttendanceStatus.ABSENT)
                    },
                enabled = if (isAddingNew) newSubjectName.isNotBlank() && newSubjectCode.isNotBlank() else selectedSubjectId != -1L,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Absent", fontWeight = FontWeight.Bold)
            }
        }
    }
}

