package com.example.attendanceschedulemanager.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    SYSTEM,
    LIGHT, // Theme A — Academic Attendance & Timetable M3
    DARK   // Theme B — Kinetic Campus
}

class ThemeController(
    val themeModeState: MutableState<ThemeMode>,
    private val context: Context
) {
    var themeMode: ThemeMode
        get() = themeModeState.value
        set(value) {
            themeModeState.value = value
            val prefs = context.getSharedPreferences("classtrack_settings", Context.MODE_PRIVATE)
            prefs.edit().putString("theme_mode", value.name).apply()
        }

    fun toggleTheme() {
        themeMode = when (themeMode) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> ThemeMode.DARK
        }
    }
}

val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    error("ThemeController not provided")
}

data class SemanticColors(
    val safe: Color,
    val safeContainer: Color,
    val onSafeContainer: Color,
    val caution: Color,
    val cautionContainer: Color,
    val onCautionContainer: Color,
    val critical: Color,
    val criticalContainer: Color,
    val onCriticalContainer: Color
)

val LightSemanticColors = SemanticColors(
    safe = LightAttendanceSafe,
    safeContainer = LightAttendanceSafeContainer,
    onSafeContainer = LightAttendanceSafeOnContainer,
    caution = LightAttendanceCaution,
    cautionContainer = LightAttendanceCautionContainer,
    onCautionContainer = LightAttendanceCautionOnContainer,
    critical = LightAttendanceCritical,
    criticalContainer = LightAttendanceCriticalContainer,
    onCriticalContainer = LightAttendanceCriticalOnContainer
)

val DarkSemanticColors = SemanticColors(
    safe = DarkAttendanceSafe,
    safeContainer = DarkAttendanceSafeContainer,
    onSafeContainer = DarkAttendanceSafeOnContainer,
    caution = DarkAttendanceCaution,
    cautionContainer = DarkAttendanceCautionContainer,
    onCautionContainer = DarkAttendanceCautionOnContainer,
    critical = DarkAttendanceCritical,
    criticalContainer = DarkAttendanceCriticalContainer,
    onCriticalContainer = DarkAttendanceCriticalOnContainer
)

val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    inversePrimary = DarkInversePrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceDim = DarkSurfaceDim,
    surfaceBright = DarkSurfaceBright,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    background = DarkSurface,
    onBackground = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    inversePrimary = LightInversePrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceDim = LightSurfaceDim,
    surfaceBright = LightSurfaceBright,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    background = LightSurface,
    onBackground = LightOnSurface
)

@Composable
fun ClassTrackTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val savedMode = remember {
        val prefs = context.getSharedPreferences("classtrack_settings", Context.MODE_PRIVATE)
        val modeStr = prefs.getString("theme_mode", themeMode.name) ?: themeMode.name
        try { ThemeMode.valueOf(modeStr) } catch (e: Exception) { ThemeMode.SYSTEM }
    }
    val themeModeState = remember { mutableStateOf(savedMode) }
    val themeController = remember { ThemeController(themeModeState, context) }
    val userProfileController = remember { UserProfileController(context) }

    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeModeState.value) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val semanticColors = if (isDark) DarkSemanticColors else LightSemanticColors
    val typography = if (isDark) DarkTypography else LightTypography
    val shapes = if (isDark) DarkShapes else Shapes

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
                window.isStatusBarContrastEnforced = false
            }
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalThemeController provides themeController,
        LocalSemanticColors provides semanticColors,
        LocalUserProfileController provides userProfileController
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

