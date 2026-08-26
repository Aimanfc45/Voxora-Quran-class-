package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.QuickStatItem
import com.example.ui.components.VoxoraHeaderBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    repository: VoxoraRepository,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by repository.userProfile.collectAsState()
    val quranSettings by repository.quranSettings.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(user.name) }
    var editBio by remember { mutableStateOf(user.bio) }
    var editCountry by remember { mutableStateOf(user.country) }
    var editLevel by remember { mutableStateOf(user.learningLevel) }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showReciterDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showDailyGoalDialog by remember { mutableStateOf(false) }

    // Settings toggles
    var dailyClassReminder by remember { mutableStateOf(true) }
    var streakNotification by remember { mutableStateOf(true) }
    var autoMicMuteOnJoin by remember { mutableStateOf(true) }
    var dailyGoalMinutes by remember { mutableStateOf(user.dailyGoalMinutes) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "Profile & Settings",
                subtitle = "Manage your account, daily goals, and audio preferences"
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
            // 1. Profile Header Card & Auth Status
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
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (user.isGuest) "Guest Mode (Local On-Device Profile)" else user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Emerald100
                            ) {
                                Text(
                                    text = "🏆 ${user.learningLevel}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldContainer
                            ) {
                                Text(
                                    text = "📍 ${user.country}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldOnContainer
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    editName = user.name
                                    editBio = user.bio
                                    editCountry = user.country
                                    editLevel = user.learningLevel
                                    showEditProfileDialog = true
                                },
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
                                onClick = { showAuthDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isGuest) Emerald700 else MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = if (user.isGuest) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
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

            // 2. Daily Learning Goals Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text(
                                text = "Daily Quran Goal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            TextButton(onClick = { showDailyGoalDialog = true }) {
                                Text("Adjust", color = Emerald700)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target: ${user.dailyGoalMinutes} minutes daily recitation",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (user.streakCount * 0.2f).coerceIn(0.1f, 1.0f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldPrimary,
                            trackColor = Emerald100
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🔥 ${user.streakCount} Day Streak Active • Total Recited: ${user.totalVersesRead} Verses",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald800
                        )
                    }
                }
            }

            // 3. Quran Reading & Reciter Preferences
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Quran Reader Preferences",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Font size slider
                        Text(
                            text = "Arabic Font Size: ${quranSettings.arabicFontSizeSp.toInt()}sp",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = quranSettings.arabicFontSizeSp,
                            onValueChange = { repository.updateArabicFontSize(it) },
                            valueRange = 20f..40f,
                            colors = SliderDefaults.colors(thumbColor = Emerald700, activeTrackColor = Emerald700)
                        )

                        // English translation toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Show English Translation", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = quranSettings.showEnglishTranslation,
                                onCheckedChange = { repository.toggleEnglishTranslation(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }

                        // Word-by-word toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Word-by-word Breakdown", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = quranSettings.showWordByWord,
                                onCheckedChange = { repository.toggleWordByWord(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }

                        // Reciter picker
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
                                Text("Default Qari / Reciter", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text(quranSettings.selectedReciter, style = MaterialTheme.typography.bodySmall, color = Emerald700)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // 4. Live Class & Notifications Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Class & Notifications",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Daily Class Reminders", style = MaterialTheme.typography.bodyMedium)
                                Text("15 minutes before scheduled class", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Streak & Goal Alerts", style = MaterialTheme.typography.bodyMedium)
                                Text("Daily motivation to recite and practice", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Auto-Mute on Join", style = MaterialTheme.typography.bodyMedium)
                                Text("Keep microphone muted when entering classroom", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = autoMicMuteOnJoin,
                                onCheckedChange = { autoMicMuteOnJoin = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald700)
                            )
                        }
                    }
                }
            }

            // 5. Storage & Cache Management
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Audio Offline Cache", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("14.2 MB cached audio streams", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(
                            onClick = {
                                onShowSnackbar("Audio cache cleared successfully (14.2 MB freed)")
                            }
                        ) {
                            Text("Clear Cache", color = Color(0xFFDC2626))
                        }
                    }
                }
            }

            // 6. App Info & Version
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAboutDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("About Voxora Quran", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Version 1.0 (Phase 1 Update 1) • Learn. Recite. Grow.", style = MaterialTheme.typography.labelSmall, color = Emerald700)
                        }
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Emerald700)
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text("Edit Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Short Bio / Goal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCountry,
                        onValueChange = { editCountry = it },
                        label = { Text("Country") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editLevel,
                        onValueChange = { editLevel = it },
                        label = { Text("Learning Level (e.g. Beginner, Intermediate, Hafiz)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateUserProfile(editName, editBio, editCountry, editLevel)
                        showEditProfileDialog = false
                        onShowSnackbar("Profile updated!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Daily Recitation Goal Dialog
    if (showDailyGoalDialog) {
        var tempGoal by remember { mutableStateOf(user.dailyGoalMinutes.toFloat()) }
        AlertDialog(
            onDismissRequest = { showDailyGoalDialog = false },
            title = { Text("Set Daily Quran Goal") },
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
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

    // Sign In / Guest Mode Dialog
    if (showAuthDialog) {
        var emailInput by remember { mutableStateOf("") }
        var nameInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            title = {
                Text(
                    text = if (user.isGuest) "Sign In to Voxora Account" else "Account Management",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (user.isGuest) {
                        Text(
                            text = "Sign in to sync your bookmarks, notes, live class attendance, and Tajwid mastery across devices.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Signed in as: ${user.name} (${user.email})")
                        Text(
                            text = "You are currently enjoying full account synchronization.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald700
                        )
                    }
                }
            },
            confirmButton = {
                if (user.isGuest) {
                    Button(
                        onClick = {
                            val finalName = if (nameInput.isNotBlank()) nameInput else "Student"
                            val finalEmail = if (emailInput.isNotBlank()) emailInput else "student@voxora.org"
                            repository.signInUser(finalName, finalEmail)
                            showAuthDialog = false
                            onShowSnackbar("Welcome back, $finalName! Signed in successfully.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                    ) {
                        Text("Sign In")
                    }
                } else {
                    Button(
                        onClick = {
                            repository.switchToGuestMode()
                            showAuthDialog = false
                            onShowSnackbar("Switched to Guest Mode.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Switch to Guest Mode")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Reciter Dialog with 10 Reciters
    if (showReciterDialog) {
        val reciters = listOf(
            "Mishary Rashid Alafasy" to "Alafasy_128kbps",
            "Abdul Basit Abdul Samad" to "Abdul_Basit_Murattal_192kbps",
            "Mahmoud Khalil Al-Hussary" to "Husary_128kbps",
            "Saad Al-Ghamdi" to "Ghamadi_40kbps",
            "Abu Bakr Ash-Shatri" to "Abu_Bakr_Ash-Shaatree_128kbps",
            "Abdur-Rahman as-Sudais" to "Abdurrahmaan_As-Sudais_192kbps",
            "Saud ash-Shuraim" to "Saood_ash-Shuraym_128kbps",
            "Ali Jaber" to "Ali_Jaber_64kbps",
            "Yasser Ad-Dussary" to "Yasser_Ad-Dussary_128kbps",
            "Maher Al-Muaiqly" to "Maher_AlMuaiqly_64kbps"
        )

        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = Emerald700)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Default Reciter (Qari)")
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(reciters) { (rName, rId) ->
                        val isSelected = quranSettings.selectedReciter == rName
                        Surface(
                            onClick = {
                                repository.updateSelectedReciter(rName, rId)
                                showReciterDialog = false
                                onShowSnackbar("Reciter set to $rName")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Voxora Quran",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Emerald800
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Version 1.0 (Phase 1 Update 1)", fontWeight = FontWeight.Bold, color = Emerald700)
                    Text("Tagline: Learn. Recite. Grow.", fontWeight = FontWeight.SemiBold, color = GoldDark)
                    Divider(color = Emerald100)
                    Text("Voxora Quran is an authentic, offline-first Quran learning platform featuring verified Uthmani text, 10 world-renowned Qaris, word-by-word vocabulary breakdowns, interactive Tajwid color rules, private reflection notes, and interactive live classes.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Audio powered by verified High Quality Quran Audio streams (EveryAyah repository).", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald700)) {
                    Text("Alhamdulillah")
                }
            }
        )
    }
}
