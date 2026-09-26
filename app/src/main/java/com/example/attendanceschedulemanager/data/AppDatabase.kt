package com.example.attendanceschedulemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.attendanceschedulemanager.data.dao.*
import com.example.attendanceschedulemanager.data.entity.*

@Database(
    entities = [
        Subject::class,
        TimetableSlot::class,
        AttendanceRecord::class,
        StudyMaterial::class,
        Semester::class,
        ReminderSetting::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun timetableSlotDao(): TimetableSlotDao
    abstract fun attendanceRecordDao(): AttendanceRecordDao
    abstract fun studyMaterialDao(): StudyMaterialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "classtrack_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
