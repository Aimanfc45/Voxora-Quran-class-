package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AuthMode
import com.example.data.model.Teacher
import com.example.data.model.VoxoraMode
import com.example.data.repository.AuthRepository
import com.example.data.repository.EcosystemRepository
import com.example.data.repository.PrayerTimesRepository
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
    val testTag: String,
    val isProminent: Boolean = false
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    QURAN("Quran", Icons.Filled.AutoStories, Icons.Outlined.AutoStories, "nav_quran"),
    CENTRE("Centre", Icons.Filled.Hub, Icons.Outlined.Hub, "nav_centre", isProminent = true),
    JOURNEY("Journey", Icons.Filled.AutoGraph, Icons.Outlined.AutoGraph, "nav_journey"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

enum class SubScreen {
    NONE,
    LIVE_CLASS,
    TEACHER_DISCOVERY,
    PROGRESS_DASHBOARD,
    SALAH_MODE,
    AUTH_SCREEN,
    ONBOARDING,
    DHIKR_MODE,
    DUA_MODE,
    RAMADAN_MODE,
    HAJJ_UMRAH_MODE,
    MASJID_MODE,
    CALENDAR_MODE,
    MODES_HUB,
    CLASSES_SCREEN,
    COMMUNITY_SCREEN
}

enum class AppFlowState {
    SPLASH,
    ONBOARDING,
    AUTH,
    MAIN
}

@Composable
fun VoxoraApp(
    repository: VoxoraRepository = remember { VoxoraRepository() }
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context = context.applicationContext) }
    val prayerTimesRepository = remember { PrayerTimesRepository(context = context.applicationContext) }
    val ecosystemRepository = remember { EcosystemRepository() }

    var appFlowState by remember { mutableStateOf(AppFlowState.SPLASH) }
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

    // Back handling
    BackHandler(enabled = currentSubScreen != SubScreen.NONE) {
        currentSubScreen = SubScreen.NONE
    }
    BackHandler(enabled = currentDestination != MainDestination.HOME && currentSubScreen == SubScreen.NONE) {
        currentDestination = MainDestination.HOME
    }

    // 1. Splash Screen Phase
    if (appFlowState == AppFlowState.SPLASH) {
        SplashScreen(
            onSplashFinished = {
                val onboardingDone = authRepository.isOnboardingCompleted()
                if (!onboardingDone) {
                    appFlowState = AppFlowState.ONBOARDING
                } else if (authRepository.authMode.value == AuthMode.UNAUTHENTICATED) {
                    appFlowState = AppFlowState.AUTH
                } else {
                    val savedUser = authRepository.currentUser.value
                    if (savedUser != null) {
                        repository.authenticateUser(savedUser.email, "")
                    } else {
                        repository.continueAsGuestUser()
                    }
                    appFlowState = AppFlowState.MAIN
                }
            }
        )
        return
    }

    // 2. First-Launch 5-Screen Onboarding Flow
    if (appFlowState == AppFlowState.ONBOARDING) {
        OnboardingScreen(
            onFinishOnboarding = { goToAuth ->
                authRepository.setOnboardingCompleted(true)
                repository.completeOnboarding()
                if (goToAuth) {
                    appFlowState = AppFlowState.AUTH
                } else {
                    appFlowState = AppFlowState.MAIN
                }
            }
        )
        return
    }

    // 3. Welcome / Authentication Entry Flow
    if (appFlowState == AppFlowState.AUTH) {
        AuthScreen(
            repository = repository,
            authRepository = authRepository,
            onAuthSuccess = {
                appFlowState = AppFlowState.MAIN
                currentSubScreen = SubScreen.NONE
            },
            onBack = if (authRepository.isOnboardingCompleted()) {
                { appFlowState = AppFlowState.MAIN }
            } else null,
            onShowSnackbar = showSnackbar
        )
        return
    }

    // 4. Main Application
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

                    // Bottom Navigation Bar on standard mobile layout (Centre prominent!)
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
                                        if (destination.isProminent) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) GoldPrimary else Emerald700,
                                                border = BorderStroke(1.dp, if (isSelected) Emerald700 else GoldPrimary),
                                                modifier = Modifier.size(38.dp),
                                                shadowElevation = 4.dp
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = destination.filledIcon,
                                                        contentDescription = destination.title,
                                                        tint = if (isSelected) Color.Black else Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            Icon(
                                                imageVector = if (isSelected) destination.filledIcon else destination.outlinedIcon,
                                                contentDescription = destination.title,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
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
                                        indicatorColor = if (destination.isProminent) Color.Transparent else Emerald700.copy(alpha = 0.12f),
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
                                if (destination.isProminent) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) GoldPrimary else Emerald700,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = destination.filledIcon,
                                                contentDescription = destination.title,
                                                tint = if (isSelected) Color.Black else Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) destination.filledIcon else destination.outlinedIcon,
                                        contentDescription = destination.title
                                    )
                                }
                            },
                            label = { Text(destination.title) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Emerald700,
                                selectedTextColor = Emerald700,
                                indicatorColor = if (destination.isProminent) Color.Transparent else Emerald700.copy(alpha = 0.12f)
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
                                if (goToAuth) {
                                    currentSubScreen = SubScreen.AUTH_SCREEN
                                } else {
                                    currentSubScreen = SubScreen.NONE
                                }
                            }
                        )
                    }
                    SubScreen.AUTH_SCREEN -> {
                        AuthScreen(
                            repository = repository,
                            authRepository = authRepository,
                            onAuthSuccess = { currentSubScreen = SubScreen.NONE },
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.SALAH_MODE -> {
                        SalahModeScreen(
                            prayerTimesRepository = prayerTimesRepository,
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.DHIKR_MODE -> {
                        DhikrModeScreen(
                            ecosystemRepository = ecosystemRepository,
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.DUA_MODE -> {
                        DuaModeScreen(
                            ecosystemRepository = ecosystemRepository,
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.RAMADAN_MODE -> {
                        RamadanModeScreen(
                            ecosystemRepository = ecosystemRepository,
                            prayerTimesRepository = prayerTimesRepository,
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onNavigateToQuran = {
                                currentSubScreen = SubScreen.NONE
                                currentDestination = MainDestination.QURAN
                            },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.HAJJ_UMRAH_MODE -> {
                        HajjUmrahModeScreen(
                            ecosystemRepository = ecosystemRepository,
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.MASJID_MODE -> {
                        MasjidModeScreen(
                            ecosystemRepository = ecosystemRepository,
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.CALENDAR_MODE -> {
                        IslamicCalendarModeScreen(
                            ecosystemRepository = ecosystemRepository,
                            onBack = { currentSubScreen = SubScreen.NONE },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.MODES_HUB -> {
                        CentreHubScreen(
                            repository = repository,
                            prayerTimesRepository = prayerTimesRepository,
                            ecosystemRepository = ecosystemRepository,
                            onSelectMode = { mode ->
                                when (mode) {
                                    VoxoraMode.HOME -> {
                                        currentSubScreen = SubScreen.NONE
                                        currentDestination = MainDestination.HOME
                                    }
                                    VoxoraMode.QURAN -> {
                                        currentSubScreen = SubScreen.NONE
                                        currentDestination = MainDestination.QURAN
                                    }
                                    VoxoraMode.SALAH -> {
                                        currentSubScreen = SubScreen.SALAH_MODE
                                    }
                                    VoxoraMode.LEARNING -> {
                                        currentSubScreen = SubScreen.CLASSES_SCREEN
                                    }
                                    VoxoraMode.LIVE_CLASS -> {
                                        currentSubScreen = SubScreen.LIVE_CLASS
                                    }
                                    VoxoraMode.DHIKR -> {
                                        currentSubScreen = SubScreen.DHIKR_MODE
                                    }
                                    VoxoraMode.DUA -> {
                                        currentSubScreen = SubScreen.DUA_MODE
                                    }
                                    VoxoraMode.RAMADAN -> {
                                        currentSubScreen = SubScreen.RAMADAN_MODE
                                    }
                                    VoxoraMode.HAJJ_UMRAH -> {
                                        currentSubScreen = SubScreen.HAJJ_UMRAH_MODE
                                    }
                                    VoxoraMode.MASJID -> {
                                        currentSubScreen = SubScreen.MASJID_MODE
                                    }
                                    VoxoraMode.CALENDAR -> {
                                        currentSubScreen = SubScreen.CALENDAR_MODE
                                    }
                                    VoxoraMode.PROFILE -> {
                                        currentSubScreen = SubScreen.NONE
                                        currentDestination = MainDestination.PROFILE
                                    }
                                }
                            },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.LIVE_CLASS -> {
                        LiveClassScreen(
                            repository = repository,
                            authRepository = authRepository,
                            onLeaveClass = { currentSubScreen = SubScreen.NONE },
                            onNavigateToAuth = { currentSubScreen = SubScreen.AUTH_SCREEN },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.TEACHER_DISCOVERY -> {
                        TeacherDiscoveryScreen(
                            repository = repository,
                            onBookTeacherSuccess = {
                                currentSubScreen = SubScreen.CLASSES_SCREEN
                            },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.PROGRESS_DASHBOARD -> {
                        JourneyScreen(
                            repository = repository,
                            prayerTimesRepository = prayerTimesRepository,
                            ecosystemRepository = ecosystemRepository,
                            onNavigateToQuran = {
                                currentSubScreen = SubScreen.NONE
                                currentDestination = MainDestination.QURAN
                            },
                            onNavigateToSalah = {
                                currentSubScreen = SubScreen.SALAH_MODE
                            },
                            onNavigateToDhikr = {
                                currentSubScreen = SubScreen.DHIKR_MODE
                            },
                            onNavigateToLearning = {
                                currentSubScreen = SubScreen.CLASSES_SCREEN
                            },
                            onNavigateToLiveClass = {
                                currentSubScreen = SubScreen.LIVE_CLASS
                            },
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.CLASSES_SCREEN -> {
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
                    SubScreen.COMMUNITY_SCREEN -> {
                        CommunityScreen(
                            repository = repository,
                            onShowSnackbar = showSnackbar
                        )
                    }
                    SubScreen.NONE -> {
                        when (currentDestination) {
                            MainDestination.HOME -> {
                                HomeScreen(
                                    repository = repository,
                                    prayerTimesRepository = prayerTimesRepository,
                                    ecosystemRepository = ecosystemRepository,
                                    onNavigateToQuran = {
                                        currentDestination = MainDestination.QURAN
                                    },
                                    onNavigateToLiveClass = {
                                        currentSubScreen = SubScreen.LIVE_CLASS
                                    },
                                    onNavigateToClasses = {
                                        currentSubScreen = SubScreen.CLASSES_SCREEN
                                    },
                                    onNavigateToTeachers = {
                                        currentSubScreen = SubScreen.TEACHER_DISCOVERY
                                    },
                                    onNavigateToCommunity = {
                                        currentSubScreen = SubScreen.COMMUNITY_SCREEN
                                    },
                                    onNavigateToProgress = {
                                        currentDestination = MainDestination.JOURNEY
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
                                    },
                                    onNavigateToDhikr = {
                                        currentSubScreen = SubScreen.DHIKR_MODE
                                    },
                                    onNavigateToDua = {
                                        currentSubScreen = SubScreen.DUA_MODE
                                    },
                                    onNavigateToRamadan = {
                                        currentSubScreen = SubScreen.RAMADAN_MODE
                                    },
                                    onNavigateToHajjUmrah = {
                                        currentSubScreen = SubScreen.HAJJ_UMRAH_MODE
                                    },
                                    onNavigateToMasjid = {
                                        currentSubScreen = SubScreen.MASJID_MODE
                                    },
                                    onNavigateToCalendar = {
                                        currentSubScreen = SubScreen.CALENDAR_MODE
                                    },
                                    onNavigateToModesHub = {
                                        currentDestination = MainDestination.CENTRE
                                    }
                                )
                            }
                            MainDestination.QURAN -> {
                                QuranReaderScreen(
                                    repository = repository,
                                    onShowSnackbar = showSnackbar
                                )
                            }
                            MainDestination.CENTRE -> {
                                CentreHubScreen(
                                    repository = repository,
                                    prayerTimesRepository = prayerTimesRepository,
                                    ecosystemRepository = ecosystemRepository,
                                    onSelectMode = { mode ->
                                        when (mode) {
                                            VoxoraMode.HOME -> {
                                                currentDestination = MainDestination.HOME
                                            }
                                            VoxoraMode.QURAN -> {
                                                currentDestination = MainDestination.QURAN
                                            }
                                            VoxoraMode.SALAH -> {
                                                currentSubScreen = SubScreen.SALAH_MODE
                                            }
                                            VoxoraMode.LEARNING -> {
                                                currentSubScreen = SubScreen.CLASSES_SCREEN
                                            }
                                            VoxoraMode.LIVE_CLASS -> {
                                                currentSubScreen = SubScreen.LIVE_CLASS
                                            }
                                            VoxoraMode.DHIKR -> {
                                                currentSubScreen = SubScreen.DHIKR_MODE
                                            }
                                            VoxoraMode.DUA -> {
                                                currentSubScreen = SubScreen.DUA_MODE
                                            }
                                            VoxoraMode.RAMADAN -> {
                                                currentSubScreen = SubScreen.RAMADAN_MODE
                                            }
                                            VoxoraMode.HAJJ_UMRAH -> {
                                                currentSubScreen = SubScreen.HAJJ_UMRAH_MODE
                                            }
                                            VoxoraMode.MASJID -> {
                                                currentSubScreen = SubScreen.MASJID_MODE
                                            }
                                            VoxoraMode.CALENDAR -> {
                                                currentSubScreen = SubScreen.CALENDAR_MODE
                                            }
                                            VoxoraMode.PROFILE -> {
                                                currentDestination = MainDestination.PROFILE
                                            }
                                        }
                                    },
                                    onShowSnackbar = showSnackbar
                                )
                            }
                            MainDestination.JOURNEY -> {
                                JourneyScreen(
                                    repository = repository,
                                    prayerTimesRepository = prayerTimesRepository,
                                    ecosystemRepository = ecosystemRepository,
                                    onNavigateToQuran = {
                                        currentDestination = MainDestination.QURAN
                                    },
                                    onNavigateToSalah = {
                                        currentSubScreen = SubScreen.SALAH_MODE
                                    },
                                    onNavigateToDhikr = {
                                        currentSubScreen = SubScreen.DHIKR_MODE
                                    },
                                    onNavigateToLearning = {
                                        currentSubScreen = SubScreen.CLASSES_SCREEN
                                    },
                                    onNavigateToLiveClass = {
                                        currentSubScreen = SubScreen.LIVE_CLASS
                                    },
                                    onShowSnackbar = showSnackbar
                                )
                            }
                            MainDestination.PROFILE -> {
                                ProfileSettingsScreen(
                                    repository = repository,
                                    authRepository = authRepository,
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
