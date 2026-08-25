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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassStatus
import com.example.data.model.ClassType
import com.example.data.model.QuranClass
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.VoxoraHeaderBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    repository: VoxoraRepository,
    onJoinLiveClass: (QuranClass) -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveClass by repository.liveClass.collectAsState()
    val upcomingClasses by repository.upcomingClasses.collectAsState()
    val completedClasses by repository.completedClasses.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Upcoming, 1: Live, 2: Completed
    var selectedClassDetails by remember { mutableStateOf<QuranClass?>(null) }
    var showScheduleDialog by remember { mutableStateOf(false) }

    // Schedule Dialog Form States
    var newClassTitle by remember { mutableStateOf("") }
    var newClassSubject by remember { mutableStateOf("Tajwid — Noon Sakinah") }
    var newClassDate by remember { mutableStateOf("Friday, Aug 28") }
    var newClassTime by remember { mutableStateOf("08:00 PM") }
    var newClassType by remember { mutableStateOf(ClassType.GROUP) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "Classes & Halaqat",
                subtitle = "Join interactive live Quran sessions"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showScheduleDialog = true },
                containerColor = Emerald700,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("schedule_class_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule Class", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs: Upcoming, Live, Completed
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Emerald700
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upcoming (${upcomingClasses.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFDC2626)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Live (1)", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Completed (${completedClasses.size})", fontWeight = FontWeight.Bold) }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        items(upcomingClasses) { cls ->
                            QuranClassCard(
                                quranClass = cls,
                                onJoin = { onJoinLiveClass(cls) },
                                onViewDetails = { selectedClassDetails = cls }
                            )
                        }
                    }
                    1 -> {
                        item {
                            QuranClassCard(
                                quranClass = liveClass,
                                onJoin = { onJoinLiveClass(liveClass) },
                                onViewDetails = { selectedClassDetails = liveClass }
                            )
                        }
                    }
                    2 -> {
                        items(completedClasses) { cls ->
                            QuranClassCard(
                                quranClass = cls,
                                onJoin = {
                                    onShowSnackbar("Viewing recording notes for ${cls.title}")
                                },
                                onViewDetails = { selectedClassDetails = cls }
                            )
                        }
                    }
                }
            }
        }
    }

    // Class Details Bottom Sheet
    if (selectedClassDetails != null) {
        val cls = selectedClassDetails!!
        ModalBottomSheet(
            onDismissRequest = { selectedClassDetails = null }
        ) {
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
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (cls.status) {
                            ClassStatus.LIVE -> Color(0xFFFEE2E2)
                            ClassStatus.UPCOMING -> Emerald100
                            ClassStatus.COMPLETED -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = cls.status.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (cls.status) {
                                ClassStatus.LIVE -> Color(0xFFDC2626)
                                ClassStatus.UPCOMING -> Emerald800
                                ClassStatus.COMPLETED -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = if (cls.type == ClassType.ONE_ON_ONE) "1-on-1 Class" else "Group Halaqah",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = cls.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Subject: ${cls.subject}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Emerald700
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Teacher Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (cls.teacher.imageDrawableRes != null) {
                        Image(
                            painter = painterResource(id = cls.teacher.imageDrawableRes),
                            contentDescription = cls.teacher.name,
                            modifier = Modifier.size(50.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cls.teacher.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = cls.teacher.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${cls.teacher.title} (${cls.teacher.country})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "About This Class",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cls.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Prerequisites: ${cls.prerequisites}",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = Emerald800
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        selectedClassDetails = null
                        onJoinLiveClass(cls)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text(
                        text = if (cls.status == ClassStatus.LIVE) "Join Live Class Now" else "Enter Classroom",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Schedule Class Dialog
    if (showScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = {
                Text(
                    text = "Schedule Quran Class",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Emerald700
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newClassTitle,
                        onValueChange = { newClassTitle = it },
                        label = { Text("Class Title") },
                        placeholder = { Text("e.g., Weekly Surah Al-Kahf Talaqqi") },
                        modifier = Modifier.fillMaxWidth().testTag("schedule_title_field"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newClassSubject,
                        onValueChange = { newClassSubject = it },
                        label = { Text("Subject / Topic") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newClassDate,
                        onValueChange = { newClassDate = it },
                        label = { Text("Date") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newClassTime,
                        onValueChange = { newClassTime = it },
                        label = { Text("Time") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newClassTitle.isNotBlank()) {
                            repository.scheduleNewClass(
                                title = newClassTitle,
                                subject = newClassSubject,
                                dateText = newClassDate,
                                timeText = newClassTime,
                                type = newClassType
                            )
                            onShowSnackbar("Class scheduled successfully!")
                            showScheduleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    modifier = Modifier.testTag("confirm_schedule_class_button")
                ) {
                    Text("Confirm Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuranClassCard(
    quranClass: QuranClass,
    onJoin: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onViewDetails() }
            .testTag("class_card_${quranClass.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status Badge & Class Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (quranClass.status) {
                        ClassStatus.LIVE -> Color(0xFFFEE2E2)
                        ClassStatus.UPCOMING -> Emerald100
                        ClassStatus.COMPLETED -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (quranClass.status == ClassStatus.LIVE) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDC2626)))
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = quranClass.status.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (quranClass.status) {
                                    ClassStatus.LIVE -> Color(0xFFDC2626)
                                    ClassStatus.UPCOMING -> Emerald800
                                    ClassStatus.COMPLETED -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        )
                    }
                }

                Text(
                    text = "${quranClass.participantsCount}/${quranClass.maxParticipants} Students",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = quranClass.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Subject: ${quranClass.subject}",
                style = MaterialTheme.typography.bodySmall,
                color = Emerald700
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Teacher Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (quranClass.teacher.imageDrawableRes != null) {
                    Image(
                        painter = painterResource(id = quranClass.teacher.imageDrawableRes),
                        contentDescription = quranClass.teacher.name,
                        modifier = Modifier.size(36.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Emerald700),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(quranClass.teacher.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = quranClass.teacher.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${quranClass.dateText} • ${quranClass.timeText} (${quranClass.durationMinutes}m)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Details")
                }

                Button(
                    onClick = onJoin,
                    modifier = Modifier.weight(1f).height(42.dp).testTag("class_join_btn_${quranClass.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (quranClass.status == ClassStatus.LIVE) Color(0xFFDC2626) else Emerald700,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (quranClass.status == ClassStatus.LIVE) "Join Live Now" else "Join Class",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
