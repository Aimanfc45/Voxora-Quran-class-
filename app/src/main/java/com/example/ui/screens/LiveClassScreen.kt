package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
                        modifier = Modifier.padding(end = 8.dp).testTag("live_class_switch_mode_btn")
                    ) {
                        Text(
                            text = if (classMode == ClassType.GROUP) "Switch 1-on-1" else "Switch Group",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Leave Action Button
                    IconButton(
                        onClick = { showLeaveConfirmation = true },
                        modifier = Modifier.testTag("live_class_leave_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Leave Call",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Live Classroom In-Call Action Control Bar
            Surface(
                color = DarkSurface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Mic Toggle
                    LiveControlButton(
                        icon = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (isMicMuted) "Unmute" else "Muted",
                        isActive = !isMicMuted,
                        activeColor = Emerald500,
                        inactiveColor = Color(0xFFEF4444),
                        onClick = {
                            repository.toggleMyMic()
                            onShowSnackbar(if (isMicMuted) "Microphone Unmuted" else "Microphone Muted")
                        },
                        testTag = "live_class_mic_toggle"
                    )

                    // 2. Video Toggle
                    LiveControlButton(
                        icon = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        label = if (isVideoOn) "Video On" else "Video Off",
                        isActive = isVideoOn,
                        activeColor = Emerald500,
                        inactiveColor = Color.Gray,
                        onClick = {
                            repository.toggleMyVideo()
                            onShowSnackbar(if (isVideoOn) "Camera Disabled" else "Camera Enabled")
                        },
                        testTag = "live_class_video_toggle"
                    )

                    // 3. Speaker Toggle
                    LiveControlButton(
                        icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        label = if (isSpeakerOn) "Speaker" else "Mute Spk",
                        isActive = isSpeakerOn,
                        activeColor = Emerald500,
                        inactiveColor = Color.Gray,
                        onClick = {
                            repository.toggleMySpeaker()
                            onShowSnackbar(if (isSpeakerOn) "Speaker Muted" else "Speaker Unmuted")
                        },
                        testTag = "live_class_speaker_toggle"
                    )

                    // 4. Raise Hand Button
                    LiveControlButton(
                        icon = Icons.Default.PanTool,
                        label = if (isHandRaised) "Hand Up" else "Raise Hand",
                        isActive = isHandRaised,
                        activeColor = GoldPrimary,
                        inactiveColor = Color.White.copy(alpha = 0.7f),
                        onClick = {
                            val raised = repository.toggleRaiseHand()
                            onShowSnackbar(if (raised) "Hand raised! Ustaz notified." else "Hand lowered.")
                        },
                        testTag = "live_class_raise_hand_toggle"
                    )

                    // 5. Participants Sheet Toggle
                    LiveControlButton(
                        icon = Icons.Default.People,
                        label = "${participants.size}",
                        isActive = showParticipantsSheet,
                        activeColor = GoldPrimary,
                        inactiveColor = Color.White.copy(alpha = 0.7f),
                        onClick = { showParticipantsSheet = true },
                        testTag = "live_class_participants_btn"
                    )

                    // 6. Chat Button with Unread Badge
                    Box {
                        LiveControlButton(
                            icon = Icons.Default.Chat,
                            label = "Chat",
                            isActive = showChatSheet,
                            activeColor = GoldPrimary,
                            inactiveColor = Color.White.copy(alpha = 0.7f),
                            onClick = { showChatSheet = true },
                            testTag = "live_class_chat_btn"
                        )
                        if (chatMessages.isNotEmpty() && !showChatSheet) {
                            Badge(
                                containerColor = GoldPrimary,
                                contentColor = Emerald950,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text("${chatMessages.size}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 7. Screen/Page Share Toggle
                    LiveControlButton(
                        icon = if (isSharingScreen) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                        label = "Share",
                        isActive = isSharingScreen,
                        activeColor = GoldPrimary,
                        inactiveColor = Color.White.copy(alpha = 0.7f),
                        onClick = {
                            isSharingScreen = !isSharingScreen
                            onShowSnackbar(if (isSharingScreen) "Sharing Quran page with classroom" else "Stopped sharing")
                        },
                        testTag = "live_class_share_toggle"
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Prototype Status Notice
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Emerald900.copy(alpha = 0.4f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive Classroom Prototype — Ready for WebRTC / LiveKit Realtime Audio & Video integration.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald200
                        )
                    }
                }
            }

            // Video Feeds Section (Adapts for 1-on-1 vs Group View)
            item {
                if (classMode == ClassType.ONE_ON_ONE) {
                    // 1-on-1 Spotlight Layout (Large Teacher view + Student PIP)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurface)
                    ) {
                        // Teacher Video Spotlight
                        Image(
                            painter = painterResource(id = liveClass.teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                            contentDescription = "Teacher Stream",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top gradient overlay with teacher speaking indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Emerald500
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Speaking", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(liveClass.teacher.name, color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.5f)
                                ) {
                                    Text("HD 1080p", color = Emerald300, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        // Student PIP (Picture-in-Picture)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(width = 100.dp, height = 130.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Emerald950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (isVideoOn) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Emerald900),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("You", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(DarkSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VideocamOff,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                // Status icons in PIP
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(4.dp)
                                ) {
                                    if (isMicMuted) {
                                        Icon(
                                            imageVector = Icons.Default.MicOff,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    if (isHandRaised) {
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(
                                            imageVector = Icons.Default.PanTool,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Group Classroom Video Grid
                    Column {
                        // Teacher Stream Header
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = liveClass.teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                                    contentDescription = "Teacher",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                                            )
                                        )
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Emerald600
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(liveClass.teacher.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Student Tiles Row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(participants.filterNot { it.isTeacher }) { p ->
                                ParticipantTile(participant = p)
                            }
                        }
                    }
                }
            }

            // Interactive Quran Whiteboard & Assessment Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald700))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Interactive Quran Sheet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldPrimary.copy(alpha = 0.2f),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Text(
                                    text = "Surah ${classSurah.nameEnglish} (2:1–5)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (teacherAnnotation != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Emerald900.copy(alpha = 0.8f),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = teacherAnnotation!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Verse recitation tiles
                        classSurah.verses.take(5).forEach { verse ->
                            val isHighlighted = verse.verseNumber == highlightedVerseNum
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        repository.setClassHighlightedVerse(verse.verseNumber)
                                        onShowSnackbar("Highlighted Verse ${verse.verseNumber} for class recitation")
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isHighlighted) Emerald900.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.05f)
                                ),
                                border = if (isHighlighted) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)) else null
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isHighlighted) GoldPrimary else Emerald800
                                        ) {
                                            Text(
                                                text = "${verse.verseNumber}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isHighlighted) Emerald950 else Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (isHighlighted) {
                                            Text(
                                                text = "🎯 Active Recitation Focus",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = GoldPrimary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = verse.textArabic,
                                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                        color = Color.White,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (verse.tajwidRuleHighlight != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        TajwidRuleBadge(ruleText = verse.tajwidRuleHighlight)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Leave Confirmation Dialog
    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            title = {
                Text(
                    text = "Leave Live Class?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text("Are you sure you want to leave this live session? You can rejoin anytime from the Classes tab.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirmation = false
                        onLeaveClass()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Leave Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmation = false }) {
                    Text("Stay in Class")
                }
            }
        )
    }

    // In-Class Chat Sheet
    if (showChatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChatSheet = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Classroom Discussion",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (msg.isMe) Emerald800 else if (msg.isTeacher) GoldPrimary.copy(alpha = 0.2f) else Emerald950
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = msg.senderName + if (msg.isTeacher) " (Teacher)" else "",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (msg.isTeacher) GoldPrimary else Emerald300
                                    )
                                    Text(
                                        text = msg.timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chat Input Row
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
                        placeholder = { Text("Ask a Tajwid question or share note...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Emerald500,
                            unfocusedBorderColor = Emerald800
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

    // In-Class Participants Sheet
    if (showParticipantsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showParticipantsSheet = false },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Participants (${participants.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(participants) { p ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald950
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
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
                                            color = if (p.isTeacher) Emerald950 else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = p.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = p.role,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (p.isTeacher) GoldPrimary else Emerald300
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (p.isHandRaised) {
                                        Icon(
                                            imageVector = Icons.Default.PanTool,
                                            contentDescription = "Hand Raised",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Icon(
                                        imageVector = if (p.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = "Mic Status",
                                        tint = if (p.isMicMuted) Color(0xFFEF4444) else Emerald400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (p.isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                        contentDescription = "Video Status",
                                        tint = if (p.isVideoOn) Emerald400 else Color.Gray,
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
private fun LiveControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) activeColor else Color.White.copy(alpha = 0.7f),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ParticipantTile(participant: Participant) {
    Surface(
        modifier = Modifier
            .size(width = 110.dp, height = 90.dp),
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = if (participant.isHandRaised) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)) else CardDefaults.outlinedCardBorder()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Emerald800),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = participant.name.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = participant.name.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1
                )
            }

            // Top status badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (participant.isHandRaised) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = "Hand Raised",
                        tint = GoldPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(12.dp))
                }

                Icon(
                    imageVector = if (participant.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = null,
                    tint = if (participant.isMicMuted) Color(0xFFEF4444) else Emerald400,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
