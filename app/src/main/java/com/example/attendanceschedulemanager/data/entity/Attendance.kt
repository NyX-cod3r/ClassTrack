package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val date: Long,
    val status: AttendanceStatus
)

enum class AttendanceStatus {
    PRESENT, ABSENT, CANCELLED, NOT_MARKED
}
