package com.example.attendanceschedulemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceschedulemanager.data.entity.AttendanceRecord
import com.example.attendanceschedulemanager.data.entity.AttendanceStatus
import com.example.attendanceschedulemanager.data.entity.Subject
import com.example.attendanceschedulemanager.data.entity.TimetableSlot
import com.example.attendanceschedulemanager.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

enum class AnalyticsFilter(val label: String) {
    ALL("All"),
    PRESENT("Present"),
    ABSENT("Absent"),
    CANCELLED("Cancelled")
}

data class SubjectAnalyticsData(
    val subject: Subject,
    val attended: Int,
    val totalConducted: Int,
    val missed: Int,
    val cancelled: Int,
    val percentage: Float, // 0.0 to 100.0
    val status: String, // "Critical Shortage", "Caution", "Safe Zone"
    val statusDescription: String,
    val targetPercentage: Float, // e.g. 75.0f
    val requiredFor75: Int,
    val requiredFor80: Int,
    val bunkAllowance: Int
)

data class RecordItemUi(
    val record: AttendanceRecord,
    val title: String,
    val timeFormatted: String,
    val room: String?,
    val sessionType: String?
)

data class AnalyticsUiState(
    val subjects: List<Subject> = emptyList(),
    val selectedSubjectId: Long? = null,
    val selectedSubjectStats: SubjectAnalyticsData? = null,
    val allSubjectStats: List<SubjectAnalyticsData> = emptyList(),
    val aggregatePercentage: Float = 0f,
    val simulationOffset: Int = 2,
    val simulatedPercentage: Float = 0f,
    val selectedFilter: AnalyticsFilter = AnalyticsFilter.ALL,
    val filteredRecords: List<RecordItemUi> = emptyList(),
    val totalRecordsCount: Int = 0,
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val cancelledCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _selectedSubjectId = MutableStateFlow<Long?>(null)
    private val _simulationOffset = MutableStateFlow(2)
    private val _selectedFilter = MutableStateFlow(AnalyticsFilter.ALL)

    private data class AnalyticsControls(
        val selectedSubjectId: Long?,
        val simulationOffset: Int,
        val selectedFilter: AnalyticsFilter
    )

    private val _analyticsControls = combine(
        _selectedSubjectId,
        _simulationOffset,
        _selectedFilter
    ) { subId, simOffset, filter ->
        AnalyticsControls(subId, simOffset, filter)
    }

    val uiState: StateFlow<AnalyticsUiState> = combine(
        repository.allSubjects,
        repository.allAttendanceRecords,
        repository.allTimetableSlots,
        _analyticsControls
    ) { subjects, records, slots, controls ->
        val selectedSubId = controls.selectedSubjectId
        val simOffset = controls.simulationOffset
        val filter = controls.selectedFilter
        if (subjects.isEmpty()) {
            return@combine AnalyticsUiState(isLoading = false)
        }

        // Calculate stats for all subjects
        val allStats = subjects.map { subject ->
            val subRecords = records.filter { it.subjectId == subject.id }
            val attended = subRecords.count { it.status == AttendanceStatus.PRESENT }
            val conducted = subRecords.count { it.status != AttendanceStatus.CANCELLED }
            val missed = subRecords.count { it.status == AttendanceStatus.ABSENT }
            val cancelled = subRecords.count { it.status == AttendanceStatus.CANCELLED }
            val percentage = if (conducted > 0) (attended.toFloat() / conducted) * 100f else 0f

            val target = (subject.customTargetPercentage ?: 0.75f) * 100f
            val req75 = calculateRequiredClasses(attended, conducted, 0.75f)
            val req80 = calculateRequiredClasses(attended, conducted, 0.80f)
            val bunk = calculateSafeMisses(attended, conducted, 0.75f)

            val status: String
            val desc: String
            when {
                percentage < target -> {
                    status = "Critical Shortage"
                    val diff = String.format(Locale.US, "%.1f", target - percentage)
                    desc = "$diff% below minimum mandatory eligibility requirement."
                }
                percentage < target + 5f -> {
                    status = "Caution Risk"
                    desc = "Close to minimum threshold. Avoid missed sessions."
                }
                else -> {
                    status = "Safe Zone"
                    val buffer = bunk
                    desc = if (buffer > 0) "Safe with buffer of $buffer bunkable class${if (buffer > 1) "es" else ""}." else "Attendance is well-maintained above target."
                }
            }

            SubjectAnalyticsData(
                subject = subject,
                attended = attended,
                totalConducted = conducted,
                missed = missed,
                cancelled = cancelled,
                percentage = percentage,
                status = status,
                statusDescription = desc,
                targetPercentage = target,
                requiredFor75 = req75,
                requiredFor80 = req80,
                bunkAllowance = bunk
            )
        }

        val totalAttendedAll = allStats.sumOf { it.attended }
        val totalConductedAll = allStats.sumOf { it.totalConducted }
        val aggregate = if (totalConductedAll > 0) (totalAttendedAll.toFloat() / totalConductedAll) * 100f else 0f

        // Pick selected subject
        val activeSubjectId = selectedSubId ?: subjects.firstOrNull()?.id

        val activeStats = if (activeSubjectId != null) {
            allStats.find { it.subject.id == activeSubjectId } ?: allStats.firstOrNull()
        } else null

        if (activeStats == null) {
            return@combine AnalyticsUiState(
                subjects = subjects,
                allSubjectStats = allStats,
                aggregatePercentage = aggregate,
                isLoading = false
            )
        }

        // Simulated percentage
        val simAttended = activeStats.attended + simOffset
        val simConducted = activeStats.totalConducted + simOffset
        val simPercentage = if (simConducted > 0) (simAttended.toFloat() / simConducted) * 100f else 0f

        // Optimization: group records by subjectId
        val recordsBySubject = records.groupBy { it.subjectId }
        val subjectRecords = recordsBySubject[activeSubjectId] ?: emptyList()
        val sortedSubjectRecords = subjectRecords.sortedByDescending { it.date }

        val totalCount = subjectRecords.size
        val presentCount = subjectRecords.count { it.status == AttendanceStatus.PRESENT }
        val absentCount = subjectRecords.count { it.status == AttendanceStatus.ABSENT }
        val cancelledCount = subjectRecords.count { it.status == AttendanceStatus.CANCELLED }

        val filteredRaw = when (filter) {
            AnalyticsFilter.ALL -> sortedSubjectRecords
            AnalyticsFilter.PRESENT -> sortedSubjectRecords.filter { it.status == AttendanceStatus.PRESENT }
            AnalyticsFilter.ABSENT -> sortedSubjectRecords.filter { it.status == AttendanceStatus.ABSENT }
            AnalyticsFilter.CANCELLED -> sortedSubjectRecords.filter { it.status == AttendanceStatus.CANCELLED }
        }

        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val slotMap = slots.associateBy { it.id }

        val recordItems = filteredRaw.map { record ->
            val slot = record.timetableSlotId?.let { slotMap[it] }
            val formattedDate = dateFormat.format(Date(record.date))
            val timeText = if (slot != null) {
                "$formattedDate · ${slot.startTime} – ${slot.endTime}"
            } else {
                formattedDate
            }

            val title = when {
                !record.note.isNullOrBlank() && !record.note.startsWith("Self") && !record.note.startsWith("Medical") -> {
                    record.note.split(" - ").first()
                }
                slot?.notes != null -> slot.notes
                slot?.sessionType != null -> "${slot.sessionType} Session"
                record.status == AttendanceStatus.CANCELLED -> "Class Cancelled by Faculty"
                else -> "Regular Lecture"
            }

            RecordItemUi(
                record = record,
                title = title,
                timeFormatted = timeText,
                room = slot?.room,
                sessionType = slot?.sessionType
            )
        }

        AnalyticsUiState(
            subjects = subjects,
            selectedSubjectId = activeSubjectId,
            selectedSubjectStats = activeStats,
            allSubjectStats = allStats,
            aggregatePercentage = aggregate,
            simulationOffset = simOffset,
            simulatedPercentage = simPercentage,
            selectedFilter = filter,
            filteredRecords = recordItems,
            totalRecordsCount = totalCount,
            presentCount = presentCount,
            absentCount = absentCount,
            cancelledCount = cancelledCount,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState(isLoading = true)
    )

    fun selectSubject(subjectId: Long) {
        _selectedSubjectId.value = subjectId
    }

    fun setSimulationOffset(offset: Int) {
        _simulationOffset.value = offset.coerceIn(0, 15)
    }

    fun setFilter(filter: AnalyticsFilter) {
        _selectedFilter.value = filter
    }

    fun updateRecordNote(recordId: Long, note: String?) {
        viewModelScope.launch {
            repository.updateAttendanceNote(recordId, note)
        }
    }

    fun generateShareReport(): String {
        val state = uiState.value
        val stats = state.selectedSubjectStats ?: return "No attendance data available."
        val sub = stats.subject

        val sb = StringBuilder()
        sb.appendLine("📊 ClassTrack Attendance Report")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("Subject: ${sub.name} (${sub.code})")
        sb.appendLine("Attendance: ${String.format(Locale.US, "%.1f", stats.percentage)}% [Goal: ${stats.targetPercentage.toInt()}%]")
        sb.appendLine("Status: ${stats.status}")
        sb.appendLine("Attended: ${stats.attended} / Conducted: ${stats.totalConducted} (Missed: ${stats.missed}, Excused: ${stats.cancelled})")
        sb.appendLine("Safe-Zone Recovery: Attend next ${stats.requiredFor75} classes consecutively for 75%.")
        sb.appendLine("Bunk Allowance: ${stats.bunkAllowance} classes.")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("Recent Logs:")
        state.filteredRecords.take(10).forEach { item ->
            val statusEmoji = when (item.record.status) {
                AttendanceStatus.PRESENT -> "✅ Present"
                AttendanceStatus.ABSENT -> "❌ Absent"
                AttendanceStatus.CANCELLED, AttendanceStatus.EXCUSED -> "🚫 Excused"
            }
            val noteStr = if (!item.record.note.isNullOrBlank()) " (${item.record.note})" else ""
            sb.appendLine("• ${item.timeFormatted} - $statusEmoji$noteStr")
        }
        sb.appendLine("\nGenerated offline via ClassTrack.")
        return sb.toString()
    }

    private fun calculateRequiredClasses(attended: Int, conducted: Int, target: Float): Int {
        if (conducted == 0) return 0
        val currentPercentage = attended.toFloat() / conducted
        if (currentPercentage >= target) return 0
        val req = ceil((target * conducted - attended) / (1f - target)).toInt()
        return max(0, req)
    }

    private fun calculateSafeMisses(attended: Int, conducted: Int, target: Float): Int {
        if (conducted == 0) return 0
        val m = floor((attended - target * conducted) / target).toInt()
        return max(0, m)
    }
}
