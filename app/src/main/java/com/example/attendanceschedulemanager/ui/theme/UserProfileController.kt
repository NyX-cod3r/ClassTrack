package com.example.attendanceschedulemanager.ui.theme

import android.content.Context
import androidx.compose.runtime.*

data class UserProfile(
    val name: String = "User",
    val semester: String = "1",
    val year: String = "1",
    val profilePictureUri: String? = null
)

class UserProfileController(
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    var profile by mutableStateOf(
        UserProfile(
            name = prefs.getString("name", "User") ?: "User",
            semester = prefs.getString("semester", "1") ?: "1",
            year = prefs.getString("year", "1") ?: "1",
            profilePictureUri = prefs.getString("profile_picture_uri", null)
        )
    )
        private set

    fun updateProfile(newProfile: UserProfile) {
        profile = newProfile
        prefs.edit().apply {
            putString("name", newProfile.name)
            putString("semester", newProfile.semester)
            putString("year", newProfile.year)
            putString("profile_picture_uri", newProfile.profilePictureUri)
        }.apply()
    }
}

val LocalUserProfileController = staticCompositionLocalOf<UserProfileController> {
    error("UserProfileController not provided")
}
