package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Achievement
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.QuickStatItem
import com.example.ui.components.VoxoraHeaderBar
import com.example.ui.theme.*

@Composable
fun ProgressScreen(
    repository: VoxoraRepository,
    onShowSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val progress by repository.progress.collectAsState()
    val achievements by repository.achievements.collectAsState()
    val user by repository.userProfile.collectAsState()

    var showLogPracticeDialog by remember { mutableStateOf(false) }
    var selectedAchievementDetail by remember { mutableStateOf<Achievement?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "Learning Progress",
                subtitle = "Track your recitation growth & milestones"
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
            // 1. Stats Overview Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickStatItem(
                        title = "Lessons",
                        value = "${user.lessonsCompleted}",
                        icon = Icons.Default.CheckCircle,
                        badgeColor = Emerald100,
                        contentColor = Emerald800,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatItem(
                        title = "Hours",
                        value = String.format("%.1fh", user.hoursSpent),
                        icon = Icons.Default.Timer,
                        badgeColor = GoldContainer,
                        contentColor = GoldOnContainer,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatItem(
                        title = "Streak",
                        value = "${user.learningStreakDays}d",
                        icon = Icons.Default.Bolt,
                        badgeColor = Color(0xFFFEE2E2),
                        contentColor = Color(0xFFDC2626),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2. Log Practice Action Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Quran Recitation Log",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Keep your streak active by logging self-study or class sessions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald200
                            )
                        }

                        Button(
                            onClick = { showLogPracticeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Emerald950),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("log_practice_btn")
                        ) {
                            Text("+ Log", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Core Skill Mastery Meters
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Core Learning Tracks",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        SkillMeterRow(
                            skillName = "Quran Reading Fluency",
                            percentage = progress.quranReadingPercent,
                            levelLabel = "Intermediate",
                            color = Emerald600
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SkillMeterRow(
                            skillName = "Tajwid Rules & Makharij",
                            percentage = progress.tajwidPercent,
                            levelLabel = "Advanced",
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SkillMeterRow(
                            skillName = "Hafazan (Juz 30 Memorization)",
                            percentage = progress.memorizationPercent,
                            levelLabel = "8 Surahs Completed",
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }

            // 4. Weekly Learning Activity Bar Chart
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
                                text = "Weekly Activity",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Target: 45 min/day",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald700
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Daily bars
                        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        val minutes = listOf(40, 55, 30, 60, 45, 75, 50)
                        val maxMin = 75

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            days.zip(minutes).forEach { (day, min) ->
                                val heightFraction = min.toFloat() / maxMin

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${min}m",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Emerald700,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(18.dp)
                                            .fillMaxHeight(heightFraction * 0.75f)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(if (day == "Sat" || day == "Sun") GoldPrimary else Emerald700)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Unlocked Milestones & Badges
            item {
                Text(
                    text = "Milestones & Achievements (${achievements.count { it.isUnlocked }}/${achievements.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(achievements, key = { it.id }) { ach ->
                AchievementCard(
                    achievement = ach,
                    onClick = { selectedAchievementDetail = ach }
                )
            }
        }
    }

    // Log Practice Dialog
    if (showLogPracticeDialog) {
        var selectedMinutes by remember { mutableStateOf(30) }

        AlertDialog(
            onDismissRequest = { showLogPracticeDialog = false },
            title = {
                Text("Log Quran Practice Session", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select minutes practiced today:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 45, 60).forEach { m ->
                            val isSelected = selectedMinutes == m
                            FilledTonalButton(
                                onClick = { selectedMinutes = m },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isSelected) Emerald700 else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("${m}m", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.logPracticeSession(selectedMinutes)
                        showLogPracticeDialog = false
                        onShowSnackbar("Logged $selectedMinutes mins! Streak updated.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Save Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogPracticeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Achievement Detail Dialog
    if (selectedAchievementDetail != null) {
        val ach = selectedAchievementDetail!!
        AlertDialog(
            onDismissRequest = { selectedAchievementDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(ach.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(ach.title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column {
                    Text(ach.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (ach.isUnlocked) Emerald100 else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (ach.isUnlocked) "✅ Unlocked on ${ach.unlockedDate ?: "Recently"}" else "🔒 In Progress",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (ach.isUnlocked) Emerald900 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedAchievementDetail = null }, colors = ButtonDefaults.buttonColors(containerColor = Emerald700)) {
                    Text("Awesome")
                }
            }
        )
    }
}

@Composable
private fun SkillMeterRow(
    skillName: String,
    percentage: Int,
    levelLabel: String,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(skillName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text("$percentage%", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(levelLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("achievement_card_${achievement.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = if (achievement.isUnlocked) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)) else CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(achievement.iconEmoji, fontSize = 32.sp)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (achievement.isUnlocked) {
                Surface(
                    shape = CircleShape,
                    color = GoldContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Unlocked",
                        tint = GoldOnContainer,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
