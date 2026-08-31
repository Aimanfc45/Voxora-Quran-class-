package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.QuranClass
import com.example.data.model.Teacher
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.QuranAudioDetailBottomSheet
import com.example.ui.components.QuranMiniAudioPlayer
import com.example.ui.screens.*
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

enum class MainDestination(
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    CLASSES("Classes", Icons.Filled.School, Icons.Outlined.School, "nav_classes"),
    QURAN("Quran", Icons.Filled.AutoStories, Icons.Outlined.AutoStories, "nav_quran"),
    COMMUNITY("Community", Icons.Filled.Groups, Icons.Outlined.Groups, "nav_community"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

enum class SubScreen {
    NONE,
    LIVE_CLASS,
    TEACHER_DISCOVERY,
    PROGRESS_DASHBOARD,
    SALAH_MODE,
    AUTH_SCREEN,
    ONBOARDING
}

@Composable
fun VoxoraApp(
    repository: VoxoraRepository = remember { VoxoraRepository() }
) {
    val hasCompletedOnboarding by repository.hasCompletedOnboarding.collectAsState()
    var currentDestination by remember { mutableStateOf(MainDestination.HOME) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }
    var selectedTeacherForDiscovery by remember { mutableStateOf<Teacher?>(null) }

    val audioState by repository.audioState.collectAsState()
    val selectedSurah by repository.selectedSurah.collectAsState()
    var showAudioDetailSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 720

    // First Launch Onboarding Flow
    if (!hasCompletedOnboarding) {
        OnboardingScreen(
            onFinishOnboarding = { goToAuth ->
                repository.completeOnboarding()
                if (goToAuth) {
                    currentSubScreen = SubScreen.AUTH_SCREEN
                } else {
                    currentSubScreen = SubScreen.NONE
                }
            }
        )
        return
    }

    val isFullScreenSubScreen = currentSubScreen == SubScreen.LIVE_CLASS || 
                                currentSubScreen == SubScreen.AUTH_SCREEN || 
                                currentSubScreen == SubScreen.ONBOARDING

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = Emerald900,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = data.visuals.message,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (!isFullScreenSubScreen) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Global persistent mini audio player
                    val isAudioActive = audioState.isPlaying || audioState.isLoading || audioState.currentPositionSeconds > 0f
                    if (isAudioActive) {
                        QuranMiniAudioPlayer(
                            audioState = audioState,
                            surahName = selectedSurah.nameEnglish,
                            onExpandControls = { showAudioDetailSheet = true },
                            onTogglePlay = { repository.toggleAudioPlayback() },
                            onNextVerse = { repository.nextAudioVerse() },
                            onPreviousVerse = { repository.previousAudioVerse() },
                            onClose = { repository.stopAudio() }
                        )
                    }

                    // Bottom Navigation Bar on standard mobile layout
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp,
                            modifier = Modifier.testTag("bottom_navigation_bar")
                        ) {
                            MainDestination.values().forEach { destination ->
                                val isSelected = currentDestination == destination && currentSubScreen == SubScreen.NONE
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        currentSubScreen = SubScreen.NONE
                                        currentDestination = destination
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) destination.filledIcon else destination.outlinedIcon,
                                            contentDescription = destination.title,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destination.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Emerald700,
                                        selectedTextColor = Emerald700,
                                        indicatorColor = Emerald700.copy(alpha = 0.12f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag(destination.testTag)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullScreenSubScreen) PaddingValues(0.dp) else paddingValues)
        ) {
            // Wide Screen Desktop Sidebar Navigation Rail
            if (isWideScreen && !isFullScreenSubScreen) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_voxora_logo),
                                contentDescription = "VOXORA",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    modifier = Modifier.testTag("desktop_sidebar_navigation")
                ) {
                    MainDestination.values().forEach { destination ->
                        val isSelected = currentDestination == destination && currentSubScreen == SubScreen.NONE
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = {
                                currentSubScreen = SubScreen.NONE
                                currentDestination = destination
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.filledIcon else destination.outlinedIcon,
                                    contentDescription = destination.title
                                )
                            },
                            label = { Text(destination.title) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Emerald700,
                                selectedTextColor = Emerald700,
                                indicatorColor = Emerald700.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }

            // Screen Content Routing
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                when (currentSubScreen) {
                    SubScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onFinishOnboarding = { goToAuth ->
                                currentSubScreen = if (goToAuth) SubScreen.AUTH_SCREEN else SubScreen.NONE
                            }
                        )
                    }
                    SubScreen.AUTH_SCREEN -> {
                        AuthScreen(
                            repository = repository,
                            onAuthSuccess = { currentSubScreen = SubScreen.NONE },
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.SALAH_MODE -> {
                        SalahModeScreen(
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.LIVE_CLASS -> {
                        LiveClassScreen(
                            repository = repository,
                            onLeaveClass = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.TEACHER_DISCOVERY -> {
                        TeacherDiscoveryScreen(
                            repository = repository,
                            onBookTeacherSuccess = {
                                currentSubScreen = SubScreen.NONE
                                currentDestination = MainDestination.CLASSES
                            },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.PROGRESS_DASHBOARD -> {
                        ProgressScreen(
                            repository = repository,
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.NONE -> {
                        when (currentDestination) {
                            MainDestination.HOME -> {
                                HomeScreen(
                                    repository = repository,
                                    onNavigateToQuran = {
                                        currentDestination = MainDestination.QURAN
                                    },
                                    onNavigateToLiveClass = {
                                        currentSubScreen = SubScreen.LIVE_CLASS
                                    },
                                    onNavigateToClasses = {
                                        currentDestination = MainDestination.CLASSES
                                    },
                                    onNavigateToTeachers = {
                                        currentSubScreen = SubScreen.TEACHER_DISCOVERY
                                    },
                                    onNavigateToCommunity = {
                                        currentDestination = MainDestination.COMMUNITY
                                    },
                                    onNavigateToProgress = {
                                        currentSubScreen = SubScreen.PROGRESS_DASHBOARD
                                    },
                                    onNavigateToProfile = {
                                        currentDestination = MainDestination.PROFILE
                                    },
                                    onTeacherSelect = { teacher ->
                                        selectedTeacherForDiscovery = teacher
                                        currentSubScreen = SubScreen.TEACHER_DISCOVERY
                                    },
                                    onShowSnackbar = showSnackbar,
                                    onNavigateToSalahMode = {
                                        currentSubScreen = SubScreen.SALAH_MODE
                                    },
                                    onNavigateToAuth = {
                                        currentSubScreen = SubScreen.AUTH_SCREEN
                                    }
                                )
                            }
                            MainDestination.CLASSES -> {
                                ClassesScreen(
                                    repository = repository,
                                    onJoinLiveClass = {
                                        currentSubScreen = SubScreen.LIVE_CLASS
                                    },
                                    onExploreTeachers = {
                                        currentSubScreen = SubScreen.TEACHER_DISCOVERY
                                    },
                                    onShowSnackbar = showSnackbar
                                )
                            }
                            MainDestination.QURAN -> {
                                QuranReaderScreen(
                                    repository = repository,
                                    onShowSnackbar = showSnackbar
                                )
                            }
                            MainDestination.COMMUNITY -> {
                                CommunityScreen(
                                    repository = repository,
                                    onShowSnackbar = showSnackbar
                                )
                            }
                            MainDestination.PROFILE -> {
                                ProfileSettingsScreen(
                                    repository = repository,
                                    onShowSnackbar = showSnackbar,
                                    onNavigateToAuth = {
                                        currentSubScreen = SubScreen.AUTH_SCREEN
                                    },
                                    onNavigateToOnboarding = {
                                        currentSubScreen = SubScreen.ONBOARDING
                                    },
                                    onNavigateToSalahMode = {
                                        currentSubScreen = SubScreen.SALAH_MODE
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Global Quran Audio Detail Bottom Sheet
    if (showAudioDetailSheet) {
        QuranAudioDetailBottomSheet(
            audioState = audioState,
            surahName = selectedSurah.nameEnglish,
            repository = repository,
            onDismiss = { showAudioDetailSheet = false }
        )
    }
}
