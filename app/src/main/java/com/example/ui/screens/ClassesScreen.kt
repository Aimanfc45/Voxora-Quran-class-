package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ClassStatus
import com.example.data.model.ClassType
import com.example.data.model.QuranClass
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.ChoicePill
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*

enum class ClassesTab {
    ALL,
    UPCOMING,
    COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    repository: VoxoraRepository,
    onJoinLiveClass: () -> Unit,
    onExploreTeachers: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveClass by repository.liveClass.collectAsState()
    val upcomingClasses by repository.upcomingClasses.collectAsState()
    val completedClasses by repository.completedClasses.collectAsState()

    var selectedTab by remember { mutableStateOf(ClassesTab.ALL) }
    var selectedClassDetail by remember { mutableStateOf<QuranClass?>(null) }
    var showJoinCodeDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showScheduleDialog = true },
                containerColor = GoldPrimary,
                contentColor = Emerald950,
                modifier = Modifier.testTag("schedule_class_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Schedule Class")
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
            // Action Banner: Join with Code & Find Teachers
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showJoinCodeDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Emerald800,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("join_by_code_button")
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Join via Code", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = onExploreTeachers,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("find_teachers_button")
                    ) {
                        Icon(imageVector = Icons.Default.PersonSearch, contentDescription = null, modifier = Modifier.size(16.dp), tint = Emerald700)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Find Ustaz", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Emerald700)
                    }
                }
            }

            // Live Class Hero Card
            item {
                Text(
                    text = "HAPPENING NOW",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LiveClassHeroCard(
                    quranClass = liveClass,
                    onJoinClick = onJoinLiveClass,
                    onDetailClick = { selectedClassDetail = liveClass }
                )
            }

            // Tabs Selector Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClassesTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        ChoicePill(
                            label = when (tab) {
                                ClassesTab.ALL -> "All Classes"
                                ClassesTab.UPCOMING -> "Upcoming (${upcomingClasses.size})"
                                ClassesTab.COMPLETED -> "Completed (${completedClasses.size})"
                            },
                            isSelected = isSelected,
                            onClick = { selectedTab = tab }
                        )
                    }
                }
            }

            // Classes List based on Tab
            when (selectedTab) {
                ClassesTab.ALL -> {
                    item {
                        Text(
                            text = "Upcoming Sessions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    items(upcomingClasses, key = { it.id }) { qClass ->
                        ClassItemCard(
                            quranClass = qClass,
                            onCardClick = { selectedClassDetail = qClass },
                            onJoinClick = {
                                onJoinLiveClass()
                                onShowSnackbar("Joining ${qClass.title}...")
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Past Sessions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    items(completedClasses, key = { it.id }) { qClass ->
                        ClassItemCard(
                            quranClass = qClass,
                            onCardClick = { selectedClassDetail = qClass },
                            onJoinClick = null
                        )
                    }
                }

                ClassesTab.UPCOMING -> {
                    items(upcomingClasses, key = { it.id }) { qClass ->
                        ClassItemCard(
                            quranClass = qClass,
                            onCardClick = { selectedClassDetail = qClass },
                            onJoinClick = {
                                onJoinLiveClass()
                                onShowSnackbar("Joining ${qClass.title}...")
                            }
                        )
                    }
                }

                ClassesTab.COMPLETED -> {
                    items(completedClasses, key = { it.id }) { qClass ->
                        ClassItemCard(
                            quranClass = qClass,
                            onCardClick = { selectedClassDetail = qClass },
                            onJoinClick = null
                        )
                    }
                }
            }
        }
    }

    // Join with Code Dialog
    if (showJoinCodeDialog) {
        JoinClassCodeDialog(
            onJoinWithCode = { code ->
                val result = repository.joinClassWithInviteCode(code)
                showJoinCodeDialog = false
                if (result.isSuccess) {
                    onShowSnackbar("Entered classroom: ${result.getOrNull()?.title}")
                    onJoinLiveClass()
                } else {
                    onShowSnackbar("Invalid class code. Please check and retry.")
                }
            },
            onDismiss = { showJoinCodeDialog = false }
        )
    }

    // Schedule Class Dialog
    if (showScheduleDialog) {
        ScheduleClassDialog(
            onSchedule = { title, subject, date, time, type, level ->
                val created = repository.scheduleNewClass(title, subject, date, time, type, level)
                showScheduleDialog = false
                onShowSnackbar("Scheduled: ${created.title} (Code: ${created.inviteCode})")
            },
            onDismiss = { showScheduleDialog = false }
        )
    }

    // Class Detail Bottom Sheet
    if (selectedClassDetail != null) {
        ClassDetailBottomSheet(
            quranClass = selectedClassDetail!!,
            onDismiss = { selectedClassDetail = null },
            onJoin = {
                val cls = selectedClassDetail!!
                selectedClassDetail = null
                onJoinLiveClass()
                onShowSnackbar("Joining ${cls.title}...")
            },
            onShowSnackbar = onShowSnackbar
        )
    }
}

@Composable
private fun LiveClassHeroCard(
    quranClass: QuranClass,
    onJoinClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
            .testTag("live_class_hero_card"),
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
                        color = Color(0xFFEF4444)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE NOW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Text(
                        text = "${quranClass.participantsCount}/${quranClass.maxParticipants} Students",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = quranClass.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = quranClass.teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                        contentDescription = quranClass.teacher.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${quranClass.teacher.name} ${quranClass.teacher.flagEmoji}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Emerald200
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                            text = "Focus: Surah ${quranClass.surahFocus ?: "Al-Baqarah"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Button(
                        onClick = onJoinClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Emerald950),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("join_live_now_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Join Room", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassItemCard(
    quranClass: QuranClass,
    onCardClick: () -> Unit,
    onJoinClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("class_card_${quranClass.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (quranClass.type == ClassType.ONE_ON_ONE) GoldContainer else Emerald100
                ) {
                    Text(
                        text = if (quranClass.type == ClassType.ONE_ON_ONE) "1-on-1 Talaqqi" else "Group Circle",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (quranClass.type == ClassType.ONE_ON_ONE) GoldOnContainer else Emerald900,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = quranClass.level,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = quranClass.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ustaz: ${quranClass.teacher.name} ${quranClass.teacher.flagEmoji}",
                style = MaterialTheme.typography.bodySmall,
                color = Emerald700
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quranClass.dateText} • ${quranClass.timeText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onJoinClick != null) {
                    FilledTonalButton(
                        onClick = onJoinClick,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Emerald700, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Enter", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JoinClassCodeDialog(
    onJoinWithCode: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Join Class via Code",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the 6-character room code or link provided by your teacher (e.g., VOX-786).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it.uppercase() },
                    placeholder = { Text("VOX-786") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("join_code_textfield"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onJoinWithCode(codeInput) },
                enabled = codeInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                modifier = Modifier.testTag("submit_join_code_button")
            ) {
                Text("Join Classroom")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ScheduleClassDialog(
    onSchedule: (title: String, subject: String, date: String, time: String, type: ClassType, level: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Tajwid & Recitation") }
    var dateText by remember { mutableStateOf("Tomorrow, 08:00 PM") }
    var classType by remember { mutableStateOf(ClassType.GROUP) }
    var level by remember { mutableStateOf("Intermediate") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Schedule Study Circle",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Session Title") },
                    placeholder = { Text("e.g. Surah Al-Mulk Talaqqi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject Focus") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date & Time") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChoicePill(
                        label = "Group Class",
                        isSelected = classType == ClassType.GROUP,
                        onClick = { classType = ClassType.GROUP }
                    )
                    ChoicePill(
                        label = "1-on-1",
                        isSelected = classType == ClassType.ONE_ON_ONE,
                        onClick = { classType = ClassType.ONE_ON_ONE }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSchedule(title, subject, dateText, "Scheduled", classType, level)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
            ) {
                Text("Create Session")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassDetailBottomSheet(
    quranClass: QuranClass,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (quranClass.status == ClassStatus.LIVE) Color(0xFFFEE2E2) else Emerald100
            ) {
                Text(
                    text = if (quranClass.status == ClassStatus.LIVE) "🔴 LIVE SESSION" else "UPCOMING CLASS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (quranClass.status == ClassStatus.LIVE) Color(0xFFDC2626) else Emerald900,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = quranClass.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = quranClass.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Teacher Info Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = quranClass.teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${quranClass.teacher.name} ${quranClass.teacher.flagEmoji}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = quranClass.teacher.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Room Invite Link / Code
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Emerald50,
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Class Room Code", style = MaterialTheme.typography.labelSmall, color = Emerald900)
                        Text(quranClass.inviteCode, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Emerald800)
                    }
                    OutlinedButton(
                        onClick = {
                            onShowSnackbar("Invite code copied: ${quranClass.inviteCode}")
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onJoin,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Enter Classroom Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}
