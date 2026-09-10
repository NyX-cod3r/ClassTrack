package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_materials")
data class StudyMaterial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val description: String? = null,
    val filePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
