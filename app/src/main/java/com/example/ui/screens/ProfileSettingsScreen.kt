package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    // Settings toggles
    var dailyClassReminder by remember { mutableStateOf(true) }
    var streakNotification by remember { mutableStateOf(true) }
    var autoMicMuteOnJoin by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "Profile & Settings",
                subtitle = "Manage your preferences and learning profile"
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
            // 1. Profile Header Card
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
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald100
                        ) {
                            Text(
                                text = "🏆 ${user.learningLevel} • ${user.country}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
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
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("edit_profile_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profile Info")
                        }
                    }
                }
            }

            // 2. Quran Reading Preferences
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
                            valueRange = 20f..38f,
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
                                Text("Default Qari / Reciter", style = MaterialTheme.typography.bodyMedium)
                                Text(quranSettings.selectedReciter, style = MaterialTheme.typography.bodySmall, color = Emerald700)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // 3. Live Class & Notifications Settings
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

            // 4. App Info & Version
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
                            Text("About Voxora Quran Class", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Version 0.1.0 (Phase 1)", style = MaterialTheme.typography.labelSmall, color = Emerald700)
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

    // Reciter Dialog
    if (showReciterDialog) {
        val reciters = listOf("Mishary Rashid Alafasy", "Abdul Basit Abdul Samad", "Mahmoud Khalil Al-Hussary", "Saad Al-Ghamdi")
        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = { Text("Select Default Qari") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reciters.forEach { r ->
                        val isSelected = quranSettings.selectedReciter == r
                        Surface(
                            onClick = {
                                repository.updateSelectedReciter(r)
                                showReciterDialog = false
                                onShowSnackbar("Reciter changed to $r")
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Emerald100 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = r + if (isSelected) "  ✓" else "",
                                modifier = Modifier.padding(12.dp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface
                            )
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
            title = { Text("Voxora Quran Class") },
            text = {
                Column {
                    Text("Version 0.1.0 (Phase 1 Major Update)", fontWeight = FontWeight.Bold, color = Emerald800)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Voxora is a modern Quran recitation & live learning platform empowering students worldwide to master Tajwid, Hafazan, and Qiraat with certified teachers.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Designed with dark emerald aesthetics and gold accents.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald700)) {
                    Text("JazakAllahu Khair")
                }
            }
        )
    }
}
