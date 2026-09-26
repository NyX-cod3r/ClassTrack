package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,
    val professor: String? = null,
    val credits: Int = 0,
    val colorTag: Int,
    val customTargetPercentage: Float? = null,
    val type: String = "Lecture"
)
