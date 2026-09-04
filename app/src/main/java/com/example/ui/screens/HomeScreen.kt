package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerName
import com.example.data.model.Teacher
import com.example.data.repository.EcosystemRepository
import com.example.data.repository.PrayerTimesRepository
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: VoxoraRepository,
    prayerTimesRepository: PrayerTimesRepository = remember { PrayerTimesRepository() },
    ecosystemRepository: EcosystemRepository = remember { EcosystemRepository() },
    onNavigateToQuran: () -> Unit,
    onNavigateToLiveClass: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onTeacherSelect: (Teacher) -> Unit,
    onShowSnackbar: (String) -> Unit,
    onNavigateToSalahMode: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    onNavigateToDhikr: () -> Unit = {},
    onNavigateToDua: () -> Unit = {},
    onNavigateToRamadan: () -> Unit = {},
    onNavigateToHajjUmrah: () -> Unit = {},
    onNavigateToMasjid: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToModesHub: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val user by repository.userProfile.collectAsState()
    val lastRead by repository.lastReadPosition.collectAsState()
    val liveClass by repository.liveClass.collectAsState()
    val unreadCount by repository.unreadNotificationsCount.collectAsState()
    val prayerState by prayerTimesRepository.prayerState.collectAsState()
    val salahProgress by prayerTimesRepository.dailySalahProgress.collectAsState()
    val activeDhikr by ecosystemRepository.activeDhikr.collectAsState()
    val todayDhikrTotal by ecosystemRepository.todayTotalDhikrCount.collectAsState()

    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showInspirationDialog by remember { mutableStateOf(false) }
    var showPrayerScheduleDialog by remember { mutableStateOf(false) }

    // Real recorded prayer count (0 to 5)
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "VOXORA",
                subtitle = "Learn. Recite. Grow.",
                unreadCount = unreadCount,
                onSearchClick = onNavigateToQuran,
                onNotificationClick = { showNotificationsSheet = true },
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. GREETING & PERSONAL COMMAND BANNER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Emerald800, Emerald950)
                            )
                        )
                ) {
                    SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.12f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Assalamu Alaikum,",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Emerald300
                                )
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${prayerState.schedule.dateFormatted} • ${prayerState.schedule.hijriFormatted}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Emerald200
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = GoldPrimary.copy(alpha = 0.2f),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${user.learningStreakDays}d Streak",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Daily Inspiration / Quran Reflection Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            modifier = Modifier.clickable { showInspirationDialog = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✨", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "\"And recite the Quran with measured recitation.\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        ),
                                        color = Color.White.copy(alpha = 0.95f)
                                    )
                                    Text(
                                        text = "Surah Al-Muzzammil (73:4) • Tap for Reflection",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Emerald300
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. REAL-TIME NEXT PRAYER CARD (With JAKIM & Countdown)
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HomePrayerCard(
                        prayerState = prayerState,
                        onViewAllPrayerTimes = { showPrayerScheduleDialog = true },
                        onOpenSalahMode = onNavigateToSalahMode
                    )
                }
            }

            // 3. YOUR JOURNEY OVERVIEW (Real recorded devotion progress)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToProgress() }
                        .testTag("home_journey_overview_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Emerald700,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AutoGraph,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Your Spiritual Journey",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Calculated from real recorded devotion",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "${(totalRealScore * 100).toInt()}%",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Emerald700
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { totalRealScore },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Emerald700,
                            trackColor = Emerald100
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🕌 Salah: $salahCheckedCount/5",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "📿 Dhikr: $todayDhikrTotal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🎓 Lessons: ${user.lessonsCompleted}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 4. CONTINUE JOURNEY CARD (Real Persisted Location)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("home_continue_journey_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.1f))

                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "CONTINUE TILAWAH",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            letterSpacing = 1.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "Real Persisted State",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Emerald300
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = lastRead,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Pick up exactly where you left off in your sacred recitation.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald200
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onNavigateToQuran,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Continue Reading", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. TODAY'S FOCUS (Quick Access Cards)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "TODAY'S FOCUS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Emerald700
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Focus 1: Quran
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToQuran() }
                                .testTag("focus_card_quran"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("📖", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Quran Tilawah",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = lastRead.substringBefore("(").trim(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        // Focus 2: Salah Checklist
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToSalahMode() }
                                .testTag("focus_card_salah"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🕌", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Salah Tracker",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$salahCheckedCount/5 Checked",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Emerald700,
                                    maxLines = 1
                                )
                            }
                        }

                        // Focus 3: Dhikr
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToDhikr() }
                                .testTag("focus_card_dhikr"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("📿", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Digital Tasbih",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$todayDhikrTotal Logged",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // 6. EXPLORE MUSLIM CENTRE MODES
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MUSLIM CENTRE MODES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Emerald700
                            )
                            Text(
                                text = "Spiritual Ecosystem",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = onNavigateToModesHub,
                            colors = ButtonDefaults.textButtonColors(contentColor = Emerald700)
                        ) {
                            Text("Open Centre", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            EcosystemModeCard(
                                emoji = "📖",
                                title = "Noble Quran",
                                tag = "Mushaf & Audio",
                                onClick = onNavigateToQuran,
                                testTag = "mode_card_quran"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🕌",
                                title = "Salah Mode",
                                tag = "Prayer Times",
                                onClick = onNavigateToSalahMode,
                                testTag = "mode_card_salah"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🎓",
                                title = "Learning Mode",
                                tag = "Tajwid Track",
                                onClick = onNavigateToClasses,
                                testTag = "mode_card_learning"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🎥",
                                title = "Live Class",
                                tag = "Interactive Studio",
                                onClick = onNavigateToLiveClass,
                                testTag = "mode_card_live_class"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "📿",
                                title = "Dhikr Mode",
                                tag = "Digital Tasbih",
                                onClick = onNavigateToDhikr,
                                testTag = "mode_card_dhikr"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🤲",
                                title = "Dua Mode",
                                tag = "Supplications",
                                onClick = onNavigateToDua,
                                testTag = "mode_card_dua"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🌙",
                                title = "Ramadan Mode",
                                tag = "Imsak & Iftar",
                                onClick = onNavigateToRamadan,
                                testTag = "mode_card_ramadan"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🏛️",
                                title = "Masjid Mode",
                                tag = "Mosque Directory",
                                onClick = onNavigateToMasjid,
                                testTag = "mode_card_masjid"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "📅",
                                title = "Islamic Calendar",
                                tag = "Hijri 1448 AH",
                                onClick = onNavigateToCalendar,
                                testTag = "mode_card_calendar"
                            )
                        }
                    }
                }
            }

            // 7. INTERACTIVE TAJWID STUDIO CARD (LiveKit Room)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("home_live_studio_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDC2626))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE RECITATION STUDIO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Emerald100
                            ) {
                                Text(
                                    text = "LiveKit Connected",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Emerald800,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = liveClass.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Interactive audio-video halaqah room with live recitation and feedback.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToClasses,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Class Schedule")
                            }

                            Button(
                                onClick = onNavigateToLiveClass,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("join_live_class_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Emerald700,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Enter Room",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Inspiration Reflection Dialog
    if (showInspirationDialog) {
        AlertDialog(
            onDismissRequest = { showInspirationDialog = false },
            title = {
                Text(
                    text = "Daily Quranic Reflection",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",
                        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = ScheherazadeNew, fontSize = 24.sp),
                        color = Emerald900,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"And recite the Quran with measured recitation.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Surah Al-Muzzammil (73:4)\n\nReflection: Recitation should not be rushed; each letter, elongation, and pause is an opportunity to internalize divine wisdom and cultivate presence before Allah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInspirationDialog = false
                        onNavigateToQuran()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Open in Quran")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInspirationDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Notification Center Sheet
    if (showNotificationsSheet) {
        NotificationCenterSheet(
            repository = repository,
            onDismiss = { showNotificationsSheet = false },
            onNavigateToClass = {
                showNotificationsSheet = false
                onNavigateToLiveClass()
            },
            onNavigateToQuran = {
                showNotificationsSheet = false
                onNavigateToQuran()
            },
            onNavigateToCommunity = {
                showNotificationsSheet = false
                onNavigateToCommunity()
            },
            onNavigateToProgress = {
                showNotificationsSheet = false
                onNavigateToProgress()
            },
            onShowSnackbar = onShowSnackbar
        )
    }

    // Full Prayer Schedule & Malaysian Zone Selector BottomSheet
    if (showPrayerScheduleDialog) {
        PrayerTimesScheduleDialog(
            prayerTimesRepository = prayerTimesRepository,
            onDismiss = { showPrayerScheduleDialog = false },
            onNavigateToSalahMode = {
                showPrayerScheduleDialog = false
                onNavigateToSalahMode()
            }
        )
    }
}

@Composable
fun EcosystemModeCard(
    emoji: String,
    title: String,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Emerald100),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = Emerald700,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                fontSize = 11.sp
            )
        }
    }
}
