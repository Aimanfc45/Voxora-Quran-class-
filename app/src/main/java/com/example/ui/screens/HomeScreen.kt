package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Teacher
import com.example.data.repository.PrayerTimesRepository
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: VoxoraRepository,
    prayerTimesRepository: PrayerTimesRepository = remember { PrayerTimesRepository() },
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
    val progress by repository.progress.collectAsState()
    val liveClass by repository.liveClass.collectAsState()
    val teachers by repository.teachers.collectAsState()
    val groups by repository.communityGroups.collectAsState()
    val unreadCount by repository.unreadNotificationsCount.collectAsState()
    val prayerState by prayerTimesRepository.prayerState.collectAsState()

    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showInspirationDialog by remember { mutableStateOf(false) }
    var showPrayerScheduleDialog by remember { mutableStateOf(false) }

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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Greeting & Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Emerald800, Emerald900)
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
                                        text = "${user.learningStreakDays} Day Streak",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Daily Inspiration / Verse snippet
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

            // 2. Real-Time Prayer Times & Clock Card
            item {
                HomePrayerCard(
                    prayerState = prayerState,
                    onViewAllPrayerTimes = { showPrayerScheduleDialog = true },
                    onOpenSalahMode = onNavigateToSalahMode
                )
            }

            // 2b. VOXORA Muslim Centre Modes Showcase
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                ) {
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
                            Text("All 10 Modes", fontWeight = FontWeight.Bold)
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
                                title = "Reading Quran",
                                tag = "Recite & Audio",
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
                                emoji = "🕋",
                                title = "Hajj & Umrah",
                                tag = "Pilgrim Guide",
                                onClick = onNavigateToHajjUmrah,
                                testTag = "mode_card_hajj_umrah"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🏛️",
                                title = "Masjid Mode",
                                tag = "Find Mosque",
                                onClick = onNavigateToMasjid,
                                testTag = "mode_card_masjid"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "📅",
                                title = "Islamic Calendar",
                                tag = "1448 AH Dates",
                                onClick = onNavigateToCalendar,
                                testTag = "mode_card_calendar"
                            )
                        }
                        item {
                            EcosystemModeCard(
                                emoji = "🎓",
                                title = "Learning Mode",
                                tag = "Quran Academy",
                                onClick = onNavigateToClasses,
                                testTag = "mode_card_learning"
                            )
                        }
                    }
                }
            }

            // 3. Continue Learning Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("continue_learning_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Emerald100),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Emerald700,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "CONTINUE LEARNING",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            letterSpacing = 1.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Emerald700
                                    )
                                    Text(
                                        text = progress.currentLessonTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Focus: ${progress.currentLessonSurah}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lesson Progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${progress.currentLessonProgress}%",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Emerald700
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress.currentLessonProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Emerald600,
                            trackColor = Emerald100
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateToQuran,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("continue_learning_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Emerald700,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Continue Lesson",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 3. Quick Actions Grid
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionItem(
                            title = "Read Quran",
                            icon = Icons.Outlined.AutoStories,
                            color = Emerald700,
                            bgColor = Emerald50,
                            onClick = onNavigateToQuran,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_read_quran"
                        )
                        QuickActionItem(
                            title = "Join Class",
                            icon = Icons.Outlined.VideoCameraFront,
                            color = Color(0xFFB45309),
                            bgColor = Color(0xFFFEF3C7),
                            onClick = onNavigateToLiveClass,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_join_class"
                        )
                        QuickActionItem(
                            title = "Find Ustaz",
                            icon = Icons.Outlined.PersonSearch,
                            color = Color(0xFF0369A1),
                            bgColor = Color(0xFFE0F2FE),
                            onClick = onNavigateToTeachers,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_find_teacher"
                        )
                        QuickActionItem(
                            title = "Community",
                            icon = Icons.Outlined.Groups,
                            color = Color(0xFF6D28D9),
                            bgColor = Color(0xFFEDE9FE),
                            onClick = onNavigateToCommunity,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_community"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionItem(
                            title = "Salah Mode",
                            icon = Icons.Outlined.AccessibilityNew,
                            color = Emerald800,
                            bgColor = Emerald100,
                            onClick = onNavigateToSalahMode,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_salah_mode"
                        )
                        QuickActionItem(
                            title = "Dashboard",
                            icon = Icons.Outlined.Insights,
                            color = Color(0xFFC05621),
                            bgColor = Color(0xFFFEEBC8),
                            onClick = onNavigateToProgress,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_progress_dashboard"
                        )
                        QuickActionItem(
                            title = "Bookmarks",
                            icon = Icons.Outlined.BookmarkBorder,
                            color = Color(0xFF0D9488),
                            bgColor = Color(0xFFCCFBF1),
                            onClick = onNavigateToProfile,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_bookmarks_home"
                        )
                        QuickActionItem(
                            title = if (user.isGuest) "Sign In" else "Account",
                            icon = if (user.isGuest) Icons.Outlined.Login else Icons.Outlined.AccountCircle,
                            color = if (user.isGuest) Color(0xFFB45309) else Emerald800,
                            bgColor = if (user.isGuest) Color(0xFFFEF3C7) else Emerald100,
                            onClick = if (user.isGuest) onNavigateToAuth else onNavigateToProfile,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_action_account_home"
                        )
                    }
                }
            }

            // 4. Live / Upcoming Class Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("upcoming_class_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    text = "LIVE NOW",
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
                                    text = "${liveClass.participantsCount} Students Active",
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
                            text = "Instructor: ${liveClass.teacher.name} (${liveClass.teacher.country})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

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
                                Text("Class Details")
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
                                    text = "Join Live",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Featured Teachers Carousel
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Featured Teachers",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onNavigateToTeachers) {
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Emerald700
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(teachers) { teacher ->
                            TeacherHomeCard(
                                teacher = teacher,
                                onClick = { onTeacherSelect(teacher) }
                            )
                        }
                    }
                }
            }

            // 6. Active Study Groups Carousel
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Community Circles",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onNavigateToCommunity) {
                            Text(
                                text = "Explore",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Emerald700
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(groups.take(3)) { group ->
                            Surface(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { onNavigateToCommunity() },
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(group.iconEmoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = group.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${group.memberCount} active learners",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Emerald700
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Daily Inspiration Dialog
    if (showInspirationDialog) {
        AlertDialog(
            onDismissRequest = { showInspirationDialog = false },
            title = {
                Text("Daily Quran Reflection", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column {
                    Text(
                        text = "وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Emerald700,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "\"And recite the Quran with measured recitation.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Surah Al-Muzzammil (73:4)\n\nReflection: Tartil signifies reciting calmly, giving each letter its rightful articulation (Makhraj) and proper elongation (Mad). Slow down your recitation and let the meanings settle into your heart.",
                        style = MaterialTheme.typography.bodyMedium,
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
private fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TeacherHomeCard(
    teacher: Teacher,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
            .testTag("teacher_home_card_${teacher.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                contentDescription = teacher.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = teacher.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Text(
                text = teacher.title,
                style = MaterialTheme.typography.bodySmall,
                color = Emerald700,
                maxLines = 1,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${teacher.rating}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

