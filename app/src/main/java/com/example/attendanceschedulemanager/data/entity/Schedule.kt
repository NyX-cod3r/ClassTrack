package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val roomNumber: String? = null
)
