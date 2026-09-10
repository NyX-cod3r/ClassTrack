package com.example.attendanceschedulemanager.data.dao

import androidx.room.*
import com.example.attendanceschedulemanager.data.entity.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE subjectId = :subjectId")
    fun getSchedulesBySubject(subjectId: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
}
