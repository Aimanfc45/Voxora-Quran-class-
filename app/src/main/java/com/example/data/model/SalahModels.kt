package com.example.data.model

enum class PrayerType(
    val englishName: String,
    val arabicName: String,
    val rakaatCount: Int,
    val timeOfDay: String,
    val iconEmoji: String,
    val description: String
) {
    FAJR("Fajr", "الفجر", 2, "Dawn (Before Sunrise)", "🌅", "2 Raka'at Sunnah + 2 Raka'at Fardh with audible recitation."),
    DHUHR("Dhuhr", "الظهر", 4, "Midday (After Sun Zenith)", "☀️", "4 Raka'at Fardh with silent recitation in all raka'at."),
    ASR("Asr", "العصر", 4, "Late Afternoon", "🌤️", "4 Raka'at Fardh with silent recitation."),
    MAGHRIB("Maghrib", "المغرب", 3, "Sunset", "🌇", "3 Raka'at Fardh (first 2 audible, 3rd silent)."),
    ISHA("Isha", "العشاء", 4, "Night", "🌙", "4 Raka'at Fardh (first 2 audible, last 2 silent).")
}

data class SalahStep(
    val stepNumber: Int,
    val stepCode: String, // e.g. "01", "02"
    val titleEnglish: String,
    val titleArabic: String,
    val transliteration: String,
    val postureName: String,
    val postureDescription: String,
    val arabicDua: String,
    val translationEnglish: String,
    val translationMalay: String,
    val focusTip: String,
    val isAudioAvailable: Boolean = false,
    val visualGuidePlaceholder: String = "Postural visual diagram coming soon"
)

object SalahDataCatalog {

    val canonicalSteps: List<SalahStep> = listOf(
        SalahStep(
            stepNumber = 1,
            stepCode = "01",
            titleEnglish = "Intention (An-Niyyah)",
            titleArabic = "الـنِّـيَّـةُ",
            transliteration = "An-Niyyah",
            postureName = "Standing upright facing Qiblah",
            postureDescription = "Stand serenely and consciously form the intention in your heart to pray the specific Fardh or Sunnah prayer solely for the sake of Allah.",
            arabicDua = "نَوَيْتُ أَنْ أُصَلِّيَ فَرْضَ ... رَكَعَاتٍ لِلَّهِ تَعَالَى",
            translationEnglish = "I intend to perform the obligatory prayer for the sake of Allah Almighty alone.",
            translationMalay = "Sahaja aku menunaikan solat fardu ... kerana Allah Ta'ala.",
            focusTip = "Focus your heart and eliminate all worldly distractions before commencing."
        ),
        SalahStep(
            stepNumber = 2,
            stepCode = "02",
            titleEnglish = "Opening Takbir (Takbiratul Ihram)",
            titleArabic = "تَكْبِيرَةُ الإِحْرَامِ",
            transliteration = "Allāhu Akbar",
            postureName = "Hands raised to earlobes or shoulder height",
            postureDescription = "Raise both hands with palms facing the Qiblah up to shoulder or earlobe level while pronouncing the Takbir.",
            arabicDua = "اللَّهُ أَكْبَرُ",
            translationEnglish = "Allah is the Greatest.",
            translationMalay = "Allah Maha Besar.",
            focusTip = "This marks entering the sacred state of Salah; worldly speech and actions become prohibited."
        ),
        SalahStep(
            stepNumber = 3,
            stepCode = "03",
            titleEnglish = "Standing & Recitation (Al-Qiyam)",
            titleArabic = "القِيَامُ وَالقِرَاءَةُ",
            transliteration = "Al-Qiyām wa Al-Qirā'ah",
            postureName = "Standing with right hand over left wrist on chest/navel",
            postureDescription = "Gaze lowered to the place of prostration. Recite the Opening Dua (Du'a al-Istiftah), Surah Al-Fatihah, and an additional Surah or verses from the Holy Quran.",
            arabicDua = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ...",
            translationEnglish = "In the name of Allah, the Entirely Merciful, the Especially Merciful. All praise is due to Allah, Lord of the worlds...",
            translationMalay = "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang. Segala puji bagi Allah, Tuhan sekalian alam...",
            focusTip = "Recite calmly with proper Tajwid, pondering the meanings of the verses."
        ),
        SalahStep(
            stepNumber = 4,
            stepCode = "04",
            titleEnglish = "Bowing (Ar-Ruku')",
            titleArabic = "الرُّكُوعُ",
            transliteration = "Subḥāna Rabbiyal-'Aẓīm",
            postureName = "Back straight horizontal, hands resting firmly on knees",
            postureDescription = "Bow at 90 degrees with a flat back, head level with spine, and fingers spread grasping the kneecaps. Recite the tasbih 3 times.",
            arabicDua = "سُبْحَانَ رَبِّيَ الْعَظِيمِ وَبِحَمْدِهِ",
            translationEnglish = "Glory be to my Lord, the Magnificent, and praise be to Him.",
            translationMalay = "Maha Suci Tuhanku Yang Maha Agung dan segala puji bagi-Nya.",
            focusTip = "Achieve full composure (Tuma'ninah) before rising."
        ),
        SalahStep(
            stepNumber = 5,
            stepCode = "05",
            titleEnglish = "Standing after Bowing (Al-I'tidal)",
            titleArabic = "الإِعْتِدَالُ",
            transliteration = "Sami'allāhu liman ḥamidah • Rabbanā lakal-ḥamd",
            postureName = "Standing upright with arms naturally at sides or folded",
            postureDescription = "Rise from Ruku' to a fully upright standing posture with tranquility.",
            arabicDua = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ • رَبَّنَا وَلَكَ الْحَمْدُ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
            translationEnglish = "Allah hears whoever praises Him. Our Lord, all praise belongs to You, abundant, wholesome, and blessed.",
            translationMalay = "Allah mendengar orang yang memuji-Nya. Wahai Tuhan kami, bagi-Mu segala pujian yang banyak, baik lagi diberkati.",
            focusTip = "Stand still and express gratitude with a present heart."
        ),
        SalahStep(
            stepNumber = 6,
            stepCode = "06",
            titleEnglish = "Prostration (As-Sujud)",
            titleArabic = "السُّجُودُ",
            transliteration = "Subḥāna Rabbiyal-A'lā",
            postureName = "Seven limbs touching ground: forehead/nose, palms, knees, toes",
            postureDescription = "Place knees, then palms, forehead and nose firmly on the ground. Elbows elevated. Recite the tasbih 3 times.",
            arabicDua = "سُبْحَانَ رَبِّيَ الأَعْلَى وَبِحَمْدِهِ",
            translationEnglish = "Glory be to my Lord, the Most High, and praise be to Him.",
            translationMalay = "Maha Suci Tuhanku Yang Maha Tinggi dan segala puji bagi-Nya.",
            focusTip = "The closest a servant is to their Lord is in prostration. Supplicate sincerely."
        ),
        SalahStep(
            stepNumber = 7,
            stepCode = "07",
            titleEnglish = "Sitting Between Two Prostrations (Al-Julus)",
            titleArabic = "الجُلُوسُ بَيْنَ السَّجْدَتَيْنِ",
            transliteration = "Rabbi-ghfir lī",
            postureName = "Sitting upright upon left foot with right foot upright (Iftirash)",
            postureDescription = "Rise from first Sujud to a composed seated posture. Rest hands on thighs/knees.",
            arabicDua = "رَبِّ اغْفِرْ لِي وَارْحَمْنِي وَاجْبُرْنِي وَارْفَعْنِي وَارْزُقْنِي وَاهْدِنِي وَعَافِنِي",
            translationEnglish = "O my Lord, forgive me, have mercy on me, mend my shortcomings, elevate me, provide for me, guide me, and grant me well-being.",
            translationMalay = "Ya Tuhanku, ampunilah aku, rahmatilah aku, cukupkanlah kekuranganku, angkatlah darjatku, kurniakanlah rezeki, bimbinglah aku, dan afiatkanlah aku.",
            focusTip = "Maintain stillness (Tuma'ninah) before descending into the second Sujud."
        ),
        SalahStep(
            stepNumber = 8,
            stepCode = "08",
            titleEnglish = "Testimony of Faith (At-Tashahhud)",
            titleArabic = "التَّشَهُّدُ الأَخِيرُ",
            transliteration = "At-Taḥiyyātu lillāh",
            postureName = "Tawarruk sitting (in final raka'ah) with right index finger raised during testimony",
            postureDescription = "Recite the greetings to Allah, peace upon the Prophet (peace be upon him), the Shahadah, and the Ibrahimic salutations (Selawat Ibrahimiyyah).",
            arabicDua = "التَّحِيَّاتُ الْمُبَارَكَاتُ الصَّلَوَاتُ الطَّيِّبَاتُ لِلَّهِ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ...",
            translationEnglish = "All blessed compliments, prayers, and pure things are for Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings...",
            translationMalay = "Segala penghormatan yang diberkati dan doa yang baik adalah untuk Allah. Sejahteralah ke atasmu wahai Nabi, dan rahmat Allah serta berkat-Nya...",
            focusTip = "Raise your right index finger with reverence upon declaring the Oneness of Allah."
        ),
        SalahStep(
            stepNumber = 9,
            stepCode = "09",
            titleEnglish = "Concluding Salutations (At-Taslim)",
            titleArabic = "التَّسْلِيمُ",
            transliteration = "As-Salāmu 'alaykum wa raḥmatullāh",
            postureName = "Turn head to right shoulder, then to left shoulder",
            postureDescription = "Conclude the prayer by turning the face to the right saying the Salam, then turning to the left.",
            arabicDua = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ",
            translationEnglish = "May the peace, mercy, and blessings of Allah be upon you all.",
            translationMalay = "Semoga kesejahteraan, rahmat Allah, dan keberkatan-Nya terlimpah ke atas kamu.",
            focusTip = "Reflect that you are concluding the sacred audience with your Creator with peace."
        )
    )
}
