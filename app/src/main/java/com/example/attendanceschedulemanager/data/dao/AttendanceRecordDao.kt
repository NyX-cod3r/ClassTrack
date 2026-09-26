package com.example.attendanceschedulemanager.data.dao

import androidx.room.*
import com.example.attendanceschedulemanager.data.entity.AttendanceRecord
import com.example.attendanceschedulemanager.data.entity.AttendanceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceRecordDao {
    @Query("SELECT * FROM attendance_records")
    fun getAllRecords(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE subjectId = :subjectId")
    fun getRecordsForSubject(subjectId: Long): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date BETWEEN :startDate AND :endDate")
    fun getRecordsBetweenDates(startDate: Long, endDate: Long): Flow<List<AttendanceRecord>>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE subjectId = :subjectId AND status = :status")
    fun getCountByStatus(subjectId: Long, status: AttendanceStatus): Flow<Int>

    @Query("SELECT * FROM attendance_records WHERE timetableSlotId = :slotId AND date BETWEEN :startOfDay AND :endOfDay LIMIT 1")
    suspend fun getRecordForSlotOnDay(slotId: Long, startOfDay: Long, endOfDay: Long): AttendanceRecord?

    @Query("UPDATE attendance_records SET note = :note WHERE id = :recordId")
    suspend fun updateNote(recordId: Long, note: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<AttendanceRecord>)

    @Update
    suspend fun updateRecord(record: AttendanceRecord)

    @Delete
    suspend fun deleteRecord(record: AttendanceRecord)
}
