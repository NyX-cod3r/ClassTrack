package com.example.attendanceschedulemanager.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Dark theme shapes synchronized with light theme for consistency
val DarkShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

object Spacing {
    val spaceXXS = 2.dp
    val spaceXS = 4.dp
    val spaceSM = 8.dp
    val spaceMD = 16.dp
    val spaceLG = 24.dp
    val spaceXL = 32.dp
    val spaceXXL = 48.dp
}
