package com.example.attendanceschedulemanager.ui.screens.analytics

import android.content.Intent
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.attendanceschedulemanager.data.entity.AttendanceStatus
import com.example.attendanceschedulemanager.ui.components.*
import com.example.attendanceschedulemanager.ui.theme.LocalSemanticColors
import com.example.attendanceschedulemanager.viewmodel.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showSubjectDropdown by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<RecordItemUi?>(null) }
    var editNoteText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.isLoading || uiState.selectedSubjectStats == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val stats = uiState.selectedSubjectStats!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = rememberTopHeaderPadding(), bottom = 100.dp)
                ) {
                    // 0. Page Header Title
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Analytics",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Attendance insights & subject statistics",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 1. Subject Switcher Bar
                    item {
                        SubjectSwitcherBar(
                            stats = stats,
                            allSubjects = uiState.allSubjectStats,
                            showDropdown = showSubjectDropdown,
                            onToggleDropdown = { showSubjectDropdown = !showSubjectDropdown },
                            onSelectSubject = {
                                viewModel.selectSubject(it)
                                showSubjectDropdown = false
                            }
                        )
                    }

                    // 2. Hero Analytics Metric Card
                    item {
                        HeroAnalyticsCard(stats = stats)
                    }

                    // 3. Attendance Safe-Zone Planner & Simulation
                    item {
                        SafeZonePlannerCard(
                            stats = stats,
                            simulationOffset = uiState.simulationOffset,
                            simulatedPercentage = uiState.simulatedPercentage,
                            onOffsetChange = { viewModel.setSimulationOffset(it) }
                        )
                    }

                    // 4. Activity Records Section Header & Filter Chips
                    item {
                        ActivityRecordsHeader(
                            totalCount = uiState.totalRecordsCount,
                            presentCount = uiState.presentCount,
                            absentCount = uiState.absentCount,
                            cancelledCount = uiState.cancelledCount,
                            activeFilter = uiState.selectedFilter,
                            onFilterChange = { viewModel.setFilter(it) }
                        )
                    }

                    // 5. Activity Records List
                    if (uiState.filteredRecords.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .animateContentSize(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            ) {
                                Box(
                                    modifier = Modifier.padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No records found for this filter",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = uiState.filteredRecords,
                            key = { it.record.id }
                        ) { item ->
                            ActivityRecordCard(
                                item = item,
                                modifier = Modifier.animateItem(),
                                onEditNote = {
                                    editingRecord = item
                                    editNoteText = item.record.note ?: ""
                                }
                            )
                        }
                    }

                    // 6. Export Attendance Report Button
                    item {
                        OutlinedButton(
                            onClick = {
                                val report = viewModel.generateShareReport()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "${stats.subject.name} Attendance Report")
                                    putExtra(Intent.EXTRA_TEXT, report)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Attendance Report"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Export Attendance Report (CSV/PDF)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Note Dialog
    if (editingRecord != null) {
        AlertDialog(
            onDismissRequest = { editingRecord = null },
            title = {
                Text(
                    text = "Edit Attendance Reason",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${editingRecord!!.title} (${editingRecord!!.timeFormatted})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editNoteText,
                        onValueChange = { editNoteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Medical leave, College event, Self recorded") },
                        label = { Text("Reason / Note") },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val rec = editingRecord
                        if (rec != null) {
                            viewModel.updateRecordNote(rec.record.id, editNoteText.trim().ifEmpty { null })
                        }
                        editingRecord = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingRecord = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SubjectSwitcherBar(
    stats: SubjectAnalyticsData,
    allSubjects: List<SubjectAnalyticsData>,
    showDropdown: Boolean,
    onToggleDropdown: () -> Unit,
    onSelectSubject: (Long) -> Unit
) {
    val semanticColors = LocalSemanticColors.current
    val isShortage = stats.percentage < stats.targetPercentage

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Surface(
                modifier = Modifier.clickable { onToggleDropdown() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isShortage) semanticColors.critical else semanticColors.safe)
                    )
                    Text(
                        text = "${stats.subject.name} (${stats.subject.code})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Switch Subject",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = onToggleDropdown
            ) {
                allSubjects.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.subject.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = item.subject.code,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                val pctColor = if (item.percentage < item.targetPercentage) {
                                    semanticColors.critical
                                } else {
                                    semanticColors.safe
                                }
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", item.percentage)}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = pctColor
                                )
                            }
                        },
                        onClick = { onSelectSubject(item.subject.id) }
                    )
                }
            }
        }

        FilledTonalButton(
            onClick = onToggleDropdown,
            shape = CircleShape,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Icon(
                Icons.Default.SwapHoriz,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Switch",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun HeroAnalyticsCard(stats: SubjectAnalyticsData) {
    val semanticColors = LocalSemanticColors.current
    val isShortage = stats.percentage < stats.targetPercentage

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.60f)
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f),
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ring indicator
                AttendanceRingIndicator(
                    percentage = stats.percentage / 100f,
                    target = stats.targetPercentage / 100f,
                    size = 110.dp,
                    strokeWidth = 10.dp
                )

                // Stats details
                Column(modifier = Modifier.weight(1f)) {
                    StatusPillChip(
                        status = if (isShortage) "missed" else "attended",
                        text = stats.status
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stats.statusDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isShortage) semanticColors.critical else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Micro numbers 3-col grid
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MicroStat(label = "Attended", value = stats.attended.toString())
                            MicroStat(label = "Conducted", value = stats.totalConducted.toString())
                            MicroStat(
                                label = "Missed",
                                value = stats.missed.toString(),
                                valueColor = if (stats.missed > 0) semanticColors.critical else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MicroStat(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SafeZonePlannerCard(
    stats: SubjectAnalyticsData,
    simulationOffset: Int,
    simulatedPercentage: Float,
    onOffsetChange: (Int) -> Unit
) {
    val semanticColors = LocalSemanticColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.60f)
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f),
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Text(
                        text = "Attendance Safe-Zone Planner",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Text(
                        text = "Smart Projection",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Projection slider container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulate next classes:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+$simulationOffset Classes",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = simulationOffset.toFloat(),
                        onValueChange = { onOffsetChange(it.toInt()) },
                        valueRange = 0f..15f,
                        steps = 14,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+5",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+10",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+15 classes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic insight 1: 75% target projection
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    val simFormatted = String.format(Locale.US, "%.1f", simulatedPercentage)
                    if (simulatedPercentage >= 75f) {
                        Text(
                            text = "Projected percentage: $simFormatted% — You've crossed the 75% threshold!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = "To reach 75%: Attend next ${stats.requiredFor75} classes consecutively. (Projected with +$simulationOffset: $simFormatted%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dynamic insight 2: 80% target
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "To reach 80%: Attend next ${stats.requiredFor80} consecutive classes without missing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dynamic insight 3: Bunk allowance
            val isCritical = stats.percentage < stats.targetPercentage
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = if (isCritical) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (isCritical) Icons.Default.Block else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isCritical) {
                            "Bunk allowance: 0 classes. Any further absence requires formal approval."
                        } else {
                            "Bunk allowance: ${stats.bunkAllowance} classes. You can safely miss without falling below 75%."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isCritical) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityRecordsHeader(
    totalCount: Int,
    presentCount: Int,
    absentCount: Int,
    cancelledCount: Int,
    activeFilter: AnalyticsFilter,
    onFilterChange: (AnalyticsFilter) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                    text = "Activity Records",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Text(
                        text = "$totalCount Logs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Timeline",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChipItem(
                label = "All ($totalCount)",
                isSelected = activeFilter == AnalyticsFilter.ALL,
                onClick = { onFilterChange(AnalyticsFilter.ALL) }
            )
            FilterChipItem(
                label = "Present ($presentCount)",
                isSelected = activeFilter == AnalyticsFilter.PRESENT,
                onClick = { onFilterChange(AnalyticsFilter.PRESENT) }
            )
            FilterChipItem(
                label = "Absent ($absentCount)",
                isSelected = activeFilter == AnalyticsFilter.ABSENT,
                onClick = { onFilterChange(AnalyticsFilter.ABSENT) }
            )
            FilterChipItem(
                label = "Cancelled ($cancelledCount)",
                isSelected = activeFilter == AnalyticsFilter.CANCELLED,
                onClick = { onFilterChange(AnalyticsFilter.CANCELLED) }
            )
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = CircleShape
    )
}

@Composable
fun ActivityRecordCard(
    item: RecordItemUi,
    modifier: Modifier = Modifier,
    onEditNote: () -> Unit
) {
    val semanticColors = LocalSemanticColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.60f)
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f),
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Status Icon in rounded box
                    val (icon, bgCol, tintCol) = when (item.record.status) {
                        AttendanceStatus.PRESENT -> Triple(
                            if (item.sessionType == "Practical-Lab") Icons.Default.Science else Icons.Default.Check,
                            MaterialTheme.colorScheme.secondaryContainer,
                            semanticColors.safe
                        )
                        AttendanceStatus.ABSENT -> Triple(
                            Icons.Default.Close,
                            MaterialTheme.colorScheme.errorContainer,
                            MaterialTheme.colorScheme.onErrorContainer
                        )
                        AttendanceStatus.CANCELLED, AttendanceStatus.EXCUSED -> Triple(
                            Icons.Default.EventBusy,
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(bgCol),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = tintCol,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            if (item.room != null) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                ) {
                                    Text(
                                        text = item.room,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = item.timeFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status chip
                val statusType = when (item.record.status) {
                    AttendanceStatus.PRESENT -> "attended"
                    AttendanceStatus.ABSENT -> "missed"
                    AttendanceStatus.CANCELLED, AttendanceStatus.EXCUSED -> "excused"
                }
                val statusLabel = when (item.record.status) {
                    AttendanceStatus.PRESENT -> "Present"
                    AttendanceStatus.ABSENT -> "Absent"
                    AttendanceStatus.CANCELLED, AttendanceStatus.EXCUSED -> "Excused"
                }
                StatusPillChip(status = statusType, text = statusLabel)
            }

            // Note strip or Add Note row
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            if (item.record.note?.contains("Medical", ignoreCase = true) == true) {
                                Icons.Default.MedicalServices
                            } else {
                                Icons.Default.EditNote
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (!item.record.note.isNullOrBlank()) item.record.note else "No reason recorded",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (!item.record.note.isNullOrBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = onEditNote,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Reason",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
