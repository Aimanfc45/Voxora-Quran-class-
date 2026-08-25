package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTranslation: String,
    val revelationType: String, // "Meccan" or "Medinan"
    val totalVerses: Int,
    val verses: List<Verse> = emptyList()
)

data class Verse(
    val id: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val textArabic: String,
    val transliteration: String,
    val translationEnglish: String,
    val translationMalay: String,
    val audioDurationSeconds: Int = 8,
    val tajwidRuleHighlight: String? = null
)

data class JuzInfo(
    val number: Int,
    val startSurahName: String,
    val startVerse: Int,
    val totalVerses: Int
)

data class QuranBookmark(
    val id: String,
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val snippetArabic: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class QuranSettings(
    val arabicFontSizeSp: Float = 26f,
    val showTranslation: Boolean = true,
    val showTransliteration: Boolean = true,
    val translationLanguage: String = "English", // "English", "Bahasa Melayu", "Arabic"
    val reciterName: String = "Mishary Rashid Alafasy",
    val autoScrollAudio: Boolean = true
)
