package com.example.attendanceschedulemanager.di

import android.content.Context
import com.example.attendanceschedulemanager.data.AppDatabase
import com.example.attendanceschedulemanager.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideSubjectDao(database: AppDatabase): SubjectDao = database.subjectDao()

    @Provides
    fun provideTimetableSlotDao(database: AppDatabase): TimetableSlotDao = database.timetableSlotDao()

    @Provides
    fun provideAttendanceRecordDao(database: AppDatabase): AttendanceRecordDao = database.attendanceRecordDao()

    @Provides
    fun provideStudyMaterialDao(database: AppDatabase): StudyMaterialDao = database.studyMaterialDao()
}
