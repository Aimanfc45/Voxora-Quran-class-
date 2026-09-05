package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.realtime.ActiveClassInfo
import com.example.data.realtime.ClassroomRole
import com.example.data.realtime.LiveClassRegistry
import com.example.ui.theme.*

enum class LobbyTab {
    JOIN_CLASS,
    CREATE_CLASS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveClassLobbyView(
    defaultStudentName: String,
    defaultTeacherName: String,
    isConnecting: Boolean,
    onJoinClass: (code: String, participantName: String, micEnabled: Boolean, videoEnabled: Boolean) -> Unit,
    onCreateClass: (className: String, topic: String, classType: String, hostName: String, micEnabled: Boolean, videoEnabled: Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(LobbyTab.JOIN_CLASS) }

    // Student Join State
    var classCodeInput by remember { mutableStateOf("") }
    var studentNameInput by remember { mutableStateOf(defaultStudentName) }
    var studentMicEnabled by remember { mutableStateOf(true) }
    var studentVideoEnabled by remember { mutableStateOf(false) }

    // Teacher Create State
    var classNameInput by remember { mutableStateOf("Quran Tajwid & Recitation Masterclass") }
    var topicInput by remember { mutableStateOf("Surah Al-Mulk & Rules of Noon Sakinah") }
    var classTypeSelection by remember { mutableStateOf("GROUP") }
    var teacherNameInput by remember { mutableStateOf(defaultTeacherName.ifBlank { "Ustaz / Teacher" }) }
    var teacherMicEnabled by remember { mutableStateOf(true) }
    var teacherVideoEnabled by remember { mutableStateOf(true) }

    // Active classes available in local registry
    val activeClasses = remember { LiveClassRegistry.getAllActiveClasses() }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_class_lobby"),
        containerColor = Emerald950,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Emerald950,
                border = BorderStroke(0.5.dp, Emerald800)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "Live Quran Classroom",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Real-time LiveKit Voice & Video",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald300
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Emerald300
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Emerald800.copy(alpha = 0.7f), Emerald950)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = GoldPrimary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.LiveTv,
                                            contentDescription = null,
                                            tint = Emerald950,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Interactive Quran Halaqah",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight
                                    )
                                    Text(
                                        text = "Two-way HD audio, video & synchronized Tajwid",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Emerald200
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Role / Action Tabs
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Emerald900,
                    border = BorderStroke(1.dp, Emerald800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Surface(
                            onClick = { selectedTab = LobbyTab.JOIN_CLASS },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == LobbyTab.JOIN_CLASS) Emerald700 else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = if (selectedTab == LobbyTab.JOIN_CLASS) GoldLight else Emerald300,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Join as Student",
                                    color = if (selectedTab == LobbyTab.JOIN_CLASS) Color.White else Emerald300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Surface(
                            onClick = { selectedTab = LobbyTab.CREATE_CLASS },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == LobbyTab.CREATE_CLASS) GoldDark else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = null,
                                    tint = if (selectedTab == LobbyTab.CREATE_CLASS) GoldLight else Emerald300,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Host as Teacher",
                                    color = if (selectedTab == LobbyTab.CREATE_CLASS) GoldLight else Emerald300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                LobbyTab.JOIN_CLASS -> {
                    // STUDENT JOIN FORM
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Emerald900),
                            border = BorderStroke(1.dp, Emerald700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Enter Classroom Code",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Ask your teacher for the 5-character class code (e.g. VX-7K29P).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Emerald200
                                )

                                OutlinedTextField(
                                    value = classCodeInput,
                                    onValueChange = { classCodeInput = it.uppercase().trim() },
                                    label = { Text("Class Code", color = Emerald300) },
                                    placeholder = { Text("e.g. VX-7K29P", color = Emerald400) },
                                    leadingIcon = {
                                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = GoldPrimary)
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                val clip = clipboardManager.getText()?.text
                                                if (!clip.isNullOrBlank()) {
                                                    classCodeInput = clip.trim().uppercase()
                                                    onShowSnackbar("Pasted code from clipboard")
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = GoldLight)
                                        }
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Emerald700,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Emerald950,
                                        unfocusedContainerColor = Emerald950
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = studentNameInput,
                                    onValueChange = { studentNameInput = it },
                                    label = { Text("Your Display Name", color = Emerald300) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Emerald700,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Emerald950,
                                        unfocusedContainerColor = Emerald950
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Pre-join Media Preferences
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        onClick = { studentMicEnabled = !studentMicEnabled },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (studentMicEnabled) Emerald800 else Color(0xFF7F1D1D),
                                        border = BorderStroke(1.dp, if (studentMicEnabled) Emerald600 else Color(0xFFEF4444)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = if (studentMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (studentMicEnabled) "Mic On" else "Muted",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = { studentVideoEnabled = !studentVideoEnabled },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (studentVideoEnabled) Emerald800 else Color(0xFF374151),
                                        border = BorderStroke(1.dp, if (studentVideoEnabled) Emerald600 else Color(0xFF6B7280)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = if (studentVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (studentVideoEnabled) "Video On" else "Video Off",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        val code = classCodeInput.trim()
                                        if (code.isBlank()) {
                                            onShowSnackbar("Please enter a class code")
                                            return@Button
                                        }
                                        val name = studentNameInput.trim().ifBlank { "Student" }
                                        onJoinClass(code, name, studentMicEnabled, studentVideoEnabled)
                                    },
                                    enabled = !isConnecting,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = Emerald950
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("join_class_submit_button")
                                ) {
                                    if (isConnecting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Emerald950,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Connecting to Classroom...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Enter Live Classroom", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Active Ongoing Classes Section
                    if (activeClasses.isNotEmpty()) {
                        item {
                            Text(
                                text = "ACTIVE ONGOING SESSIONS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = GoldLight
                            )
                        }

                        activeClasses.forEach { activeClass ->
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Emerald900),
                                    border = BorderStroke(1.dp, Emerald800),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFFDC2626)
                                                ) {
                                                    Text(
                                                        text = "LIVE",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Text(
                                                    text = activeClass.className,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${activeClass.topic} • ${activeClass.hostName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Emerald300,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Emerald800
                                            ) {
                                                Text(
                                                    text = "Code: ${activeClass.code}",
                                                    color = GoldLight,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                val name = studentNameInput.trim().ifBlank { "Student" }
                                                onJoinClass(activeClass.code, name, studentMicEnabled, studentVideoEnabled)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Emerald700,
                                                contentColor = Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text("Quick Join", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                LobbyTab.CREATE_CLASS -> {
                    // TEACHER CREATE FORM
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Emerald900),
                            border = BorderStroke(1.dp, GoldDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = GoldPrimary)
                                    Text(
                                        text = "Teacher Classroom Setup",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight
                                    )
                                }

                                Text(
                                    text = "Start a real LiveKit class session. A unique class code will be created immediately for you to invite students.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Emerald200
                                )

                                OutlinedTextField(
                                    value = classNameInput,
                                    onValueChange = { classNameInput = it },
                                    label = { Text("Classroom Title", color = Emerald300) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Emerald700,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Emerald950,
                                        unfocusedContainerColor = Emerald950
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = topicInput,
                                    onValueChange = { topicInput = it },
                                    label = { Text("Topic or Surah", color = Emerald300) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Emerald700,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Emerald950,
                                        unfocusedContainerColor = Emerald950
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = teacherNameInput,
                                    onValueChange = { teacherNameInput = it },
                                    label = { Text("Teacher Name", color = Emerald300) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Emerald700,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Emerald950,
                                        unfocusedContainerColor = Emerald950
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Class Type Selector
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        onClick = { classTypeSelection = "GROUP" },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (classTypeSelection == "GROUP") GoldDark else Emerald800,
                                        border = BorderStroke(1.dp, if (classTypeSelection == "GROUP") GoldLight else Emerald700),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Groups,
                                                contentDescription = null,
                                                tint = if (classTypeSelection == "GROUP") GoldLight else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Group Class",
                                                color = if (classTypeSelection == "GROUP") GoldLight else Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = { classTypeSelection = "ONE_ON_ONE" },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (classTypeSelection == "ONE_ON_ONE") GoldDark else Emerald800,
                                        border = BorderStroke(1.dp, if (classTypeSelection == "ONE_ON_ONE") GoldLight else Emerald700),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (classTypeSelection == "ONE_ON_ONE") GoldLight else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "1-on-1 Mentorship",
                                                color = if (classTypeSelection == "ONE_ON_ONE") GoldLight else Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // Pre-join Media Preferences for Teacher
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        onClick = { teacherMicEnabled = !teacherMicEnabled },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (teacherMicEnabled) Emerald800 else Color(0xFF7F1D1D),
                                        border = BorderStroke(1.dp, if (teacherMicEnabled) Emerald600 else Color(0xFFEF4444)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = if (teacherMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (teacherMicEnabled) "Mic Enabled" else "Muted",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = { teacherVideoEnabled = !teacherVideoEnabled },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (teacherVideoEnabled) Emerald800 else Color(0xFF374151),
                                        border = BorderStroke(1.dp, if (teacherVideoEnabled) Emerald600 else Color(0xFF6B7280)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = if (teacherVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (teacherVideoEnabled) "Camera On" else "Camera Off",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        val title = classNameInput.trim().ifBlank { "Quran Tajwid Session" }
                                        val topic = topicInput.trim().ifBlank { "Quranic Recitation" }
                                        val host = teacherNameInput.trim().ifBlank { "Teacher" }
                                        onCreateClass(title, topic, classTypeSelection, host, teacherMicEnabled, teacherVideoEnabled)
                                    },
                                    enabled = !isConnecting,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = Emerald950
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("create_class_submit_button")
                                ) {
                                    if (isConnecting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Emerald950,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Initializing LiveKit Room...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.VideoCall, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Start Live Class as Teacher", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
