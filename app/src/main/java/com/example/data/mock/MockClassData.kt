package com.example.data.mock

import com.example.data.model.*

object MockClassData {

    val verifiedInstructor = Teacher(
        id = "inst_1",
        name = "Quran Instructor",
        title = "Tajwid & Qira'at Facilitator",
        country = "Malaysia",
        flagEmoji = "🇲🇾",
        languages = listOf("Arabic", "English", "Bahasa Melayu"),
        specializations = listOf("Tajwid", "Makharij", "Beginner Quran"),
        experienceYears = 10,
        rating = 5.0f,
        reviewsCount = 0,
        activeStudents = 0,
        bio = "Certified Tajwid facilitator assisting students in accurate articulation points and elongation rules.",
        imageDrawableRes = null,
        hourlyRate = "Verified",
        availableSlots = listOf("Schedule on request")
    )

    val allTeachers = listOf(verifiedInstructor)

    val liveTajwidClass = QuranClass(
        id = "cls_live_1",
        title = "Tajwid Interactive Studio",
        subject = "Tajwid & Recitation Practice",
        teacher = verifiedInstructor,
        type = ClassType.GROUP,
        status = ClassStatus.LIVE,
        dateText = "Today",
        timeText = "Live Room Active",
        durationMinutes = 60,
        participantsCount = 1,
        maxParticipants = 20,
        surahFocus = "Al-Baqarah",
        verseFocus = 2,
        description = "Live interactive audio-video practice room powered by LiveKit for real-time recitation coaching and pronunciation review.",
        prerequisites = "Open to all students."
    )

    // No fake upcoming classes - UI displays honest 'Classes Coming Soon'
    val upcomingClasses: List<QuranClass> = emptyList()

    val completedClasses: List<QuranClass> = emptyList()

    val initialParticipants = listOf(
        Participant(id = "p_teacher", name = "Session Host", isHandRaised = false, isMicMuted = false, isVideoOn = true, isTeacher = true, role = "Host"),
        Participant(id = "p_1", name = "You", isHandRaised = false, isMicMuted = true, isVideoOn = true, role = "Student")
    )

    val initialChatMessages = listOf(
        ClassChatMessage(id = "m_1", senderName = "Session Host", message = "Assalamu Alaikum! Welcome to the interactive Quran recitation room.", timestamp = "Now", isTeacher = true)
    )
}

