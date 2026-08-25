package com.example.data.mock

import com.example.data.model.*

object MockUserData {

    val currentUser = UserProfile(
        name = "Ahmed Al-Farsi",
        username = "@ahmed_alfarsi",
        email = "ahmed.farsi@voxora.app",
        country = "Malaysia",
        flagEmoji = "🇲🇾",
        languages = listOf("English", "Bahasa Melayu", "Arabic"),
        learningLevel = "Intermediate (Juz 5)",
        bio = "Devoted to perfecting Tajwid rules, accurate articulation points (Makharij), and steady memorization.",
        lessonsCompleted = 32,
        hoursSpent = 48.5f,
        surahsMemorized = 8,
        learningStreakDays = 14
    )

    val userProgress = LearningProgress(
        quranReadingPercent = 68,
        tajwidPercent = 84,
        memorizationPercent = 45,
        currentLessonTitle = "Tajwid — Rules of Mad Asli & Far'i",
        currentLessonSurah = "Surah Al-Baqarah (Verses 1–5)",
        currentLessonProgress = 72
    )

    val initialBookmarks = listOf(
        QuranBookmark(
            id = "bm_1",
            surahNumber = 2,
            surahName = "Al-Baqarah",
            verseNumber = 255,
            snippetArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ...",
            note = "Ayat al-Kursi — recite after every fardh prayer"
        ),
        QuranBookmark(
            id = "bm_2",
            surahNumber = 1,
            surahName = "Al-Fatihah",
            verseNumber = 7,
            snippetArabic = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ...",
            note = "Practice 6 counts on Mad Lazim"
        ),
        QuranBookmark(
            id = "bm_3",
            surahNumber = 112,
            surahName = "Al-Ikhlas",
            verseNumber = 1,
            snippetArabic = "قُلْ هُوَ اللَّهُ أَحَدٌ",
            note = "Tawheed & Qalqalah Kubra reflection"
        )
    )

    val achievements = listOf(
        Achievement(
            id = "ach_1",
            title = "First Lesson",
            description = "Completed your first live interactive Quran class on Voxora.",
            iconEmoji = "🎓",
            isUnlocked = true,
            progressPercent = 100,
            unlockedDate = "Aug 10, 2026"
        ),
        Achievement(
            id = "ach_2",
            title = "7-Day Streak",
            description = "Maintained continuous daily Quran recitation practice for a full week.",
            iconEmoji = "🔥",
            isUnlocked = true,
            progressPercent = 100,
            unlockedDate = "Aug 18, 2026"
        ),
        Achievement(
            id = "ach_3",
            title = "14-Day Streak",
            description = "Maintained continuous daily Quran learning for two uninterrupted weeks.",
            iconEmoji = "⚡",
            isUnlocked = true,
            progressPercent = 100,
            unlockedDate = "Today"
        ),
        Achievement(
            id = "ach_4",
            title = "Tajwid Beginner",
            description = "Mastered Noon Sakinah, Tanween, and Meem Sakinah rules with Ustaz Ahmad.",
            iconEmoji = "🌟",
            isUnlocked = true,
            progressPercent = 100,
            unlockedDate = "Aug 22, 2026"
        ),
        Achievement(
            id = "ach_5",
            title = "First Surah Completed",
            description = "Completed comprehensive recitation and Tajwid testing for Surah Al-Fatihah.",
            iconEmoji = "📜",
            isUnlocked = true,
            progressPercent = 100,
            unlockedDate = "Aug 15, 2026"
        ),
        Achievement(
            id = "ach_6",
            title = "Juz 30 Progress",
            description = "Memorized and tested 8 Surahs in Juz 'Amma.",
            iconEmoji = "🏆",
            isUnlocked = false,
            progressPercent = 65
        ),
        Achievement(
            id = "ach_7",
            title = "Master Reciter",
            description = "Attend 50 live classes with certified Ijazah instructors.",
            iconEmoji = "👑",
            isUnlocked = false,
            progressPercent = 64
        )
    )
}
