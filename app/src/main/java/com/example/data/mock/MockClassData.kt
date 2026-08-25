package com.example.data.mock

import com.example.R
import com.example.data.model.*

object MockClassData {

    val teacherAhmad = Teacher(
        id = "t_1",
        name = "Ustaz Ahmad Al-Azhari",
        title = "Senior Tajwid & Qira'at Specialist",
        country = "Egypt / Malaysia",
        flagEmoji = "🇪🇬",
        languages = listOf("Arabic (Native)", "English", "Bahasa Melayu"),
        specializations = listOf("Tajwid", "Hafazan", "Qiraat", "Beginner Quran"),
        experienceYears = 12,
        rating = 4.95f,
        reviewsCount = 342,
        activeStudents = 128,
        bio = "Al-Azhar graduate with Ijazah in Hafs 'an 'Asim and Warsh. Specializes in helping students correct articulation points and master advanced rules of elongation.",
        imageDrawableRes = R.drawable.img_teacher_ahmad,
        hourlyRate = "$18/session",
        availableSlots = listOf("Today 04:00 PM", "Today 08:00 PM", "Tomorrow 10:00 AM", "Tomorrow 05:00 PM")
    )

    val teacherFatima = Teacher(
        id = "t_2",
        name = "Ustazah Fatima Zahra",
        title = "Certified Hafizah & Children Pedagogy Expert",
        country = "Morocco",
        flagEmoji = "🇲🇦",
        languages = listOf("Arabic", "English", "French"),
        specializations = listOf("Children's Quran", "Hafazan", "Tajwid"),
        experienceYears = 8,
        rating = 4.98f,
        reviewsCount = 215,
        activeStudents = 95,
        bio = "Experienced Quran tutor with Sanad in 10 Qira'at minor. Known for interactive, child-friendly teaching methods and memory techniques for Juz 30.",
        imageDrawableRes = R.drawable.img_teacher_fatima,
        hourlyRate = "$16/session",
        availableSlots = listOf("Today 06:00 PM", "Tomorrow 02:00 PM", "Saturday 11:00 AM")
    )

    val teacherBilal = Teacher(
        id = "t_3",
        name = "Sheikh Bilal Tariq",
        title = "Quran Reciter & Maqamat Instructor",
        country = "Jordan",
        flagEmoji = "🇯🇴",
        languages = listOf("Arabic", "English"),
        specializations = listOf("Tajwid", "Beginner Quran", "Qiraat"),
        experienceYears = 15,
        rating = 4.90f,
        reviewsCount = 480,
        activeStudents = 160,
        bio = "Former radio reciter in Amman. Teaches proper breathing techniques, voice control, and precision in Tajwid rules for adult learners.",
        imageDrawableRes = R.drawable.img_teacher_ahmad,
        hourlyRate = "$20/session",
        availableSlots = listOf("Tomorrow 09:00 AM", "Tomorrow 07:00 PM")
    )

    val allTeachers = listOf(teacherAhmad, teacherFatima, teacherBilal)

    val liveTajwidClass = QuranClass(
        id = "cls_live_1",
        title = "Tajwid Masterclass — Rules of Mad Asli & Far'i",
        subject = "Tajwid — Mad Asli",
        teacher = teacherAhmad,
        type = ClassType.GROUP,
        status = ClassStatus.LIVE,
        dateText = "Today",
        timeText = "Live Now",
        durationMinutes = 60,
        participantsCount = 12,
        maxParticipants = 15,
        surahFocus = "Al-Baqarah",
        verseFocus = 2,
        description = "Live practice on distinguishing 2, 4, and 6 counts in recitation. We will read Surah Al-Baqarah verses 1-5 and test student elongation accuracy.",
        prerequisites = "Ability to read Arabic text with vowels."
    )

    val upcomingClasses = listOf(
        QuranClass(
            id = "cls_up_1",
            title = "Juz 30 Memorization Circle — Surah Al-Falaq & An-Nas",
            subject = "Hafazan Juz 30",
            teacher = teacherFatima,
            type = ClassType.GROUP,
            status = ClassStatus.UPCOMING,
            dateText = "Tomorrow, Aug 26",
            timeText = "05:00 PM - 06:00 PM",
            durationMinutes = 60,
            participantsCount = 8,
            maxParticipants = 10,
            surahFocus = "Al-Falaq",
            verseFocus = 1,
            description = "Structured memorization session with verse-by-verse repetition and correction of common pitfalls in Surah Al-Falaq."
        ),
        QuranClass(
            id = "cls_up_2",
            title = "1-on-1 Personalized Recitation Assessment",
            subject = "Quran Recitation",
            teacher = teacherAhmad,
            type = ClassType.ONE_ON_ONE,
            status = ClassStatus.UPCOMING,
            dateText = "Thursday, Aug 27",
            timeText = "08:00 PM - 08:45 PM",
            durationMinutes = 45,
            participantsCount = 1,
            maxParticipants = 1,
            surahFocus = "Al-Fatihah",
            verseFocus = 1,
            description = "Private one-on-one session diagnosing your individual Makharij, breath control, and Tajwid accuracy."
        )
    )

    val completedClasses = listOf(
        QuranClass(
            id = "cls_comp_1",
            title = "Introduction to Noon Sakinah & Tanween Rules",
            subject = "Tajwid — Idh-har & Idgham",
            teacher = teacherAhmad,
            type = ClassType.GROUP,
            status = ClassStatus.COMPLETED,
            dateText = "Aug 22, 2026",
            timeText = "Completed (60 mins)",
            durationMinutes = 60,
            participantsCount = 14,
            maxParticipants = 15,
            surahFocus = "Al-Fatihah",
            verseFocus = 7,
            description = "Covered 6 letters of Idh-har Halqi with live practical drill."
        ),
        QuranClass(
            id = "cls_comp_2",
            title = "Makharij Al-Huruf — Throat Letters",
            subject = "Tajwid — Makharij",
            teacher = teacherBilal,
            type = ClassType.GROUP,
            status = ClassStatus.COMPLETED,
            dateText = "Aug 18, 2026",
            timeText = "Completed (45 mins)",
            durationMinutes = 45,
            participantsCount = 10,
            maxParticipants = 12,
            surahFocus = "Al-Ikhlas",
            verseFocus = 1,
            description = "Practicing the articulation points for Hamzah, Haa, 'Ayn, Haa, Ghayn, Khaa."
        )
    )

    val initialParticipants = listOf(
        Participant(id = "p_0", name = "Ustaz Ahmad (Host)", isHandRaised = false, isMicMuted = false, isVideoOn = true, isTeacher = true, role = "Teacher"),
        Participant(id = "p_1", name = "Ahmed Al-Farsi (You)", isHandRaised = false, isMicMuted = true, isVideoOn = true, role = "Student"),
        Participant(id = "p_2", name = "Sarah binti Ridzwan", isHandRaised = true, isMicMuted = true, isVideoOn = true, role = "Student"),
        Participant(id = "p_3", name = "Zayd Ibrahim", isHandRaised = false, isMicMuted = true, isVideoOn = false, role = "Student"),
        Participant(id = "p_4", name = "Maryam Farooq", isHandRaised = false, isMicMuted = true, isVideoOn = true, role = "Student"),
        Participant(id = "p_5", name = "Omar Khalid", isHandRaised = false, isMicMuted = true, isVideoOn = true, role = "Student"),
        Participant(id = "p_6", name = "Nurul Huda", isHandRaised = true, isMicMuted = true, isVideoOn = true, role = "Student")
    )

    val initialChatMessages = listOf(
        ClassChatMessage(id = "m_1", senderName = "Ustaz Ahmad", message = "Assalamu Alaikum everyone, please open Surah Al-Baqarah verse 2.", timestamp = "10:02 AM", isTeacher = true),
        ClassChatMessage(id = "m_2", senderName = "Sarah binti Ridzwan", message = "Wa Alaikum Assalam Ustaz! Ready.", timestamp = "10:03 AM"),
        ClassChatMessage(id = "m_3", senderName = "Ahmed Al-Farsi", message = "Screen and audio are crystal clear.", timestamp = "10:04 AM", isMe = true),
        ClassChatMessage(id = "m_4", senderName = "Ustaz Ahmad", message = "Notice the Idgham Bila Ghunnah on هُدًى لِّلْمُتَّقِينَ. Let us recite together.", timestamp = "10:05 AM", isTeacher = true)
    )
}
