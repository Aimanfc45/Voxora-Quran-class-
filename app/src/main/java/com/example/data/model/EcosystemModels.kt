package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Voxora Muslim Centre Master Modes
 */
enum class VoxoraMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val tag: String,
    val description: String
) {
    HOME("home", "Muslim Centre", "Learn. Recite. Grow.", "🕌", "HUB", "Main ecosystem dashboard and daily spiritual overview"),
    QURAN("quran", "Reading Quran Mode", "Noble Quran & Tajwid", "📖", "QURAN", "Recite, listen, and reflect on the authentic Quranic verses"),
    SALAH("salah", "Salah Mode", "Prayer Times & Qiblah", "🕌", "SALAH", "Real-time prayer times, Athan alerts, and 3D Qiblah direction"),
    LEARNING("learning", "Learning Mode", "Classes & Tajwid Journey", "🎓", "ACADEMY", "Live classes, certified ustaz guidance, and structured Tajwid courses"),
    LIVE_CLASS("live_class", "Live Class Mode", "Interactive Halaqah & Studio", "🎥", "LIVE", "Live interactive audio-video practice room powered by LiveKit"),
    DHIKR("dhikr", "Dhikr Mode", "Digital Tasbih & Remembrance", "📿", "DHIKR", "Authentic morning, evening, and daily remembrance counters"),
    DUA("dua", "Dua Mode", "Supplications & Adhkar", "🤲", "DUA", "Categorized authentic supplications from Quran and Sunnah"),
    RAMADAN("ramadan", "Ramadan Mode", "Fasting & Khatam Tracker", "🌙", "RAMADAN", "Imsak, Iftar countdowns, daily fasting goals, and Tarawih tracker"),
    HAJJ_UMRAH("hajj_umrah", "Hajj & Umrah Mode", "Pilgrim Step-by-Step Guide", "🕋", "PILGRIMAGE", "Interactive Tawaf & Sa'i counters and step-by-step rituals"),
    MASJID("masjid", "Masjid Mode", "Mosque Discovery & Facilities", "🏛️", "MASJID", "Find nearby mosques, prayer facilities, and Jumu'ah times"),
    CALENDAR("calendar", "Islamic Calendar Mode", "Hijri Dates & Islamic Events", "📅", "CALENDAR", "Hijri calendar conversion and upcoming blessed Islamic dates"),
    PROFILE("profile", "Profile Mode", "Spiritual Journey & Settings", "👤", "PROFILE", "Personal spiritual achievements, streak tracking, and app customization")
}

// -------------------------------------------------------------
// DHIKR & TASBIH MODELS
// -------------------------------------------------------------
enum class DhikrType {
    GENERAL, MORNING, EVENING, AFTER_PRAYER, SPECIAL
}

data class DhikrItem(
    val id: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val targetCount: Int = 33,
    val type: DhikrType = DhikrType.GENERAL,
    val benefit: String = "",
    val reference: String = "Authentic Sunnah",
    val currentCount: Int = 0,
    val isCompleted: Boolean = false
)

// -------------------------------------------------------------
// DUA MODELS
// -------------------------------------------------------------
enum class DuaCategory(val displayName: String, val iconEmoji: String) {
    DAILY("Daily Life", "☀️"),
    MORNING_EVENING("Morning & Evening", "🌅"),
    AFTER_SALAH("After Salah", "🕌"),
    PROTECTION("Protection & Health", "🛡️"),
    TRAVEL("Travel & Journey", "🚗"),
    FORGIVENESS("Forgiveness & Guidance", "🤲"),
    RAMADAN("Ramadan & Fasting", "🌙"),
    PARENTS_FAMILY("Parents & Family", "👨‍👩‍👧")
}

data class DuaItem(
    val id: String,
    val title: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val category: DuaCategory,
    val reference: String,
    val occasion: String = "",
    val isBookmarked: Boolean = false
)

// -------------------------------------------------------------
// RAMADAN MODELS
// -------------------------------------------------------------
data class RamadanDayPlan(
    val dayNumber: Int,
    val hijriDate: String,
    val imsakTime: String,
    val iftarTime: String,
    val targetJuz: Int,
    val isFasted: Boolean = false,
    val isTarawihCompleted: Boolean = false,
    val dailyCharityDone: Boolean = false
)

data class RamadanStats(
    val daysCompleted: Int = 0,
    val totalDays: Int = 30,
    val pagesRecitedToday: Int = 0,
    val targetPagesPerDay: Int = 20,
    val tarawihCount: Int = 0,
    val charityDaysCount: Int = 0
)

// -------------------------------------------------------------
// HAJJ & UMRAH MODELS
// -------------------------------------------------------------
enum class PilgrimageType {
    UMRAH, HAJJ_TAMATTU, HAJJ_IFRAD, HAJJ_QIRAN
}

data class PilgrimageStep(
    val stepNumber: Int,
    val title: String,
    val arabicTitle: String,
    val location: String,
    val description: String,
    val importantNotes: List<String>,
    val duas: List<String>,
    val isCompleted: Boolean = false,
    val hasCounter: Boolean = false,
    val counterTarget: Int = 0,
    val counterCurrent: Int = 0
)

// -------------------------------------------------------------
// MASJID MODELS
// -------------------------------------------------------------
data class MasjidItem(
    val id: String,
    val name: String,
    val arabicName: String = "",
    val address: String,
    val city: String,
    val stateOrCountry: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double = 0.0,
    val capacity: Int = 1000,
    val hasWomenSection: Boolean = true,
    val hasParking: Boolean = true,
    val hasWheelchairAccess: Boolean = true,
    val hasAirConditioning: Boolean = true,
    val hasWudhuArea: Boolean = true,
    val nextPrayerName: String = "Dhuhr",
    val nextPrayerTime: String = "1:15 PM",
    val jumuahTime: String = "1:30 PM",
    val isFavorite: Boolean = false,
    val phone: String = ""
)

// -------------------------------------------------------------
// ISLAMIC CALENDAR MODELS
// -------------------------------------------------------------
data class IslamicEvent(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val hijriDate: String,
    val gregorianDate: String,
    val description: String,
    val isSunnahFasting: Boolean = false,
    val isMajorHoliday: Boolean = false,
    val daysRemaining: Int = 0
)

data class HijriMonthData(
    val monthNumber: Int,
    val monthNameArabic: String,
    val monthNameEnglish: String,
    val yearHijri: Int,
    val daysInMonth: Int = 30,
    val isSacredMonth: Boolean = false
)
