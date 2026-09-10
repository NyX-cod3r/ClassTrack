package com.example.attendanceschedulemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.attendanceschedulemanager.data.dao.AttendanceDao
import com.example.attendanceschedulemanager.data.dao.MaterialDao
import com.example.attendanceschedulemanager.data.dao.ScheduleDao
import com.example.attendanceschedulemanager.data.dao.SubjectDao
import com.example.attendanceschedulemanager.data.entity.Attendance
import com.example.attendanceschedulemanager.data.entity.Schedule
import com.example.attendanceschedulemanager.data.entity.StudyMaterial
import com.example.attendanceschedulemanager.data.entity.Subject

@Database(
    entities = [Subject::class, Schedule::class, Attendance::class, StudyMaterial::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun materialDao(): MaterialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_schedule_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
