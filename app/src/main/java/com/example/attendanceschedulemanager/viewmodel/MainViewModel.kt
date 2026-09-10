package com.example.attendanceschedulemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceschedulemanager.data.AppDatabase
import com.example.attendanceschedulemanager.data.entity.Subject
import com.example.attendanceschedulemanager.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    val allSubjects: StateFlow<List<Subject>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(
            db.subjectDao(),
            db.scheduleDao(),
            db.attendanceDao(),
            db.materialDao()
        )
        allSubjects = repository.allSubjects.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun addSubject(name: String, code: String? = null) {
        viewModelScope.launch {
            repository.insertSubject(Subject(name = name, code = code))
        }
    }
}
