package com.example.attendanceschedulemanager.data.dao

import androidx.room.*
import com.example.attendanceschedulemanager.data.entity.TimetableSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableSlotDao {
    @Query("SELECT * FROM timetable_slots")
    fun getAllSlots(): Flow<List<TimetableSlot>>

    @Query("SELECT * FROM timetable_slots WHERE dayOfWeek = :day")
    fun getSlotsForDay(day: Int): Flow<List<TimetableSlot>>

    @Query("SELECT * FROM timetable_slots WHERE subjectId = :subjectId")
    fun getSlotsForSubject(subjectId: Long): Flow<List<TimetableSlot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: TimetableSlot): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<TimetableSlot>)

    @Update
    suspend fun updateSlot(slot: TimetableSlot)

    @Delete
    suspend fun deleteSlot(slot: TimetableSlot)
}
