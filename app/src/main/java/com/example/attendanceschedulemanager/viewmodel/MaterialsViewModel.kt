package com.example.attendanceschedulemanager.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceschedulemanager.data.entity.StudyMaterial
import com.example.attendanceschedulemanager.data.entity.Subject
import com.example.attendanceschedulemanager.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class SubjectWithCount(
    val id: Long?,
    val name: String,
    val count: Int
)

data class MaterialsUiState(
    val allMaterials: List<StudyMaterial> = emptyList(),
    val filteredMaterials: List<StudyMaterial> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val subjectTabs: List<SubjectWithCount> = emptyList(),
    val selectedSubjectId: Long? = null,
    val searchQuery: String = "",
    val isGridView: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class MaterialsViewModel @Inject constructor(
    private val repository: AttendanceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedSubjectId = MutableStateFlow<Long?>(null)
    private val _isGridView = MutableStateFlow(false)

    val uiState: StateFlow<MaterialsUiState> = combine(
        repository.allSubjects,
        repository.allMaterials,
        _searchQuery,
        _selectedSubjectId,
        _isGridView
    ) { subjects, materials, query, selectedSubId, isGrid ->
        val subjectMap = subjects.associateBy { it.id }

        // Filter by subject
        val subjectFiltered = if (selectedSubId == null) {
            materials
        } else {
            materials.filter { it.subjectId == selectedSubId }
        }

        // Filter by search query
        val queryFiltered = if (query.isBlank()) {
            subjectFiltered
        } else {
            subjectFiltered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.tags.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                (subjectMap[it.subjectId]?.name?.contains(query, ignoreCase = true) == true)
            }
        }

        // Compute tab counts
        val materialsBySubject = materials.groupBy { it.subjectId }
        val subjectTabs = subjects.map { sub ->
            SubjectWithCount(
                id = sub.id,
                name = sub.name,
                count = materialsBySubject[sub.id]?.size ?: 0
            )
        }.toMutableList()
        subjectTabs.add(0, SubjectWithCount(id = null, name = "All Subjects", count = materials.size))

        MaterialsUiState(
            allMaterials = materials,
            filteredMaterials = queryFiltered,
            subjects = subjects,
            subjectTabs = subjectTabs,
            selectedSubjectId = selectedSubId,
            searchQuery = query,
            isGridView = isGrid,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MaterialsUiState(isLoading = true)
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectSubject(subjectId: Long?) {
        _selectedSubjectId.value = subjectId
    }

    fun toggleGridView(isGrid: Boolean) {
        _isGridView.value = isGrid
    }

    fun toggleFavorite(material: StudyMaterial) {
        viewModelScope.launch {
            repository.updateMaterial(material.copy(isFavorite = !material.isFavorite))
        }
    }

    fun createTextNote(
        subjectId: Long,
        title: String,
        content: String,
        category: String,
        tags: String
    ) {
        viewModelScope.launch {
            val fileName = "note_${System.currentTimeMillis()}.txt"
            val file = File(context.filesDir, fileName)
            file.writeText(content)

            val material = StudyMaterial(
                subjectId = subjectId,
                title = if (title.endsWith(".txt")) title else "$title.txt",
                description = content.take(120),
                fileUri = file.absolutePath,
                fileType = "txt",
                fileSizeBytes = file.length(),
                category = category,
                tags = tags,
                addedDate = System.currentTimeMillis()
            )
            repository.insertMaterial(material)
        }
    }

    fun importFile(
        uri: Uri,
        subjectId: Long,
        title: String,
        category: String,
        tags: String
    ) {
        viewModelScope.launch {
            val file = copyFileToInternalStorage(uri, title)
            val extension = getFileExtension(uri)
            val material = StudyMaterial(
                subjectId = subjectId,
                title = if (title.contains(".")) title else "$title.$extension",
                description = "Locally stored offline document",
                fileUri = file?.absolutePath ?: uri.toString(),
                fileType = extension,
                fileSizeBytes = file?.length() ?: 1024L,
                category = category,
                tags = tags,
                addedDate = System.currentTimeMillis()
            )
            repository.insertMaterial(material)
        }
    }

    fun deleteMaterial(material: StudyMaterial) {
        viewModelScope.launch {
            if (material.fileUri.startsWith("/")) {
                val file = File(material.fileUri)
                if (file.exists()) file.delete()
            }
            repository.deleteMaterial(material)
        }
    }

    fun getMaterialFile(material: StudyMaterial): File {
        if (material.fileUri.startsWith("/")) {
            val file = File(material.fileUri)
            if (file.exists()) return file
        }
        // Fallback sample content file for seeded local materials
        val sampleFile = File(context.filesDir, "${material.id}_${material.title}")
        if (!sampleFile.exists()) {
            sampleFile.writeText(
                "Document: ${material.title}\nCategory: ${material.category}\nDetails: ${material.description}\nTags: ${material.tags}\n\n[Full contents cached offline in ClassTrack vault]"
            )
        }
        return sampleFile
    }

    private fun copyFileToInternalStorage(uri: Uri, title: String): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val ext = getFileExtension(uri)
            val cleanTitle = title.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
            val fileName = "${System.currentTimeMillis()}_$cleanTitle.$ext"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileExtension(uri: Uri): String {
        val mime = context.contentResolver.getType(uri)
        return when {
            mime?.contains("pdf") == true -> "pdf"
            mime?.contains("image") == true -> "jpg"
            mime?.contains("text") == true -> "txt"
            mime?.contains("word") == true -> "docx"
            mime?.contains("presentation") == true -> "pptx"
            else -> uri.lastPathSegment?.substringAfterLast('.', "doc") ?: "doc"
        }
    }
}
