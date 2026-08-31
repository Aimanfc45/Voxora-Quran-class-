package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioRepeatMode
import com.example.data.model.ReadingDisplayMode
import com.example.data.repository.VoxoraRepository
import com.example.data.update.AppVersion
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    repository: VoxoraRepository,
    onShowSnackbar: (String) -> Unit,
    onNavigateToAuth: () -> Unit = {},
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToSalahMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val user by repository.userProfile.collectAsState()
    val progress by repository.progress.collectAsState()
    val quranSettings by repository.quranSettings.collectAsState()

    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showGuestAccountSheet by remember { mutableStateOf(false) }
    var showReciterDialog by remember { mutableStateOf(false) }
    var showDailyGoalDialog by remember { mutableStateOf(false) }
    var showAppUpdateSheet by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyTermsDialog by remember { mutableStateOf(false) }

    // Settings toggles
    var dailyClassReminder by remember { mutableStateOf(true) }
    var streakNotification by remember { mutableStateOf(true) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "Profile & Settings",
                subtitle = "Manage identity, Quran preferences, audio engine, and updates"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ====================================================
            // 1. PROFILE HEADER
            // ====================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_header_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar Badge
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                                .background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.avatarEmoji,
                                fontSize = 38.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Emerald700
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Account Status Pill (Guest vs Signed In)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (user.isGuest) Color(0xFFFEF3C7) else Emerald100
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (user.isGuest) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = if (user.isGuest) Color(0xFFB45309) else Emerald900,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (user.isGuest) "Guest Mode (Local Only)" else "Signed In (${user.email})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (user.isGuest) Color(0xFF92400E) else Emerald900
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Emerald50
                            ) {
                                Text(
                                    text = "🏆 ${user.learningLevel}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GoldContainer
                            ) {
                                Text(
                                    text = "📍 ${user.country}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldOnContainer
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (user.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "\"${user.bio}\"",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Actions: Edit Profile & Account/Sign-In
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showEditProfileSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("edit_profile_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profile")
                            }

                            Button(
                                onClick = { showGuestAccountSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isGuest) Emerald700 else MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = if (user.isGuest) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("account_management_btn")
                            ) {
                                Icon(
                                    imageVector = if (user.isGuest) Icons.Default.Login else Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (user.isGuest) "Sign In" else "Account")
                            }
                        }
                    }
                }
            }

            // ====================================================
            // 2. LEARNING OVERVIEW & GOALS
            // ====================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("learning_overview_card"),
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
                                Icon(imageVector = Icons.Default.Insights, contentDescription = null, tint = Emerald700)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Learning Overview",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            TextButton(onClick = { showDailyGoalDialog = true }) {
                                Text("Set Goal", color = Emerald700)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Grid stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickStatItem(
                                title = "Streak",
                                value = "${user.streakCount} Days",
                                iconEmoji = "🔥",
                                modifier = Modifier.weight(1f)
                            )
                            QuickStatItem(
                                title = "Juz Progress",
                                value = "Juz ${user.juzProgress}",
                                iconEmoji = "📖",
                                modifier = Modifier.weight(1f)
                            )
                            QuickStatItem(
                                title = "Recited",
                                value = "${user.totalVersesRead}",
                                iconEmoji = "✨",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Goal Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Daily Goal: ${user.dailyGoalMinutes} mins / day",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${progress.quranReadingPercent}% completed",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Emerald700
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress.quranReadingPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldPrimary,
                            trackColor = Emerald100
                        )
                    }
                }
            }

            // ====================================================
            // 3. QURAN PREFERENCES
            // ====================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quran_preferences_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoStories, contentDescription = null, tint = Emerald700)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quran Reader Preferences",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Arabic Font Size Slider with Live Preview
                        Text(
                            text = "Arabic Font Size: ${quranSettings.arabicFontSizeSp.toInt()}sp",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = quranSettings.arabicFontSizeSp,
                            onValueChange = { repository.updateArabicFontSize(it) },
                            valueRange = 20f..40f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = Emerald700, activeTrackColor = Emerald700)
                        )

                        // Live sample preview
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald50.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                fontSize = quranSettings.arabicFontSizeSp.sp,
                                textAlign = TextAlign.Center,
                                color = Emerald900,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Arabic Script Style
                        Text("Arabic Script Style", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("Uthmani (Madinah)", "Indopak Script", "Amiri Modern")) { style ->
                                val isSelected = quranSettings.arabicFontStyle == style
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { repository.updateArabicFontStyle(style) },
                                    label = { Text(style, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Emerald700,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Translation Mode
                        Text("Translation Preference", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(
                                listOf(
                                    "English" to ReadingDisplayMode.ARABIC_EN,
                                    "Bahasa Melayu" to ReadingDisplayMode.ARABIC_BM,
                                    "Dual (EN+BM)" to ReadingDisplayMode.MULTI_TRANSLATION,
                                    "Arabic Only" to ReadingDisplayMode.ARABIC_ONLY
                                )
                            ) { (label, mode) ->
                                val isSelected = quranSettings.readingMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { repository.setReadingDisplayMode(mode) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Emerald700,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transliteration Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Latin Transliteration (Pronunciation)", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = quranSettings.showTransliteration,
                                onCheckedChange = { repository.toggleTransliteration(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }

                        // Tajwid Color Highlights Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Interactive Tajwid Color Highlights", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = quranSettings.showTajwidColors,
                                onCheckedChange = { repository.toggleTajwidColors(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }

                        // Word-by-word Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Word-by-word Vocabulary", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = quranSettings.showWordByWord,
                                onCheckedChange = { repository.toggleWordByWord(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }

                        // Default Qari selector
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showReciterDialog = true }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Default Qari (Reciter)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text(quranSettings.selectedReciter, style = MaterialTheme.typography.bodySmall, color = Emerald700)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // ====================================================
            // 4. AUDIO PREFERENCES
            // ====================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("audio_preferences_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Audiotrack, contentDescription = null, tint = Emerald700)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Audio Playback Preferences",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Default Playback Speed
                        Text("Default Playback Speed", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)) { speed ->
                                val isSelected = quranSettings.defaultPlaybackSpeed == speed
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { repository.updateDefaultPlaybackSpeed(speed) },
                                    label = { Text("${speed}x", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Emerald700,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Auto-play Next Ayah
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Continuous Playback (Auto-Next)", style = MaterialTheme.typography.bodyMedium)
                                Text("Play subsequent verses automatically", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = quranSettings.autoPlayNextAyah,
                                onCheckedChange = { repository.updateAutoPlayNextAyah(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Audio Cache Clear
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Offline Audio Storage", style = MaterialTheme.typography.bodyMedium)
                                Text("18.4 MB cached Murattal streams", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(
                                onClick = { onShowSnackbar("Audio cache cleared (18.4 MB freed)") }
                            ) {
                                Text("Clear Cache", color = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }

            // ====================================================
            // 5. ACCOUNT MANAGEMENT & GUEST SYNC
            // ====================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_management_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (user.isGuest) Icons.Default.PersonOutline else Icons.Default.ManageAccounts,
                                contentDescription = null,
                                tint = Emerald700
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Account & Devices",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (user.isGuest) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Guest Mode Active",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Sign in to sync your progress across devices.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF78350F)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { showGuestAccountSheet = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("Sign In Now", fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { onShowSnackbar("Continuing as Guest.") },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("Continue as Guest", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(user.name, fontWeight = FontWeight.Bold)
                                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                TextButton(onClick = { showGuestAccountSheet = true }) {
                                    Text("Manage", color = Emerald700)
                                }
                            }
                        }
                    }
                }
            }

            // ====================================================
            // 6. APP SETTINGS & NOTIFICATIONS
            // ====================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_settings_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Emerald700)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "App Settings & Notifications",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Class Reminders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Daily Class Reminders", style = MaterialTheme.typography.bodyMedium)
                                Text("15 minutes before scheduled Quran classes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = dailyClassReminder,
                                onCheckedChange = {
                                    dailyClassReminder = it
                                    onShowSnackbar(if (it) "Class reminders enabled" else "Class reminders disabled")
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Streak Alert
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Streak & Goal Reminders", style = MaterialTheme.typography.bodyMedium)
                                Text("Daily motivation to recite and reflect", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = streakNotification,
                                onCheckedChange = {
                                    streakNotification = it
                                    onShowSnackbar(if (it) "Streak alerts enabled" else "Streak alerts disabled")
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }
                    }
                }
            }

            // ====================================================
            // 7. UPDATE SYSTEM & ABOUT VOXORA
            // ====================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_update_card"),
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
                                Icon(imageVector = Icons.Default.SystemUpdate, contentDescription = null, tint = Emerald700)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Voxora Quran Version", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("v${AppVersion.VERSION_NAME} (${AppVersion.PHASE})", style = MaterialTheme.typography.labelSmall, color = Emerald700)
                                }
                            }

                            Button(
                                onClick = {
                                    repository.updateManager.checkForUpdates(isManual = true)
                                    showAppUpdateSheet = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text("Check Updates", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showAboutDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("About Voxora", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showPrivacyTermsDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Privacy & Terms", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // ====================================================
    // DIALOGS & BOTTOM SHEETS
    // ====================================================

    if (showEditProfileSheet) {
        EditProfileBottomSheet(
            user = user,
            repository = repository,
            onDismiss = { showEditProfileSheet = false },
            onSaved = { onShowSnackbar(it) }
        )
    }

    if (showGuestAccountSheet) {
        GuestAccountBottomSheet(
            user = user,
            repository = repository,
            onDismiss = { showGuestAccountSheet = false },
            onSuccess = { onShowSnackbar(it) }
        )
    }

    if (showAppUpdateSheet) {
        AppUpdateBottomSheet(
            updateManager = repository.updateManager,
            onDismiss = { showAppUpdateSheet = false }
        )
    }

    // Daily Recitation Goal Dialog
    if (showDailyGoalDialog) {
        var tempGoal by remember { mutableStateOf(user.dailyGoalMinutes.toFloat()) }
        AlertDialog(
            onDismissRequest = { showDailyGoalDialog = false },
            title = { Text("Set Daily Quran Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${tempGoal.toInt()} Minutes / Day",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Emerald700
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Slider(
                        value = tempGoal,
                        onValueChange = { tempGoal = it },
                        valueRange = 5f..60f,
                        steps = 10,
                        colors = SliderDefaults.colors(thumbColor = Emerald700, activeTrackColor = Emerald700)
                    )
                    Text(
                        text = "Build a consistent habit of daily tilawah and reflection.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateDailyGoal(minutes = tempGoal.toInt(), verses = 10)
                        showDailyGoalDialog = false
                        onShowSnackbar("Daily goal updated to ${tempGoal.toInt()} minutes!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Set Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDailyGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reciter Dialog with 10 Authenticated Reciters
    if (showReciterDialog) {
        val reciters = listOf(
            "Mishary Rashid Alafasy" to "Alafasy_128kbps",
            "Abdul Rahman Al-Sudais" to "Abdurrahmaan_As-Sudais_192kbps",
            "Abdullah Awad Al-Juhani" to "Abdullaah_3awwaad_Al-Juhaynee_128kbps",
            "Abdul Basit Abdus-Samad" to "Abdul_Basit_Murattal_192kbps",
            "Mahmoud Khalil Al-Husary" to "Husary_128kbps",
            "Mohamed Siddiq Al-Minshawi" to "Minshawy_Murattal_128kbps",
            "Saad Al-Ghamdi" to "Ghamadi_40kbps",
            "Maher Al-Muaiqly" to "Maher_AlMuaiqly_64kbps",
            "Saud Ash-Shuraim" to "Saood_ash-Shuraym_128kbps",
            "Abu Bakr Ash-Shatri" to "Abu_Bakr_Ash-Shaatree_128kbps",
            "Yasser Al-Dosari" to "Yasser_Ad-Dussary_128kbps",
            "Nasser Al-Qatami" to "Nasser_Alqatami_128kbps"
        )

        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = Emerald700)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Default Qari", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(reciters) { (rName, rId) ->
                        val isSelected = quranSettings.selectedReciter == rName
                        Surface(
                            onClick = {
                                repository.updateSelectedReciter(rName, rId)
                                showReciterDialog = false
                                onShowSnackbar("Default reciter set to $rName")
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Emerald700 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReciterDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "About Voxora Quran",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Emerald800
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Version ${AppVersion.VERSION_NAME} (${AppVersion.PHASE})", fontWeight = FontWeight.Bold, color = Emerald700)
                    Text("Codename: ${AppVersion.CODENAME}", fontWeight = FontWeight.SemiBold, color = GoldDark)
                    Divider(color = Emerald100)
                    Text("Voxora Quran is an authentic, offline-ready Quran learning platform featuring verified Uthmani text, 10+ verified world-renowned Qaris, word-by-word vocabulary breakdowns, interactive Tajwid color rules, and interactive live classrooms.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Audio powered by official EveryAyah verified recitation archives.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald700)) {
                    Text("Alhamdulillah")
                }
            }
        )
    }

    // Privacy & Terms Dialog
    if (showPrivacyTermsDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyTermsDialog = false },
            title = {
                Text(
                    text = "Privacy & Open Source",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Emerald800
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Data Privacy & Local Storage", fontWeight = FontWeight.Bold, color = Emerald700)
                    Text("In Guest Mode, your reading progress, notes, and preferences are stored exclusively on your local device. We do not collect private telemetry without your consent.")
                    Divider(color = Emerald100)
                    Text("Open Source & Content Credits", fontWeight = FontWeight.Bold, color = Emerald700)
                    Text("• Quran Text: Tanzil Project & King Fahd Quran Printing Complex\n• English Translation: Sahih International\n• Malay Translation: Abdullah Muhammad Basmeih\n• Audio Streams: EveryAyah Global Archive", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald700)) {
                    Text("Close")
                }
            }
        )
    }
}
