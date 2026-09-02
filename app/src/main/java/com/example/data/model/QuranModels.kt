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
    COMPACT(1.3f, "Compact"),
    NORMAL(1.6f, "Standard"),
    RELAXED(2.0f, "Relaxed")
}

enum class QuranReadingTheme(
    val label: String,
    val backgroundHex: Long,
    val cardBackgroundHex: Long,
    val textPrimaryHex: Long,
    val textSecondaryHex: Long,
    val isDark: Boolean
) {
    EMERALD("Emerald Classic", 0xFF022C22, 0xFF064E3B, 0xFFFFFFFF, 0xFFA7F3D0, true),
    NIGHT_AMOLED("Night AMOLED", 0xFF000000, 0xFF111827, 0xFFF9FAFB, 0xFF9CA3AF, true),
    SEPIA_WARM("Warm Sepia", 0xFFFDF6E2, 0xFFF4E8C1, 0xFF2D2A26, 0xFF78716C, false),
    CRISP_LIGHT("Crisp Day", 0xFFF8FAFC, 0xFFFFFFFF, 0xFF0F172A, 0xFF64748B, false)
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
    val isDefault: Boolean = false,
    val previewVerseUrl: String = "",
    val description: String = "Certified master Qari with high-fidelity Murattal audio."
) {
    val verifiedPreviewUrl: String
        get() = if (previewVerseUrl.isNotBlank()) previewVerseUrl else "https://everyayah.com/data/$audioFolder/001001.mp3"
}

data class TajwidRule(
    val id: String,
    val name: String,
    val arabicName: String,
    val category: String, // "Mad", "Ikhfa", "Idgham", "Iqlab", "Qalqalah", "Ghunnah"
    val colorHex: Long,
    val harakatCount: String, // e.g. "2 Harakat", "6 Harakat", "2-Count Nasal Ghunnah"
    val description: String,
    val ruleSummary: String,
    val exampleArabic: String,
    val exampleTransliteration: String,
    val verseReference: String,
    val pronunciationTip: String,
    val hasVerifiedAudio: Boolean = true
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
    val audioUrl: String = "",
    val sleepTimerMinutes: Int = 0, // 0 = Off, 15, 30, 45, 60, -1 = End of Surah
    val sleepTimerRemainingSeconds: Int = 0,
    val isPreviewPlaying: Boolean = false,
    val previewReciterName: String? = null
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

data class VerseHighlight(
    val id: String = "",
    val surahNumber: Int,
    val verseNumber: Int,
    val colorHex: Long = 0xFFF59E0B, // 0xFFF59E0B (Gold), 0xFF10B981 (Emerald), 0xFF06B6D4 (Cyan), 0xFFF43F5E (Coral), 0xFF8B5CF6 (Purple)
    val timestamp: Long = System.currentTimeMillis()
)

data class VerseTafsir(
    val surahNumber: Int,
    val verseNumber: Int,
    val surahName: String,
    val textArabic: String,
    val transliteration: String,
    val translationMalay: String,
    val translationEnglish: String,
    val tafsirSummaryMalay: String,
    val tafsirSummaryEnglish: String,
    val sourceName: String = "Tafsir Ringkas JAKIM & Sahih International",
    val keyThemes: List<String> = emptyList(),
    val revelationContext: String? = null
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
    val title: String = "",
    val note: String = "",
    val category: String = "Favourite Verses" // "Ramadan", "Memorization", "Favourite Verses", "Daily Tilawah", "Reflections"
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
    val readingMode: ReadingDisplayMode = ReadingDisplayMode.MULTI_TRANSLATION,
    val readingTheme: QuranReadingTheme = QuranReadingTheme.EMERALD,
    val readingBrightness: Float = 1.0f,
    val showTranslation: Boolean = true,
    val showTransliteration: Boolean = true,
    val showEnglishTranslation: Boolean = true,
    val showMalayTranslation: Boolean = true,
    val showTafsir: Boolean = true,
    val showWordByWord: Boolean = false,
    val showTajwidColors: Boolean = true,
    val translationLanguage: String = "Dual (EN + BM)", // "English", "Bahasa Melayu", "Arabic", "Dual (EN + BM)"
    val reciterName: String = "Mishary Rashid Alafasy",
    val selectedReciter: String = "Mishary Rashid Alafasy",
    val defaultReciter: String = "Mishary Rashid Alafasy",
    val defaultPlaybackSpeed: Float = 1.0f,
    val autoPlayNextAyah: Boolean = true,
    val defaultRepeatMode: AudioRepeatMode = AudioRepeatMode.OFF,
    val autoScrollAudio: Boolean = true,
    val favoriteReciters: Set<String> = setOf("Mishary Rashid Alafasy", "Abdul Rahman Al-Sudais", "Mahmoud Khalil Al-Husary")
)
