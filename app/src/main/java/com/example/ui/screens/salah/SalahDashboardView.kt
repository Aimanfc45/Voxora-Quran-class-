package com.example.ui.screens.salah

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailySalahProgress
import com.example.data.model.PrayerName
import com.example.data.model.SalahLearningProgress
import com.example.data.repository.PrayerTimesRepository
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahDashboardView(
    prayerTimesRepository: PrayerTimesRepository,
    onBack: () -> Unit,
    onOpenSalahGuide: () -> Unit,
    onOpenQiblahCompass: () -> Unit,
    onOpenQiblah3DMap: () -> Unit,
    onOpenPrayerSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prayerState by prayerTimesRepository.prayerState.collectAsState()
    val selectedLocation by prayerTimesRepository.selectedLocation.collectAsState()
    val dailyProgress by prayerTimesRepository.dailySalahProgress.collectAsState()
    val learningProgress by prayerTimesRepository.salahLearningProgress.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Salah Experience",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "📍 ${selectedLocation.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald300,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { prayerTimesRepository.refreshPrayerTimes() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = GoldLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Emerald950)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize().testTag("salah_dashboard_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Large Modern Salah Clock Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald950),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.08f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Location & Hijri Date Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Emerald800
                            ) {
                                Text(
                                    text = "ZONE: ${selectedLocation.zoneCode}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = prayerState.schedule.hijriFormatted,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = Emerald300
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Digital Clock
                        Text(
                            text = prayerState.currentTimeFormatted,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum",
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${prayerState.currentDayFormatted}, ${prayerState.currentDateFormatted}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Emerald200
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next Prayer Banner Inside Clock Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Emerald900.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, Emerald700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "NEXT PRAYER",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                letterSpacing = 1.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = GoldLight
                                        )
                                        Text(
                                            text = "${prayerState.nextPrayer.englishName} (${prayerState.nextPrayer.time12})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = GoldPrimary.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                                    ) {
                                        Text(
                                            text = prayerState.formattedCountdown,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFeatureSettings = "tnum"
                                            ),
                                            color = GoldLight,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { prayerState.progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = GoldPrimary,
                                    trackColor = Emerald950
                                )
                            }
                        }
                    }
                }
            }

            // 2. The 4 Main Action Hubs Grid
            Text(
                text = "SALAH SUITE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Emerald800
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Start Salah Guide
                ActionHubCard(
                    modifier = Modifier.weight(1f),
                    title = "Salah Guide",
                    subtitle = "Step-by-step raka'at & recitations",
                    icon = Icons.Default.SelfImprovement,
                    iconEmoji = "🕌",
                    badge = "Canonical",
                    containerColor = Emerald800,
                    contentColor = Color.White,
                    onClick = onOpenSalahGuide,
                    testTag = "hub_salah_guide"
                )

                // Card 2: Find Qiblah
                ActionHubCard(
                    modifier = Modifier.weight(1f),
                    title = "Find Qiblah",
                    subtitle = "Live compass sensor & bearing",
                    icon = Icons.Default.Explore,
                    iconEmoji = "🧭",
                    badge = "Sensor",
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = onOpenQiblahCompass,
                    testTag = "hub_find_qiblah"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 3: Qiblah 3D Map
                ActionHubCard(
                    modifier = Modifier.weight(1f),
                    title = "3D Qiblah Map",
                    subtitle = "Spherical globe & geodesic arc",
                    icon = Icons.Default.Public,
                    iconEmoji = "🌍",
                    badge = "3D Globe",
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = onOpenQiblah3DMap,
                    testTag = "hub_qiblah_3d_map"
                )

                // Card 4: Prayer Times
                ActionHubCard(
                    modifier = Modifier.weight(1f),
                    title = "Prayer Times",
                    subtitle = "JAKIM zones & schedule",
                    icon = Icons.Default.CalendarMonth,
                    iconEmoji = "🕐",
                    badge = "JAKIM",
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = onOpenPrayerSchedule,
                    testTag = "hub_prayer_times"
                )
            }

            // 3. Today's Daily Salah Checklist
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Emerald100)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TODAY'S SALAH TRACKER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Emerald800
                            )
                            Text(
                                text = "${dailyProgress.completedCount} of 5 Completed",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Circular Progress Indicator
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { dailyProgress.progressFraction },
                                modifier = Modifier.size(42.dp),
                                color = GoldPrimary,
                                trackColor = Emerald100,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "${(dailyProgress.progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = Emerald900
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5 Prayer Rows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrayerCheckRow(
                            prayerName = "Fajr (Subuh)",
                            arabicName = "الفجر",
                            isDone = dailyProgress.fajrCompleted,
                            onToggle = { prayerTimesRepository.toggleSalahCompleted(PrayerName.FAJR) }
                        )
                        PrayerCheckRow(
                            prayerName = "Dhuhr (Zohor)",
                            arabicName = "الظهر",
                            isDone = dailyProgress.dhuhrCompleted,
                            onToggle = { prayerTimesRepository.toggleSalahCompleted(PrayerName.DHUHR) }
                        )
                        PrayerCheckRow(
                            prayerName = "Asr (Asar)",
                            arabicName = "العصر",
                            isDone = dailyProgress.asrCompleted,
                            onToggle = { prayerTimesRepository.toggleSalahCompleted(PrayerName.ASR) }
                        )
                        PrayerCheckRow(
                            prayerName = "Maghrib",
                            arabicName = "المغرب",
                            isDone = dailyProgress.maghribCompleted,
                            onToggle = { prayerTimesRepository.toggleSalahCompleted(PrayerName.MAGHRIB) }
                        )
                        PrayerCheckRow(
                            prayerName = "Isha (Isyak)",
                            arabicName = "العشاء",
                            isDone = dailyProgress.ishaCompleted,
                            onToggle = { prayerTimesRepository.toggleSalahCompleted(PrayerName.ISHA) }
                        )
                    }
                }
            }

            // 4. Learning Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald50),
                border = BorderStroke(1.dp, Emerald200)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Emerald800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SALAH LEARNING PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                            color = Emerald800
                        )
                        Text(
                            text = "${learningProgress.completedStepIds.size} of ${learningProgress.totalSteps} steps mastered",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Emerald950
                        )
                        Text(
                            text = "Last practiced: ${learningProgress.lastPracticedPrayer} • ${learningProgress.completedPrayersCount} full sessions completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald700
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionHubCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconEmoji: String,
    badge: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, if (containerColor == Emerald800) GoldPrimary.copy(alpha = 0.3f) else Emerald100)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (containerColor == Emerald800) GoldPrimary.copy(alpha = 0.2f) else Emerald100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 20.sp)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (containerColor == Emerald800) GoldPrimary.copy(alpha = 0.25f) else Emerald50
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = if (containerColor == Emerald800) GoldLight else Emerald800,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = if (containerColor == Emerald800) Emerald200 else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PrayerCheckRow(
    prayerName: String,
    arabicName: String,
    isDone: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDone) Emerald100.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isDone) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isDone) Emerald900 else MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = arabicName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDone) Emerald800 else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
