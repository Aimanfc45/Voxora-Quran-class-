package com.example.data.model

data class LearningProgress(
    val quranReadingPercent: Int = 68,
    val tajwidPercent: Int = 84,
    val memorizationPercent: Int = 45,
    val currentLessonTitle: String = "Tajwid — Rules of Noon Sakinah & Tanween",
    val currentLessonSurah: String = "Surah Al-Baqarah",
    val currentLessonProgress: Int = 72,
    val weeklyMinutes: List<DayActivity> = listOf(
        DayActivity("Mon", 45),
        DayActivity("Tue", 60),
        DayActivity("Wed", 30),
        DayActivity("Thu", 50),
        DayActivity("Fri", 90),
        DayActivity("Sat", 40),
        DayActivity("Sun", 75)
    )
)

data class DayActivity(
    val day: String,
    val minutes: Int
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val progressPercent: Int = 100,
    val unlockedDate: String? = null
)
