package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.PrayerType
import com.example.data.repository.PrayerTimesRepository
import com.example.ui.components.PrayerTimesScheduleDialog
import com.example.ui.screens.salah.*

enum class SalahSubView {
    DASHBOARD,
    GUIDE,
    QIBLAH_COMPASS,
    QIBLAH_3D_MAP
}

@Composable
fun SalahModeScreen(
    prayerTimesRepository: PrayerTimesRepository = remember { PrayerTimesRepository() },
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isIntroDone by prayerTimesRepository.isSalahOnboardingDone.collectAsState()
    var currentSubView by remember { mutableStateOf(SalahSubView.DASHBOARD) }
    var selectedPrayerForGuide by remember { mutableStateOf(PrayerType.FAJR) }
    var showPrayerScheduleDialog by remember { mutableStateOf(false) }

    if (!isIntroDone) {
        SalahModeIntro(
            onFinishIntro = {
                prayerTimesRepository.setSalahOnboardingCompleted(true)
            },
            modifier = modifier
        )
    } else {
        Box(modifier = modifier.fillMaxSize().testTag("salah_mode_screen")) {
            AnimatedContent(
                targetState = currentSubView,
                transitionSpec = {
                    if (targetState != SalahSubView.DASHBOARD) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "salah_subview_anim"
            ) { subView ->
                when (subView) {
                    SalahSubView.DASHBOARD -> {
                        SalahDashboardView(
                            prayerTimesRepository = prayerTimesRepository,
                            onBack = onBack,
                            onOpenSalahGuide = { currentSubView = SalahSubView.GUIDE },
                            onOpenQiblahCompass = { currentSubView = SalahSubView.QIBLAH_COMPASS },
                            onOpenQiblah3DMap = { currentSubView = SalahSubView.QIBLAH_3D_MAP },
                            onOpenPrayerSchedule = { showPrayerScheduleDialog = true }
                        )
                    }
                    SalahSubView.GUIDE -> {
                        SalahGuideView(
                            prayerTimesRepository = prayerTimesRepository,
                            initialPrayer = selectedPrayerForGuide,
                            onBack = { currentSubView = SalahSubView.DASHBOARD },
                            onShowSnackbar = onShowSnackbar
                        )
                    }
                    SalahSubView.QIBLAH_COMPASS -> {
                        QiblahCompassView(
                            prayerTimesRepository = prayerTimesRepository,
                            onBack = { currentSubView = SalahSubView.DASHBOARD }
                        )
                    }
                    SalahSubView.QIBLAH_3D_MAP -> {
                        Qiblah3DMapView(
                            prayerTimesRepository = prayerTimesRepository,
                            onBack = { currentSubView = SalahSubView.DASHBOARD }
                        )
                    }
                }
            }

            if (showPrayerScheduleDialog) {
                PrayerTimesScheduleDialog(
                    prayerTimesRepository = prayerTimesRepository,
                    onDismiss = { showPrayerScheduleDialog = false },
                    onNavigateToSalahMode = {
                        showPrayerScheduleDialog = false
                        currentSubView = SalahSubView.GUIDE
                    }
                )
            }
        }
    }
}
