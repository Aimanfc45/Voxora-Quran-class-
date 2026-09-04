package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerName
import com.example.data.repository.EcosystemRepository
import com.example.data.repository.PrayerTimesRepository
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*

@Composable
fun JourneyScreen(
    repository: VoxoraRepository,
    prayerTimesRepository: PrayerTimesRepository,
    ecosystemRepository: EcosystemRepository,
    onNavigateToQuran: () -> Unit,
    onNavigateToSalah: () -> Unit,
    onNavigateToDhikr: () -> Unit,
    onNavigateToLearning: () -> Unit,
    onNavigateToLiveClass: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by repository.userProfile.collectAsState()
    val bookmarks by repository.bookmarks.collectAsState()
    val notes by repository.verseNotes.collectAsState()
    val lastRead by repository.lastReadPosition.collectAsState()
    val prayerState by prayerTimesRepository.prayerState.collectAsState()
    val salahProgress by prayerTimesRepository.dailySalahProgress.collectAsState()
    val activeDhikr by ecosystemRepository.activeDhikr.collectAsState()
    val todayDhikrTotal by ecosystemRepository.todayTotalDhikrCount.collectAsState()

    // Calculate real prayer checks (0 to 5)
    val salahCheckedCount = remember(salahProgress) {
        listOf(
            salahProgress.fajrCompleted,
            salahProgress.dhuhrCompleted,
            salahProgress.asrCompleted,
            salahProgress.maghribCompleted,
            salahProgress.ishaCompleted
        ).count { it }
    }

    val salahFraction = salahCheckedCount / 5f
    val dhikrFraction = if (activeDhikr.targetCount > 0) {
        (activeDhikr.currentCount.toFloat() / activeDhikr.targetCount).coerceIn(0f, 1f)
    } else 0f

    val totalRealScore = remember(salahFraction, dhikrFraction, user.lessonsCompleted) {
        ((salahFraction * 0.5f) + (dhikrFraction * 0.3f) + (if (user.lessonsCompleted > 0) 0.2f else 0f)).coerceIn(0f, 1f)
    }

    val allAvailableGoals = remember {
        listOf(
            "Read Quran Daily",
            "5 Prayers on Time",
            "Morning Adhkar",
            "Evening Adhkar",
            "Learn Tajwid Rules",
            "Tahajjud Prayer",
            "Memorize Juz 30",
            "Daily Duas"
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VOXORA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldPrimary
                        )
                        Text(
                            text = "Spiritual Journey",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GoldPrimary.copy(alpha = 0.12f),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user.learningStreakDays} Day Streak",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Overall Progress Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journey_hero_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.1f))

                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "DAILY DEVOTION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            letterSpacing = 1.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Emerald300
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${(totalRealScore * 100).toInt()}% Fulfilled Today",
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$salahCheckedCount of 5 Salah • $todayDhikrTotal Dhikr counts logged",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Emerald200
                                    )
                                }

                                // Circular Progress Gauge
                                Box(
                                    modifier = Modifier.size(68.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { totalRealScore },
                                        modifier = Modifier.fillMaxSize(),
                                        color = GoldPrimary,
                                        trackColor = Emerald800,
                                        strokeWidth = 6.dp
                                    )
                                    Text(
                                        text = "${(totalRealScore * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Emerald800)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "“The most beloved deeds to Allah are those done regularly, even if small.” — Sahih al-Bukhari",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                color = Emerald300
                            )
                        }
                    }
                }
            }

            // 2. SALAH TRACKER (Interactive Checklist)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("journey_salah_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Emerald700,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Mosque,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Today's 5 Daily Prayers",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "$salahCheckedCount/5 completed • Tap to toggle",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            TextButton(onClick = onNavigateToSalah) {
                                Text("Salah Mode", color = Emerald700)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 5 Prayer Rows
                        val prayers = listOf(
                            Triple("Fajr", PrayerName.FAJR, salahProgress.fajrCompleted),
                            Triple("Dhuhr", PrayerName.DHUHR, salahProgress.dhuhrCompleted),
                            Triple("Asr", PrayerName.ASR, salahProgress.asrCompleted),
                            Triple("Maghrib", PrayerName.MAGHRIB, salahProgress.maghribCompleted),
                            Triple("Isha", PrayerName.ISHA, salahProgress.ishaCompleted)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            prayers.forEach { (name, prayerEnum, isDone) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            prayerTimesRepository.toggleSalahCompleted(prayerEnum)
                                            onShowSnackbar(if (!isDone) "$name marked as fulfilled." else "$name unmarked.")
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isDone) Emerald700 else MaterialTheme.colorScheme.surface,
                                        border = if (!isDone) CardDefaults.outlinedCardBorder() else null,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isDone) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Completed",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = name.take(1),
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isDone) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. QURAN TILAWAH JOURNEY
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("journey_quran_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Emerald700,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AutoStories,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Quran Tilawah Journey",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Last read: $lastRead",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Real Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${user.totalVersesRead}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Emerald700
                                    )
                                    Text(text = "Verses Read", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${bookmarks.size}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldPrimary
                                    )
                                    Text(text = "Bookmarks", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${notes.size}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Emerald700
                                    )
                                    Text(text = "Reflections", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onNavigateToQuran,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Text("Continue Reading ($lastRead)")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 4. DHIKR & TASBIH PROGRESS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("journey_dhikr_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Emerald700,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("📿", fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Dhikr & Remembrance",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Today's Total: $todayDhikrTotal counts",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            TextButton(onClick = onNavigateToDhikr) {
                                Text("Tasbih", color = Emerald700)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Active Dhikr Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = activeDhikr.arabicText,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = ScheherazadeNew, fontSize = 20.sp),
                                        color = Emerald900
                                    )
                                    Text(
                                        text = "${activeDhikr.currentCount}/${activeDhikr.targetCount}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (activeDhikr.isCompleted) Emerald700 else GoldPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { dhikrFraction },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = Emerald700,
                                    trackColor = Emerald100
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = activeDhikr.transliteration,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )

                                    FilledTonalButton(
                                        onClick = {
                                            ecosystemRepository.incrementDhikrCount()
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = Emerald700,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("+1 Count")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. LEARNING & LIVE HALAQAH
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("journey_learning_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Emerald700,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Learning & Tajwid Track",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${user.lessonsCompleted} Lessons completed (${user.learningLevel})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Interactive Recitation Studio",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Real-time audio-video halaqah room",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                FilledTonalButton(
                                    onClick = onNavigateToLiveClass,
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Emerald700, contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Join Room")
                                }
                            }
                        }
                    }
                }
            }

            // 6. SPIRITUAL GOALS (Non-judgmental & interactive)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("journey_goals_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "My Spiritual Intentions & Goals",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Select the habits you intend to nurture consistently",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Flow-style goal chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            allAvailableGoals.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    pair.forEach { goal ->
                                        val isSelected = user.selectedGoals.contains(goal)
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    repository.toggleGoal(goal)
                                                    onShowSnackbar(if (isSelected) "Removed '$goal'" else "Added intention '$goal'")
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) Emerald700.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                            border = if (isSelected) BorderStroke(1.dp, Emerald700) else CardDefaults.outlinedCardBorder()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = goal,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) Emerald700 else MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
