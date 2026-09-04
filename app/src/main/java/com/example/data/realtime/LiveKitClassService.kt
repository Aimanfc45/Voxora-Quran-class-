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

    private var localParticipantIdentity: String = ""
    private var localParticipantName: String = ""

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
    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    // Chat messages
    private val _chatMessages = MutableStateFlow<List<ClassChatMessage>>(emptyList())
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
    private val _activeSpeaker = MutableStateFlow<String?>(null)
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
                            val isTeacher = event.participant.identity?.value?.contains("teacher", ignoreCase = true) == true ||
                                event.participant.name?.contains("Ustaz", ignoreCase = true) == true ||
                                event.participant.name?.contains("Teacher", ignoreCase = true) == true ||
                                event.participant.metadata?.contains("teacher", ignoreCase = true) == true
                            if (isTeacher || _teacherVideoTrack.value == null) {
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

    fun updateConfig(
        serverUrl: String = _config.value.serverUrl,
        devTokenServerId: String = _config.value.devTokenServerId,
        tokenEndpoint: String = _config.value.tokenEndpoint,
        devToken: String = _config.value.devToken
    ) {
        val configured = serverUrl.isNotBlank() && (devTokenServerId.isNotBlank() || tokenEndpoint.isNotBlank() || devToken.isNotBlank())
        _config.value = LiveKitConfig(
            serverUrl = serverUrl.trim(),
            devTokenServerId = devTokenServerId.trim(),
            tokenEndpoint = tokenEndpoint.trim(),
            devToken = devToken.trim(),
            isConfigured = configured
        )
        if (!configured) {
            _connectionQuality.value = ConnectionQualityLevel.UNCONFIGURED
        }
    }

    private fun getStableParticipantIdentity(name: String): String {
        val sanitized = name.lowercase().replace("\\s+".toRegex(), "_")
        val suffix = Math.abs(context.packageName.hashCode()).toString(16).takeLast(4)
        return "user_${sanitized}_$suffix"
    }

    suspend fun joinClass(
        classId: String = "cls_live_01",
        participantName: String = "Student",
        participantIdentity: String = getStableParticipantIdentity(participantName),
        role: ClassroomRole = _myRole.value
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        _myRole.value = role
        localParticipantName = participantName
        localParticipantIdentity = participantIdentity
        val currentConfig = _config.value

        if (!currentConfig.isConfigured) {
            withContext(Dispatchers.Main) {
                _isConnecting.value = false
                _isConnectedToRealRoom.value = false
                _connectionQuality.value = ConnectionQualityLevel.UNCONFIGURED
                _participants.value = emptyList()
            }
            return@withContext Result.failure(LiveKitError.InvalidConfiguration("LiveKit configuration is incomplete."))
        }

        try {
            withContext(Dispatchers.Main) {
                _isConnecting.value = true
                _connectionError.value = null
            }

            // 1. Resolve TokenSource according to priority
            val tokenSource: LiveKitTokenSource = when {
                currentConfig.devToken.isNotBlank() -> {
                    LiveKitTokenSource.fromLiteral(
                        serverUrl = currentConfig.serverUrl,
                        participantToken = currentConfig.devToken
                    )
                }
                currentConfig.tokenEndpoint.isNotBlank() -> {
                    LiveKitTokenSource.fromEndpoint(
                        endpointUrl = currentConfig.tokenEndpoint,
                        okHttpClient = okHttpClient
                    )
                }
                currentConfig.devTokenServerId.isNotBlank() -> {
                    LiveKitTokenSource.fromDevelopmentTokenServer(
                        tokenServerId = currentConfig.devTokenServerId,
                        okHttpClient = okHttpClient
                    )
                }
                else -> {
                    throw LiveKitError.InvalidConfiguration("LiveKit Development Token Server ID or Token Endpoint must be configured.")
                }
            }

            // 2. Fetch Credentials from TokenSource
            val credentials = tokenSource.fetch(
                roomName = classId,
                participantName = participantName,
                participantIdentity = participantIdentity
            )

            val connectUrl = credentials.serverUrl.ifBlank { currentConfig.serverUrl }
            if (connectUrl.isBlank()) {
                throw LiveKitError.InvalidConfiguration("LiveKit Server URL is required.")
            }

            // 3. Connect Room with credentials
            val r = room ?: LiveKit.create(context.applicationContext).also {
                room = it
                setupRoomEventListener()
            }

            try {
                r.connect(connectUrl, credentials.participantToken)
            } catch (e: Exception) {
                throw LiveKitError.RoomConnectionFailed("Failed to connect to LiveKit room ($connectUrl): ${e.message}", e)
            }

            // Setup local audio/video based on preferences
            withContext(Dispatchers.Main) {
                try {
                    r.localParticipant.setMicrophoneEnabled(!_isMicMuted.value)
                    r.localParticipant.setCameraEnabled(_isVideoOn.value)
                    val localVid = r.localParticipant.getTrackPublication(Track.Source.CAMERA)?.track as? VideoTrack
                    _localVideoTrack.value = localVid
                } catch (pe: Exception) {
                    Log.w(tag, "Initial media setup warning: ${pe.message}")
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            val liveKitError = if (e is LiveKitError) e else LiveKitError.RoomConnectionFailed(e.message ?: "Connection failed", e)
            Log.e(tag, "LiveKit connect error [${liveKitError.code}]: ${liveKitError.message}", liveKitError)
            withContext(Dispatchers.Main) {
                _isConnecting.value = false
                _isConnectedToRealRoom.value = false
                _connectionError.value = "[${liveKitError.code}] ${liveKitError.message}"
                _connectionQuality.value = ConnectionQualityLevel.DISCONNECTED
            }
            Result.failure(liveKitError)
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

    private val _isFrontCamera = MutableStateFlow(true)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    fun flipCamera() {
        val nextState = !_isFrontCamera.value
        _isFrontCamera.value = nextState
        val r = room ?: return
        if (_isConnectedToRealRoom.value) {
            coroutineScope.launch {
                try {
                    val track = r.localParticipant.getTrackPublication(Track.Source.CAMERA)?.track as? LocalVideoTrack
                    // LiveKit handles camera flipping or restarting
                    track?.let {
                        // Switch camera or toggle device
                        r.localParticipant.setCameraEnabled(false)
                        r.localParticipant.setCameraEnabled(true)
                        val updatedTrack = r.localParticipant.getTrackPublication(Track.Source.CAMERA)?.track as? VideoTrack
                        _localVideoTrack.value = updatedTrack
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error flipping camera: ${e.message}")
                }
            }
        }
    }

    fun toggleSpeaker() {
        _isSpeakerOn.update { !it }
    }

    fun toggleRaiseHand(): Boolean {
        val newRaised = !_isHandRaised.value
        _isHandRaised.value = newRaised

        val myId = room?.localParticipant?.sid?.value ?: localParticipantIdentity.ifBlank { "local_p" }
        val myName = localParticipantName.ifBlank { "Student" }

        val packet = HandRaisePacket(
            type = "hand_raise",
            participantId = myId,
            participantName = myName,
            isRaised = newRaised,
            status = if (newRaised) "PENDING" else "DISMISSED"
        )

        publishDataPacket(handRaiseAdapter.toJson(packet))

        _participants.update { list ->
            list.map { if (it.id == myId || it.id == "local_p" || it.id == room?.localParticipant?.sid?.value) it.copy(isHandRaised = newRaised) else it }
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
        val myId = room?.localParticipant?.sid?.value ?: localParticipantIdentity.ifBlank { "local_p" }

        val packet = ChatPacket(
            type = "chat_message",
            id = msgId,
            senderId = myId,
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
            val myId = room?.localParticipant?.sid?.value ?: localParticipantIdentity.ifBlank { "local_p" }
            val myIdent = room?.localParticipant?.identity?.value ?: localParticipantIdentity

            when (type) {
                "quran_state" -> {
                    val packet = quranPacketAdapter.fromJson(jsonStr)
                    if (packet != null) {
                        _synchronizedQuranState.value = packet
                    }
                }
                "chat_message" -> {
                    val packet = chatPacketAdapter.fromJson(jsonStr)
                    if (packet != null && packet.senderId != myId && packet.senderId != myIdent) {
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
        val myId = room?.localParticipant?.sid?.value ?: localParticipantIdentity.ifBlank { "local_p" }
        val myIdent = room?.localParticipant?.identity?.value ?: localParticipantIdentity

        if (packet.targetParticipantId == myId || packet.targetParticipantId == myIdent || packet.targetParticipantId == "all") {
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
        val localName = local.name ?: localParticipantName.ifBlank { "You" }
        list.add(
            Participant(
                id = local.sid?.value ?: local.identity?.value ?: "local_p",
                name = localName,
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
                    rp.name?.contains("Ustaz", ignoreCase = true) == true ||
                    rp.name?.contains("Teacher", ignoreCase = true) == true ||
                    rp.metadata?.contains("teacher", ignoreCase = true) == true
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

}

/**
 * LiveKit Token Source Abstraction.
 * Encapsulates token retrieval for Development Token Server, Backend Endpoints, or Literal tokens.
 */
sealed interface LiveKitTokenSource {
    suspend fun fetch(
        roomName: String,
        participantName: String,
        participantIdentity: String
    ): TokenSourceResponse

    companion object {
        fun fromDevelopmentTokenServer(
            tokenServerId: String,
            okHttpClient: OkHttpClient = OkHttpClient()
        ): LiveKitTokenSource = DevelopmentTokenServerTokenSource(tokenServerId, okHttpClient)

        fun fromEndpoint(
            endpointUrl: String,
            okHttpClient: OkHttpClient = OkHttpClient()
        ): LiveKitTokenSource = EndpointTokenSource(endpointUrl, okHttpClient)

        fun fromLiteral(
            serverUrl: String,
            participantToken: String
        ): LiveKitTokenSource = LiteralTokenSource(serverUrl, participantToken)
    }
}

/**
 * Development Token Server TokenSource.
 * Connects securely to the official LiveKit Cloud Development Token Server.
 */
class DevelopmentTokenServerTokenSource(
    private val tokenServerId: String,
    private val client: OkHttpClient
) : LiveKitTokenSource {

    override suspend fun fetch(
        roomName: String,
        participantName: String,
        participantIdentity: String
    ): TokenSourceResponse = withContext(Dispatchers.IO) {
        if (tokenServerId.isBlank()) {
            throw LiveKitError.InvalidConfiguration("Development Token Server ID is missing.")
        }

        val jsonBody = JSONObject().apply {
            put("room_name", roomName)
            put("participant_name", participantName)
            put("participant_identity", participantIdentity)
        }
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(DEV_TOKEN_SERVER_ENDPOINT)
            .header("X-Sandbox-ID", tokenServerId.trim())
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: java.io.IOException) {
            throw LiveKitError.NetworkError("Network request to Development Token Server failed: ${e.message}", e)
        } catch (e: Exception) {
            throw LiveKitError.TokenFetchFailed("Could not connect to Development Token Server: ${e.message}", e)
        }

        response.use { resp ->
            val responseBody = resp.body?.string() ?: ""
            if (!resp.isSuccessful) {
                val errorMsg = try {
                    JSONObject(responseBody).optString("message", resp.message)
                } catch (_: Exception) {
                    resp.message
                }
                throw LiveKitError.TokenServerError("Development Token Server error [HTTP ${resp.code}]: $errorMsg")
            }

            try {
                val json = JSONObject(responseBody)
                val token = json.optString("participantToken")
                    .ifBlank { json.optString("token") }
                    .ifBlank { json.optString("jwt") }

                if (token.isBlank()) {
                    throw LiveKitError.TokenFetchFailed("Development Token Server returned empty token.")
                }

                val serverUrl = json.optString("serverUrl")
                TokenSourceResponse(
                    serverUrl = serverUrl,
                    participantToken = token,
                    roomName = json.optString("roomName", roomName),
                    participantName = json.optString("participantName", participantName)
                )
            } catch (e: Exception) {
                if (e is LiveKitError) throw e
                throw LiveKitError.TokenFetchFailed("Failed to parse token credentials: ${e.message}", e)
            }
        }
    }

    companion object {
        const val DEV_TOKEN_SERVER_ENDPOINT = "https://cloud-api.livekit.io/api/sandbox/connection-details"
    }
}

/**
 * Production Endpoint TokenSource.
 * Queries custom backend service for signed JWT tokens.
 */
class EndpointTokenSource(
    private val endpointUrl: String,
    private val client: OkHttpClient
) : LiveKitTokenSource {

    override suspend fun fetch(
        roomName: String,
        participantName: String,
        participantIdentity: String
    ): TokenSourceResponse = withContext(Dispatchers.IO) {
        if (endpointUrl.isBlank()) {
            throw LiveKitError.InvalidConfiguration("Backend Token Endpoint URL is missing.")
        }

        val urlWithParams = if (endpointUrl.contains("?")) {
            "$endpointUrl&room=$roomName&identity=$participantIdentity&name=$participantName"
        } else {
            "$endpointUrl?room=$roomName&identity=$participantIdentity&name=$participantName"
        }

        val request = Request.Builder()
            .url(urlWithParams)
            .header("Accept", "application/json")
            .get()
            .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: java.io.IOException) {
            throw LiveKitError.NetworkError("Failed to reach Backend Token Endpoint: ${e.message}", e)
        }

        response.use { resp ->
            val responseBody = resp.body?.string() ?: ""
            if (!resp.isSuccessful) {
                throw LiveKitError.TokenServerError("Backend Token Endpoint error [HTTP ${resp.code}]: $responseBody")
            }

            try {
                val json = JSONObject(responseBody)
                val token = json.optString("participantToken")
                    .ifBlank { json.optString("token") }
                    .ifBlank { json.optString("jwt") }
                val serverUrl = json.optString("serverUrl")

                TokenSourceResponse(
                    serverUrl = serverUrl,
                    participantToken = token.ifBlank { responseBody.trim() },
                    roomName = json.optString("roomName", roomName),
                    participantName = json.optString("participantName", participantName)
                )
            } catch (_: Exception) {
                TokenSourceResponse(
                    serverUrl = "",
                    participantToken = responseBody.trim(),
                    roomName = roomName,
                    participantName = participantName
                )
            }
        }
    }
}

/**
 * Literal TokenSource.
 * Directly provides pre-generated token for test environments.
 */
class LiteralTokenSource(
    private val serverUrl: String,
    private val participantToken: String
) : LiveKitTokenSource {

    override suspend fun fetch(
        roomName: String,
        participantName: String,
        participantIdentity: String
    ): TokenSourceResponse {
        if (participantToken.isBlank()) {
            throw LiveKitError.InvalidConfiguration("Provided JWT Token is empty.")
        }
        return TokenSourceResponse(
            serverUrl = serverUrl,
            participantToken = participantToken,
            roomName = roomName,
            participantName = participantName
        )
    }
}

/**
 * Structured response containing token credentials.
 */
data class TokenSourceResponse(
    val serverUrl: String,
    val participantToken: String,
    val roomName: String? = null,
    val participantName: String? = null
)

/**
 * Structured LiveKit error hierarchy.
 */
sealed class LiveKitError(val code: String, message: String, cause: Throwable? = null) : Exception(message, cause) {
    class InvalidConfiguration(message: String = "LiveKit Server URL or Token Server ID is missing.") :
        LiveKitError("INVALID_CONFIGURATION", message)
    class NetworkError(message: String = "Unable to reach LiveKit server. Please check internet connection.", cause: Throwable? = null) :
        LiveKitError("NETWORK_ERROR", message, cause)
    class TokenServerError(message: String = "LiveKit Development Token Server rejected the request.", cause: Throwable? = null) :
        LiveKitError("TOKEN_SERVER_ERROR", message, cause)
    class TokenFetchFailed(message: String = "Failed to obtain room credentials.", cause: Throwable? = null) :
        LiveKitError("TOKEN_FETCH_FAILED", message, cause)
    class RoomConnectionFailed(message: String = "Failed to establish LiveKit room connection.", cause: Throwable? = null) :
        LiveKitError("ROOM_CONNECTION_FAILED", message, cause)
    class PermissionDenied(message: String = "Microphone or camera permission was not granted.") :
        LiveKitError("PERMISSION_DENIED", message)
}
