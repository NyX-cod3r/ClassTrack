package com.example.attendanceschedulemanager.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Color Hunt Palette: #000000 | #BD4B4B | #EFB7B7 | #EEEEEE
// ==========================================
val PalettePureBlack = Color(0xFF000000)  // #000000
val PaletteCrimson = Color(0xFFBD4B4B)    // #bd4b4b
val PaletteSoftPink = Color(0xFFEFB7B7)   // #efb7b7
val PaletteOffWhite = Color(0xFFEEEEEE)   // #eeeeee

// ==========================================
// Light Color Scheme (#EEEEEE Base Background & Header)
// ==========================================
val LightSurface = Color(0xFFEEEEEE)                  // Palette #EEEEEE
val LightSurfaceDim = Color(0xFFD6D6D6)
val LightSurfaceBright = Color(0xFFF8F8F8)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFE4E4E4)
val LightSurfaceContainer = Color(0xFFDADADA)
val LightSurfaceContainerHigh = Color(0xFFD0D0D0)
val LightSurfaceContainerHighest = Color(0xFFC6C6C6)

val LightOnSurface = Color(0xFF000000)                // Palette #000000
val LightSurfaceVariant = Color(0xFFE0E0E0)
val LightOnSurfaceVariant = Color(0xFF333333)
val LightInverseSurface = Color(0xFF000000)           // Palette #000000
val LightInverseOnSurface = Color(0xFFEEEEEE)
val LightOutline = Color(0xFFBD4B4B)                  // Palette #BD4B4B
val LightOutlineVariant = Color(0xFFCCCCCC)
val LightSurfaceTint = Color(0xFFBD4B4B)

val LightPrimary = Color(0xFFBD4B4B)                  // Palette #BD4B4B
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFF9DADA)
val LightOnPrimaryContainer = Color(0xFF420D0D)
val LightInversePrimary = Color(0xFFEFB7B7)

val LightSecondary = Color(0xFF8C3434)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFEFB7B7)       // Palette #EFB7B7
val LightOnSecondaryContainer = Color(0xFF3B1010)

val LightTertiary = Color(0xFFBD4B4B)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFEFB7B7)
val LightOnTertiaryContainer = Color(0xFF2E0909)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

// Light Attendance Semantics
val LightAttendanceSafe = Color(0xFF2E6F40)
val LightAttendanceSafeContainer = Color(0xFFD5E8D4)
val LightAttendanceSafeOnContainer = Color(0xFF0F3215)

val LightAttendanceCaution = Color(0xFF915712)
val LightAttendanceCautionContainer = Color(0xFFF3E1CA)
val LightAttendanceCautionOnContainer = Color(0xFF381B02)

val LightAttendanceCritical = Color(0xFFBD4B4B)
val LightAttendanceCriticalContainer = Color(0xFFF9DADA)
val LightAttendanceCriticalOnContainer = Color(0xFF420D0D)


// ==========================================
// Dark Color Scheme (Refined Greyish Charcoal Base & Layered Containers)
// ==========================================
val DarkSurface = Color(0xFF141518)                   // Refined Charcoal Grey Base (#141518)
val DarkSurfaceDim = Color(0xFF101113)
val DarkSurfaceBright = Color(0xFF2C2D33)
val DarkSurfaceContainerLowest = Color(0xFF0F1012)
val DarkSurfaceContainerLow = Color(0xFF1B1C20)
val DarkSurfaceContainer = Color(0xFF222328)
val DarkSurfaceContainerHigh = Color(0xFF2A2B31)
val DarkSurfaceContainerHighest = Color(0xFF33353D)

val DarkOnSurface = Color(0xFFF1F1F3)                 // Off-White Soft Text
val DarkSurfaceVariant = Color(0xFF27282E)
val DarkOnSurfaceVariant = Color(0xFFC7C9D0)
val DarkInverseSurface = Color(0xFFEDEFEF)
val DarkInverseOnSurface = Color(0xFF141518)
val DarkOutline = Color(0xFF9E4848)
val DarkOutlineVariant = Color(0xFF423B3E)
val DarkSurfaceTint = Color(0xFFEFB7B7)

val DarkPrimary = Color(0xFFEFB7B7)                   // Palette #EFB7B7 (Soft Pink)
val DarkOnPrimary = Color(0xFF4A1212)
val DarkPrimaryContainer = Color(0xFFBD4B4B)           // Palette #BD4B4B (Crimson Rose)
val DarkOnPrimaryContainer = Color(0xFFFFFFFF)
val DarkInversePrimary = Color(0xFFBD4B4B)

val DarkSecondary = Color(0xFFEFB7B7)                 // Palette #EFB7B7
val DarkOnSecondary = Color(0xFF2B0A0A)
val DarkSecondaryContainer = Color(0xFF521E1E)
val DarkOnSecondaryContainer = Color(0xFFEEEEEE)

val DarkTertiary = Color(0xFFEFB7B7)
val DarkOnTertiary = Color(0xFF2B0A0A)
val DarkTertiaryContainer = Color(0xFF4A1818)
val DarkOnTertiaryContainer = Color(0xFFEEEEEE)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

// Dark Attendance Semantics
val DarkAttendanceSafe = Color(0xFF86A990)
val DarkAttendanceSafeContainer = Color(0x3D86A990)
val DarkAttendanceSafeOnContainer = Color(0xFFEEEEEE)

val DarkAttendanceCaution = Color(0xFFE5B880)
val DarkAttendanceCautionContainer = Color(0x3DE5B880)
val DarkAttendanceCautionOnContainer = Color(0xFFEEEEEE)

val DarkAttendanceCritical = Color(0xFFEFB7B7)
val DarkAttendanceCriticalContainer = Color(0x3DEFB7B7)
val DarkAttendanceCriticalOnContainer = Color(0xFFEFB7B7)

// Common / Backwards compatible aliases
val AttendanceSafe = LightAttendanceSafe
val AttendanceSafeContainer = LightAttendanceSafeContainer
val AttendanceCaution = LightAttendanceCaution
val AttendanceCautionContainer = LightAttendanceCautionContainer
val AttendanceCritical = LightAttendanceCritical
val AttendanceCriticalContainer = LightAttendanceCriticalContainer
