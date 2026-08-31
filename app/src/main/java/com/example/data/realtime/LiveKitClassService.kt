package com.example.data.realtime

import android.content.Context
import android.util.Log
import com.example.data.mock.MockClassData
import com.example.data.model.ClassChatMessage
import com.example.data.model.ClassType
import com.example.data.model.Participant
import com.example.data.model.Teacher
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.livekit.android.LiveKit
import io.livekit.android.events.RoomEvent
import io.livekit.android.room.Room
import io.livekit.android.room.participant.ConnectionQuality
import io.livekit.android.room.participant.Participant as LKParticipant
import io.livekit.android.room.participant.RemoteParticipant
import io.livekit.android.room.track.DataPublishReliability
import io.livekit.android.room.track.LocalVideoTrack
import io.livekit.android.room.track.RemoteVideoTrack
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveKitClassService(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val tag = "LiveKitClassService"
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val quranPacketAdapter = moshi.adapter(QuranSyncPacket::class.java)
    private val chatPacketAdapter = moshi.adapter(ChatPacket::class.java)
    private val handRaiseAdapter = moshi.adapter(HandRaisePacket::class.java)
    private val assessmentAdapter = moshi.adapter(RecitationAssessment::class.java)
    private val participantActionAdapter = moshi.adapter(ParticipantActionPacket::class.java)
    private val okHttpClient = OkHttpClient()

    var room: Room? = null
        private set

    // Configuration
    private val _config = MutableStateFlow(LiveKitConfig.createDefault())
    val config: StateFlow<LiveKitConfig> = _config.asStateFlow()

    // Connection quality & state
    private val _connectionQuality = MutableStateFlow(ConnectionQualityLevel.UNCONFIGURED)
    val connectionQuality: StateFlow<ConnectionQualityLevel> = _connectionQuality.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _isConnectedToRealRoom = MutableStateFlow(false)
    val isConnectedToRealRoom: StateFlow<Boolean> = _isConnectedToRealRoom.asStateFlow()

    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError.asStateFlow()

    // Local Media States
    private val _isMicMuted = MutableStateFlow(true)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isVideoOn = MutableStateFlow(true)
    val isVideoOn: StateFlow<Boolean> = _isVideoOn.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(true)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    private val _isHandRaised = MutableStateFlow(false)
    val isHandRaised: StateFlow<Boolean> = _isHandRaised.asStateFlow()

    // Video tracks for rendering
    private val _localVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val localVideoTrack: StateFlow<VideoTrack?> = _localVideoTrack.asStateFlow()

    private val _teacherVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val teacherVideoTrack: StateFlow<VideoTrack?> = _teacherVideoTrack.asStateFlow()

    // Classroom Role & Mode
    private val _myRole = MutableStateFlow(ClassroomRole.STUDENT)
    val myRole: StateFlow<ClassroomRole> = _myRole.asStateFlow()

    private val _classMode = MutableStateFlow(ClassType.GROUP)
    val classMode: StateFlow<ClassType> = _classMode.asStateFlow()

    // Room Participants
    private val _participants = MutableStateFlow<List<Participant>>(MockClassData.initialParticipants)
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    // Chat messages
    private val _chatMessages = MutableStateFlow<List<ClassChatMessage>>(MockClassData.initialChatMessages)
    val chatMessages: StateFlow<List<ClassChatMessage>> = _chatMessages.asStateFlow()

    // Synchronized Quran Sheet State
    private val _synchronizedQuranState = MutableStateFlow(
        QuranSyncPacket(
            type = "quran_state",
            surah = 2,
            ayah = 1,
            highlight = true,
            tajwidRule = "Mad Asli",
            note = "Focus on the natural 2-vowel count elongation."
        )
    )
    val synchronizedQuranState: StateFlow<QuranSyncPacket> = _synchronizedQuranState.asStateFlow()

    // Recitation Assessments
    private val _latestAssessment = MutableStateFlow<RecitationAssessment?>(null)
    val latestAssessment: StateFlow<RecitationAssessment?> = _latestAssessment.asStateFlow()

    // Active Speaker & Reciter
    private val _activeSpeaker = MutableStateFlow<String?>("Ustaz Ahmad")
    val activeSpeaker: StateFlow<String?> = _activeSpeaker.asStateFlow()

    private val _selectedStudentReciter = MutableStateFlow<String?>(null)
    val selectedStudentReciter: StateFlow<String?> = _selectedStudentReciter.asStateFlow()

    // Active hand raise alert for teacher
    private val _activeHandRaiseAlert = MutableStateFlow<HandRaisePacket?>(null)
    val activeHandRaiseAlert: StateFlow<HandRaisePacket?> = _activeHandRaiseAlert.asStateFlow()

    private var roomEventsJob: Job? = null

    init {
        // Initialize LiveKit Room instance safely
        try {
            room = LiveKit.create(context.applicationContext)
            setupRoomEventListener()
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize LiveKit Room: ${e.message}")
        }
    }

    private fun setupRoomEventListener() {
        val r = room ?: return
        roomEventsJob?.cancel()
        roomEventsJob = coroutineScope.launch {
            r.events.events.collect { event ->
                when (event) {
                    is RoomEvent.Connected -> {
                        _isConnecting.value = false
                        _isConnectedToRealRoom.value = true
                        _connectionQuality.value = ConnectionQualityLevel.EXCELLENT
                        _connectionError.value = null
                        updateParticipantsFromRoom()
                    }
                    is RoomEvent.Disconnected -> {
                        _isConnecting.value = false
                        _isConnectedToRealRoom.value = false
                        _connectionQuality.value = if (_config.value.isConfigured) ConnectionQualityLevel.DISCONNECTED else ConnectionQualityLevel.UNCONFIGURED
                        _localVideoTrack.value = null
                        _teacherVideoTrack.value = null
                    }
                    is RoomEvent.Reconnecting -> {
                        _connectionQuality.value = ConnectionQualityLevel.RECONNECTING
                    }
                    is RoomEvent.Reconnected -> {
                        _connectionQuality.value = ConnectionQualityLevel.GOOD
                        updateParticipantsFromRoom()
                    }
                    is RoomEvent.ParticipantConnected,
                    is RoomEvent.ParticipantDisconnected -> {
                        updateParticipantsFromRoom()
                    }
                    is RoomEvent.TrackSubscribed -> {
                        if (event.track is VideoTrack) {
                            if (event.participant.identity?.value?.contains("teacher", ignoreCase = true) == true ||
                                event.participant.name?.contains("Ustaz", ignoreCase = true) == true) {
                                _teacherVideoTrack.value = event.track as VideoTrack
                            }
                        }
                        updateParticipantsFromRoom()
                    }
                    is RoomEvent.TrackUnsubscribed -> {
                        if (event.track == _teacherVideoTrack.value) {
                            _teacherVideoTrack.value = null
                        }
                        updateParticipantsFromRoom()
                    }
                    is RoomEvent.TrackMuted,
                    is RoomEvent.TrackUnmuted -> {
                        updateParticipantsFromRoom()
                    }
                    is RoomEvent.ActiveSpeakersChanged -> {
                        val speakers = event.speakers
                        val topSpeaker = speakers.firstOrNull()?.name ?: speakers.firstOrNull()?.identity?.value
                        _activeSpeaker.value = topSpeaker
                        updateSpeakingStates(speakers)
                    }
                    is RoomEvent.ConnectionQualityChanged -> {
                        if (event.participant == r.localParticipant) {
                            _connectionQuality.value = when (event.quality) {
                                ConnectionQuality.EXCELLENT -> ConnectionQualityLevel.EXCELLENT
                                ConnectionQuality.GOOD -> ConnectionQualityLevel.GOOD
                                ConnectionQuality.POOR -> ConnectionQualityLevel.POOR
                                else -> ConnectionQualityLevel.GOOD
                            }
                        }
                    }
                    is RoomEvent.DataReceived -> {
                        handleIncomingData(event.data, event.participant)
                    }
                    else -> {}
                }
            }
        }
    }

    fun updateConfig(serverUrl: String, tokenEndpoint: String, devToken: String) {
        val configured = serverUrl.isNotBlank() && (tokenEndpoint.isNotBlank() || devToken.isNotBlank())
        _config.value = LiveKitConfig(
            serverUrl = serverUrl.trim(),
            tokenEndpoint = tokenEndpoint.trim(),
            devToken = devToken.trim(),
            isConfigured = configured
        )
        if (!configured) {
            _connectionQuality.value = ConnectionQualityLevel.UNCONFIGURED
        }
    }

    suspend fun joinClass(
        classId: String = "cls_live_01",
        participantName: String = "Student",
        role: ClassroomRole = _myRole.value
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        _myRole.value = role
        val currentConfig = _config.value

        if (!currentConfig.isConfigured) {
            // Unconfigured mode: operate in Interactive Prototype Sandbox
            withContext(Dispatchers.Main) {
                _isConnecting.value = false
                _isConnectedToRealRoom.value = false
                _connectionQuality.value = ConnectionQualityLevel.UNCONFIGURED
                _participants.value = MockClassData.initialParticipants
            }
            return@withContext Result.success(true)
        }

        try {
            withContext(Dispatchers.Main) {
                _isConnecting.value = true
                _connectionError.value = null
            }

            val r = room ?: LiveKit.create(context.applicationContext).also {
                room = it
                setupRoomEventListener()
            }

            // Retrieve token from devToken, tokenEndpoint, or devTokenServerId
            val token = when {
                currentConfig.devToken.isNotBlank() -> currentConfig.devToken
                currentConfig.tokenEndpoint.isNotBlank() -> fetchTokenFromBackend(currentConfig.tokenEndpoint, classId, participantName)
                currentConfig.devTokenServerId.isNotBlank() -> fetchDevSandboxToken(currentConfig.devTokenServerId, classId, participantName, currentConfig.serverUrl)
                else -> throw IllegalStateException("LiveKit Token or Development Token Server must be configured.")
            }

            r.connect(currentConfig.serverUrl, token)

            // Setup local audio/video based on preferences
            withContext(Dispatchers.Main) {
                r.localParticipant.setMicrophoneEnabled(!_isMicMuted.value)
                r.localParticipant.setCameraEnabled(_isVideoOn.value)
                val localVid = r.localParticipant.getTrackPublication(Track.Source.CAMERA)?.track as? VideoTrack
                _localVideoTrack.value = localVid
            }

            Result.success(true)
        } catch (e: Exception) {
            Log.e(tag, "Failed to connect to LiveKit Room: ${e.message}", e)
            withContext(Dispatchers.Main) {
                _isConnecting.value = false
                _isConnectedToRealRoom.value = false
                _connectionError.value = e.message ?: "Connection failed"
                _connectionQuality.value = ConnectionQualityLevel.DISCONNECTED
            }
            Result.failure(e)
        }
    }

    fun leaveClass() {
        coroutineScope.launch {
            try {
                room?.disconnect()
            } catch (e: Exception) {
                Log.e(tag, "Error disconnecting: ${e.message}")
            } finally {
                _isConnectedToRealRoom.value = false
                _isConnecting.value = false
                _localVideoTrack.value = null
                _teacherVideoTrack.value = null
                _connectionQuality.value = if (_config.value.isConfigured) ConnectionQualityLevel.DISCONNECTED else ConnectionQualityLevel.UNCONFIGURED
            }
        }
    }

    fun reconnect() {
        coroutineScope.launch {
            if (_config.value.isConfigured) {
                joinClass(role = _myRole.value)
            }
        }
    }

    // ----------------------------------------------------
    // Local Media Controls
    // ----------------------------------------------------

    fun toggleMic() {
        val newMuted = !_isMicMuted.value
        _isMicMuted.value = newMuted
        val r = room
        if (r != null && _isConnectedToRealRoom.value) {
            coroutineScope.launch {
                try {
                    r.localParticipant.setMicrophoneEnabled(!newMuted)
                } catch (e: Exception) {
                    Log.e(tag, "Error toggling microphone: ${e.message}")
                }
            }
        } else {
            // Update local prototype state
            _participants.update { list ->
                list.map { if (it.id == "p_1") it.copy(isMicMuted = newMuted) else it }
            }
        }
    }

    fun toggleVideo() {
        val newVideo = !_isVideoOn.value
        _isVideoOn.value = newVideo
        val r = room
        if (r != null && _isConnectedToRealRoom.value) {
            coroutineScope.launch {
                try {
                    r.localParticipant.setCameraEnabled(newVideo)
                    val localVid = r.localParticipant.getTrackPublication(Track.Source.CAMERA)?.track as? VideoTrack
                    _localVideoTrack.value = if (newVideo) localVid else null
                } catch (e: Exception) {
                    Log.e(tag, "Error toggling camera: ${e.message}")
                }
            }
        } else {
            // Update local prototype state
            _participants.update { list ->
                list.map { if (it.id == "p_1") it.copy(isVideoOn = newVideo) else it }
            }
        }
    }

    fun toggleSpeaker() {
        _isSpeakerOn.update { !it }
    }

    fun toggleRaiseHand(): Boolean {
        val newRaised = !_isHandRaised.value
        _isHandRaised.value = newRaised

        val packet = HandRaisePacket(
            type = "hand_raise",
            participantId = "p_1",
            participantName = "You (Student)",
            isRaised = newRaised,
            status = if (newRaised) "PENDING" else "DISMISSED"
        )

        publishDataPacket(handRaiseAdapter.toJson(packet))

        _participants.update { list ->
            list.map { if (it.id == "p_1") it.copy(isHandRaised = newRaised) else it }
        }

        return newRaised
    }

    // ----------------------------------------------------
    // Shared Quran Sheet (Synchronized Quran State)
    // ----------------------------------------------------

    fun setSynchronizedQuranVerse(
        surah: Int,
        ayah: Int,
        tajwidRule: String? = null,
        note: String? = null,
        highlight: Boolean = true
    ) {
        val packet = QuranSyncPacket(
            type = "quran_state",
            surah = surah,
            ayah = ayah,
            highlight = highlight,
            tajwidRule = tajwidRule ?: _synchronizedQuranState.value.tajwidRule,
            note = note ?: "Ustaz highlighted Verse $ayah for recitation assessment."
        )

        _synchronizedQuranState.value = packet
        publishDataPacket(quranPacketAdapter.toJson(packet))
    }

    fun nextVerse(maxVerses: Int = 286) {
        val current = _synchronizedQuranState.value
        if (current.ayah < maxVerses) {
            setSynchronizedQuranVerse(
                surah = current.surah,
                ayah = current.ayah + 1,
                tajwidRule = current.tajwidRule,
                note = "Moved to Verse ${current.ayah + 1}"
            )
        }
    }

    fun previousVerse() {
        val current = _synchronizedQuranState.value
        if (current.ayah > 1) {
            setSynchronizedQuranVerse(
                surah = current.surah,
                ayah = current.ayah - 1,
                tajwidRule = current.tajwidRule,
                note = "Moved to Verse ${current.ayah - 1}"
            )
        }
    }

    // ----------------------------------------------------
    // Chat & Messaging
    // ----------------------------------------------------

    fun sendChatMessage(text: String, senderName: String = "You") {
        if (text.isBlank()) return
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val msgId = "msg_${System.currentTimeMillis()}"
        val isTeacher = _myRole.value == ClassroomRole.TEACHER

        val packet = ChatPacket(
            type = "chat_message",
            id = msgId,
            senderId = "p_1",
            senderName = senderName,
            senderRole = if (isTeacher) "Teacher" else "Student",
            message = text.trim(),
            timestamp = timeStr,
            isAnnouncement = isTeacher
        )

        val localMsg = ClassChatMessage(
            id = msgId,
            senderName = senderName,
            message = text.trim(),
            timestamp = timeStr,
            isTeacher = isTeacher,
            isMe = true
        )

        _chatMessages.update { it + localMsg }
        publishDataPacket(chatPacketAdapter.toJson(packet))
    }

    // ----------------------------------------------------
    // Teacher Specific Actions
    // ----------------------------------------------------

    fun setMyRole(role: ClassroomRole) {
        _myRole.value = role
    }

    fun setClassMode(mode: ClassType) {
        _classMode.value = mode
        val packet = ParticipantActionPacket(
            type = "participant_action",
            targetParticipantId = "all",
            action = "MODE_SWITCH_${mode.name}"
        )
        publishDataPacket(participantActionAdapter.toJson(packet))
    }

    fun acceptHandRaise(packet: HandRaisePacket) {
        _activeHandRaiseAlert.value = null
        _selectedStudentReciter.value = packet.participantName

        val updated = packet.copy(status = "ACCEPTED")
        publishDataPacket(handRaiseAdapter.toJson(updated))

        // Update participant in list
        _participants.update { list ->
            list.map {
                if (it.id == packet.participantId || it.name == packet.participantName) {
                    it.copy(isSpeaking = true, isMicMuted = false, isHandRaised = false)
                } else it
            }
        }
    }

    fun dismissHandRaise(packet: HandRaisePacket) {
        _activeHandRaiseAlert.value = null
        val updated = packet.copy(status = "DISMISSED", isRaised = false)
        publishDataPacket(handRaiseAdapter.toJson(updated))

        _participants.update { list ->
            list.map {
                if (it.id == packet.participantId || it.name == packet.participantName) {
                    it.copy(isHandRaised = false)
                } else it
            }
        }
    }

    fun muteParticipant(participantId: String) {
        val packet = ParticipantActionPacket(
            type = "participant_action",
            targetParticipantId = participantId,
            action = "MUTE"
        )
        publishDataPacket(participantActionAdapter.toJson(packet))

        _participants.update { list ->
            list.map { if (it.id == participantId) it.copy(isMicMuted = true, isSpeaking = false) else it }
        }
    }

    fun selectStudentReciter(studentId: String, studentName: String) {
        _selectedStudentReciter.value = studentName
        val packet = ParticipantActionPacket(
            type = "participant_action",
            targetParticipantId = studentId,
            action = "SELECT_RECITER"
        )
        publishDataPacket(participantActionAdapter.toJson(packet))
    }

    fun removeParticipant(participantId: String) {
        val packet = ParticipantActionPacket(
            type = "participant_action",
            targetParticipantId = participantId,
            action = "KICK"
        )
        publishDataPacket(participantActionAdapter.toJson(packet))

        _participants.update { list ->
            list.filterNot { it.id == participantId }
        }
    }

    fun submitAssessment(studentId: String, studentName: String, surah: Int, ayah: Int, score: Int, feedback: String) {
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val assessment = RecitationAssessment(
            type = "assessment",
            studentId = studentId,
            studentName = studentName,
            surah = surah,
            ayah = ayah,
            score = score,
            tajwidFeedback = feedback,
            timestamp = timeStr
        )
        _latestAssessment.value = assessment
        publishDataPacket(assessmentAdapter.toJson(assessment))
    }

    // ----------------------------------------------------
    // Realtime Data Packets (Publish & Receive)
    // ----------------------------------------------------

    private fun publishDataPacket(jsonString: String) {
        val r = room
        if (r != null && _isConnectedToRealRoom.value) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val bytes = jsonString.toByteArray(Charsets.UTF_8)
                    r.localParticipant.publishData(
                        data = bytes,
                        reliability = DataPublishReliability.RELIABLE,
                        topic = "voxora_classroom"
                    )
                } catch (e: Exception) {
                    Log.e(tag, "Failed to publish data packet: ${e.message}")
                }
            }
        }
    }

    private fun handleIncomingData(data: ByteArray, sender: RemoteParticipant?) {
        try {
            val jsonStr = String(data, Charsets.UTF_8)
            val json = JSONObject(jsonStr)
            val type = json.optString("type")

            when (type) {
                "quran_state" -> {
                    val packet = quranPacketAdapter.fromJson(jsonStr)
                    if (packet != null) {
                        _synchronizedQuranState.value = packet
                    }
                }
                "chat_message" -> {
                    val packet = chatPacketAdapter.fromJson(jsonStr)
                    if (packet != null && packet.senderId != "p_1") {
                        val isTeacher = packet.senderRole.contains("Teacher", ignoreCase = true)
                        val msg = ClassChatMessage(
                            id = packet.id,
                            senderName = packet.senderName,
                            message = packet.message,
                            timestamp = packet.timestamp,
                            isTeacher = isTeacher,
                            isMe = false
                        )
                        _chatMessages.update { it + msg }
                    }
                }
                "hand_raise" -> {
                    val packet = handRaiseAdapter.fromJson(jsonStr)
                    if (packet != null) {
                        if (packet.isRaised && _myRole.value == ClassroomRole.TEACHER) {
                            _activeHandRaiseAlert.value = packet
                        }
                        _participants.update { list ->
                            list.map {
                                if (it.id == packet.participantId || it.name == packet.participantName) {
                                    it.copy(isHandRaised = packet.isRaised)
                                } else it
                            }
                        }
                    }
                }
                "assessment" -> {
                    val packet = assessmentAdapter.fromJson(jsonStr)
                    if (packet != null) {
                        _latestAssessment.value = packet
                    }
                }
                "participant_action" -> {
                    val packet = participantActionAdapter.fromJson(jsonStr)
                    if (packet != null) {
                        handleParticipantAction(packet)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to parse incoming data packet: ${e.message}")
        }
    }

    private fun handleParticipantAction(packet: ParticipantActionPacket) {
        if (packet.targetParticipantId == "p_1" || packet.targetParticipantId == "all") {
            when {
                packet.action == "MUTE" -> {
                    _isMicMuted.value = true
                    room?.localParticipant?.let { lp ->
                        coroutineScope.launch { lp.setMicrophoneEnabled(false) }
                    }
                }
                packet.action == "SELECT_RECITER" -> {
                    _isMicMuted.value = false
                    room?.localParticipant?.let { lp ->
                        coroutineScope.launch { lp.setMicrophoneEnabled(true) }
                    }
                }
                packet.action == "KICK" -> {
                    leaveClass()
                }
                packet.action.startsWith("MODE_SWITCH_") -> {
                    val modeName = packet.action.removePrefix("MODE_SWITCH_")
                    if (modeName == ClassType.ONE_ON_ONE.name) {
                        _classMode.value = ClassType.ONE_ON_ONE
                    } else {
                        _classMode.value = ClassType.GROUP
                    }
                }
            }
        }
    }

    private fun updateParticipantsFromRoom() {
        val r = room ?: return
        if (!_isConnectedToRealRoom.value) return

        val list = mutableListOf<Participant>()

        // Add local participant
        val local = r.localParticipant
        list.add(
            Participant(
                id = local.sid?.value ?: "local_p",
                name = local.name ?: "You",
                isHandRaised = _isHandRaised.value,
                isMicMuted = !local.isMicrophoneEnabled(),
                isVideoOn = local.isCameraEnabled(),
                isTeacher = _myRole.value == ClassroomRole.TEACHER,
                role = if (_myRole.value == ClassroomRole.TEACHER) "Teacher" else "Student",
                isSpeaking = local.isSpeaking
            )
        )

        // Add remote participants
        r.remoteParticipants.values.forEach { rp ->
            val isTeacher = rp.identity?.value?.contains("teacher", ignoreCase = true) == true ||
                    rp.name?.contains("Ustaz", ignoreCase = true) == true
            list.add(
                Participant(
                    id = rp.sid?.value ?: rp.identity?.value ?: "rp_${rp.hashCode()}",
                    name = rp.name ?: rp.identity?.value ?: "Participant",
                    isHandRaised = false,
                    isMicMuted = !rp.isMicrophoneEnabled(),
                    isVideoOn = rp.isCameraEnabled(),
                    isTeacher = isTeacher,
                    role = if (isTeacher) "Teacher" else "Student",
                    isSpeaking = rp.isSpeaking
                )
            )
        }

        _participants.value = list
    }

    private fun updateSpeakingStates(speakers: List<LKParticipant>) {
        val speakerSids = speakers.mapNotNull { it.sid?.value }.toSet()
        _participants.update { list ->
            list.map { p ->
                p.copy(isSpeaking = speakerSids.contains(p.id))
            }
        }
    }

    private fun fetchTokenFromBackend(endpoint: String, classId: String, participantName: String): String {
        val urlWithParams = "$endpoint?classId=$classId&participantName=$participantName"
        val request = Request.Builder()
            .url(urlWithParams)
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Failed to fetch token from backend: HTTP ${response.code}")
        }
        val body = response.body?.string() ?: throw IllegalStateException("Empty response body from token endpoint")
        val json = JSONObject(body)
        return json.optString("token").ifBlank {
            json.optString("jwt")
        }
    }

    private fun fetchDevSandboxToken(
        sandboxId: String,
        roomName: String,
        participantName: String,
        serverUrl: String
    ): String {
        val payload = JSONObject().apply {
            put("sandboxId", sandboxId)
            put("roomName", roomName)
            put("participantName", participantName)
            put("serverUrl", serverUrl)
        }

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = payload.toString().toRequestBody(mediaType)

        val sandboxUrls = listOf(
            "https://cloud-api.livekit.io/api/sandbox/tokens",
            "https://cloud.livekit.io/api/sandbox/tokens",
            "https://api.livekit.io/api/sandbox/tokens"
        )

        var lastError: Exception? = null
        for (url in sandboxUrls) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: continue
                    val json = JSONObject(body)
                    val token = json.optString("token")
                        .ifBlank { json.optString("jwt") }
                        .ifBlank { json.optString("accessToken") }
                    if (token.isNotBlank()) {
                        return token
                    }
                }
            } catch (e: Exception) {
                lastError = e
                Log.w(tag, "Sandbox token request to $url failed: ${e.message}")
            }
        }

        throw IllegalStateException(
            "Unable to obtain development token from LiveKit Server ($sandboxId). " +
                    (lastError?.message ?: "Please check internet connection or provide custom token.")
        )
    }
}
