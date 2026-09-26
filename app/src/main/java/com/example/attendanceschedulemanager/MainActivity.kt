package com.example.attendanceschedulemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.attendanceschedulemanager.navigation.Screen
import com.example.attendanceschedulemanager.ui.components.AppTopHeader
import com.example.attendanceschedulemanager.ui.components.bouncyClickable
import com.example.attendanceschedulemanager.ui.components.liquidGlass
import com.example.attendanceschedulemanager.ui.screens.analytics.AnalyticsScreen
import com.example.attendanceschedulemanager.ui.screens.materials.MaterialsScreen
import com.example.attendanceschedulemanager.ui.screens.timetable.TimetableScreen
import com.example.attendanceschedulemanager.ui.screens.today.TodayScreen
import com.example.attendanceschedulemanager.ui.theme.ClassTrackTheme
import com.example.attendanceschedulemanager.ui.theme.LocalUserProfileController
import com.example.attendanceschedulemanager.ui.theme.UserProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private enum class NavigationAnimation {
    SWIPE,
    NAV_BAR_TAP
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        setContent {
            ClassTrackTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val userProfileController = LocalUserProfileController.current
    var showProfileSheet by remember { mutableStateOf(false) }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomNavItems = Screen.bottomNavItems
    var navigationAnimation by remember { mutableStateOf(NavigationAnimation.SWIPE) }
    val currentIndex = bottomNavItems.indexOfFirst { it.route == currentDestination?.route }

    val navigateTo: (String, NavigationAnimation) -> Unit = { route, animation ->
        navigationAnimation = animation
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LaunchedEffect(navigationAnimation) {
        if (navigationAnimation == NavigationAnimation.NAV_BAR_TAP) {
            delay(350)
            navigationAnimation = NavigationAnimation.SWIPE
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Keep page content below the shared header; the bottom bar remains an overlay.
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(currentDestination?.route) {
                    var horizontalDrag = 0f
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount -> horizontalDrag += dragAmount },
                        onDragEnd = {
                            if (abs(horizontalDrag) > 96f && currentIndex >= 0) {
                                val nextIndex = if (horizontalDrag < 0) {
                                    currentIndex + 1
                                } else {
                                    currentIndex - 1
                                }
                                bottomNavItems.getOrNull(nextIndex)?.let { screen ->
                                    navigateTo(screen.route, NavigationAnimation.SWIPE)
                                }
                            }
                        }
                    )
                },
            enterTransition = {
                if (navigationAnimation == NavigationAnimation.NAV_BAR_TAP) {
                    val targetIndex = bottomNavItems.indexOfFirst { it.route == targetState.destination.route }
                    val originX = ((targetIndex + 0.5f) / bottomNavItems.size).coerceIn(0f, 1f)
                    scaleIn(
                        animationSpec = tween(280),
                        initialScale = 0.82f,
                        transformOrigin = TransformOrigin(originX, 1f)
                    ) + fadeIn(animationSpec = tween(180))
                } else {
                    val targetIndex = bottomNavItems.indexOfFirst { it.route == targetState.destination.route }
                    val initialIndex = bottomNavItems.indexOfFirst { it.route == initialState.destination.route }
                    val animationSpec = spring<IntOffset>(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                    if (targetIndex > initialIndex) {
                        slideInHorizontally(animationSpec = animationSpec, initialOffsetX = { it }) +
                            fadeIn(animationSpec = tween(300))
                    } else {
                        slideInHorizontally(animationSpec = animationSpec, initialOffsetX = { -it }) +
                            fadeIn(animationSpec = tween(300))
                    }
                }
            },
            exitTransition = {
                if (navigationAnimation == NavigationAnimation.NAV_BAR_TAP) {
                    fadeOut(animationSpec = tween(120))
                } else {
                    val targetIndex = bottomNavItems.indexOfFirst { it.route == targetState.destination.route }
                    val initialIndex = bottomNavItems.indexOfFirst { it.route == initialState.destination.route }
                    val animationSpec = spring<IntOffset>(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                    if (targetIndex > initialIndex) {
                        slideOutHorizontally(animationSpec = animationSpec, targetOffsetX = { -it }) +
                            fadeOut(animationSpec = tween(300))
                    } else {
                        slideOutHorizontally(animationSpec = animationSpec, targetOffsetX = { it }) +
                            fadeOut(animationSpec = tween(300))
                    }
                }
            }
        ) {
            composable(Screen.Today.route) { 
                TodayScreen(onProfileClick = { showProfileSheet = true })
            }
            composable(Screen.Timetable.route) {
                TimetableScreen(onProfileClick = { showProfileSheet = true })
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen(onProfileClick = { showProfileSheet = true })
            }
            composable(Screen.Materials.route) {
                MaterialsScreen(onProfileClick = { showProfileSheet = true })
            }
        }

        // Translucent Glass Top App Header (Overlay)
        AppTopHeader(
            modifier = Modifier.align(Alignment.TopCenter),
            title = "ClassTrack",
            subtitle = "Attendance & Schedule",
            onNotificationClick = { },
            onProfileClick = { showProfileSheet = true }
        )

        // Floating Translucent Blurred Navigation Bar (Android 15+ Glass Pill Effect)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 36.dp)
                .padding(bottom = 4.dp)
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .liquidGlass(CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.88f),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.32f)
                ),
                tonalElevation = 1.dp,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f) 
                                    else Color.Transparent
                                )
                                .bouncyClickable {
                                    navigateTo(screen.route, NavigationAnimation.NAV_BAR_TAP)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            ProfileEditSheetContent(
                onClose = { showProfileSheet = false }
            )
        }
    }
}

@Composable
fun ProfileEditSheetContent(onClose: () -> Unit) {
    val controller = LocalUserProfileController.current
    val profile = controller.profile
    
    var name by remember { mutableStateOf(profile.name) }
    var semester by remember { mutableStateOf(profile.semester) }
    var year by remember { mutableStateOf(profile.year) }
    var dpUri by remember { mutableStateOf(profile.profilePictureUri) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            dpUri = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        // Profile Picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (dpUri != null) {
                AsyncImage(
                    model = dpUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Image",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = semester,
                onValueChange = { semester = it },
                label = { Text("Semester") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Year") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Button(
            onClick = {
                controller.updateProfile(
                    UserProfile(
                        name = name,
                        semester = semester,
                        year = year,
                        profilePictureUri = dpUri
                    )
                )
                onClose()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape
        ) {
            Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
