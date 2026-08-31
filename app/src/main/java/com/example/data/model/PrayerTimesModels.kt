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
    val slots: List<PrayerSlot>
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
    val schedule: PrayerSchedule
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
        MalaysianZone("WLY01", "Wilayah Persekutuan", "Kuala Lumpur & Putrajaya", 3.1390, 101.6869),
        MalaysianZone("WLY02", "Wilayah Persekutuan", "Labuan", 5.2831, 115.2308),
        MalaysianZone("SGR01", "Selangor", "Shah Alam, Petaling, Klang, Gombak, Sepang", 3.0738, 101.5183),
        MalaysianZone("SGR02", "Selangor", "Kuala Selangor, Sabak Bernam", 3.3421, 101.2505),
        MalaysianZone("SGR03", "Selangor", "Hulu Selangor, Rawang", 3.5500, 101.6500),
        MalaysianZone("JHR02", "Johor", "Johor Bahru, Kulai, Kota Tinggi", 1.4927, 103.7414),
        MalaysianZone("JHR01", "Johor", "Pulau Aur dan Pulau Pemanggil", 2.4490, 104.5150),
        MalaysianZone("JHR03", "Johor", "Kluang, Pontian", 2.0251, 103.3328),
        MalaysianZone("PNG01", "Pulau Pinang", "Seluruh Negeri Pulau Pinang (Georgetown, Butterworth)", 5.4141, 100.3288),
        MalaysianZone("PRK02", "Perak", "Ipoh, Batu Gajah, Kampar, Sungai Siput", 4.5975, 101.0901),
        MalaysianZone("KDH01", "Kedah", "Kota Setar, Alor Setar, Kubang Pasu, Pokok Sena", 6.1248, 100.3678),
        MalaysianZone("KDH06", "Kedah", "Pulau Langkawi", 6.3500, 99.8000),
        MalaysianZone("TRG01", "Terengganu", "Kuala Terengganu, Marang, Kuala Nerus", 5.3117, 103.1324),
        MalaysianZone("KTN01", "Kelantan", "Kota Bharu, Bachok, Pasir Puteh, Tumpat", 6.1254, 102.2381),
        MalaysianZone("MLK01", "Melaka", "Seluruh Negeri Melaka (Bandar Melaka, Alor Gajah)", 2.1896, 102.2501),
        MalaysianZone("NSN01", "Negeri Sembilan", "Tampin, Jempol", 2.4701, 102.2302),
        MalaysianZone("NSN02", "Negeri Sembilan", "Seremban, Port Dickson, Rembau", 2.7258, 101.9424),
        MalaysianZone("PHG02", "Pahang", "Kuantan, Pekan, Rompin", 3.8077, 103.3260),
        MalaysianZone("PLS01", "Perlis", "Seluruh Negeri Perlis (Kangar, Arau)", 6.4449, 100.2048),
        MalaysianZone("SBH01", "Sabah", "Kota Kinabalu, Ranau, Kota Belud, Tuaran, Penampang", 5.9804, 116.0735),
        MalaysianZone("SBH02", "Sabah", "Sandakan, Beluran, Telupid", 5.8402, 118.1179),
        MalaysianZone("SBH04", "Sabah", "Tawau, Lahad Datu, Semporna", 4.2498, 117.8871),
        MalaysianZone("SWK08", "Sarawak", "Kuching, Bau, Lundu, Samarahan", 1.5533, 110.3592),
        MalaysianZone("SWK04", "Sarawak", "Sibu, Mukah, Kanowit, Dalat", 2.3000, 111.8167),
        MalaysianZone("SWK02", "Sarawak", "Miri, Niah, Bekenu, Marudi", 4.3995, 113.9914)
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
