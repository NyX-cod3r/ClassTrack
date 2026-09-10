package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String? = null,
    val color: Int = 0xFF6200EE.toInt(),
    val attendanceThreshold: Int = 75
)
