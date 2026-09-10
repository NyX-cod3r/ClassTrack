package com.example.attendanceschedulemanager.data.repository

import com.example.attendanceschedulemanager.data.dao.AttendanceDao
import com.example.attendanceschedulemanager.data.dao.MaterialDao
import com.example.attendanceschedulemanager.data.dao.ScheduleDao
import com.example.attendanceschedulemanager.data.dao.SubjectDao
import com.example.attendanceschedulemanager.data.entity.Attendance
import com.example.attendanceschedulemanager.data.entity.Schedule
import com.example.attendanceschedulemanager.data.entity.StudyMaterial
import com.example.attendanceschedulemanager.data.entity.Subject
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val subjectDao: SubjectDao,
    private val scheduleDao: ScheduleDao,
    private val attendanceDao: AttendanceDao,
    private val materialDao: MaterialDao
) {
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()
    val allSchedules: Flow<List<Schedule>> = scheduleDao.getAllSchedules()

    suspend fun insertSubject(subject: Subject) = subjectDao.insertSubject(subject)
    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)
    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    suspend fun insertSchedule(schedule: Schedule) = scheduleDao.insertSchedule(schedule)
    suspend fun deleteSchedule(schedule: Schedule) = scheduleDao.deleteSchedule(schedule)

    fun getAttendanceBySubject(subjectId: Long) = attendanceDao.getAttendanceBySubject(subjectId)
    suspend fun insertAttendance(attendance: Attendance) = attendanceDao.insertAttendance(attendance)

    fun getMaterialsBySubject(subjectId: Long) = materialDao.getMaterialsBySubject(subjectId)
    suspend fun insertMaterial(material: StudyMaterial) = materialDao.insertMaterial(material)
}
