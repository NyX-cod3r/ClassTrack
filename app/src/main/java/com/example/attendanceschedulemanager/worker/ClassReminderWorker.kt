package com.example.attendanceschedulemanager.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.attendanceschedulemanager.R
import com.example.attendanceschedulemanager.data.entity.AttendanceStatus
import com.example.attendanceschedulemanager.data.repository.AttendanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ClassReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: AttendanceRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val subjects = repository.allSubjects.first()
        val records = repository.allAttendanceRecords.first()

        subjects.forEach { subject ->
            val subjectRecords = records.filter { it.subjectId == subject.id }
            val attended = subjectRecords.count { it.status == AttendanceStatus.PRESENT }
            val conducted = subjectRecords.count { it.status != AttendanceStatus.CANCELLED }
            val percentage = if (conducted > 0) attended.toFloat() / conducted else 0f
            
            val target = subject.customTargetPercentage ?: 0.75f
            if (percentage < target && conducted > 0) {
                showNotification(subject.name, percentage)
            }
        }

        return Result.success()
    }

    private fun showNotification(subjectName: String, percentage: Float) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "attendance_risk_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Attendance Risk Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Attendance Risk: $subjectName")
            .setContentText("Your attendance is at ${(percentage * 100).toInt()}%, which is below your target.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Placeholder
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(subjectName.hashCode(), notification)
    }
}
