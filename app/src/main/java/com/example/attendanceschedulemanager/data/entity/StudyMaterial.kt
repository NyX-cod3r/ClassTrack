package com.example.attendanceschedulemanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_materials",
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
data class StudyMaterial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val description: String = "",
    val fileUri: String,
    val fileType: String,
    val fileSizeBytes: Long = 0,
    val category: String = "Lecture Notes",
    val tags: String = "", // Comma separated tags
    val addedDate: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
