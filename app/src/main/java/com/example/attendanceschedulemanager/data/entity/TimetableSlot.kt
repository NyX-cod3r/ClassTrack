package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timetable_slots",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class TimetableSlot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val dayOfWeek: Int, // 1 (Mon) to 7 (Sun)
    val startTime: String, // HH:mm format
    val endTime: String, // HH:mm format
    val room: String? = null,
    val sessionType: String = "Lecture",
    val isRecurring: Boolean = true,
    val semesterId: Long = 0,
    val notes: String? = null
)
