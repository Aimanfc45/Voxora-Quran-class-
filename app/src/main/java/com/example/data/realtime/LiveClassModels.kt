package com.example.data.realtime

import com.example.data.model.ClassStatus
import com.example.data.model.ClassType
import com.example.data.model.Participant
import com.example.data.model.Teacher
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuranSyncPacket(
    val type: String = "quran_state",
    val surah: Int = 2,
    val ayah: Int = 1,
    val highlight: Boolean = true,
    val tajwidRule: String? = null,
    val note: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatPacket(
    val type: String = "chat_message",
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "Student",
    val message: String = "",
    val timestamp: String = "",
    val isAnnouncement: Boolean = false
)

@JsonClass(generateAdapter = true)
data class HandRaisePacket(
    val type: String = "hand_raise",
    val participantId: String = "",
    val participantName: String = "",
    val isRaised: Boolean = false,
    val status: String = "PENDING" // "PENDING", "ACCEPTED", "DISMISSED"
)

@JsonClass(generateAdapter = true)
data class RecitationAssessment(
    val type: String = "assessment",
    val studentId: String = "",
    val studentName: String = "",
    val surah: Int = 2,
    val ayah: Int = 1,
    val score: Int = 95,
    val tajwidFeedback: String = "Excellent articulation and elongation",
    val timestamp: String = ""
)

@JsonClass(generateAdapter = true)
data class ParticipantActionPacket(
    val type: String = "participant_action",
    val targetParticipantId: String = "",
    val action: String = "MUTE" // "MUTE", "KICK", "SELECT_RECITER", "MODE_SWITCH"
)

@JsonClass(generateAdapter = true)
data class ClassInfoPacket(
    val type: String = "class_info",
    val classCode: String = "",
    val className: String = "",
    val topic: String = "",
    val classType: String = "GROUP",
    val hostName: String = "Teacher / Host"
)

data class LiveQuranRoomModel(
    val roomId: String = "vox-room-786",
    val classId: String = "cls_live_01",
    val teacher: Teacher,
    val participants: List<Participant> = emptyList(),
    val subject: String = "Tajwid & Hafazan — Mad Asli Rules",
    val surahNumber: Int = 2,
    val surahName: String = "Al-Baqarah",
    val selectedVerse: Int = 1,
    val activeTajwidRule: String? = "Mad Asli",
    val activeTeachingNote: String? = "Focus on the natural 2-vowel count elongation.",
    val classStatus: ClassStatus = ClassStatus.LIVE,
    val classType: ClassType = ClassType.GROUP,
    val connectionQuality: ConnectionQualityLevel = ConnectionQualityLevel.UNCONFIGURED,
    val activeSpeakerName: String? = null,
    val activeReciterName: String? = null
)
