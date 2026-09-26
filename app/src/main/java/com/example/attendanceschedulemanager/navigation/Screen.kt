package com.example.attendanceschedulemanager.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Today : Screen("today", "Today", Icons.Default.EventAvailable)
    object Timetable : Screen("timetable", "Timetable", Icons.Default.CalendarViewWeek)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.PieChart)
    object Materials : Screen("materials", "Materials", Icons.Default.AutoStories)

    companion object {
        val bottomNavItems = listOf(Today, Timetable, Analytics, Materials)
    }
}
