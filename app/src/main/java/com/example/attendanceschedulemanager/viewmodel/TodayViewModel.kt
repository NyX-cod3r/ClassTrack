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
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil

data class SessionWithSubject(
    val slot: TimetableSlot,
    val subject: Subject,
    val record: AttendanceRecord? = null,
    val subjectAttended: Int = 0,
    val subjectConducted: Int = 0,
    val subjectPercentage: Float = 0f
)

data class CriticalShortageInfo(
    val subject: Subject,
    val percentage: Float,
    val shortfallClasses: Int
)

data class NextClassInfo(
    val session: SessionWithSubject,
    val minutesRemaining: Int
)

data class TodayUiState(
    val subjects: List<Subject> = emptyList(),
    val todaySessions: List<SessionWithSubject> = emptyList(),
    val selectedDayIndex: Int = 1, // Default to Monday
    val activeDaysWithClasses: Set<Int> = emptySet(),
    val aggregateAttendance: Float = 0f,
    val totalAttended: Int = 0,
    val totalConducted: Int = 0,
    val bufferClasses: Int = 0,
    val isBufferPositive: Boolean = true,
    val criticalShortage: CriticalShortageInfo? = null,
    val nextClass: NextClassInfo? = null,
    val isQuickLogOpen: Boolean = false,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodayViewModel @Inject constructor(
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
    private val _isQuickLogOpen = MutableStateFlow(false)

    val uiState: StateFlow<TodayUiState> = combine(
        repository.allSubjects.distinctUntilChanged(),
        repository.allTimetableSlots.distinctUntilChanged(),
        repository.allAttendanceRecords.distinctUntilChanged(),
        _selectedDay,
        _isQuickLogOpen
    ) { subjects, allSlots, records, selectedDay, isQuickLogOpen ->
        // Keep attendance aggregation off the main thread during the first render.
        withContext(Dispatchers.Default) {
            val slotsForSelectedDay = allSlots.filter { it.dayOfWeek == selectedDay }
            
            // Optimization: Group records by subject ID once
            val recordsBySubject = records.groupBy { it.subjectId }

            // Compute running stats per subject
            val subjectStatsMap = subjects.associate { subject ->
                val subjRecords = recordsBySubject[subject.id] ?: emptyList()
                val attended = subjRecords.count { it.status == AttendanceStatus.PRESENT }
                val conducted = subjRecords.count { it.status != AttendanceStatus.CANCELLED }
                val pct = if (conducted > 0) attended.toFloat() / conducted else 0f
                subject.id to Triple(attended, conducted, pct)
            }

            // Build session list
            val sessions = slotsForSelectedDay.map { slot ->
                val subject = subjects.find { it.id == slot.subjectId }
                    ?: Subject(name = "Unknown Course", code = "??", colorTag = 0)
                val stats = subjectStatsMap[subject.id] ?: Triple(0, 0, 0f)

                // Look for attendance record on selected day
                val selectedDayTimestamp = getSelectedDayTimestamp(selectedDay)
                val record = records.find { rec ->
                    rec.timetableSlotId == slot.id && isSameDay(rec.date, selectedDayTimestamp)
                }

                SessionWithSubject(
                    slot = slot,
                    subject = subject,
                    record = record,
                    subjectAttended = stats.first,
                    subjectConducted = stats.second,
                    subjectPercentage = stats.third
                )
            }.sortedBy { it.slot.startTime }

            // Overall Aggregate attendance
            val totalAttended = records.count { it.status == AttendanceStatus.PRESENT }
            val totalConducted = records.count { it.status != AttendanceStatus.CANCELLED }
            val aggregate = if (totalConducted > 0) totalAttended.toFloat() / totalConducted else 0f

            // Buffer calculation against 75%
            val target = 0.75f
            val buffer = if (totalConducted > 0) {
                val margin = (totalAttended - target * totalConducted) / target
                margin.toInt()
            } else 0

            // Critical shortage banner: check if any subject is below target
            val criticalSubject = subjects.mapNotNull { subj ->
                val stats = subjectStatsMap[subj.id] ?: Triple(0, 0, 0f)
                val targetPct = subj.customTargetPercentage ?: 0.75f
                if (stats.second > 0 && stats.third < targetPct) {
                    // Shortfall calculation
                    val needed = ceil((targetPct * stats.second - stats.first) / (1f - targetPct)).toInt()
                    CriticalShortageInfo(subj, stats.third, maxOf(1, needed))
                } else null
            }.minByOrNull { it.percentage }

            // Next upcoming class
            val nextClassInfo = sessions.firstOrNull { it.record == null }?.let { session ->
                val cal = Calendar.getInstance()
                val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
                val startTimeParts = session.slot.startTime.split(":")
                if (startTimeParts.size == 2) {
                    val startMinutes = startTimeParts[0].toInt() * 60 + startTimeParts[1].toInt()
                    val diff = startMinutes - nowMinutes
                    if (diff > 0) NextClassInfo(session = session, minutesRemaining = diff) else null
                } else null
            }

            // Active days with scheduled slots
            val activeDays = allSlots.map { it.dayOfWeek }.toSet()

            TodayUiState(
                subjects = subjects,
                todaySessions = sessions,
                selectedDayIndex = selectedDay,
                activeDaysWithClasses = activeDays,
                aggregateAttendance = aggregate,
                totalAttended = totalAttended,
                totalConducted = totalConducted,
                bufferClasses = abs(buffer),
                isBufferPositive = buffer >= 0,
                criticalShortage = criticalSubject,
                nextClass = nextClassInfo,
                isQuickLogOpen = isQuickLogOpen,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodayUiState(isLoading = true)
    )

    fun selectDay(dayIndex: Int) {
        _selectedDay.value = dayIndex
    }

    fun setQuickLogOpen(isOpen: Boolean) {
        _isQuickLogOpen.value = isOpen
    }

    fun markAttendance(subjectId: Long, slotId: Long, status: AttendanceStatus) {
        viewModelScope.launch {
            val date = getSelectedDayTimestamp(_selectedDay.value)
            repository.upsertAttendanceRecord(subjectId, slotId, date, status)
        }
    }

    fun quickLogAttendance(subjectId: Long, status: AttendanceStatus) {
        viewModelScope.launch {
            val date = System.currentTimeMillis()
            // Find today's slot for this subject if available
            val slot = uiState.value.todaySessions.find { it.subject.id == subjectId }?.slot
            repository.upsertAttendanceRecord(subjectId, slot?.id, date, status)
            _isQuickLogOpen.value = false
        }
    }

    fun addSubjectAndLog(name: String, code: String, status: AttendanceStatus) {
        viewModelScope.launch {
            val subject = repository.getOrCreateSubject(name, code)
            quickLogAttendance(subject.id, status)
        }
    }

    private fun getSelectedDayTimestamp(dayIndex: Int): Long {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val currentDay = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
        val diff = dayIndex - currentDay
        cal.add(Calendar.DAY_OF_MONTH, diff)
        return cal.timeInMillis
    }

    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}

