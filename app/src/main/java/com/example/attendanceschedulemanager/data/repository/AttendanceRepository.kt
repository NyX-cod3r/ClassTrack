package com.example.attendanceschedulemanager.data.repository

import com.example.attendanceschedulemanager.data.dao.*
import com.example.attendanceschedulemanager.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val subjectDao: SubjectDao,
    private val timetableSlotDao: TimetableSlotDao,
    private val attendanceRecordDao: AttendanceRecordDao,
    private val studyMaterialDao: StudyMaterialDao
) {
    // Subjects
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()
    
    suspend fun getSubjectById(id: Long) = subjectDao.getSubjectById(id)
    suspend fun insertSubject(subject: Subject) = subjectDao.insertSubject(subject)
    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)
    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    // Timetable
    val allTimetableSlots: Flow<List<TimetableSlot>> = timetableSlotDao.getAllSlots()
    
    fun getSlotsForDay(day: Int) = timetableSlotDao.getSlotsForDay(day)
    fun getSlotsForSubject(subjectId: Long) = timetableSlotDao.getSlotsForSubject(subjectId)
    suspend fun insertSlot(slot: TimetableSlot) = timetableSlotDao.insertSlot(slot)
    suspend fun insertSlots(slots: List<TimetableSlot>) = timetableSlotDao.insertSlots(slots)
    suspend fun updateSlot(slot: TimetableSlot) = timetableSlotDao.updateSlot(slot)
    suspend fun deleteSlot(slot: TimetableSlot) = timetableSlotDao.deleteSlot(slot)

    // Attendance
    val allAttendanceRecords: Flow<List<AttendanceRecord>> = attendanceRecordDao.getAllRecords()
    
    fun getRecordsForSubject(subjectId: Long) = attendanceRecordDao.getRecordsForSubject(subjectId)
    fun getRecordsBetweenDates(startDate: Long, endDate: Long) = 
        attendanceRecordDao.getRecordsBetweenDates(startDate, endDate)
    fun getCountByStatus(subjectId: Long, status: AttendanceStatus) = 
        attendanceRecordDao.getCountByStatus(subjectId, status)
    
    suspend fun insertRecord(record: AttendanceRecord) = attendanceRecordDao.insertRecord(record)
    suspend fun updateRecord(record: AttendanceRecord) = attendanceRecordDao.updateRecord(record)
    suspend fun deleteRecord(record: AttendanceRecord) = attendanceRecordDao.deleteRecord(record)
    suspend fun updateAttendanceNote(recordId: Long, note: String?) = attendanceRecordDao.updateNote(recordId, note)

    suspend fun upsertAttendanceRecord(
        subjectId: Long,
        slotId: Long?,
        date: Long,
        status: AttendanceStatus,
        note: String? = null
    ) {
        val cal = java.util.Calendar.getInstance().apply {
            timeInMillis = date
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        val endOfDay = cal.timeInMillis - 1

        if (slotId != null) {
            val existing = attendanceRecordDao.getRecordForSlotOnDay(slotId, startOfDay, endOfDay)
            if (existing != null) {
                attendanceRecordDao.updateRecord(
                    existing.copy(
                        status = status,
                        note = note ?: existing.note,
                        loggedAt = System.currentTimeMillis()
                    )
                )
                return
            }
        }
        attendanceRecordDao.insertRecord(
            AttendanceRecord(
                subjectId = subjectId,
                timetableSlotId = slotId,
                date = date,
                status = status,
                note = note
            )
        )
    }

    // Study Materials
    val allMaterials: Flow<List<StudyMaterial>> = studyMaterialDao.getAllMaterials()
    
    fun getMaterialsForSubject(subjectId: Long) = studyMaterialDao.getMaterialsForSubject(subjectId)
    fun searchMaterials(query: String) = studyMaterialDao.searchMaterials(query)
    suspend fun insertMaterial(material: StudyMaterial) = studyMaterialDao.insertMaterial(material)
    suspend fun updateMaterial(material: StudyMaterial) = studyMaterialDao.updateMaterial(material)
    suspend fun deleteMaterial(material: StudyMaterial) = studyMaterialDao.deleteMaterial(material)

    // Helper for Subject Creation
    suspend fun getOrCreateSubject(name: String, code: String): Subject {
        val all = subjectDao.getAllSubjectsOnce()
        val existing = all.find { it.code.equals(code, ignoreCase = true) || it.name.equals(name, ignoreCase = true) }
        if (existing != null) return existing

        val newSubject = Subject(
            name = name,
            code = code,
            colorTag = (0xFF000000.toLong() or (Random().nextInt(0xFFFFFF).toLong())).toInt()
        )
        val id = subjectDao.insertSubject(newSubject)
        return newSubject.copy(id = id)
    }
}
