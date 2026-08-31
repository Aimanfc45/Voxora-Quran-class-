package com.example.data.model

enum class AudioRepeatMode {
    OFF,
    REPEAT_VERSE,
    REPEAT_RANGE,
    REPEAT_SURAH
}

enum class ReadingDisplayMode(val label: String, val description: String) {
    ARABIC_ONLY("Arabic Only", "Mushaf style recitation view"),
    ARABIC_EN("Arabic + English", "Arabic script with English Sahih International"),
    ARABIC_BM("Arabic + Bahasa Melayu", "Arabic script with Malaysian translation"),
    ARABIC_TRANSLITERATION("Arabic + Transliteration", "Phonetic romanization for pronunciation"),
    MULTI_TRANSLATION("Full Dual Translation", "Arabic, Transliteration, English & BM")
}

enum class QuranLineSpacing(val factor: Float, val label: String) {
    COMPACT(1.2f, "Compact"),
    NORMAL(1.5f, "Standard"),
    RELAXED(1.9f, "Relaxed")
}

data class ReciterInfo(
    val id: String,
    val name: String,
    val arabicName: String,
    val country: String,
    val flagEmoji: String,
    val style: String, // "Murattal" or "Mujawwad"
    val audioFolder: String,
    val bitRate: String = "128kbps",
    val isFavorite: Boolean = false,
    val description: String = "Certified master Qari with high-fidelity Murattal audio."
)

data class TajwidRule(
    val id: String,
    val name: String,
    val arabicName: String,
    val category: String, // "Mad", "Ikhfa", "Idgham", "Iqlab", "Qalqalah", "Ghunnah"
    val colorHex: Long,
    val description: String,
    val ruleSummary: String,
    val exampleArabic: String,
    val exampleTransliteration: String,
    val verseReference: String,
    val pronunciationTip: String
)

data class QuranSearchResult(
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val textArabic: String,
    val textEnglish: String,
    val textMalay: String,
    val matchType: String // "Surah", "Ayah Number", "Arabic", "Translation"
)

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
    val repeatCountSetting: Int = 1, // 1x, 3x, 5x, or 999 for infinite
    val repeatCurrentCount: Int = 0,
    val autoNextVerse: Boolean = true,
    val reciterName: String = "Mishary Rashid Alafasy",
    val reciterId: String = "alafasy",
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
    val tajwidCategory: String? = null, // "MAD", "IKHFA", "IDGHAM", "IQLAB", "QALQALAH", "GHUNNAH"
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
    val surahName: String,
    val verseNumber: Int,
    val snippetArabic: String,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPrivate: Boolean = true
)

data class QuranSettings(
    val arabicFontSizeSp: Float = 26f,
    val arabicFontStyle: String = "Uthmani (Madinah)", // "Uthmani (Madinah)", "Indopak Script", "Amiri Modern"
    val lineSpacing: QuranLineSpacing = QuranLineSpacing.NORMAL,
    val readingMode: ReadingDisplayMode = ReadingDisplayMode.ARABIC_EN,
    val showTranslation: Boolean = true,
    val showTransliteration: Boolean = true,
    val showEnglishTranslation: Boolean = true,
    val showMalayTranslation: Boolean = false,
    val showWordByWord: Boolean = false,
    val showTajwidColors: Boolean = true,
    val translationLanguage: String = "English", // "English", "Bahasa Melayu", "Arabic", "Dual (EN + BM)"
    val reciterName: String = "Mishary Rashid Alafasy",
    val selectedReciter: String = "Mishary Rashid Alafasy",
    val defaultPlaybackSpeed: Float = 1.0f,
    val autoPlayNextAyah: Boolean = true,
    val defaultRepeatMode: AudioRepeatMode = AudioRepeatMode.OFF,
    val autoScrollAudio: Boolean = true,
    val favoriteReciters: Set<String> = setOf("Mishary Rashid Alafasy", "Abdul Rahman Al-Sudais")
)
