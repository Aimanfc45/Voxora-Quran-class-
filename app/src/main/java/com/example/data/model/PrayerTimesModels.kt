package com.example.data.model

enum class PrayerName(
    val englishName: String,
    val arabicName: String,
    val malayName: String,
    val iconEmoji: String,
    val isObligatory: Boolean
) {
    FAJR("Fajr", "الفجر", "Subuh", "🌅", true),
    SUNRISE("Sunrise", "الشروق", "Syuruk", "☀️", false),
    DHUHR("Dhuhr", "الظهر", "Zohor", "☀️", true),
    ASR("Asr", "العصر", "Asar", "🌤️", true),
    MAGHRIB("Maghrib", "المغرب", "Maghrib", "🌇", true),
    ISHA("Isha", "العشاء", "Isyak", "🌙", true)
}

data class PrayerSlot(
    val name: PrayerName,
    val englishName: String = name.englishName,
    val arabicName: String = name.arabicName,
    val malayName: String = name.malayName,
    val time24: String, // "05:42"
    val time12: String, // "5:42 AM"
    val hour: Int,
    val minute: Int,
    val isPassed: Boolean = false,
    val isCurrent: Boolean = false,
    val isNext: Boolean = false,
    val iconEmoji: String = name.iconEmoji
)

data class PrayerSchedule(
    val dateFormatted: String, // "Tuesday, 1 September 2026"
    val hijriFormatted: String, // "19 Safar 1448H"
    val locationName: String, // "Kuala Lumpur, Malaysia"
    val zoneCode: String, // "WLY01"
    val slots: List<PrayerSlot>,
    val lastUpdatedFormatted: String = "Just now",
    val isUsingCachedData: Boolean = false,
    val isUnavailable: Boolean = false
) {
    val fajr: PrayerSlot get() = slots.firstOrNull { it.name == PrayerName.FAJR } ?: slots[0]
    val sunrise: PrayerSlot get() = slots.firstOrNull { it.name == PrayerName.SUNRISE } ?: slots[1]
    val dhuhr: PrayerSlot get() = slots.firstOrNull { it.name == PrayerName.DHUHR } ?: slots[2]
    val asr: PrayerSlot get() = slots.firstOrNull { it.name == PrayerName.ASR } ?: slots[3]
    val maghrib: PrayerSlot get() = slots.firstOrNull { it.name == PrayerName.MAGHRIB } ?: slots[4]
    val isha: PrayerSlot get() = slots.firstOrNull { it.name == PrayerName.ISHA } ?: slots[5]
}

data class PrayerCountdownState(
    val currentTimeFormatted: String, // "03:25:40 AM"
    val currentDateFormatted: String, // "Tuesday, 1 September"
    val currentDayFormatted: String, // "Tuesday"
    val currentPrayer: PrayerSlot?,
    val nextPrayer: PrayerSlot,
    val formattedCountdown: String, // "01:24:32 remaining"
    val remainingSeconds: Long,
    val progressFraction: Float, // 0.0f to 1.0f
    val schedule: PrayerSchedule,
    val lastUpdatedFormatted: String = schedule.lastUpdatedFormatted,
    val isUsingCachedData: Boolean = schedule.isUsingCachedData,
    val isUnavailable: Boolean = schedule.isUnavailable
)

data class MalaysianZone(
    val code: String,
    val state: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

object MalaysianZonesCatalog {
    val zones = listOf(
        // Wilayah Persekutuan
        MalaysianZone("WLY01", "Wilayah Persekutuan", "Kuala Lumpur & Putrajaya", 3.1390, 101.6869),
        MalaysianZone("WLY02", "Wilayah Persekutuan", "Labuan", 5.2831, 115.2308),
        
        // Selangor
        MalaysianZone("SGR01", "Selangor", "Gombak, Petaling, Sepang, Hulu Langat, Hulu Selangor, Shah Alam", 3.0738, 101.5183),
        MalaysianZone("SGR02", "Selangor", "Kuala Selangor, Sabak Bernam", 3.3421, 101.2505),
        MalaysianZone("SGR03", "Selangor", "Klang, Kuala Langat", 3.0449, 101.4456),
        
        // Johor
        MalaysianZone("JHR01", "Johor", "Pulau Aur dan Pulau Pemanggil", 2.4490, 104.5150),
        MalaysianZone("JHR02", "Johor", "Johor Bahru, Kulai, Kota Tinggi", 1.4927, 103.7414),
        MalaysianZone("JHR03", "Johor", "Kluang, Pontian", 2.0251, 103.3328),
        MalaysianZone("JHR04", "Johor", "Batu Pahat, Muar, Segamat, Tangkak", 1.8548, 102.9325),
        
        // Pulau Pinang
        MalaysianZone("PNG01", "Pulau Pinang", "Seluruh Negeri Pulau Pinang (Georgetown, Butterworth, Seberang Perai)", 5.4141, 100.3288),
        
        // Perak
        MalaysianZone("PRK01", "Perak", "Tapah, Slim River, Tanjung Malim", 3.6833, 101.5167),
        MalaysianZone("PRK02", "Perak", "Ipoh, Batu Gajah, Kampar, Sungai Siput, Kuala Kangsar", 4.5975, 101.0901),
        MalaysianZone("PRK03", "Perak", "Pengkalan Hulu, Grik, Lenggong", 5.7000, 101.0000),
        MalaysianZone("PRK05", "Perak", "Teluk Intan, Bagan Datuk, Kampung Gajah", 4.0259, 101.0213),
        MalaysianZone("PRK06", "Perak", "Taiping, Larut, Matang, Selama, Bagan Serai, Parit Buntar", 4.8500, 100.7333),
        
        // Kedah
        MalaysianZone("KDH01", "Kedah", "Kota Setar, Alor Setar, Kubang Pasu, Pokok Sena", 6.1248, 100.3678),
        MalaysianZone("KDH02", "Kedah", "Kuala Muda, Yan, Pendang", 5.6433, 100.4905),
        MalaysianZone("KDH03", "Kedah", "Padang Terap, Sik", 6.0000, 100.7000),
        MalaysianZone("KDH04", "Kedah", "Baling", 5.6769, 100.9167),
        MalaysianZone("KDH05", "Kedah", "Bandar Baharu, Kulim", 5.3667, 100.5500),
        MalaysianZone("KDH06", "Kedah", "Pulau Langkawi", 6.3500, 99.8000),
        
        // Perlis
        MalaysianZone("PLS01", "Perlis", "Seluruh Negeri Perlis (Kangar, Arau, Padang Besar)", 6.4449, 100.2048),
        
        // Kelantan
        MalaysianZone("KTN01", "Kelantan", "Kota Bharu, Bachok, Pasir Puteh, Tumpat, Pasir Mas, Tanah Merah, Machang", 6.1254, 102.2381),
        MalaysianZone("KTN03", "Kelantan", "Gua Musang, Jeli, Dabong", 4.8833, 101.9667),
        
        // Terengganu
        MalaysianZone("TRG01", "Terengganu", "Kuala Terengganu, Marang, Kuala Nerus", 5.3117, 103.1324),
        MalaysianZone("TRG02", "Terengganu", "Besut, Setiu", 5.7500, 102.6000),
        MalaysianZone("TRG03", "Terengganu", "Hulu Terengganu", 5.0667, 102.9833),
        MalaysianZone("TRG04", "Terengganu", "Dungun, Kemaman", 4.7750, 103.4167),
        
        // Pahang
        MalaysianZone("PHG01", "Pahang", "Pulau Tioman", 2.7833, 104.1667),
        MalaysianZone("PHG02", "Pahang", "Kuantan, Pekan, Rompin, Muadzam Shah", 3.8077, 103.3260),
        MalaysianZone("PHG03", "Pahang", "Jerantut, Temerloh, Maran, Bera, Chenor, Jengka", 3.4833, 102.4167),
        MalaysianZone("PHG04", "Pahang", "Bentong, Lipis, Raub", 3.5167, 101.9000),
        MalaysianZone("PHG05", "Pahang", "Genting Sempah, Bukit Tinggi, Janda Baik", 3.3500, 101.8000),
        MalaysianZone("PHG06", "Pahang", "Cameron Highlands, Genting Highlands, Bukit Fraser", 4.4714, 101.3789),
        
        // Negeri Sembilan
        MalaysianZone("NSN01", "Negeri Sembilan", "Tampin, Jempol", 2.4701, 102.2302),
        MalaysianZone("NSN02", "Negeri Sembilan", "Seremban, Port Dickson, Rembau, Jelebu, Kuala Pilah", 2.7258, 101.9424),
        
        // Melaka
        MalaysianZone("MLK01", "Melaka", "Seluruh Negeri Melaka (Bandar Melaka, Alor Gajah, Jasin)", 2.1896, 102.2501),
        
        // Sabah
        MalaysianZone("SBH01", "Sabah", "Kota Kinabalu, Ranau, Kota Belud, Tuaran, Penampang, Papar, Putatan", 5.9804, 116.0735),
        MalaysianZone("SBH02", "Sabah", "Sandakan, Beluran, Telupid, Pinangah, Terusan, Kuamut", 5.8402, 118.1179),
        MalaysianZone("SBH03", "Sabah", "Lahad Datu, Silabukan, Kunak, Sahabat, Semporna, Tungku", 5.0268, 118.3270),
        MalaysianZone("SBH04", "Sabah", "Tawau, Balung, Merotai, Kalabakan", 4.2498, 117.8871),
        MalaysianZone("SBH05", "Sabah", "Kudat, Kota Marudu, Pitas, Pulau Banggi", 6.8837, 116.8475),
        MalaysianZone("SBH06", "Sabah", "Gunung Kinabalu", 6.0753, 116.5583),
        MalaysianZone("SBH07", "Sabah", "Beaufort, Kuala Penyu, Sipitang, Tenom, Long Pa Sia, Membakut, Weston", 5.3473, 115.7455),
        MalaysianZone("SBH08", "Sabah", "Keningau, Tambunan, Nabawan", 5.3436, 116.1601),
        
        // Sarawak
        MalaysianZone("SWK01", "Sarawak", "Limbang, Lawas, Sundar, Trusan", 4.7500, 115.0000),
        MalaysianZone("SWK02", "Sarawak", "Miri, Niah, Bekenu, Sibuti, Marudi", 4.3995, 113.9914),
        MalaysianZone("SWK03", "Sarawak", "Pandan, Belaga, Suai, Tatau, Sebauh, Bintulu", 3.1667, 113.0333),
        MalaysianZone("SWK04", "Sarawak", "Sibu, Mukah, Dalat, Song, Igan, Oya, Balingian, Kanowit, Kapit", 2.3000, 111.8167),
        MalaysianZone("SWK05", "Sarawak", "Sarikei, Matu, Julau, Rajang, Daro, Bintangor, Belawai", 2.1167, 111.5167),
        MalaysianZone("SWK06", "Sarawak", "Lubok Antu, Sri Aman, Roban, Debak, Betong, Saratuk, Kabong", 1.2333, 111.4667),
        MalaysianZone("SWK07", "Sarawak", "Serian, Simunjan, Samarahan, Sebuyau, Meludam", 1.1667, 110.5667),
        MalaysianZone("SWK08", "Sarawak", "Kuching, Bau, Lundu, Sematan", 1.5533, 110.3592)
    )
}

data class PrayerLocation(
    val name: String,
    val state: String,
    val zoneCode: String,
    val latitude: Double,
    val longitude: Double,
    val isAutoLocation: Boolean = false
)

data class DailySalahProgress(
    val dateKey: String, // "2026-09-01"
    val fajrCompleted: Boolean = false,
    val dhuhrCompleted: Boolean = false,
    val asrCompleted: Boolean = false,
    val maghribCompleted: Boolean = false,
    val ishaCompleted: Boolean = false
) {
    val completedCount: Int get() = listOf(fajrCompleted, dhuhrCompleted, asrCompleted, maghribCompleted, ishaCompleted).count { it }
    val progressFraction: Float get() = completedCount.toFloat() / 5.0f
}

data class SalahLearningProgress(
    val completedStepIds: Set<Int> = emptySet(),
    val totalSteps: Int = 9,
    val completedPrayersCount: Int = 0,
    val lastPracticedPrayer: String = "Fajr"
)
