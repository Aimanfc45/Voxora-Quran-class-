package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.mock.MockQuranData
import com.example.data.model.ClassChatMessage
import com.example.data.model.ClassType
import com.example.data.model.Participant
import com.example.data.realtime.ClassroomRole
import com.example.data.realtime.ConnectionQualityLevel
import com.example.data.realtime.LiveKitClassService
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.LiveKitVideoRenderer
import com.example.ui.components.TajwidRuleBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveClassScreen(
    repository: VoxoraRepository,
    onLeaveClass: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Real-time Service Instance
    val liveKitService = remember { LiveKitClassService(context, coroutineScope) }

    // Service state collection
    val connectionQuality by liveKitService.connectionQuality.collectAsState()
    val isConnectedToRealRoom by liveKitService.isConnectedToRealRoom.collectAsState()
    val isConnecting by liveKitService.isConnecting.collectAsState()
    val isMicMuted by liveKitService.isMicMuted.collectAsState()
    val isVideoOn by liveKitService.isVideoOn.collectAsState()
    val isSpeakerOn by liveKitService.isSpeakerOn.collectAsState()
    val isHandRaised by liveKitService.isHandRaised.collectAsState()
    val participants by liveKitService.participants.collectAsState()
    val chatMessages by liveKitService.chatMessages.collectAsState()
    val quranSyncState by liveKitService.synchronizedQuranState.collectAsState()
    val activeSpeaker by liveKitService.activeSpeaker.collectAsState()
    val selectedReciter by liveKitService.selectedStudentReciter.collectAsState()
    val latestAssessment by liveKitService.latestAssessment.collectAsState()
    val handRaiseAlert by liveKitService.activeHandRaiseAlert.collectAsState()
    val localVideoTrack by liveKitService.localVideoTrack.collectAsState()
    val teacherVideoTrack by liveKitService.teacherVideoTrack.collectAsState()
    val currentRole by liveKitService.myRole.collectAsState()
    val classMode by liveKitService.classMode.collectAsState()
    val currentConfig by liveKitService.config.collectAsState()

    val liveClass by repository.liveClass.collectAsState()

    // UI Dialog & Sheet states
    var showChatSheet by remember { mutableStateOf(false) }
    var showParticipantsSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showAssessmentDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    var isSharingScreen by remember { mutableStateOf(false) }

    // Assessment inputs
    var assessmentStudentName by remember { mutableStateOf("Ahmed Al-Farsi") }
    var assessmentScore by remember { mutableFloatStateOf(95f) }
    var assessmentFeedback by remember { mutableStateOf("Excellent Mad Asli elongation and clear makhraj.") }

    // Config inputs
    var configServerUrl by remember { mutableStateOf(currentConfig.serverUrl) }
    var configTokenEndpoint by remember { mutableStateOf(currentConfig.tokenEndpoint) }
    var configDevToken by remember { mutableStateOf(currentConfig.devToken) }

    // Permissions launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true

        if (recordAudioGranted && cameraGranted) {
            onShowSnackbar("Microphone & Camera permissions granted")
        } else if (!recordAudioGranted || !cameraGranted) {
            showPermissionRationale = true
        }
    }

    LaunchedEffect(Unit) {
        val hasAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!hasAudio || !hasCamera) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
            )
        }
        liveKitService.joinClass(classId = liveClass.id, participantName = "Ahmed Al-Farsi (You)", role = currentRole)
    }

    DisposableEffect(Unit) {
        onDispose {
            liveKitService.leaveClass()
        }
    }

    // Active Quran Surah
    val classSurah = remember { MockQuranData.surahList.find { it.number == quranSyncState.surah } ?: MockQuranData.surahList.first() }

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
                            // Live Pulse Dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isConnectedToRealRoom) Color(0xFF10B981) else Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isConnectedToRealRoom) "LIVE (CONNECTED)" else "LIVE CLASS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isConnectedToRealRoom) Emerald400 else Color(0xFFEF4444)
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
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    // Mode Switcher (1-on-1 vs Group)
                    FilledTonalButton(
                        onClick = {
                            val newMode = if (classMode == ClassType.GROUP) ClassType.ONE_ON_ONE else ClassType.GROUP
                            liveKitService.setClassMode(newMode)
                            onShowSnackbar("Switched to ${if (newMode == ClassType.GROUP) "Group Mode" else "1-on-1 Focus Mode"}")
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Emerald800,
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 4.dp).testTag("live_class_switch_mode_btn")
                    ) {
                        Text(
                            text = if (classMode == ClassType.GROUP) "1-on-1" else "Group",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Config Dialog Toggle Button
                    IconButton(
                        onClick = { showConfigDialog = true },
                        modifier = Modifier.testTag("live_class_config_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsEthernet,
                            contentDescription = "LiveKit Configuration",
                            tint = if (isConnectedToRealRoom) Emerald400 else GoldPrimary
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
            Surface(
                color = DarkSurface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Mic Toggle
                    LiveControlButton(
                        icon = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (isMicMuted) "Unmute" else "Mute",
                        isActive = !isMicMuted,
                        activeColor = Emerald500,
                        inactiveColor = Color(0xFFEF4444),
                        onClick = {
                            liveKitService.toggleMic()
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
                            liveKitService.toggleVideo()
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
                            liveKitService.toggleSpeaker()
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
                            val raised = liveKitService.toggleRaiseHand()
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
                            onShowSnackbar(if (isSharingScreen) "Quran Sheet highlighted for all participants" else "Stopped sharing")
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
            // Live Status & Connection Banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (connectionQuality) {
                        ConnectionQualityLevel.EXCELLENT -> Emerald900.copy(alpha = 0.4f)
                        ConnectionQualityLevel.GOOD -> Emerald900.copy(alpha = 0.3f)
                        ConnectionQualityLevel.POOR -> Color(0xFF78350F).copy(alpha = 0.4f)
                        ConnectionQualityLevel.RECONNECTING -> Color(0xFF831843).copy(alpha = 0.4f)
                        ConnectionQualityLevel.DISCONNECTED -> Color(0xFF7F1D1D).copy(alpha = 0.4f)
                        ConnectionQualityLevel.UNCONFIGURED -> Emerald900.copy(alpha = 0.35f)
                    },
                    border = BorderStroke(1.dp, if (isConnectedToRealRoom) Emerald600.copy(alpha = 0.5f) else GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = if (isConnectedToRealRoom) Icons.Default.Wifi else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isConnectedToRealRoom) Emerald400 else GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (connectionQuality) {
                                    ConnectionQualityLevel.EXCELLENT -> "LiveKit WebRTC Connected • Excellent Quality"
                                    ConnectionQualityLevel.GOOD -> "LiveKit WebRTC Connected • Good Quality"
                                    ConnectionQualityLevel.POOR -> "Weak Connection • Audio priority mode"
                                    ConnectionQualityLevel.RECONNECTING -> "Reconnecting to live room..."
                                    ConnectionQualityLevel.DISCONNECTED -> "Disconnected from live room"
                                    ConnectionQualityLevel.UNCONFIGURED -> "Interactive Sandbox Mode • LiveKit ready"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isConnectedToRealRoom) Emerald200 else GoldLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Role Switcher Badge (Student / Teacher Mode)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentRole == ClassroomRole.TEACHER) GoldPrimary else Emerald800,
                            modifier = Modifier.clickable {
                                val nextRole = if (currentRole == ClassroomRole.TEACHER) ClassroomRole.STUDENT else ClassroomRole.TEACHER
                                liveKitService.setMyRole(nextRole)
                                onShowSnackbar("Acting as ${if (nextRole == ClassroomRole.TEACHER) "Ustaz (Teacher)" else "Student"}")
                            }
                        ) {
                            Text(
                                text = if (currentRole == ClassroomRole.TEACHER) "Ustaz Role" else "Student Role",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (currentRole == ClassroomRole.TEACHER) Emerald950 else Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Teacher Hand Raise Alert Banner (When a student raises hand)
            if (handRaiseAlert != null && currentRole == ClassroomRole.TEACHER) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldPrimary.copy(alpha = 0.15f),
                        border = BorderStroke(1.5.dp, GoldPrimary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("✋", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${handRaiseAlert!!.participantName} raised hand",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight
                                    )
                                    Text(
                                        text = "Requests permission to recite Verse ${quranSyncState.ayah}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Row {
                                Button(
                                    onClick = {
                                        liveKitService.acceptHandRaise(handRaiseAlert!!)
                                        onShowSnackbar("Granted recitation to ${handRaiseAlert!!.participantName}")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                OutlinedButton(
                                    onClick = {
                                        liveKitService.dismissHandRaise(handRaiseAlert!!)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Dismiss", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Latest Assessment Banner (If received)
            if (latestAssessment != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Emerald900.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, GoldPrimary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌟", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Recitation Score: ${latestAssessment!!.score}/100",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldPrimary
                                    )
                                    Text(
                                        text = latestAssessment!!.timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Emerald300
                                    )
                                }
                                Text(
                                    text = "For ${latestAssessment!!.studentName} • ${latestAssessment!!.tajwidFeedback}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Video Feeds Section (1-on-1 vs Group View)
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
                        if (teacherVideoTrack != null && isConnectedToRealRoom) {
                            LiveKitVideoRenderer(
                                room = liveKitService.room,
                                videoTrack = teacherVideoTrack,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = liveClass.teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                                contentDescription = "Teacher Stream",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

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
                                            Text(
                                                text = if (activeSpeaker != null) "$activeSpeaker Speaking" else "Ustaz Ahmad (Speaking)",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = if (isConnectedToRealRoom) "LiveKit HD" else "Sandbox Mode",
                                        color = Emerald300,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
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
                            border = BorderStroke(1.5.dp, GoldPrimary)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (isVideoOn) {
                                    if (localVideoTrack != null && isConnectedToRealRoom) {
                                        LiveKitVideoRenderer(
                                            room = liveKitService.room,
                                            videoTrack = localVideoTrack,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Emerald900),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = GoldLight,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                                Text("You", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                        }
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (teacherVideoTrack != null && isConnectedToRealRoom) {
                                    LiveKitVideoRenderer(
                                        room = liveKitService.room,
                                        videoTrack = teacherVideoTrack,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = liveClass.teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                                        contentDescription = "Teacher",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

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
                                            Text(
                                                text = if (activeSpeaker != null) "$activeSpeaker Speaking" else liveClass.teacher.name,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (selectedReciter != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = GoldPrimary.copy(alpha = 0.9f)
                                        ) {
                                            Text(
                                                text = "🎙️ Reciter: $selectedReciter",
                                                color = Emerald950,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
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
                                ParticipantTile(
                                    participant = p,
                                    isSelectedReciter = p.name == selectedReciter,
                                    onSelectAsReciter = {
                                        if (currentRole == ClassroomRole.TEACHER) {
                                            liveKitService.selectStudentReciter(p.id, p.name)
                                            onShowSnackbar("Selected ${p.name} as active reciter")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Shared Quran Sheet (Synchronized with All Participants)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, Emerald700)
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
                                    text = "Synchronized Quran Sheet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Previous / Next Verse Stepper
                                IconButton(
                                    onClick = { liveKitService.previousVerse() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous Verse",
                                        tint = GoldLight
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldPrimary.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "Surah ${classSurah.nameEnglish} (2:1–5)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { liveKitService.nextVerse() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Verse",
                                        tint = GoldLight
                                    )
                                }
                            }
                        }

                        if (quranSyncState.note != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Emerald900.copy(alpha = 0.8f),
                                border = BorderStroke(1.dp, GoldPrimary)
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
                                        text = quranSyncState.note ?: "Ustaz highlighted Verse ${quranSyncState.ayah} for recitation assessment.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Verse recitation tiles
                        classSurah.verses.take(5).forEach { verse ->
                            val isHighlighted = verse.verseNumber == quranSyncState.ayah
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        liveKitService.setSynchronizedQuranVerse(
                                            surah = 2,
                                            ayah = verse.verseNumber,
                                            tajwidRule = verse.tajwidRuleHighlight ?: "Mad Asli",
                                            note = "Ustaz highlighted Verse ${verse.verseNumber} for active recitation & Tajwid feedback."
                                        )
                                        onShowSnackbar("Synchronized Verse ${verse.verseNumber} across classroom")
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isHighlighted) Emerald900.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.05f)
                                ),
                                border = if (isHighlighted) BorderStroke(1.5.dp, GoldPrimary) else null
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
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "🎯 Active Recitation Focus",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = GoldPrimary
                                                )
                                                if (currentRole == ClassroomRole.TEACHER) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = Emerald600,
                                                        modifier = Modifier.clickable {
                                                            showAssessmentDialog = true
                                                        }
                                                    ) {
                                                        Text(
                                                            text = "Score Recitation",
                                                            fontSize = 10.sp,
                                                            color = Color.White,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
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
                        liveKitService.leaveClass()
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

    // Permission Explanation Dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = {
                Text("Permissions Needed for Classroom")
            },
            text = {
                Text("Voxora Quran requires Microphone and Camera permissions to allow interactive Tajwid recitation, teacher assessments, and live classroom video participation.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationale = false
                        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                ) {
                    Text("Grant Permissions")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Continue in Listen Only")
                }
            }
        )
    }

    // LiveKit WebRTC Configuration Dialog
    if (showConfigDialog) {
        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.SettingsEthernet, contentDescription = null, tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LiveKit Server Configuration", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Connect to your LiveKit Cloud or Self-Hosted WebRTC server. When unconfigured, Voxora operates in Interactive Sandbox Prototype Mode.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = configServerUrl,
                        onValueChange = { configServerUrl = it },
                        label = { Text("Server URL (e.g. wss://...)") },
                        placeholder = { Text("wss://your-subdomain.livekit.cloud") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = configTokenEndpoint,
                        onValueChange = { configTokenEndpoint = it },
                        label = { Text("Token Auth Endpoint (Recommended)") },
                        placeholder = { Text("https://api.voxora.app/api/token") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = configDevToken,
                        onValueChange = { configDevToken = it },
                        label = { Text("Dev Token (Optional JWT for Testing)") },
                        placeholder = { Text("Paste LiveKit JWT token...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        liveKitService.updateConfig(
                            serverUrl = configServerUrl,
                            tokenEndpoint = configTokenEndpoint,
                            devToken = configDevToken
                        )
                        showConfigDialog = false
                        coroutineScope.launch {
                            liveKitService.joinClass(classId = liveClass.id, participantName = "Ahmed Al-Farsi", role = currentRole)
                        }
                        onShowSnackbar("LiveKit Configuration Updated")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                ) {
                    Text("Save & Connect")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Teacher Assessment Dialog
    if (showAssessmentDialog) {
        AlertDialog(
            onDismissRequest = { showAssessmentDialog = false },
            title = {
                Text("Give Recitation Assessment", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Student: $assessmentStudentName • Verse ${quranSyncState.ayah}", style = MaterialTheme.typography.bodyMedium)

                    Text("Score: ${assessmentScore.toInt()}/100", style = MaterialTheme.typography.titleMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold))
                    Slider(
                        value = assessmentScore,
                        onValueChange = { assessmentScore = it },
                        valueRange = 50f..100f,
                        steps = 50,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = Emerald500
                        )
                    )

                    OutlinedTextField(
                        value = assessmentFeedback,
                        onValueChange = { assessmentFeedback = it },
                        label = { Text("Tajwid Feedback & Observations") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        liveKitService.submitAssessment(
                            studentId = "p_1",
                            studentName = assessmentStudentName,
                            surah = quranSyncState.surah,
                            ayah = quranSyncState.ayah,
                            score = assessmentScore.toInt(),
                            feedback = assessmentFeedback
                        )
                        showAssessmentDialog = false
                        onShowSnackbar("Assessment published to classroom!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                ) {
                    Text("Publish Score")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssessmentDialog = false }) {
                    Text("Cancel")
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
                                liveKitService.sendChatMessage(
                                    text = chatInputText,
                                    senderName = if (currentRole == ClassroomRole.TEACHER) "Ustaz Ahmad" else "Ahmed Al-Farsi"
                                )
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

                                    if (currentRole == ClassroomRole.TEACHER && !p.isTeacher) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = {
                                                liveKitService.muteParticipant(p.id)
                                                onShowSnackbar("Muted ${p.name}")
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeMute,
                                                contentDescription = "Mute Student",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
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
private fun ParticipantTile(
    participant: Participant,
    isSelectedReciter: Boolean = false,
    onSelectAsReciter: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .size(width = 110.dp, height = 90.dp)
            .clickable { onSelectAsReciter() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelectedReciter) Emerald900 else DarkSurface,
        border = BorderStroke(
            1.5.dp,
            if (isSelectedReciter) GoldPrimary else if (participant.isHandRaised) GoldPrimary.copy(alpha = 0.6f) else Emerald800
        )
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
                        .background(if (isSelectedReciter) GoldPrimary else Emerald800),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = participant.name.take(1),
                        color = if (isSelectedReciter) Emerald950 else Color.White,
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
                } else if (isSelectedReciter) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Active Reciter",
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
