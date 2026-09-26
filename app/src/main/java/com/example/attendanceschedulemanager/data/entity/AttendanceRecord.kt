package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_records",
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
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val timetableSlotId: Long? = null, // Nullable for ad-hoc sessions
    val date: Long, // Epoch millis
    val status: AttendanceStatus,
    val note: String? = null,
    val loggedAt: Long = System.currentTimeMillis(),
    val isSelfMarked: Boolean = true
)

enum class AttendanceStatus {
    PRESENT, ABSENT, CANCELLED, EXCUSED
}
