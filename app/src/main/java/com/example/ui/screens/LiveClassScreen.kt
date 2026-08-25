package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.mock.MockQuranData
import com.example.data.model.ClassChatMessage
import com.example.data.model.ClassType
import com.example.data.model.Participant
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.components.TajwidRuleBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveClassScreen(
    repository: VoxoraRepository,
    onLeaveClass: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveClass by repository.liveClass.collectAsState()
    val participants by repository.participants.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val isMicMuted by repository.isMyMicMuted.collectAsState()
    val isVideoOn by repository.isMyVideoOn.collectAsState()
    val isSpeakerOn by repository.isMySpeakerOn.collectAsState()
    val isHandRaised by repository.isMyHandRaised.collectAsState()
    val highlightedVerseNum by repository.classHighlightedVerse.collectAsState()
    val teacherAnnotation by repository.teacherAnnotation.collectAsState()
    val classMode by repository.liveClassMode.collectAsState()

    var showChatSheet by remember { mutableStateOf(false) }
    var showParticipantsSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    var isSharingScreen by remember { mutableStateOf(false) }

    // Sample Quran surah for active lesson
    val classSurah = remember { MockQuranData.surahList.find { it.number == 2 } ?: MockQuranData.surahList.first() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { showLeaveConfirmation = true },
                        modifier = Modifier.testTag("live_class_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Leave")
                    }
                },
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE CLASS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (classMode == ClassType.GROUP) "Group Session" else "1-on-1 Session",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald300
                            )
                        }
                        Text(
                            text = liveClass.subject,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                },
                actions = {
                    // Switch 1-on-1 vs Group mode toggle
                    FilledTonalButton(
                        onClick = {
                            val newMode = if (classMode == ClassType.GROUP) ClassType.ONE_ON_ONE else ClassType.GROUP
                            repository.setLiveClassMode(newMode)
                            onShowSnackbar("Switched to ${if (newMode == ClassType.GROUP) "Group Mode" else "1-on-1 Focus Mode"}")
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Emerald800,
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (classMode == ClassType.GROUP) "Switch 1-on-1" else "Switch Group",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Live Classroom Controls Bar
            LiveClassControlsBar(
                isMicMuted = isMicMuted,
                isVideoOn = isVideoOn,
                isSpeakerOn = isSpeakerOn,
                isHandRaised = isHandRaised,
                isSharingScreen = isSharingScreen,
                unreadChatCount = 1,
                participantsCount = participants.size,
                onToggleMic = {
                    repository.toggleMyMic()
                    onShowSnackbar(if (isMicMuted) "Microphone Unmuted" else "Microphone Muted")
                },
                onToggleVideo = {
                    repository.toggleMyVideo()
                    onShowSnackbar(if (isVideoOn) "Camera Disabled" else "Camera Enabled")
                },
                onToggleSpeaker = {
                    repository.toggleMySpeaker()
                    onShowSnackbar(if (isSpeakerOn) "Audio Muted" else "Audio Enabled")
                },
                onToggleRaiseHand = {
                    repository.toggleRaiseHand()
                    onShowSnackbar(if (isHandRaised) "Lowered hand" else "✋ Raised hand for teacher recitation!")
                },
                onToggleShare = {
                    isSharingScreen = !isSharingScreen
                    onShowSnackbar(if (isSharingScreen) "Sharing your Quran Reading View" else "Stopped Sharing")
                },
                onOpenChat = { showChatSheet = true },
                onOpenParticipants = { showParticipantsSheet = true },
                onLeave = { showLeaveConfirmation = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // 1. Teacher Video Area
            item {
                TeacherVideoCard(
                    teacher = liveClass.teacher,
                    lesson = liveClass.subject,
                    participantsCount = participants.size,
                    isAnnotationActive = teacherAnnotation != null
                )
            }

            // 2. Student Participant Thumbnails (if Group mode)
            if (classMode == ClassType.GROUP) {
                item {
                    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Students in Room (${participants.size})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextLightSecondary
                            )
                            Text(
                                text = "Tap to view list",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald400,
                                modifier = Modifier.clickable { showParticipantsSheet = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(participants.filterNot { it.isTeacher }) { p ->
                                ParticipantMiniTile(participant = p)
                            }
                        }
                    }
                }
            }

            // 3. Interactive Quran Reading & Annotation Panel
            item {
                QuranLiveClassPanel(
                    surah = classSurah,
                    highlightedVerse = highlightedVerseNum,
                    teacherAnnotation = teacherAnnotation,
                    onVerseClick = { verseNum ->
                        repository.setClassHighlightedVerse(verseNum)
                        onShowSnackbar("Highlighted Verse $verseNum for live recitation")
                    }
                )
            }
        }
    }

    // Leave Class Confirmation Dialog
    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            title = { Text("Leave Live Class?") },
            text = { Text("You are currently in an active session with ${liveClass.teacher.name}. Are you sure you want to exit the classroom?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirmation = false
                        onLeaveClass()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Leave Class")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLeaveConfirmation = false }) {
                    Text("Stay in Class")
                }
            }
        )
    }

    // Live Chat Sheet Modal
    if (showChatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChatSheet = false },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Class Live Chat",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    IconButton(onClick = { showChatSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Divider(color = DarkSurfaceVariant)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { msg ->
                        ChatMessageItem(message = msg)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInputText,
                        onValueChange = { chatInputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("class_chat_input"),
                        placeholder = { Text("Type question or reflection...", color = TextLightSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Emerald400,
                            unfocusedBorderColor = DarkSurfaceVariant
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (chatInputText.isNotBlank()) {
                                repository.sendClassChatMessage(chatInputText)
                                chatInputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Emerald700)
                            .testTag("class_chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Participants List Sheet Modal
    if (showParticipantsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showParticipantsSheet = false },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Participants (${participants.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(participants) { p ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (p.isTeacher) GoldPrimary else Emerald700),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = p.name.take(1),
                                            fontWeight = FontWeight.Bold,
                                            color = if (p.isTeacher) Emerald900 else Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = p.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = p.role,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (p.isTeacher) GoldLight else TextLightSecondary
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (p.isHandRaised) {
                                        Text("✋", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Icon(
                                        imageVector = if (p.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = if (p.isMicMuted) Color(0xFFEF4444) else Emerald400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (p.isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                        contentDescription = null,
                                        tint = if (p.isVideoOn) Emerald400 else TextLightSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherVideoCard(
    teacher: com.example.data.model.Teacher,
    lesson: String,
    participantsCount: Int,
    isAnnotationActive: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(210.dp)
            .testTag("teacher_video_area"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (teacher.imageDrawableRes != null) {
                Image(
                    painter = painterResource(id = teacher.imageDrawableRes),
                    contentDescription = teacher.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Emerald900, DarkBackground)
                            )
                        )
                )
            }

            // Dark gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Emerald900.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "$participantsCount Students",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Emerald300,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Bottom Teacher Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = teacher.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = lesson,
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald300
                        )
                    }

                    // Speaking Audio Waves Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Box(modifier = Modifier.size(3.dp, 12.dp).background(Emerald400, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(3.dp, 18.dp).background(Emerald400, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(3.dp, 10.dp).background(Emerald400, RoundedCornerShape(2.dp)))
                    }
                }
            }
        }
    }
}

@Composable
private fun ParticipantMiniTile(participant: Participant) {
    Surface(
        modifier = Modifier
            .size(80.dp, 90.dp)
            .clip(RoundedCornerShape(14.dp)),
        color = DarkSurfaceVariant,
        border = if (participant.isHandRaised) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)
        ) else CardDefaults.outlinedCardBorder()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Emerald700),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = participant.name.take(1),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = participant.name.split(" ").firstOrNull() ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1
                )
            }

            if (participant.isHandRaised) {
                Text(
                    text = "✋",
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            Icon(
                imageVector = if (participant.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = null,
                tint = if (participant.isMicMuted) Color(0xFFEF4444) else Emerald400,
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun QuranLiveClassPanel(
    surah: com.example.data.model.Surah,
    highlightedVerse: Int,
    teacherAnnotation: String?,
    onVerseClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("quran_live_reading_panel"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shared Quran — Surah ${surah.nameEnglish}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldContainer
                ) {
                    Text(
                        text = "Live Shared View",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldOnContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (teacherAnnotation != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Emerald50
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📌", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = teacherAnnotation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Emerald900
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Verses for live recitation drill
            surah.verses.forEach { verse ->
                val isHighlighted = verse.verseNumber == highlightedVerse
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onVerseClick(verse.verseNumber) },
                    color = if (isHighlighted) GoldContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (isHighlighted) CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)
                    ) else null
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Verse ${verse.verseNumber}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHighlighted) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            if (verse.tajwidRuleHighlight != null) {
                                TajwidRuleBadge(ruleText = verse.tajwidRuleHighlight)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = verse.textArabic,
                            fontSize = 22.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = verse.translationEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveClassControlsBar(
    isMicMuted: Boolean,
    isVideoOn: Boolean,
    isSpeakerOn: Boolean,
    isHandRaised: Boolean,
    isSharingScreen: Boolean,
    unreadChatCount: Int,
    participantsCount: Int,
    onToggleMic: () -> Unit,
    onToggleVideo: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleRaiseHand: () -> Unit,
    onToggleShare: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenParticipants: () -> Unit,
    onLeave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic
            IconButton(
                onClick = onToggleMic,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isMicMuted) Color(0xFFEF4444).copy(alpha = 0.2f) else Emerald700.copy(alpha = 0.2f))
                    .testTag("live_mic_control")
            ) {
                Icon(
                    imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = if (isMicMuted) Color(0xFFEF4444) else Emerald400
                )
            }

            // Video
            IconButton(
                onClick = onToggleVideo,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (!isVideoOn) Color(0xFFEF4444).copy(alpha = 0.2f) else Emerald700.copy(alpha = 0.2f))
                    .testTag("live_video_control")
            ) {
                Icon(
                    imageVector = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    contentDescription = "Video",
                    tint = if (isVideoOn) Emerald400 else Color(0xFFEF4444)
                )
            }

            // Raise Hand
            IconButton(
                onClick = onToggleRaiseHand,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isHandRaised) GoldPrimary else DarkSurfaceVariant)
                    .testTag("live_raise_hand_control")
            ) {
                Text("✋", fontSize = 18.sp)
            }

            // Chat
            IconButton(
                onClick = onOpenChat,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .testTag("live_chat_control")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadChatCount > 0) {
                            Badge(containerColor = GoldPrimary, contentColor = Emerald900) {
                                Text("$unreadChatCount", fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = "Chat", tint = Color.White)
                }
            }

            // Participants
            IconButton(
                onClick = onOpenParticipants,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .testTag("live_participants_control")
            ) {
                Icon(imageVector = Icons.Default.PeopleOutline, contentDescription = "Participants", tint = Color.White)
            }

            // Leave Button
            FilledIconButton(
                onClick = onLeave,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("live_leave_button"),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFDC2626))
            ) {
                Icon(imageVector = Icons.Default.CallEnd, contentDescription = "Leave", tint = Color.White)
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ClassChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
                message.isMe -> Emerald700
                message.isTeacher -> GoldDark
                else -> DarkSurfaceVariant
            },
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (message.isTeacher) GoldLight else Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}
