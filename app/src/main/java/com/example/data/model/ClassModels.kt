package com.example.data.model

enum class ClassStatus {
    UPCOMING,
    LIVE,
    COMPLETED
}

enum class ClassType {
    ONE_ON_ONE,
    GROUP
}

data class Teacher(
    val id: String,
    val name: String,
    val title: String,
    val country: String,
    val flagEmoji: String,
    val languages: List<String>,
    val specializations: List<String>,
    val experienceYears: Int,
    val rating: Float,
    val reviewsCount: Int,
    val activeStudents: Int,
    val bio: String,
    val imageDrawableRes: Int? = null,
    val hourlyRate: String = "$15/hr",
    val availableSlots: List<String> = emptyList()
)

data class QuranClass(
    val id: String,
    val title: String,
    val subject: String, // "Tajwid — Mad Asli", "Hafazan Juz 30", "Quran Recitation"
    val teacher: Teacher,
    val type: ClassType,
    val status: ClassStatus,
    val dateText: String,
    val timeText: String,
    val durationMinutes: Int,
    val participantsCount: Int,
    val maxParticipants: Int = 20,
    val surahFocus: String = "Al-Baqarah",
    val verseFocus: Int = 1,
    val description: String = "Live interactive session covering articulation points, elongation rules and guided recitation feedback.",
    val prerequisites: String = "Basic Arabic letter recognition and Makharij awareness."
)

data class ClassChatMessage(
    val id: String,
    val senderName: String,
    val message: String,
    val timestamp: String,
    val isTeacher: Boolean = false,
    val isMe: Boolean = false
)

data class Participant(
    val id: String,
    val name: String,
    val isHandRaised: Boolean = false,
    val isMicMuted: Boolean = true,
    val isVideoOn: Boolean = true,
    val isTeacher: Boolean = false,
    val role: String = "Student"
)
