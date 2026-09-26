package com.example.attendanceschedulemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceschedulemanager.data.entity.AttendanceRecord
import com.example.attendanceschedulemanager.data.entity.AttendanceStatus
import com.example.attendanceschedulemanager.data.entity.Subject
import com.example.attendanceschedulemanager.data.entity.TimetableSlot
import com.example.attendanceschedulemanager.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class SlotWithDetails(
    val slot: TimetableSlot,
    val subject: Subject,
    val durationMinutes: Int,
    val rollingHistoryAttended: Int = 0,
    val rollingHistoryTotal: Int = 0,
    val historyPercentage: Float = 0f,
    val shortageRecoveryNote: String? = null,
    val isReminderSet: Boolean = true,
    val isMarkedToday: Boolean = false
)

data class DayDiagnosticOverview(
    val dayName: String,
    val lectureCount: Int,
    val totalHours: Float,
    val labCount: Int,
    val targetPercentage: Int = 75
)

data class CalendarDayStatus(
    val dayOfMonth: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val hasClasses: Boolean,
    val hasAbsence: Boolean,
    val allPresent: Boolean
)

data class TimetableUiState(
    val slots: List<SlotWithDetails> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val allSlotsCount: Int = 0,
    val selectedDay: Int = 1, // Default to Monday
    val dayOverview: DayDiagnosticOverview = DayDiagnosticOverview("Monday Overview", 0, 0f, 0, 75),
    val isCalendarMode: Boolean = false,
    val calendarDays: List<CalendarDayStatus> = emptyList(),
    val isAddSlotOpen: Boolean = false,
    val timeColumns: List<String> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val currentDayOfWeek: Int = run {
        val cal = Calendar.getInstance()
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    private val _selectedDay = MutableStateFlow(currentDayOfWeek)
    private val _isCalendarMode = MutableStateFlow(false)
    private val _isAddSlotOpen = MutableStateFlow(false)
    private val _timeColumns = MutableStateFlow<List<String>>(emptyList())
    private val _editingCell = MutableStateFlow<Pair<Int, String>?>(null) // dayOfWeek to timing

    private data class TimetableControls(
        val selectedDay: Int,
        val isCalendarMode: Boolean,
        val isAddSlotOpen: Boolean,
        val timeColumns: List<String>,
        val editingCell: Pair<Int, String>?
    )

    private val _timetableControls = combine(
        _selectedDay,
        _isCalendarMode,
        _isAddSlotOpen,
        _timeColumns,
        _editingCell
    ) { day, calMode, addOpen, columns, cell ->
        TimetableControls(day, calMode, addOpen, columns, cell)
    }

    val uiState: StateFlow<TimetableUiState> = combine(
        repository.allSubjects.distinctUntilChanged(),
        repository.allTimetableSlots.distinctUntilChanged(),
        repository.allAttendanceRecords.distinctUntilChanged(),
        _timetableControls
    ) { subjects, allSlots, records, controls ->
        with(Dispatchers.Default) {
            val selectedDay = controls.selectedDay
            val isCalendarMode = controls.isCalendarMode
            val isAddSlotOpen = controls.isAddSlotOpen
            
            // Use a Set to avoid redundant processing if timings haven't changed
            val allTimings = (allSlots.map { "${it.startTime}-${it.endTime}" } + controls.timeColumns).distinct().sortedBy { t ->
                val start = t.split("-").first().trim()
                try {
                    val parts = start.split(":")
                    parts[0].toInt() * 60 + parts[1].toInt()
                } catch (e: Exception) { 0 }
            }

            // Optimization: group records by subjectId for O(1) lookup
            val recordsBySubject = records.groupBy { it.subjectId }

            // Build detailed slots
            val detailedSlots = allSlots.map { slot ->
                val subject = subjects.find { it.id == slot.subjectId }
                    ?: Subject(name = "Unknown Course", code = "??", colorTag = 0)

                // Duration in minutes
                val duration = calculateDurationMinutes(slot.startTime, slot.endTime)

                // Rolling history for this slot
                val subjRecords = recordsBySubject[subject.id] ?: emptyList()
                val attended = subjRecords.count { it.status == AttendanceStatus.PRESENT }
                val conducted = subjRecords.count { it.status != AttendanceStatus.CANCELLED }
                val pct = if (conducted > 0) attended.toFloat() / conducted else 0f

                val recoveryNote = if (pct > 0 && pct < (subject.customTargetPercentage ?: 0.75f)) {
                    "Requires recovery"
                } else null

                SlotWithDetails(
                    slot = slot,
                    subject = subject,
                    durationMinutes = duration,
                    rollingHistoryAttended = attended,
                    rollingHistoryTotal = conducted,
                    historyPercentage = pct,
                    shortageRecoveryNote = recoveryNote,
                    isReminderSet = true,
                    isMarkedToday = false
                )
            }

            // Diagnostic overview
            val dayName = when (selectedDay) {
                1 -> "Monday Overview"
                2 -> "Tuesday Overview"
                3 -> "Wednesday Overview"
                4 -> "Thursday Overview"
                5 -> "Friday Overview"
                6 -> "Saturday Overview"
                7 -> "Sunday Overview"
                else -> "Overview"
            }
            val currentDaySlots = detailedSlots.filter { it.slot.dayOfWeek == selectedDay }
            val labs = currentDaySlots.count { it.slot.sessionType.contains("Lab", ignoreCase = true) }
            val totalMinutes = currentDaySlots.sumOf { it.durationMinutes }
            val hours = (totalMinutes / 60f * 10).toInt() / 10f
            val overview = DayDiagnosticOverview(
                dayName = dayName,
                lectureCount = currentDaySlots.size,
                totalHours = hours,
                labCount = labs,
                targetPercentage = 75
            )

            // Generate month calendar days
            val calendarDays = generateCalendarDays(records, allSlots)

            TimetableUiState(
                slots = detailedSlots,
                subjects = subjects,
                allSlotsCount = allSlots.size,
                selectedDay = selectedDay,
                dayOverview = overview,
                isCalendarMode = isCalendarMode,
                calendarDays = calendarDays,
                isAddSlotOpen = isAddSlotOpen,
                timeColumns = allTimings,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimetableUiState(isLoading = true)
    )

    fun selectDay(day: Int) {
        _selectedDay.value = day
    }

    fun toggleCalendarMode() {
        _isCalendarMode.value = !_isCalendarMode.value
    }

    fun setAddSlotOpen(open: Boolean) {
        _isAddSlotOpen.value = open
    }

    fun addTimeColumn(timing: String) {
        val current = _timeColumns.value.toMutableList()
        if (!current.contains(timing)) {
            current.add(timing)
            // Sort by start time
            _timeColumns.value = current.sortedBy { t ->
                val start = t.split("-").first().trim()
                try {
                    val parts = start.split(":")
                    parts[0].toInt() * 60 + parts[1].toInt()
                } catch (e: Exception) { 0 }
            }
        }
    }

    fun removeTimeColumn(timing: String) {
        val current = _timeColumns.value.toMutableList()
        current.remove(timing)
        _timeColumns.value = current
    }

    fun setEditingCell(day: Int?, timing: String?) {
        if (day == null || timing == null) {
            _editingCell.value = null
        } else {
            _editingCell.value = day to timing
        }
    }

    fun getEditingCell(): Pair<Int, String>? = _editingCell.value

    fun addSlotWithSubject(
        subjectName: String,
        subjectCode: String,
        dayOfWeek: Int,
        timing: String,
        room: String?,
        sessionType: String,
        notes: String?
    ) {
        viewModelScope.launch {
            val subject = repository.getOrCreateSubject(subjectName, subjectCode)
            val times = timing.split("-")
            val startTime = times.getOrNull(0)?.trim() ?: "09:00"
            val endTime = times.getOrNull(1)?.trim() ?: "10:30"

            val slot = TimetableSlot(
                subjectId = subject.id,
                dayOfWeek = dayOfWeek,
                startTime = startTime,
                endTime = endTime,
                room = room,
                sessionType = sessionType,
                notes = notes
            )
            repository.insertSlot(slot)
            _editingCell.value = null
        }
    }

    fun deleteSlot(slot: TimetableSlot) {
        viewModelScope.launch {
            repository.deleteSlot(slot)
        }
    }

    fun markSlotPresent(slot: TimetableSlot) {
        viewModelScope.launch {
            repository.upsertAttendanceRecord(
                subjectId = slot.subjectId,
                slotId = slot.id,
                date = System.currentTimeMillis(),
                status = AttendanceStatus.PRESENT,
                note = "Marked from Timetable"
            )
        }
    }

    private fun calculateDurationMinutes(start: String, end: String): Int {
        return try {
            val startParts = start.split(":").map { it.toInt() }
            val endParts = end.split(":").map { it.toInt() }
            val startTotal = startParts[0] * 60 + startParts[1]
            val endTotal = endParts[0] * 60 + endParts[1]
            (endTotal - startTotal).coerceAtLeast(30)
        } catch (e: Exception) {
            90
        }
    }

    private fun generateCalendarDays(
        records: List<AttendanceRecord>,
        slots: List<TimetableSlot>
    ): List<CalendarDayStatus> {
        val days = mutableListOf<CalendarDayStatus>()
        val cal = Calendar.getInstance()
        val todayYear = cal.get(Calendar.YEAR)
        val todayDayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Pre-grouping for performance
        val slotsByDayOfWeek = slots.groupBy { it.dayOfWeek }
        val recordsByDayOfYear = records.groupBy { 
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.YEAR) to c.get(Calendar.DAY_OF_YEAR)
        }

        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateMillis = cal.timeInMillis
            val year = cal.get(Calendar.YEAR)
            val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
            val isToday = year == todayYear && dayOfYear == todayDayOfYear

            val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> 1
            }

            val daySlots = slotsByDayOfWeek[dayOfWeek] ?: emptyList()
            val dayRecords = recordsByDayOfYear[year to dayOfYear] ?: emptyList()

            val hasAbsence = dayRecords.any { it.status == AttendanceStatus.ABSENT }
            val allPresent = dayRecords.isNotEmpty() && dayRecords.all { it.status == AttendanceStatus.PRESENT }
            val hasClasses = daySlots.isNotEmpty() || dayRecords.isNotEmpty()

            days.add(
                CalendarDayStatus(
                    dayOfMonth = day,
                    dateMillis = dateMillis,
                    isCurrentMonth = true,
                    isToday = isToday,
                    hasClasses = hasClasses,
                    hasAbsence = hasAbsence,
                    allPresent = allPresent
                )
            )
        }
        return days
    }
}


