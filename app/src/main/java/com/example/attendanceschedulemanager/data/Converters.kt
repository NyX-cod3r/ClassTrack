package com.example.attendanceschedulemanager.data

import androidx.room.TypeConverter
import com.example.attendanceschedulemanager.data.entity.AttendanceStatus

class Converters {
    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String {
        return status.name
    }

    @TypeConverter
    fun toAttendanceStatus(status: String): AttendanceStatus {
        return AttendanceStatus.valueOf(status)
    }
}
