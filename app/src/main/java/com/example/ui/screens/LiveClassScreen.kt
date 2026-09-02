package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.realtime.QuranSyncPacket
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

    // Real-time LiveKit Service Instance
    val liveKitService = remember { LiveKitClassService(context, coroutineScope) }

    // Service state flows
    val connectionQuality by liveKitService.connectionQuality.collectAsState()
    val isConnectedToRealRoom by liveKitService.isConnectedToRealRoom.collectAsState()
    val isConnecting by liveKitService.isConnecting.collectAsState()
    val connectionError by liveKitService.connectionError.collectAsState()

    val isMicMuted by liveKitService.isMicMuted.collectAsState()
    val isVideoOn by liveKitService.isVideoOn.collectAsState()
    val isSpeakerOn by liveKitService.isSpeakerOn.collectAsState()
    val isHandRaised by liveKitService.isHandRaised.collectAsState()
    val isFrontCamera by liveKitService.isFrontCamera.collectAsState()

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

    val userProfile by repository.userProfile.collectAsState()
    val liveClass by repository.liveClass.collectAsState()

    // UI Dialog & Sheet states
    var showChatSheet by remember { mutableStateOf(false) }
    var showSharedQuranSheet by remember { mutableStateOf(false) }
    var showTeacherControlsSheet by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showAssessmentDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var permissionRationaleMessage by remember { mutableStateOf("") }

    // Assessment state inputs
    var assessmentStudentName by remember { mutableStateOf("") }
    var assessmentScore by remember { mutableFloatStateOf(90f) }
    var assessmentFeedback by remember { mutableStateOf("Great articulation of Makharij and smooth elongation.") }

    // Config dialog state inputs
    var configServerUrl by remember { mutableStateOf(currentConfig.serverUrl) }
    var configDevTokenServerId by remember { mutableStateOf(currentConfig.devTokenServerId) }
    var configTokenEndpoint by remember { mutableStateOf(currentConfig.tokenEndpoint) }
    var configDevToken by remember { mutableStateOf(currentConfig.devToken) }

    // Permissions launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true

        if (recordAudioGranted) {
            liveKitService.toggleMic()
        }
        if (cameraGranted) {
            liveKitService.toggleVideo()
        }
    }

    // Connect to room on entry, disconnect on exit
    LaunchedEffect(Unit) {
        liveKitService.joinClass(
            classId = liveClass.id,
            participantName = userProfile.name.ifBlank { "Student" },
            role = ClassroomRole.STUDENT
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            liveKitService.leaveClass()
        }
    }

    // Main Live Class UI Canvas
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_class_screen"),
        containerColor = Emerald950,
        topBar = {
            LiveClassHeader(
                title = liveClass.title,
                subject = liveClass.subject,
                isConnecting = isConnecting,
                isConnected = isConnectedToRealRoom,
                connectionQuality = connectionQuality,
                connectionError = connectionError,
                currentRole = currentRole,
                onRoleChange = { liveKitService.setMyRole(it) },
                onOpenSettings = { showConfigDialog = true },
                onReconnect = { liveKitService.reconnect() },
                onLeaveClick = { showLeaveConfirmation = true }
            )
        },
        bottomBar = {
            LiveClassBottomBar(
                isMicMuted = isMicMuted,
                isVideoOn = isVideoOn,
                isSpeakerOn = isSpeakerOn,
                isHandRaised = isHandRaised,
                isFrontCamera = isFrontCamera,
                currentRole = currentRole,
                chatUnreadCount = chatMessages.size,
                onToggleMic = {
                    val hasAudioPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasAudioPermission) {
                        permissionRationaleMessage = "Microphone access is required to recite and speak in live class."
                        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                    } else {
                        liveKitService.toggleMic()
                    }
                },
                onToggleVideo = {
                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasCameraPermission) {
                        permissionRationaleMessage = "Camera access is required to show your video during live recitation."
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                    } else {
                        liveKitService.toggleVideo()
                    }
                },
                onFlipCamera = { liveKitService.flipCamera() },
                onToggleSpeaker = {
                    liveKitService.toggleSpeaker()
                    onShowSnackbar(if (!isSpeakerOn) "Speakerphone Enabled" else "Earpiece Audio Mode")
                },
                onToggleRaiseHand = {
                    val raised = liveKitService.toggleRaiseHand()
                    onShowSnackbar(if (raised) "Hand raised! Ustaz notified." else "Hand lowered.")
                },
                onOpenSharedQuran = { showSharedQuranSheet = true },
                onOpenChat = { showChatSheet = true },
                onOpenTeacherControls = { showTeacherControlsSheet = true },
                onLeaveClick = { showLeaveConfirmation = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hand Raise Alert banner for Teacher
            if (currentRole == ClassroomRole.TEACHER && handRaiseAlert != null) {
                item {
                    val alert = handRaiseAlert!!
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldDark.copy(alpha = 0.25f)),
                        border = BorderStroke(1.dp, GoldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("✋", fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = "${alert.participantName} raised hand",
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Requesting recitation turn",
                                        color = Emerald200,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { liveKitService.dismissHandRaise(alert) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Dismiss", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { liveKitService.acceptHandRaise(alert) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Emerald950),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Allow Turn", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Connection notification / banner if reconnecting or error
            if (connectionQuality == ConnectionQualityLevel.RECONNECTING || connectionError != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (connectionError != null) Color(0xFF7F1D1D) else Color(0xFF78350F)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (connectionError != null) Icons.Default.ErrorOutline else Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = if (connectionError != null) Color(0xFFFCA5A5) else GoldLight
                                )
                                Text(
                                    text = connectionError ?: "Reconnecting to LiveKit Room...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            TextButton(
                                onClick = { liveKitService.reconnect() },
                                colors = ButtonDefaults.textButtonColors(contentColor = GoldLight)
                            ) {
                                Text("Retry", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Recitation Assessment banner if newly issued
            if (latestAssessment != null) {
                item {
                    val assessment = latestAssessment!!
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Emerald800.copy(alpha = 0.7f)),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Ustaz's Assessment for ${assessment.studentName}",
                                        color = GoldLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"${assessment.tajwidFeedback}\"",
                                    color = Emerald50,
                                    fontSize = 12.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldPrimary,
                                contentColor = Emerald950,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "${assessment.score}%",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Active Reciter Banner
            if (!selectedReciter.isNullOrBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Emerald900,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = GoldPrimary,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = Emerald950,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Current Reciter",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldLight
                                    )
                                    Text(
                                        text = selectedReciter ?: "Student",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            if (currentRole == ClassroomRole.TEACHER) {
                                Button(
                                    onClick = {
                                        assessmentStudentName = selectedReciter ?: "Student"
                                        showAssessmentDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Emerald950),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Grade Recitation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Teacher View (Hero Tile)
            item {
                TeacherHeroView(
                    teacher = liveClass.teacher,
                    teacherVideoTrack = teacherVideoTrack,
                    room = liveKitService.room,
                    activeSpeaker = activeSpeaker,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Synchronized Quran Card Section
            item {
                SynchronizedQuranHeroCard(
                    packet = quranSyncState,
                    isTeacher = currentRole == ClassroomRole.TEACHER,
                    onNextVerse = { liveKitService.nextVerse() },
                    onPrevVerse = { liveKitService.previousVerse() },
                    onOpenFullscreen = { showSharedQuranSheet = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Student Participants Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDENT PARTICIPANTS (${participants.count { !it.isTeacher }})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = GoldLight
                    )

                    if (isConnectedToRealRoom) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Emerald800.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "Real LiveKit Room",
                                color = Emerald200,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            val studentParticipants = participants.filter { !it.isTeacher }

            if (studentParticipants.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, Emerald800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PeopleOutline,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No participants connected",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Classroom is active. When classmates join the LiveKit room, they will appear here in real time.",
                                color = Emerald200,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(studentParticipants) { participant ->
                            ParticipantTile(
                                participant = participant,
                                activeSpeaker = activeSpeaker,
                                isTeacher = currentRole == ClassroomRole.TEACHER,
                                onSelectReciter = {
                                    liveKitService.selectStudentReciter(participant.id, participant.name)
                                    onShowSnackbar("${participant.name} selected as active reciter")
                                },
                                onMuteParticipant = {
                                    liveKitService.muteParticipant(participant.id)
                                    onShowSnackbar("Muted ${participant.name}")
                                },
                                onKickParticipant = {
                                    liveKitService.removeParticipant(participant.id)
                                    onShowSnackbar("Removed ${participant.name} from room")
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ====================================================
    // DIALOGS & BOTTOM SHEETS
    // ====================================================

    // Live Chat Sheet
    if (showChatSheet) {
        ClassroomChatBottomSheet(
            messages = chatMessages,
            myRole = currentRole,
            onSendMessage = { text ->
                val myName = userProfile.name.ifBlank { "Student" }
                liveKitService.sendChatMessage(text, myName)
            },
            onDismiss = { showChatSheet = false }
        )
    }

    // Shared Quran Sheet (Full viewer & synchronizer)
    if (showSharedQuranSheet) {
        SharedQuranSheet(
            packet = quranSyncState,
            isTeacher = currentRole == ClassroomRole.TEACHER,
            onNext = { liveKitService.nextVerse() },
            onPrev = { liveKitService.previousVerse() },
            onSetVerse = { surah, ayah, rule, note ->
                liveKitService.setSynchronizedQuranVerse(surah, ayah, rule, note)
            },
            onDismiss = { showSharedQuranSheet = false }
        )
    }

    // Teacher Controls Sheet
    if (showTeacherControlsSheet) {
        TeacherControlsBottomSheet(
            participants = participants,
            classMode = classMode,
            onModeChange = { liveKitService.setClassMode(it) },
            onMuteAll = {
                participants.forEach {
                    if (!it.isTeacher) liveKitService.muteParticipant(it.id)
                }
                onShowSnackbar("Muted all participants")
            },
            onSelectReciter = { id, name ->
                liveKitService.selectStudentReciter(id, name)
                showTeacherControlsSheet = false
                onShowSnackbar("$name is now reciting")
            },
            onMuteParticipant = { liveKitService.muteParticipant(it) },
            onKickParticipant = { liveKitService.removeParticipant(it) },
            onDismiss = { showTeacherControlsSheet = false }
        )
    }

    // Leave Confirmation Dialog
    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            title = {
                Text(
                    text = "Leave Live Class?",
                    fontWeight = FontWeight.Bold,
                    color = Emerald950
                )
            },
            text = {
                Text("Are you sure you want to exit the live session? You can rejoin anytime while the class remains active.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirmation = false
                        liveKitService.leaveClass()
                        onLeaveClass()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Leave Class", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmation = false }) {
                    Text("Stay in Class", color = Emerald700)
                }
            }
        )
    }

    // LiveKit Configuration Dialog
    if (showConfigDialog) {
        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Emerald700)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LiveKit Real-Time Setup", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Configure LiveKit WebRTC Cloud or Sandbox endpoints for real-time video, audio, and Quran synchronization.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = configServerUrl,
                        onValueChange = { configServerUrl = it },
                        label = { Text("Server URL (wss://...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = configDevTokenServerId,
                        onValueChange = { configDevTokenServerId = it },
                        label = { Text("Development Token Server ID") },
                        placeholder = { Text("voxoraquranclass-1pdkmx") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = configTokenEndpoint,
                        onValueChange = { configTokenEndpoint = it },
                        label = { Text("Backend Token Endpoint (Production)") },
                        placeholder = { Text("https://api.voxora.app/livekit/token") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = configDevToken,
                        onValueChange = { configDevToken = it },
                        label = { Text("Literal JWT Token (Optional override)") },
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
                            devTokenServerId = configDevTokenServerId,
                            tokenEndpoint = configTokenEndpoint,
                            devToken = configDevToken
                        )
                        showConfigDialog = false
                        liveKitService.reconnect()
                        onShowSnackbar("LiveKit configuration updated. Reconnecting...")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Apply & Reconnect")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tajwid & Recitation Assessment", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Assessing: $assessmentStudentName",
                        fontWeight = FontWeight.Bold,
                        color = Emerald800
                    )

                    Text(
                        text = "Score: ${assessmentScore.toInt()}%",
                        fontWeight = FontWeight.SemiBold,
                        color = Emerald700
                    )

                    Slider(
                        value = assessmentScore,
                        onValueChange = { assessmentScore = it },
                        valueRange = 50f..100f,
                        steps = 10,
                        colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                    )

                    OutlinedTextField(
                        value = assessmentFeedback,
                        onValueChange = { assessmentFeedback = it },
                        label = { Text("Teacher Feedback & Tajwid Notes") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        liveKitService.submitAssessment(
                            studentId = "student_${System.currentTimeMillis()}",
                            studentName = assessmentStudentName,
                            surah = quranSyncState.surah,
                            ayah = quranSyncState.ayah,
                            score = assessmentScore.toInt(),
                            feedback = assessmentFeedback
                        )
                        showAssessmentDialog = false
                        onShowSnackbar("Assessment submitted and broadcasted to class!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Broadcast Score")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssessmentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// =========================================================================
// HEADER COMPONENT
// =========================================================================

@Composable
fun LiveClassHeader(
    title: String,
    subject: String,
    isConnecting: Boolean,
    isConnected: Boolean,
    connectionQuality: ConnectionQualityLevel,
    connectionError: String?,
    currentRole: ClassroomRole,
    onRoleChange: (ClassroomRole) -> Unit,
    onOpenSettings: () -> Unit,
    onReconnect: () -> Unit,
    onLeaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Emerald950,
        border = BorderStroke(0.5.dp, Emerald800)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onLeaveClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("leave_class_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Leave",
                            tint = Color.White
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subject,
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald300,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Real Connection Status Badge
                    ConnectionStatusBadge(
                        isConnecting = isConnecting,
                        isConnected = isConnected,
                        quality = connectionQuality,
                        hasError = connectionError != null,
                        onReconnect = onReconnect
                    )

                    // LiveKit Cloud Settings
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Emerald300,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Role Badge (Student / Teacher toggle)
                    Surface(
                        onClick = {
                            val nextRole = if (currentRole == ClassroomRole.STUDENT) ClassroomRole.TEACHER else ClassroomRole.STUDENT
                            onRoleChange(nextRole)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentRole == ClassroomRole.TEACHER) GoldDark else Emerald800,
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (currentRole == ClassroomRole.TEACHER) "Ustaz Mode" else "Student",
                                color = if (currentRole == ClassroomRole.TEACHER) GoldLight else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionStatusBadge(
    isConnecting: Boolean,
    isConnected: Boolean,
    quality: ConnectionQualityLevel,
    hasError: Boolean,
    onReconnect: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val (badgeColor, dotColor, labelText) = when {
        isConnecting -> Triple(Color(0xFF78350F), GoldPrimary, "Connecting...")
        hasError -> Triple(Color(0xFF7F1D1D), Color(0xFFEF4444), "Connection Failed")
        quality == ConnectionQualityLevel.RECONNECTING -> Triple(Color(0xFF78350F), GoldPrimary, "Reconnecting...")
        isConnected -> Triple(Emerald800, Color(0xFF10B981), "Connected")
        else -> Triple(Color(0xFF374151), Color(0xFF9CA3AF), "Disconnected")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = badgeColor,
        modifier = Modifier
            .clickable(enabled = !isConnected && !isConnecting) { onReconnect() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isConnecting || isConnected) dotColor.copy(alpha = alpha) else dotColor)
            )
            Text(
                text = labelText,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// =========================================================================
// TEACHER HERO VIEW
// =========================================================================

@Composable
fun TeacherHeroView(
    teacher: com.example.data.model.Teacher,
    teacherVideoTrack: io.livekit.android.room.track.VideoTrack?,
    room: io.livekit.android.room.Room?,
    activeSpeaker: String?,
    modifier: Modifier = Modifier
) {
    val isSpeaking = activeSpeaker?.contains("teacher", ignoreCase = true) == true ||
            activeSpeaker?.contains(teacher.name, ignoreCase = true) == true

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald900),
        border = BorderStroke(
            if (isSpeaking) 2.dp else 1.dp,
            if (isSpeaking) GoldPrimary else Emerald700
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (teacherVideoTrack != null && room != null) {
                LiveKitVideoRenderer(
                    room = room,
                    videoTrack = teacherVideoTrack,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Elegant Islamic Canvas for Ustaz
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Emerald800, Emerald950)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Emerald700,
                            border = BorderStroke(2.dp, GoldPrimary),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (teacher.imageDrawableRes != null) {
                                    Image(
                                        painter = painterResource(id = teacher.imageDrawableRes),
                                        contentDescription = teacher.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = "📖",
                                        fontSize = 32.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = teacher.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Text(
                            text = teacher.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald300
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            teacher.credentials.take(2).forEach { cred ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Emerald800
                                ) {
                                    Text(
                                        text = cred,
                                        color = GoldLight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Top Badges (Host & Speaking Indicator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Emerald950.copy(alpha = 0.85f),
                    border = BorderStroke(0.5.dp, GoldPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                        Text(
                            text = "Teacher / Host",
                            color = GoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isSpeaking) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldDark,
                        border = BorderStroke(0.5.dp, GoldLight)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = GoldLight, modifier = Modifier.size(12.dp))
                            Text(
                                text = "Speaking",
                                color = GoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bottom Name Tag
            Surface(
                shape = RoundedCornerShape(topEnd = 10.dp),
                color = Emerald950.copy(alpha = 0.85f),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = "${teacher.name} (${teacher.country})",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// =========================================================================
// SYNCHRONIZED QURAN HERO CARD
// =========================================================================

@Composable
fun SynchronizedQuranHeroCard(
    packet: QuranSyncPacket,
    isTeacher: Boolean,
    onNextVerse: () -> Unit,
    onPrevVerse: () -> Unit,
    onOpenFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald900),
        border = BorderStroke(1.dp, Emerald700),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Surah Al-Baqarah — Verse ${packet.ayah}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!packet.tajwidRule.isNullOrBlank()) {
                        TajwidRuleBadge(ruleText = packet.tajwidRule)
                    }

                    IconButton(
                        onClick = onOpenFullscreen,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expand Quran",
                            tint = Emerald300,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Arabic Verse with tajwid highlighting
            val sampleArabic = when (packet.ayah) {
                1 -> "الٓمٓ"
                2 -> "ذَٰلِكَ ٱلْكِتَـٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًۭى لِّلْمُتَّقِينَ"
                3 -> "ٱلَّذِينَ يُؤْمِنُونَ بِٱلْغَيْبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ وَمِمَّا رَزَقْنَـٰهُمْ يُنفِقُونَ"
                4 -> "وَٱلَّذِينَ يُؤْمِنُونَ بِمَآ أُنزِلَ إِلَيْكَ وَمَآ أُنزِلَ مِن قَبْلِكَ وَبِٱلْـَٔاخِرَةِ هُمْ يُوقِنُونَ"
                5 -> "أُو۟لَـٰٓئِكَ عَلَىٰ هُدًۭى مِّن رَّبِّهِمْ ۖ وَأُو۟لَـٰٓئِكَ هُمُ ٱلْمُفْلِحُونَ"
                else -> "ذَٰلِكَ ٱلْكِتَـٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًۭى لِّلْمُتَّقِينَ"
            }

            val sampleTranslation = when (packet.ayah) {
                1 -> "Alif, Lam, Meem."
                2 -> "This is the Book about which there is no doubt, a guidance for those conscious of Allah."
                3 -> "Who believe in the unseen, establish prayer, and spend out of what We have provided for them."
                4 -> "And who believe in what has been revealed to you, [O Muhammad], and what was revealed before you, and of the Hereafter they are certain."
                5 -> "Those are upon [right] guidance from their Lord, and it is those who are the successful."
                else -> "This is the Book about which there is no doubt, a guidance for those conscious of Allah."
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Emerald950,
                border = BorderStroke(0.5.dp, GoldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = sampleArabic,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        textAlign = TextAlign.End,
                        lineHeight = 36.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = sampleTranslation,
                        fontSize = 12.sp,
                        color = Emerald100,
                        lineHeight = 18.sp
                    )
                }
            }

            // Ustaz Instruction Note
            if (!packet.note.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                    Text(
                        text = packet.note,
                        color = GoldLight,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Sync controls for verse traversal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrevVerse,
                    enabled = packet.ayah > 1,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Prev Ayah", fontSize = 11.sp)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Emerald800
                ) {
                    Text(
                        text = "LIVE SYNCED",
                        color = Emerald200,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Button(
                    onClick = onNextVerse,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Next Ayah", fontSize = 11.sp)
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// =========================================================================
// PARTICIPANT TILE COMPONENT
// =========================================================================

@Composable
fun ParticipantTile(
    participant: Participant,
    activeSpeaker: String?,
    isTeacher: Boolean,
    onSelectReciter: () -> Unit,
    onMuteParticipant: () -> Unit,
    onKickParticipant: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSpeaking = participant.isSpeaking || activeSpeaker == participant.name || activeSpeaker == participant.id
    var showActionMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Emerald900,
        border = BorderStroke(
            if (isSpeaking) 2.dp else 1.dp,
            if (isSpeaking) GoldPrimary else Emerald700
        ),
        modifier = modifier
            .width(130.dp)
            .height(130.dp)
            .clickable {
                if (isTeacher) showActionMenu = true
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (participant.isHandRaised) GoldPrimary else Emerald800,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = participant.name.take(2).uppercase(),
                            color = if (participant.isHandRaised) Emerald950 else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = participant.name,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (isSpeaking) "Speaking" else if (participant.isMicMuted) "Muted" else "Listening",
                    color = if (isSpeaking) GoldLight else Emerald300,
                    fontSize = 10.sp
                )
            }

            // Top Status Icons (Mic & Hand Raise)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = if (participant.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = null,
                    tint = if (participant.isMicMuted) Color(0xFFEF4444) else Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )

                if (participant.isHandRaised) {
                    Text("✋", fontSize = 12.sp)
                }
            }

            // Teacher Action Dropdown
            DropdownMenu(
                expanded = showActionMenu,
                onDismissRequest = { showActionMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Select for Recitation") },
                    onClick = {
                        showActionMenu = false
                        onSelectReciter()
                    },
                    leadingIcon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Mute Participant") },
                    onClick = {
                        showActionMenu = false
                        onMuteParticipant()
                    },
                    leadingIcon = { Icon(Icons.Default.MicOff, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Remove from Class", color = Color.Red) },
                    onClick = {
                        showActionMenu = false
                        onKickParticipant()
                    },
                    leadingIcon = { Icon(Icons.Default.PersonRemove, contentDescription = null, tint = Color.Red) }
                )
            }
        }
    }
}

// =========================================================================
// BOTTOM CONTROL BAR
// =========================================================================

@Composable
fun LiveClassBottomBar(
    isMicMuted: Boolean,
    isVideoOn: Boolean,
    isSpeakerOn: Boolean,
    isHandRaised: Boolean,
    isFrontCamera: Boolean,
    currentRole: ClassroomRole,
    chatUnreadCount: Int,
    onToggleMic: () -> Unit,
    onToggleVideo: () -> Unit,
    onFlipCamera: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleRaiseHand: () -> Unit,
    onOpenSharedQuran: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenTeacherControls: () -> Unit,
    onLeaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Emerald950,
        border = BorderStroke(0.5.dp, Emerald800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Microphone Toggle
            ControlBarButton(
                icon = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                label = if (isMicMuted) "Unmute" else "Mute",
                isActive = !isMicMuted,
                activeColor = Emerald700,
                inactiveColor = Color(0xFFDC2626),
                onClick = onToggleMic,
                testTag = "control_mic_button"
            )

            // Camera Toggle
            ControlBarButton(
                icon = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                label = if (isVideoOn) "Video On" else "Video Off",
                isActive = isVideoOn,
                activeColor = Emerald700,
                inactiveColor = Color(0xFF4B5563),
                onClick = onToggleVideo,
                testTag = "control_video_button"
            )

            // Flip Camera
            if (isVideoOn) {
                ControlBarButton(
                    icon = Icons.Default.FlipCameraAndroid,
                    label = "Flip",
                    isActive = false,
                    activeColor = Emerald700,
                    inactiveColor = Emerald800,
                    onClick = onFlipCamera,
                    testTag = "control_flip_camera_button"
                )
            }

            // Raise Hand
            ControlBarButton(
                icon = if (isHandRaised) Icons.Default.PanTool else Icons.Outlined.PanTool,
                label = if (isHandRaised) "Raised" else "Raise Hand",
                isActive = isHandRaised,
                activeColor = GoldPrimary,
                inactiveColor = Emerald800,
                iconTint = if (isHandRaised) Emerald950 else Color.White,
                onClick = onToggleRaiseHand,
                testTag = "control_raise_hand_button"
            )

            // Shared Quran
            ControlBarButton(
                icon = Icons.Default.MenuBook,
                label = "Quran",
                isActive = false,
                activeColor = Emerald700,
                inactiveColor = Emerald800,
                onClick = onOpenSharedQuran,
                testTag = "control_shared_quran_button"
            )

            // Chat
            Box {
                ControlBarButton(
                    icon = Icons.Default.ChatBubbleOutline,
                    label = "Chat",
                    isActive = false,
                    activeColor = Emerald700,
                    inactiveColor = Emerald800,
                    onClick = onOpenChat,
                    testTag = "control_chat_button"
                )
                if (chatUnreadCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$chatUnreadCount",
                                color = Emerald950,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Teacher Controls (if teacher)
            if (currentRole == ClassroomRole.TEACHER) {
                ControlBarButton(
                    icon = Icons.Default.AdminPanelSettings,
                    label = "Controls",
                    isActive = true,
                    activeColor = GoldDark,
                    inactiveColor = Emerald800,
                    onClick = onOpenTeacherControls,
                    testTag = "control_teacher_panel_button"
                )
            }

            // Leave Button
            ControlBarButton(
                icon = Icons.Default.CallEnd,
                label = "Leave",
                isActive = true,
                activeColor = Color(0xFFDC2626),
                inactiveColor = Color(0xFFDC2626),
                onClick = onLeaveClick,
                testTag = "control_leave_button"
            )
        }
    }
}

@Composable
fun ControlBarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.White,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = if (isActive) activeColor else inactiveColor,
            modifier = Modifier
                .size(42.dp)
                .testTag(testTag)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = label,
            color = Emerald200,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// =========================================================================
// REAL-TIME CHAT BOTTOM SHEET
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomChatBottomSheet(
    messages: List<ClassChatMessage>,
    myRole: ClassroomRole,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Emerald950,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Emerald700) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = GoldPrimary)
                    Text(
                        text = "Classroom Live Chat",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Emerald800
                ) {
                    Text(
                        text = "${messages.size} Messages",
                        color = Emerald200,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No messages yet.\nType below to send a question to the class!",
                        color = Emerald300,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubble(message = msg)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chat Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask Ustaz a question or comment...", color = Emerald400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Emerald700,
                        focusedContainerColor = Emerald900,
                        unfocusedContainerColor = Emerald900
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("classroom_chat_input")
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .testTag("classroom_chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Emerald950
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ClassChatMessage) {
    val isMe = message.isMe
    val isTeacher = message.isTeacher

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = message.senderName,
                fontSize = 11.sp,
                fontWeight = if (isTeacher) FontWeight.Bold else FontWeight.Medium,
                color = if (isTeacher) GoldLight else Emerald300
            )
            if (isTeacher) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = GoldDark
                ) {
                    Text(
                        text = "Ustaz",
                        color = GoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
            Text(
                text = "• ${message.timestamp}",
                fontSize = 10.sp,
                color = Emerald400
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isMe) 12.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 12.dp
            ),
            color = when {
                isTeacher -> Emerald800
                isMe -> GoldDark
                else -> Emerald900
            },
            border = BorderStroke(0.5.dp, if (isTeacher) GoldPrimary else Emerald700)
        ) {
            Text(
                text = message.message,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

// =========================================================================
// SHARED QURAN FULL VIEWER & SYNCHRONIZER SHEET
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedQuranSheet(
    packet: QuranSyncPacket,
    isTeacher: Boolean,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSetVerse: (surah: Int, ayah: Int, rule: String, note: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSurah by remember { mutableIntStateOf(packet.surah) }
    var selectedAyah by remember { mutableIntStateOf(packet.ayah) }
    var customTajwidRule by remember { mutableStateOf(packet.tajwidRule ?: "Mad Asli") }
    var customNote by remember { mutableStateOf(packet.note ?: "Pay attention to correct elongation and makharij.") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Emerald950,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Emerald700) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shared Quran Classroom Canvas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldPrimary,
                    contentColor = Emerald950
                ) {
                    Text(
                        text = "LIVE SYNC",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Arabic Display Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Emerald900,
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Surah Al-Baqarah (Verse ${packet.ayah})",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    val arabicText = when (packet.ayah) {
                        1 -> "الٓمٓ"
                        2 -> "ذَٰلِكَ ٱلْكِتَـٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًۭى لِّلْمُتَّقِينَ"
                        3 -> "ٱلَّذِينَ يُؤْمِنُونَ بِٱلْغَيْبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ وَمِمَّا رَزَقْنَـٰهُمْ يُنفِقُونَ"
                        4 -> "وَٱلَّذِينَ يُؤْمِنُونَ بِمَآ أُنزِلَ إِلَيْكَ وَمَآ أُنزِلَ مِن قَبْلِكَ وَبِٱلْـَٔاخِرَةِ هُمْ يُوقِنُونَ"
                        5 -> "أُو۟لَـٰٓئِكَ عَلَىٰ هُدًۭى مِّن رَّبِّهِمْ ۖ وَأُو۟لَـٰٓئِكَ هُمُ ٱلْمُفْلِحُونَ"
                        else -> "ذَٰلِكَ ٱلْكِتَـٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًۭى لِّلْمُتَّقِينَ"
                    }

                    Text(
                        text = arabicText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.End,
                        lineHeight = 40.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider(color = Emerald800)

                    Text(
                        text = "Tajwid Focus: ${packet.tajwidRule ?: "Standard"}",
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = packet.note ?: "Listen to Ustaz's demonstration.",
                        color = Emerald200,
                        fontSize = 12.sp
                    )
                }
            }

            // Teacher Broadcast Controls
            if (isTeacher) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900),
                    border = BorderStroke(1.dp, Emerald700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Ustaz Classroom Broadcast Controls",
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            fontSize = 13.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customTajwidRule,
                                onValueChange = { customTajwidRule = it },
                                label = { Text("Tajwid Rule Focus") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = customNote,
                            onValueChange = { customNote = it },
                            label = { Text("Instruction Note for Students") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                onSetVerse(selectedSurah, selectedAyah, customTajwidRule, customNote)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Emerald950),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Podcasts, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Broadcast Quran State to All Students", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Traversal Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = packet.ayah > 1,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Previous Ayah")
                }

                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Next Ayah")
                }
            }
        }
    }
}

// =========================================================================
// TEACHER CONTROLS BOTTOM SHEET
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherControlsBottomSheet(
    participants: List<Participant>,
    classMode: ClassType,
    onModeChange: (ClassType) -> Unit,
    onMuteAll: () -> Unit,
    onSelectReciter: (id: String, name: String) -> Unit,
    onMuteParticipant: (String) -> Unit,
    onKickParticipant: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Emerald950,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Emerald700) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Teacher Management Panel",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            // Global Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onMuteAll,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.VolumeOff, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mute All", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                FilledTonalButton(
                    onClick = {
                        val nextMode = if (classMode == ClassType.GROUP) ClassType.ONE_ON_ONE else ClassType.GROUP
                        onModeChange(nextMode)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Emerald800, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (classMode == ClassType.GROUP) "Switch 1-on-1" else "Switch Group",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Divider(color = Emerald800)

            Text(
                text = "Class Participants (${participants.size})",
                fontWeight = FontWeight.Bold,
                color = GoldLight,
                fontSize = 13.sp
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(participants) { p ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Emerald900,
                        border = BorderStroke(0.5.dp, Emerald800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = p.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (p.isTeacher) "Ustaz" else if (p.isHandRaised) "✋ Hand Raised" else "Student",
                                    color = if (p.isHandRaised) GoldLight else Emerald300,
                                    fontSize = 11.sp
                                )
                            }

                            if (!p.isTeacher) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { onSelectReciter(p.id, p.name) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = "Recite", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { onMuteParticipant(p.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.MicOff, contentDescription = "Mute", tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { onKickParticipant(p.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PersonRemove, contentDescription = "Kick", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
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
