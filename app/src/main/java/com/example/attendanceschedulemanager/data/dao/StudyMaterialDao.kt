package com.example.attendanceschedulemanager.data.dao

import androidx.room.*
import com.example.attendanceschedulemanager.data.entity.StudyMaterial
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyMaterialDao {
    @Query("SELECT * FROM study_materials")
    fun getAllMaterials(): Flow<List<StudyMaterial>>

    @Query("SELECT * FROM study_materials WHERE subjectId = :subjectId")
    fun getMaterialsForSubject(subjectId: Long): Flow<List<StudyMaterial>>

    @Query("SELECT * FROM study_materials WHERE title LIKE '%' || :query || '%'")
    fun searchMaterials(query: String): Flow<List<StudyMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: StudyMaterial): Long

    @Update
    suspend fun updateMaterial(material: StudyMaterial)

    @Delete
    suspend fun deleteMaterial(material: StudyMaterial)
}
