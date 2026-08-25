package com.example.data.model

enum class AudioRepeatMode {
    OFF,
    REPEAT_VERSE,
    REPEAT_RANGE,
    REPEAT_SURAH
}

data class QuranAudioState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val surahNumber: Int = 1,
    val verseNumber: Int = 1,
    val currentPositionSeconds: Float = 0f,
    val totalDurationSeconds: Float = 8f,
    val playbackSpeed: Float = 1.0f,
    val volume: Float = 1.0f,
    val repeatMode: AudioRepeatMode = AudioRepeatMode.OFF,
    val autoNextVerse: Boolean = true,
    val reciterName: String = "Mishary Rashid Alafasy",
    val repeatRangeStart: Int = 1,
    val repeatRangeEnd: Int = 7,
    val audioUrl: String = ""
)

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
    val tajwidRuleHighlight: String? = null,
    val note: String? = null
)

data class JuzInfo(
    val number: Int,
    val nameArabic: String = "",
    val startSurahNumber: Int,
    val startSurahName: String,
    val startVerse: Int,
    val totalVerses: Int = 150
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

data class VerseNote(
    val id: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class QuranSettings(
    val arabicFontSizeSp: Float = 26f,
    val showTranslation: Boolean = true,
    val showTransliteration: Boolean = true,
    val showEnglishTranslation: Boolean = true,
    val showWordByWord: Boolean = false,
    val translationLanguage: String = "English", // "English", "Bahasa Melayu", "Arabic", "French", "Urdu"
    val reciterName: String = "Mishary Rashid Alafasy",
    val selectedReciter: String = "Mishary Rashid Alafasy",
    val autoScrollAudio: Boolean = true
)
