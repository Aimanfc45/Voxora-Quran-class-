package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoxoraMode
import com.example.data.repository.EcosystemRepository
import com.example.data.repository.PrayerTimesRepository
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*

@Composable
fun CentreHubScreen(
    repository: VoxoraRepository,
    prayerTimesRepository: PrayerTimesRepository,
    ecosystemRepository: EcosystemRepository,
    onSelectMode: (VoxoraMode) -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastRead by repository.lastReadPosition.collectAsState()
    val prayerState by prayerTimesRepository.prayerState.collectAsState()
    val todayDhikrTotal by ecosystemRepository.todayTotalDhikrCount.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

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
                            text = "Muslim Centre",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Emerald700.copy(alpha = 0.12f),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Emerald700)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ecosystem",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Emerald700
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
            // 1. Central Hero Emblem Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("centre_hero_emblem"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.1f))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldPrimary.copy(alpha = 0.15f),
                                border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f)),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Hub,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "VOXORA MUSLIM CENTRE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = GoldLight
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Your Islamic Journey, in One Place",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Connect with every core dimension of your spiritual life — recitation, prayer, knowledge, and daily remembrance.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald200,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 2. Search & Filter Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search modes, features, tools...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("centre_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Emerald700,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )
            }

            // 3. CORE SANCTUARY MODES (Primary Orbit)
            val isCoreMatched = searchQuery.isBlank() ||
                "quran".contains(searchQuery, ignoreCase = true) ||
                "salah prayer".contains(searchQuery, ignoreCase = true) ||
                "learning tajwid".contains(searchQuery, ignoreCase = true)

            if (isCoreMatched) {
                item {
                    Text(
                        text = "CORE SANCTUARY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Emerald700,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                item {
                    CentreModeCard(
                        title = "Noble Quran",
                        subtitle = "Recite, listen & reflect with authentic Tajwid",
                        icon = Icons.Default.AutoStories,
                        badge = "Last: $lastRead",
                        tag = "ESSENTIAL",
                        accentColor = Emerald700,
                        onClick = { onSelectMode(VoxoraMode.QURAN) }
                    )
                }

                item {
                    CentreModeCard(
                        title = "Salah & Prayer Times",
                        subtitle = "Accurate JAKIM times, Qiblah compass & tracker",
                        icon = Icons.Default.Mosque,
                        badge = "Next: ${prayerState.nextPrayer.englishName} • ${prayerState.formattedCountdown}",
                        tag = "PRAYER",
                        accentColor = GoldPrimary,
                        onClick = { onSelectMode(VoxoraMode.SALAH) }
                    )
                }

                item {
                    CentreModeCard(
                        title = "Learning Academy",
                        subtitle = "Structured Tajwid curriculum, Makharij & courses",
                        icon = Icons.Default.School,
                        badge = "Academy Track Active",
                        tag = "KNOWLEDGE",
                        accentColor = Emerald700,
                        onClick = { onSelectMode(VoxoraMode.LEARNING) }
                    )
                }
            }

            // 4. DAILY DEVOTION & SESSIONS (Secondary Orbit)
            val isDevotionMatched = searchQuery.isBlank() ||
                "live class halaqah".contains(searchQuery, ignoreCase = true) ||
                "dhikr tasbih".contains(searchQuery, ignoreCase = true) ||
                "dua adhkar".contains(searchQuery, ignoreCase = true)

            if (isDevotionMatched) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "DAILY DEVOTION & SESSIONS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Emerald700,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                item {
                    CentreModeCard(
                        title = "Live Class Studio",
                        subtitle = "Interactive audio-video halaqah room with LiveKit",
                        icon = Icons.Default.Videocam,
                        badge = "Live Studio Ready",
                        tag = "INTERACTIVE",
                        accentColor = Color(0xFFE11D48),
                        onClick = { onSelectMode(VoxoraMode.LIVE_CLASS) }
                    )
                }

                item {
                    CentreModeCard(
                        title = "Dhikr & Tasbih",
                        subtitle = "Digital counter, morning & evening wirds",
                        icon = Icons.Default.TouchApp,
                        badge = "Today: $todayDhikrTotal logged",
                        tag = "REMEMBRANCE",
                        accentColor = Emerald700,
                        onClick = { onSelectMode(VoxoraMode.DHIKR) }
                    )
                }

                item {
                    CentreModeCard(
                        title = "Dua & Supplications",
                        subtitle = "Authentic Hisnul Muslim prayers & categories",
                        icon = Icons.Default.VolunteerActivism,
                        badge = "8 Categories",
                        tag = "SUPPLICATION",
                        accentColor = GoldPrimary,
                        onClick = { onSelectMode(VoxoraMode.DUA) }
                    )
                }
            }

            // 5. SACRED SEASONS & COMMUNITY (Tertiary Orbit)
            val isSeasonsMatched = searchQuery.isBlank() ||
                "ramadan fasting".contains(searchQuery, ignoreCase = true) ||
                "masjid mosque".contains(searchQuery, ignoreCase = true) ||
                "calendar hijri".contains(searchQuery, ignoreCase = true) ||
                "hajj umrah".contains(searchQuery, ignoreCase = true)

            if (isSeasonsMatched) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SACRED SEASONS & COMMUNITY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Emerald700,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                item {
                    CentreModeCard(
                        title = "Ramadan & Fasting Tracker",
                        subtitle = "Imsak & Iftar countdowns, Tarawih & Khatam logs",
                        icon = Icons.Default.NightsStay,
                        badge = "Seasonal Tracker",
                        tag = "RAMADAN",
                        accentColor = Emerald700,
                        onClick = { onSelectMode(VoxoraMode.RAMADAN) }
                    )
                }

                item {
                    CentreModeCard(
                        title = "Masjid & Community",
                        subtitle = "Mosque facilities, prayer rooms & Jumu'ah schedules",
                        icon = Icons.Default.AccountBalance,
                        badge = "Nearby Directory",
                        tag = "COMMUNITY",
                        accentColor = GoldPrimary,
                        onClick = { onSelectMode(VoxoraMode.MASJID) }
                    )
                }

                item {
                    CentreModeCard(
                        title = "Islamic Calendar",
                        subtitle = "Hijri conversion, blessed dates & sunnah fasts",
                        icon = Icons.Default.CalendarMonth,
                        badge = "Hijri 1448 AH",
                        tag = "CALENDAR",
                        accentColor = Emerald700,
                        onClick = { onSelectMode(VoxoraMode.CALENDAR) }
                    )
                }

                item {
                    CentreModeCard(
                        title = "Hajj & Umrah Companion",
                        subtitle = "Tawaf, Sa'i counters & step-by-step pilgrim guides",
                        icon = Icons.Default.FlightTakeoff,
                        badge = "Pilgrimage Guide",
                        tag = "PILGRIMAGE",
                        accentColor = GoldPrimary,
                        onClick = { onSelectMode(VoxoraMode.HAJJ_UMRAH) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CentreModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    tag: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("centre_mode_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
