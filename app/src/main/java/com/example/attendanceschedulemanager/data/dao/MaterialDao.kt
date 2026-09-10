package com.example.attendanceschedulemanager.data.dao

import androidx.room.*
import com.example.attendanceschedulemanager.data.entity.StudyMaterial
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM study_materials WHERE subjectId = :subjectId")
    fun getMaterialsBySubject(subjectId: Long): Flow<List<StudyMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: StudyMaterial)

    @Delete
    suspend fun deleteMaterial(material: StudyMaterial)
}
