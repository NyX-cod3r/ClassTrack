package com.example.attendanceschedulemanager.ui.screens.timetable

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.attendanceschedulemanager.data.entity.Subject
import com.example.attendanceschedulemanager.ui.components.DayPickerStrip
import com.example.attendanceschedulemanager.ui.components.bouncyClickable
import com.example.attendanceschedulemanager.ui.components.rememberTopHeaderPadding
import com.example.attendanceschedulemanager.ui.theme.LocalSemanticColors
import com.example.attendanceschedulemanager.viewmodel.CalendarDayStatus
import com.example.attendanceschedulemanager.viewmodel.DayDiagnosticOverview
import com.example.attendanceschedulemanager.viewmodel.SlotWithDetails
import com.example.attendanceschedulemanager.viewmodel.TimetableViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = hiltViewModel(),
    onProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }
    var showAddColumnDialog by remember { mutableStateOf(false) }
    var editingCell by remember { mutableStateOf<Pair<Int, String>?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (!isEditMode) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.setAddSlotOpen(true) },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 68.dp)
                        .bouncyClickable { viewModel.setAddSlotOpen(true) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Class Slot", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { _ ->
        val topHeaderPadding = rememberTopHeaderPadding()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topHeaderPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Page Header Title & Edit Mode Action Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isEditMode) "Timetable Editor" else "Timetable",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isEditMode) "Define time columns & fill slot details" else "Weekly class schedule",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { isEditMode = !isEditMode },
                    modifier = Modifier.bouncyClickable { isEditMode = !isEditMode },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (isEditMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isEditMode) "Done" else "Edit")
                }
            }

            AnimatedContent(
                targetState = isEditMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "EditModeTransition"
            ) { editMode ->
                if (editMode) {
                    TimetableGridEditor(
                        timeColumns = uiState.timeColumns,
                        slots = uiState.slots,
                        onAddColumn = { showAddColumnDialog = true },
                        onCellClick = { day, timing -> editingCell = day to timing },
                        onRemoveColumn = { viewModel.removeTimeColumn(it) }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
                    ) {
                        if (uiState.isCalendarMode) {
                            item {
                                MonthCalendarCard(
                                    days = uiState.calendarDays,
                                    onDayClick = { /* Click day */ }
                                )
                            }
                        } else {
                            item {
                                DayPickerStrip(
                                    selectedDayIndex = uiState.selectedDay,
                                    onSelectDay = { viewModel.selectDay(it) },
                                    activeDaysWithClasses = uiState.slots.map { it.slot.dayOfWeek }.toSet()
                                )
                            }

                            item {
                                DailyDiagnosisRibbon(overview = uiState.dayOverview)
                            }

                            val daySlots = uiState.slots.filter { it.slot.dayOfWeek == uiState.selectedDay }
                                .sortedBy { it.slot.startTime }

                            if (daySlots.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem()
                                            .animateContentSize(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            ),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(28.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Weekend,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "No classes scheduled for this day.",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Tap 'Edit' to build your timetable.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(
                                    items = daySlots,
                                    key = { it.slot.id }
                                ) { slotDetails ->
                                    TimetableTimelineCard(
                                        slotDetails = slotDetails,
                                        modifier = Modifier.animateItem(),
                                        onMarkPresent = { viewModel.markSlotPresent(slotDetails.slot) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddColumnDialog) {
            AddColumnDialog(
                onDismiss = { showAddColumnDialog = false },
                onAdd = { timing ->
                    viewModel.addTimeColumn(timing)
                    showAddColumnDialog = false
                }
            )
        }

        if (editingCell != null) {
            ModalBottomSheet(
                onDismissRequest = { editingCell = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                val day = editingCell!!.first
                val timing = editingCell!!.second
                val existingSlot = uiState.slots.find { it.slot.dayOfWeek == day && "${it.slot.startTime}-${it.slot.endTime}" == timing }

                CellEditSheet(
                    day = day,
                    timing = timing,
                    existingSlot = existingSlot,
                    onSave = { name, code, room, type ->
                        viewModel.addSlotWithSubject(name, code, day, timing, room, type, null)
                        editingCell = null
                    },
                    onDelete = {
                        existingSlot?.let { viewModel.deleteSlot(it.slot) }
                        editingCell = null
                    },
                    onClose = { editingCell = null }
                )
            }
        }

        if (uiState.isAddSlotOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.setAddSlotOpen(false) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                AddSlotSheetContent(
                    subjects = uiState.subjects,
                    initialDay = uiState.selectedDay,
                    onSaveWithSubject = { name, code, day, start, end, room, type ->
                        viewModel.addSlotWithSubject(name, code, day, "$start-$end", room, type, null)
                    },
                    onClose = { viewModel.setAddSlotOpen(false) }
                )
            }
        }
    }
}

@Composable
fun TimetableGridEditor(
    timeColumns: List<String>,
    slots: List<SlotWithDetails>,
    onAddColumn: () -> Unit,
    onCellClick: (Int, String) -> Unit,
    onRemoveColumn: (String) -> Unit
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.width(60.dp))
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(timeColumns) { timing ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .width(120.dp)
                            .clickable { onRemoveColumn(timing) }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = timing,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onAddColumn() },
                        modifier = Modifier
                            .width(120.dp)
                            .bouncyClickable { onAddColumn() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Time", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(
                items = days,
                key = { it }
            ) { dayName ->
                val dayIdx = days.indexOf(dayName)
                val dayOfWeek = dayIdx + 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(80.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = days[dayIdx],
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = timeColumns,
                            key = { it }
                        ) { timing ->
                            val slot = remember(slots, dayOfWeek, timing) {
                                slots.find { it.slot.dayOfWeek == dayOfWeek && "${it.slot.startTime}-${it.slot.endTime}" == timing }
                            }
                            GridCell(
                                slot = slot,
                                onClick = { onCellClick(dayOfWeek, timing) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridCell(
    slot: SlotWithDetails?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp)
            .bouncyClickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (slot != null) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        border = if (slot == null) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        if (slot != null) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = slot.subject.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = slot.slot.room ?: "TBD",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        } else {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AddColumnDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var startTime by remember { mutableStateOf("0900") }
    var endTime by remember { mutableStateOf("1030") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Time Slot Column") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { if (it.length <= 4) startTime = it },
                    label = { Text("Start Time (HHmm)") },
                    visualTransformation = TimeVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { if (it.length <= 4) endTime = it },
                    label = { Text("End Time (HHmm)") },
                    visualTransformation = TimeVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val s = formatTime(startTime)
                val e = formatTime(endTime)
                onAdd("$s-$e") 
            }) {
                Text("Add Column")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

class TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0, 4) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += ":"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

private fun formatTime(raw: String): String {
    val padded = raw.padEnd(4, '0')
    return "${padded.substring(0, 2)}:${padded.substring(2, 4)}"
}

@Composable
fun CellEditSheet(
    day: Int,
    timing: String,
    existingSlot: SlotWithDetails?,
    onSave: (String, String, String?, String) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf(existingSlot?.subject?.name ?: "") }
    var code by remember { mutableStateOf(existingSlot?.subject?.code ?: "") }
    var room by remember { mutableStateOf(existingSlot?.slot?.room ?: "") }
    var type by remember { mutableStateOf(existingSlot?.slot?.sessionType ?: "Lecture") }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Edit Slot: ${days[day-1]}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Time: $timing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.bouncyClickable { onClose() }
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Subject Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Subject Code") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Room / Location") },
            modifier = Modifier.fillMaxWidth()
        )

        val types = listOf("Lecture", "Practical-Lab", "Tutorial")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { t ->
                FilterChip(
                    selected = type == t,
                    onClick = { type = t },
                    label = { Text(t) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (existingSlot != null) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }
            Button(
                onClick = { onSave(name, code, room.ifEmpty { null }, type) },
                modifier = Modifier
                    .weight(2f)
                    .bouncyClickable(enabled = name.isNotBlank() && code.isNotBlank()) {
                        onSave(name, code, room.ifEmpty { null }, type)
                    },
                enabled = name.isNotBlank() && code.isNotBlank()
            ) {
                Text("Save Slot")
            }
        }
    }
}

@Composable
fun MonthCalendarCard(
    days: List<CalendarDayStatus>,
    onDayClick: (CalendarDayStatus) -> Unit
) {
    val semanticColors = LocalSemanticColors.current
    val weekHeader = listOf("M", "T", "W", "T", "F", "S", "S")
    val monthName = remember {
        SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$monthName Calendar Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                weekHeader.forEach { dayLetter ->
                    Text(
                        text = dayLetter,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(days) { dayStatus ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (dayStatus.isToday) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)
                            )
                            .clickable { onDayClick(dayStatus) }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dayStatus.dayOfMonth}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (dayStatus.isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (dayStatus.isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            if (dayStatus.hasClasses) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                dayStatus.hasAbsence -> semanticColors.critical
                                                dayStatus.allPresent -> semanticColors.safe
                                                else -> MaterialTheme.colorScheme.outlineVariant
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(semanticColors.safe))
                    Text("Attended", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(semanticColors.critical))
                    Text("Has Absence", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant))
                    Text("Scheduled", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun DailyDiagnosisRibbon(overview: DayDiagnosticOverview) {
    val semanticColors = LocalSemanticColors.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = overview.dayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${overview.lectureCount} Lectures (${overview.totalHours} hrs total) · ${overview.labCount} Lab",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = semanticColors.safe,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${overview.targetPercentage}% Target",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun TimetableTimelineCard(
    slotDetails: SlotWithDetails,
    modifier: Modifier = Modifier,
    onMarkPresent: () -> Unit
) {
    val semanticColors = LocalSemanticColors.current
    var isNotified by remember { mutableStateOf(slotDetails.isReminderSet) }
    var isLogged by remember { mutableStateOf(slotDetails.isMarkedToday) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(48.dp)
                .padding(top = 4.dp)
        ) {
            Text(
                text = slotDetails.slot.startTime,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${slotDetails.durationMinutes}m",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier
                .weight(1f)
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
            Box {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .background(
                            if (slotDetails.shortageRecoveryNote != null) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                )

                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .padding(start = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = slotDetails.subject.code,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = "${slotDetails.subject.credits} Credits",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = slotDetails.slot.sessionType,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = slotDetails.subject.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.MeetingRoom,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${slotDetails.slot.room ?: "Location Not Set"} · ${slotDetails.slot.notes ?: slotDetails.subject.professor ?: "Instructor"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Insights,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "History: ${slotDetails.rollingHistoryAttended}/${slotDetails.rollingHistoryTotal} Attended",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = String.format(Locale.US, "%.1f%%", slotDetails.historyPercentage * 100f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    slotDetails.shortageRecoveryNote?.let { note ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.clickable { isNotified = !isNotified }
                        ) {
                            Switch(
                                checked = isNotified,
                                onCheckedChange = { isNotified = it },
                                modifier = Modifier.height(24.dp)
                            )
                            Text(
                                text = "Notify 15m prior",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                onMarkPresent()
                                isLogged = true
                            },
                            modifier = Modifier.bouncyClickable {
                                onMarkPresent()
                                isLogged = true
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLogged) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = if (isLogged) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                if (isLogged) Icons.Default.TaskAlt else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isLogged) MaterialTheme.colorScheme.onPrimaryContainer else semanticColors.safe,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLogged) "Logged" else "Mark Present",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddSlotSheetContent(
    subjects: List<Subject>,
    initialDay: Int,
    onSaveWithSubject: (String, String, Int, String, String, String?, String) -> Unit,
    onClose: () -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var subjectCode by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: -1L) }
    var isNewSubject by remember { mutableStateOf(subjects.isEmpty()) }
    var selectedDay by remember { mutableStateOf(initialDay) }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:30") }
    var room by remember { mutableStateOf("") }
    var sessionType by remember { mutableStateOf("Lecture") }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val types = listOf("Lecture", "Practical-Lab", "Tutorial")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "New Class Slot",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.bouncyClickable { onClose() }
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isNewSubject) "Course Details" else "Select Course",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (subjects.isNotEmpty()) {
                TextButton(
                    onClick = { isNewSubject = !isNewSubject },
                    modifier = Modifier.bouncyClickable { isNewSubject = !isNewSubject }
                ) {
                    Text(if (isNewSubject) "Select Existing" else "Add New")
                }
            }
        }

        if (isNewSubject) {
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Subject Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subjectCode,
                onValueChange = { subjectCode = it },
                label = { Text("Subject Code") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(subjects) { subj ->
                    FilterChip(
                        selected = subj.id == selectedSubjectId,
                        onClick = { selectedSubjectId = subj.id },
                        label = { Text(subj.name) }
                    )
                }
            }
        }

        Text("Day of Week", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(days.indices.toList()) { idx ->
                FilterChip(
                    selected = (idx + 1) == selectedDay,
                    onClick = { selectedDay = idx + 1 },
                    label = { Text(days[idx]) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = startTime.replace(":", ""),
                onValueChange = { if (it.length <= 4) startTime = it },
                label = { Text("Start Time") },
                modifier = Modifier.weight(1f),
                visualTransformation = TimeVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = endTime.replace(":", ""),
                onValueChange = { if (it.length <= 4) endTime = it },
                label = { Text("End Time") },
                modifier = Modifier.weight(1f),
                visualTransformation = TimeVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Room / Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Type", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { t ->
                FilterChip(
                    selected = t == sessionType,
                    onClick = { sessionType = t },
                    label = { Text(t) }
                )
            }
        }

        Button(
            onClick = {
                val finalName = if (isNewSubject) subjectName else subjects.find { it.id == selectedSubjectId }?.name ?: ""
                val finalCode = if (isNewSubject) subjectCode else subjects.find { it.id == selectedSubjectId }?.code ?: ""
                val s = formatTime(startTime.replace(":", ""))
                val e = formatTime(endTime.replace(":", ""))
                onSaveWithSubject(finalName, finalCode, selectedDay, s, e, room.ifEmpty { null }, sessionType)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .bouncyClickable(enabled = (isNewSubject && subjectName.isNotBlank() && subjectCode.isNotBlank()) || (!isNewSubject && selectedSubjectId != -1L)) {
                    val finalName = if (isNewSubject) subjectName else subjects.find { it.id == selectedSubjectId }?.name ?: ""
                    val finalCode = if (isNewSubject) subjectCode else subjects.find { it.id == selectedSubjectId }?.code ?: ""
                    val s = formatTime(startTime.replace(":", ""))
                    val e = formatTime(endTime.replace(":", ""))
                    onSaveWithSubject(finalName, finalCode, selectedDay, s, e, room.ifEmpty { null }, sessionType)
                },
            enabled = (isNewSubject && subjectName.isNotBlank() && subjectCode.isNotBlank()) || (!isNewSubject && selectedSubjectId != -1L),
            shape = CircleShape
        ) {
            Text("Save to Timetable", fontWeight = FontWeight.Bold)
        }
    }
}
