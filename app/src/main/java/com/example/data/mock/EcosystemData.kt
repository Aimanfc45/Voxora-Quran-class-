package com.example.data.mock

import com.example.data.model.*

object EcosystemData {

    // =========================================================================
    // 1. AUTHENTIC DHIKR LIST
    // =========================================================================
    val dhikrList = listOf(
        DhikrItem(
            id = "dhikr_tasbih_33",
            arabicText = "سُبْحَانَ اللَّهِ",
            transliteration = "SubhanAllah",
            translation = "Glory be to Allah",
            targetCount = 33,
            type = DhikrType.AFTER_PRAYER,
            benefit = "Whoever glorifies Allah 33 times after each prayer will have their sins forgiven even if they were like the foam of the sea.",
            reference = "Sahih Muslim 597"
        ),
        DhikrItem(
            id = "dhikr_tahmid_33",
            arabicText = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            translation = "Praise be to Allah",
            targetCount = 33,
            type = DhikrType.AFTER_PRAYER,
            benefit = "Fills the scale with good deeds on the Day of Resurrection.",
            reference = "Sahih Muslim 223"
        ),
        DhikrItem(
            id = "dhikr_takbir_34",
            arabicText = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            translation = "Allah is the Greatest",
            targetCount = 34,
            type = DhikrType.AFTER_PRAYER,
            benefit = "Completes the 100 post-prayer glorifications before concluding with the Tawheed.",
            reference = "Sahih Muslim 597"
        ),
        DhikrItem(
            id = "dhikr_tahlil_100",
            arabicText = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illallah wahdahu la sharika lahu, lahul mulku wa lahul hamdu, wa Huwa 'ala kulli shay'in Qadir",
            translation = "There is no deity worthy of worship except Allah alone, without partner. To Him belongs the dominion, and to Him belongs all praise, and He has power over all things.",
            targetCount = 100,
            type = DhikrType.MORNING,
            benefit = "Whoever says this 100 times in a day will have a reward equal to freeing 10 slaves, 100 good deeds recorded, and 100 sins erased.",
            reference = "Sahih al-Bukhari 3293"
        ),
        DhikrItem(
            id = "dhikr_istighfar_100",
            arabicText = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            transliteration = "Astaghfirullah wa atoobu ilayh",
            translation = "I seek forgiveness from Allah and repent unto Him",
            targetCount = 100,
            type = DhikrType.GENERAL,
            benefit = "The Prophet (ﷺ) said: 'By Allah, I seek Allah's forgiveness and turn to Him in repentance more than seventy times a day.'",
            reference = "Sahih al-Bukhari 6307"
        ),
        DhikrItem(
            id = "dhikr_subhanallahi_wa_bihamdihi",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "SubhanAllahi wa bihamdihi, SubhanAllahil 'Azeem",
            translation = "Glory be to Allah and all praise is His; Glory be to Allah the Most Great",
            targetCount = 100,
            type = DhikrType.MORNING,
            benefit = "Two words are light on the tongue, heavy on the Balance, and beloved to the Most Merciful.",
            reference = "Sahih al-Bukhari 6682"
        ),
        DhikrItem(
            id = "dhikr_hawqalah",
            arabicText = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            translation = "There is no power and no strength except with Allah",
            targetCount = 33,
            type = DhikrType.GENERAL,
            benefit = "One of the treasures of Paradise (Kanz min kunuz al-Jannah).",
            reference = "Sahih al-Bukhari 4205"
        ),
        DhikrItem(
            id = "dhikr_salawat_10",
            arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ",
            transliteration = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammad",
            translation = "O Allah, bestow Your blessings upon Muhammad and upon the family of Muhammad",
            targetCount = 10,
            type = DhikrType.GENERAL,
            benefit = "Whoever sends blessings upon the Prophet once, Allah will send blessings upon him tenfold.",
            reference = "Sahih Muslim 408"
        ),
        DhikrItem(
            id = "dhikr_sayyidul_istighfar",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduk, wa ana 'ala 'ahdika wa wa'dika mastata't, a'udhu bika min sharri ma sana't, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa'innahu la yaghfiru-dhunuba illa Ant",
            translation = "O Allah, You are my Lord; none has the right to be worshipped but You. You created me and I am Your slave, and I abide by Your covenant and promise as best as I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favors upon me, and I acknowledge my sin; so forgive me, for none forgives sins except You.",
            targetCount = 1,
            type = DhikrType.MORNING,
            benefit = "Master of seeking forgiveness. Whoever says it with firm faith in the morning/evening and dies on that day/night enters Jannah.",
            reference = "Sahih al-Bukhari 6306"
        )
    )

    // =========================================================================
    // 2. AUTHENTIC DUA LIBRARY
    // =========================================================================
    val duaList = listOf(
        DuaItem(
            id = "dua_rabbana_atina",
            title = "Dua for Goodness in Both Worlds",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar",
            translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good and protect us from the punishment of the Fire.",
            category = DuaCategory.DAILY,
            reference = "Surah Al-Baqarah (2:201)",
            occasion = "General prayer & at Tawaf between Rukn al-Yamani and Hajar al-Aswad"
        ),
        DuaItem(
            id = "dua_waking_up",
            title = "Dua Upon Waking Up",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushoor",
            translation = "All praise is for Allah who gave us life after having taken it from us and unto Him is the resurrection.",
            category = DuaCategory.DAILY,
            reference = "Sahih al-Bukhari 6312",
            occasion = "Every morning upon opening eyes"
        ),
        DuaItem(
            id = "dua_before_sleep",
            title = "Dua Before Sleeping",
            arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            transliteration = "Bismika Allahumma amootu wa ahya",
            translation = "In Your Name, O Allah, I die and I live.",
            category = DuaCategory.DAILY,
            reference = "Sahih al-Bukhari 6324",
            occasion = "Upon lying down to sleep"
        ),
        DuaItem(
            id = "dua_parents",
            title = "Dua for Parents' Mercy",
            arabicText = "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-hamhuma kama rabbayani sagheera",
            translation = "My Lord, have mercy upon them as they brought me up when I was small.",
            category = DuaCategory.PARENTS_FAMILY,
            reference = "Surah Al-Isra (17:24)",
            occasion = "Daily prayers for mother and father"
        ),
        DuaItem(
            id = "dua_leaving_home",
            title = "Dua When Leaving the House",
            arabicText = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Bismillahi tawakkaltu 'alallah, wa la hawla wa la quwwata illa billah",
            translation = "In the name of Allah, I place my trust in Allah, and there is no might nor power except with Allah.",
            category = DuaCategory.TRAVEL,
            reference = "Sunan Abi Dawud 5095",
            occasion = "Stepping out through the doorway"
        ),
        DuaItem(
            id = "dua_travel_mount",
            title = "Dua for Travel & Vehicles",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ",
            transliteration = "Subhanal-ladhi sakh-khara lana hadha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon",
            translation = "Glory to Him who has brought this under our control, though we could not have done it by ourselves, and indeed, to our Lord we will return.",
            category = DuaCategory.TRAVEL,
            reference = "Surah Az-Zukhruf (43:13-14) / Sahih Muslim 1342",
            occasion = "Starting a vehicle journey"
        ),
        DuaItem(
            id = "dua_iftar_fasting",
            title = "Dua When Breaking the Fast (Iftar)",
            arabicText = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ",
            transliteration = "Dhahaba adh-dhama'u wabtallatil-'urooqu wa thabatal-ajru in sha Allah",
            translation = "The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.",
            category = DuaCategory.RAMADAN,
            reference = "Sunan Abi Dawud 2357",
            occasion = "At Maghrib when taking the first date or water"
        ),
        DuaItem(
            id = "dua_laylatul_qadr",
            title = "Dua for Laylatul Qadr (Night of Decree)",
            arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            transliteration = "Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'anni",
            translation = "O Allah, You are Most Forgiving, and You love forgiveness; so forgive me.",
            category = DuaCategory.RAMADAN,
            reference = "Jami` at-Tirmidhi 3513 (Reported by Aisha R.A.)",
            occasion = "During the last ten nights of Ramadan"
        ),
        DuaItem(
            id = "dua_anxiety_distress",
            title = "Dua for Relief from Anxiety and Sorrow",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allahumma inni a'udhu bika minal-hammi wal-hazan, wal-'ajzi wal-kasal, wal-bukhli wal-jubn, wa dala'id-dayni wa ghalabatir-rijal",
            translation = "O Allah, I seek refuge in You from grief and sadness, from weakness and laziness, from stinginess and cowardice, from the burden of debt and from being overpowered by people.",
            category = DuaCategory.PROTECTION,
            reference = "Sahih al-Bukhari 6369",
            occasion = "During times of hardship, stress, or morning/evening"
        ),
        DuaItem(
            id = "dua_protection_evil",
            title = "Dua for Complete Protection Against Harm",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Samee'ul-'Aleem",
            translation = "In the Name of Allah, with Whose Name nothing on the earth or in the heavens can cause harm, and He is the All-Hearing, the All-Knowing.",
            category = DuaCategory.PROTECTION,
            reference = "Sunan Abi Dawud 5088 / Jami` at-Tirmidhi 3388 (Recite 3x)",
            occasion = "Recited 3 times every morning and evening"
        ),
        DuaItem(
            id = "dua_after_salah_ayatal_kursi",
            title = "Ayat al-Kursi (Post-Prayer Protection)",
            arabicText = "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ...",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum. La ta'khudhuhu sinatun wa la nawm...",
            translation = "Allah! There is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep...",
            category = DuaCategory.AFTER_SALAH,
            reference = "Surah Al-Baqarah (2:255) / Sunan an-Nasa'i Al-Kubra 9928",
            occasion = "Recited after every compulsory (Fard) prayer"
        ),
        DuaItem(
            id = "dua_younus_relief",
            title = "Dua of Prophet Yunus (A.S.) in Distress",
            arabicText = "لَّا إِلَهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            transliteration = "La ilaha illa Anta subhanaka inni kuntu minadh-dhalimeen",
            translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            category = DuaCategory.FORGIVENESS,
            reference = "Surah Al-Anbiya (21:87) / Jami` at-Tirmidhi 3505",
            occasion = "Whenever a Muslim supplicates with it in distress, Allah answers."
        )
    )

    // =========================================================================
    // 3. AUTHENTIC HAJJ & UMRAH GUIDES
    // =========================================================================
    val umrahSteps = listOf(
        PilgrimageStep(
            stepNumber = 1,
            title = "Ihram & Niyyah at Miqat",
            arabicTitle = "الإِحْرَامُ وَالنِّيَّةُ",
            location = "At designated Miqat (e.g., Dhu'l-Hulayfah, Yalamlam, Qarn al-Manazil)",
            description = "Perform Ghusl, wear clean two-piece unstitched white cloth (for men) or modest modest dress (for women). Make the intention: 'Labbayk Allahumma Umrah' and recite the Talbiyah continuously.",
            importantNotes = listOf(
                "Do not apply perfume after entering Ihram state.",
                "Do not cut hair or nails.",
                "Recite Talbiyah frequently until reaching the Holy Kaabah."
            ),
            duas = listOf(
                "لَبَّيْكَ اللَّهُمَّ عُمْرَةً (Labbayk Allahumma Umrah)",
                "لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لَا شَرِيكَ لَكَ لَبَّيْكَ، إِنَّ الْحَمْدَ وَالنِّعْمَةَ لَكَ وَالْمُلْكَ، لَا شَرِيكَ لَكَ (Labbayk Allahumma labbayk...)"
            ),
            hasCounter = false
        ),
        PilgrimageStep(
            stepNumber = 2,
            title = "Tawaf al-Umrah (7 Circuits)",
            arabicTitle = "طَوَافُ الْعُمْرَةِ (٧ أَشْوَاطٍ)",
            location = "Mataf around the Holy Kaabah, Masjid al-Haram",
            description = "Begin at the Black Stone (Hajar al-Aswad) corner with Takbir. Complete 7 anti-clockwise circuits around the Kaabah keeping the Kaabah to your left.",
            importantNotes = listOf(
                "Must be in state of Wudhu.",
                "Idtiba (uncovering right shoulder) for men during all 7 rounds of Tawaf.",
                "Raml (quick brisk pace) for men in the first 3 rounds only.",
                "Supplicate between Yemeni Corner and Black Stone with 'Rabbana atina...'"
            ),
            duas = listOf(
                "بِسْمِ اللَّهِ وَاللَّهُ أَكْبَرُ (Bismillahi Allahu Akbar - At the start of each round)",
                "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ (Between Yemeni corner & Black Stone)"
            ),
            hasCounter = true,
            counterTarget = 7,
            counterCurrent = 0
        ),
        PilgrimageStep(
            stepNumber = 3,
            title = "Two Rak'ahs behind Maqam Ibrahim",
            arabicTitle = "رَكْعَتَا مَقَامِ إِبْرَاهِيمَ",
            location = "Behind Maqam Ibrahim or anywhere in Masjid al-Haram",
            description = "Recite Surah Al-Kafirun in the 1st Rak'ah and Surah Al-Ikhlas in the 2nd Rak'ah. Then drink Zamzam water and pour some over your head.",
            importantNotes = listOf(
                "If area behind Maqam is crowded, pray anywhere inside the Haram.",
                "Drink Zamzam standing or sitting, facing the Qiblah, with sincere supplication."
            ),
            duas = listOf(
                "وَاتَّخِذُوا مِن مَّقَامِ إِبْرَاهِيمَ مُصَلًّى (Wattakhidhoo min Maqami Ibraheema musalla)",
                "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا وَاسِعًا، وَشِفَاءً مِنْ كُلِّ دَاءٍ (Zamzam Dua)"
            ),
            hasCounter = false
        ),
        PilgrimageStep(
            stepNumber = 4,
            title = "Sa'i between Safa & Marwah (7 Laps)",
            arabicTitle = "السَّعْيُ بَيْنَ الصَّفَا وَالْمَرْوَةِ",
            location = "Mas'a corridor starting at Mount Safa and ending at Mount Marwah",
            description = "Begin at Safa facing the Kaabah with Takbir & Dua. Walk towards Marwah (1 lap). Complete 7 laps (ending at Marwah). Men run briskly between the green light markers.",
            importantNotes = listOf(
                "Round 1: Safa to Marwah. Round 2: Marwah to Safa. Round 7: Safa to Marwah.",
                "Wudhu is recommended although not strictly required.",
                "Make heartfelt Dua at the top of Safa and Marwah."
            ),
            duas = listOf(
                "إِنَّ الصَّفَا وَالْمَرْوَةَ مِن شَعَائِرِ اللَّهِ ۖ فَمَنْ حَجَّ الْبَيْتَ أَوِ اعْتَمَرَ فَلَا جُنَاحَ عَلَيْهِ أَن يَطَّوَّفَ بِهِمَا (Innas-Safa wal-Marwata min sha'a'irillah...)",
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، يُحْيِي وَيُمِيتُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ"
            ),
            hasCounter = true,
            counterTarget = 7,
            counterCurrent = 0
        ),
        PilgrimageStep(
            stepNumber = 5,
            title = "Tahallul (Hair Cut / Shave)",
            arabicTitle = "التَّحَلُّلُ (الْحَلْقُ أَوِ التَّقْصِيرُ)",
            location = "Outside Haram / Barbershop in Makkah",
            description = "Men shave (Halq) or trim equally (Taqsir) all over the head. Women cut a fingertip length from the ends of their braided hair. Your Umrah is now complete!",
            importantNotes = listOf(
                "Shaving is superior for men as the Prophet (ﷺ) prayed for them thrice.",
                "Once cut, all Ihram restrictions are completely lifted."
            ),
            duas = listOf(
                "الْحَمْدُ لِلَّهِ الَّذِي بَلَّغَنَا هَذَا وَتَقَبَّلَ مِنَّا (All praise is due to Allah who brought us here and accepted our Umrah)"
            ),
            hasCounter = false
        )
    )

    val hajjDays = listOf(
        PilgrimageStep(
            stepNumber = 1,
            title = "Day 8 Dhu al-Hijjah: Day of Tarwiyah (Mina)",
            arabicTitle = "يَوْمُ التَّرْوِيَةِ (مِنَى)",
            location = "Mina Tents Valley",
            description = "Enter Ihram from Makkah and move to Mina before Dhuhr. Pray Dhuhr, Asr, Maghrib, Isha and Fajr shortened without combining (Qasr only). Stay the night in Mina.",
            importantNotes = listOf("Focus on Quran, Dhikr, and resting for the major Day of Arafah."),
            duas = listOf("لَبَّيْكَ اللَّهُمَّ حَجًّا (Labbayk Allahumma Hajjan)"),
            hasCounter = false
        ),
        PilgrimageStep(
            stepNumber = 2,
            title = "Day 9 Dhu al-Hijjah: Day of Arafah (The Pinnacle of Hajj)",
            arabicTitle = "يَوْمُ عَرَفَةَ (الْحَجُّ عَرَفَةُ)",
            location = "Plains of Arafah & Jabal ar-Rahmah",
            description = "Move after sunrise to Arafah. Pray Dhuhr and Asr combined and shortened at Dhuhr time. Stand in sincere supplication until sunset. After sunset, proceed calmly to Muzdalifah.",
            importantNotes = listOf(
                "The Prophet (ﷺ) said: 'Hajj is Arafah.'",
                "Do not leave Arafah before Maghrib adhan."
            ),
            duas = listOf("خَيْرُ الدُّعَاءِ دُعَاءُ يَوْمِ عَرَفَةَ: لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ"),
            hasCounter = false
        ),
        PilgrimageStep(
            stepNumber = 3,
            title = "Night of Day 9 / Eve of Day 10: Muzdalifah",
            arabicTitle = "الْمُزْدَلِفَةُ وَجَمْعُ الْحَصَى",
            location = "Muzdalifah Plains between Arafah and Mina",
            description = "Arrive and pray Maghrib and Isha combined (3+2 rak'ahs). Sleep under the open sky until Fajr. Collect 7 or 49-70 small pebbles for the Jamarat.",
            importantNotes = listOf("Rest and prioritize sleep to have energy for Yawm an-Nahr rituals."),
            duas = listOf("Dhikr at Al-Mash'ar Al-Haram after Fajr until sunrise."),
            hasCounter = false
        ),
        PilgrimageStep(
            stepNumber = 4,
            title = "Day 10 Dhu al-Hijjah: Yawm an-Nahr (Eid Day)",
            arabicTitle = "يَوْمُ النَّحْرِ (عِيدُ الْأَضْحَى)",
            location = "Mina & Masjid al-Haram",
            description = "Four major rituals in sequence: 1. Rami Jamarat al-Aqaba (7 pebbles) 2. Hady (Animal sacrifice) 3. Halq or Taqsir (First Tahallul) 4. Tawaf al-Ifadah & Sa'i in Makkah (Second Tahallul).",
            importantNotes = listOf("After Halq/Taqsir, all restrictions lifted except intimacy until Tawaf al-Ifadah."),
            duas = listOf("بِسْمِ اللَّهِ، اللَّهُ أَكْبَرُ رَغْمًا لِلشَّيْطَانِ (With each pebble thrown)"),
            hasCounter = true,
            counterTarget = 7,
            counterCurrent = 0
        ),
        PilgrimageStep(
            stepNumber = 5,
            title = "Days 11-13 Dhu al-Hijjah: Ayyam at-Tashriq & Farewell",
            arabicTitle = "أَيَّامُ التَّشْرِيقِ وَطَوَافُ الْوَدَاعِ",
            location = "Mina Jamarat & Masjid al-Haram",
            description = "Stay overnight in Mina. Stone all 3 Jamarat (Sughra, Wusta, Kubra with 7 pebbles each = 21 daily) after Zawal. Before leaving Makkah, perform Tawaf al-Wada (Farewell Tawaf).",
            importantNotes = listOf("Tawaf al-Wada is the final act before departing Makkah."),
            duas = listOf("Dua facing Qiblah with raised hands after Jamarah Sughra and Wusta."),
            hasCounter = true,
            counterTarget = 21,
            counterCurrent = 0
        )
    )

    // =========================================================================
    // 4. AUTHENTIC MOSQUES DATA (MALAYSIA & NOTABLE GLOBAL MASJIDS)
    // =========================================================================
    val masjidList = listOf(
        MasjidItem(
            id = "masjid_negara",
            name = "Masjid Negara (National Mosque of Malaysia)",
            arabicName = "المسجد الوطني الماليزي",
            address = "Jalan Perdana, Tasik Perdana",
            city = "Kuala Lumpur",
            stateOrCountry = "Malaysia",
            latitude = 3.1418,
            longitude = 101.6917,
            distanceKm = 1.2,
            capacity = 15000,
            hasWomenSection = true,
            hasParking = true,
            hasWheelchairAccess = true,
            hasAirConditioning = true,
            hasWudhuArea = true,
            nextPrayerName = "Dhuhr",
            nextPrayerTime = "1:22 PM",
            jumuahTime = "1:30 PM",
            isFavorite = true,
            phone = "+60 3-2693 7905"
        ),
        MasjidItem(
            id = "masjid_wilayah",
            name = "Masjid Wilayah Persekutuan (Federal Territory Mosque)",
            arabicName = "مسجد الإقليم الفيدرالي",
            address = "Jalan Tuanku Abdul Halim, Kompleks Kerajaan",
            city = "Kuala Lumpur",
            stateOrCountry = "Malaysia",
            latitude = 3.1714,
            longitude = 101.6775,
            distanceKm = 3.8,
            capacity = 17000,
            hasWomenSection = true,
            hasParking = true,
            hasWheelchairAccess = true,
            hasAirConditioning = true,
            hasWudhuArea = true,
            nextPrayerName = "Dhuhr",
            nextPrayerTime = "1:22 PM",
            jumuahTime = "1:30 PM",
            isFavorite = true,
            phone = "+60 3-6201 8791"
        ),
        MasjidItem(
            id = "masjid_putra",
            name = "Masjid Putra (Putra Mosque, Pink Dome)",
            arabicName = "مسجد بوترا",
            address = "Presint 1",
            city = "Putrajaya",
            stateOrCountry = "Malaysia",
            latitude = 2.9361,
            longitude = 101.6892,
            distanceKm = 24.5,
            capacity = 15000,
            hasWomenSection = true,
            hasParking = true,
            hasWheelchairAccess = true,
            hasAirConditioning = true,
            hasWudhuArea = true,
            nextPrayerName = "Dhuhr",
            nextPrayerTime = "1:22 PM",
            jumuahTime = "1:30 PM",
            isFavorite = false,
            phone = "+60 3-8888 5678"
        ),
        MasjidItem(
            id = "masjid_syakirin_klcc",
            name = "Masjid As-Syakirin (KLCC Mosque)",
            arabicName = "مسجد الشاكرين كي إل سي سي",
            address = "KLCC Park, Jalan Ampang",
            city = "Kuala Lumpur",
            stateOrCountry = "Malaysia",
            latitude = 3.1565,
            longitude = 101.7153,
            distanceKm = 0.8,
            capacity = 12000,
            hasWomenSection = true,
            hasParking = true,
            hasWheelchairAccess = true,
            hasAirConditioning = true,
            hasWudhuArea = true,
            nextPrayerName = "Dhuhr",
            nextPrayerTime = "1:22 PM",
            jumuahTime = "1:30 PM",
            isFavorite = true,
            phone = "+60 3-2382 8000"
        ),
        MasjidItem(
            id = "masjid_blue_shahalam",
            name = "Masjid Sultan Salahuddin Abdul Aziz Shah (Blue Mosque)",
            arabicName = "مسجد السلطان صلاح الدين عبد العزيز شاه",
            address = "Persiaran Masjid, Seksyen 14",
            city = "Shah Alam",
            stateOrCountry = "Selangor, Malaysia",
            latitude = 3.0784,
            longitude = 101.5208,
            distanceKm = 22.1,
            capacity = 24000,
            hasWomenSection = true,
            hasParking = true,
            hasWheelchairAccess = true,
            hasAirConditioning = true,
            hasWudhuArea = true,
            nextPrayerName = "Dhuhr",
            nextPrayerTime = "1:22 PM",
            jumuahTime = "1:30 PM",
            isFavorite = false,
            phone = "+60 3-5519 9988"
        ),
        MasjidItem(
            id = "masjid_tuanku_mizan",
            name = "Masjid Tuanku Mizan Zainal Abidin (Iron Mosque)",
            arabicName = "مسجد توانكو ميزان زين العابدين",
            address = "Presint 3",
            city = "Putrajaya",
            stateOrCountry = "Malaysia",
            latitude = 2.9192,
            longitude = 101.6828,
            distanceKm = 26.2,
            capacity = 20000,
            hasWomenSection = true,
            hasParking = true,
            hasWheelchairAccess = true,
            hasAirConditioning = true,
            hasWudhuArea = true,
            nextPrayerName = "Dhuhr",
            nextPrayerTime = "1:22 PM",
            jumuahTime = "1:30 PM",
            isFavorite = false,
            phone = "+60 3-8880 4300"
        )
    )

    // =========================================================================
    // 5. AUTHENTIC ISLAMIC EVENTS & CALENDAR DATA
    // =========================================================================
    val islamicMonths = listOf(
        HijriMonthData(1, "مُحَرَّم", "Muharram", 1448, 30, isSacredMonth = true),
        HijriMonthData(2, "صَفَر", "Safar", 1448, 29, isSacredMonth = false),
        HijriMonthData(3, "رَبِيع الأَوَّل", "Rabi' al-Awwal", 1448, 30, isSacredMonth = false),
        HijriMonthData(4, "رَبِيع الآخِر", "Rabi' al-Thani", 1448, 29, isSacredMonth = false),
        HijriMonthData(5, "جُمَادَى الأُولَى", "Jumada al-Awwal", 1448, 30, isSacredMonth = false),
        HijriMonthData(6, "جُمَادَى الآخِرَة", "Jumada al-Thani", 1448, 29, isSacredMonth = false),
        HijriMonthData(7, "رَجَب", "Rajab", 1448, 30, isSacredMonth = true),
        HijriMonthData(8, "شَعْبَان", "Sha'ban", 1448, 29, isSacredMonth = false),
        HijriMonthData(9, "رَمَضَان", "Ramadan", 1448, 30, isSacredMonth = false),
        HijriMonthData(10, "شَوَّال", "Shawwal", 1448, 29, isSacredMonth = false),
        HijriMonthData(11, "ذُو القَعْدَة", "Dhu al-Qi'dah", 1448, 30, isSacredMonth = true),
        HijriMonthData(12, "ذُو الحِجَّة", "Dhu al-Hijjah", 1448, 29, isSacredMonth = true)
    )

    val keyIslamicEvents = listOf(
        IslamicEvent(
            id = "event_islamic_new_year",
            title = "Awal Muharram (Islamic New Year)",
            arabicTitle = "رَأْسُ السَّنَةِ الْهِجْرِيَّةِ",
            hijriDate = "1 Muharram 1448H",
            gregorianDate = "June 16, 2026",
            description = "Commemorates the blessed Hijrah (migration) of Prophet Muhammad (ﷺ) from Makkah to Madinah.",
            isSunnahFasting = false,
            isMajorHoliday = true,
            daysRemaining = 0
        ),
        IslamicEvent(
            id = "event_ashura",
            title = "Day of Ashura (10th Muharram)",
            arabicTitle = "يَوْمُ عَاشُورَاء",
            hijriDate = "10 Muharram 1448H",
            gregorianDate = "June 25, 2026",
            description = "Fasting on Ashura expiates the minor sins of the preceding year (Sahih Muslim). It commemorates Allah saving Prophet Musa (A.S.) and Bani Israel from Pharaoh.",
            isSunnahFasting = true,
            isMajorHoliday = false,
            daysRemaining = 0
        ),
        IslamicEvent(
            id = "event_mawlid",
            title = "Mawlid al-Nabi (Birth of the Prophet ﷺ)",
            arabicTitle = "مَوْلِدُ النَّبِيِّ ﷺ",
            hijriDate = "12 Rabi' al-Awwal 1448H",
            gregorianDate = "August 26, 2026",
            description = "A day reflecting on the life (Seerah), character, and blessed guidance of the final Messenger Muhammad (ﷺ).",
            isSunnahFasting = false,
            isMajorHoliday = true,
            daysRemaining = 0
        ),
        IslamicEvent(
            id = "event_isra_miraj",
            title = "Isra' & Mi'raj (The Night Journey & Ascension)",
            arabicTitle = "الإِسْرَاءُ وَالْمِعْرَاجُ",
            hijriDate = "27 Rajab 1448H",
            gregorianDate = "January 6, 2027",
            description = "Commemorates the miraculous journey of the Prophet (ﷺ) from Makkah to Jerusalem and ascending through the heavens where the five daily prayers were ordained.",
            isSunnahFasting = false,
            isMajorHoliday = false,
            daysRemaining = 126
        ),
        IslamicEvent(
            id = "event_nisf_shaban",
            title = "Nisf Sha'ban (Middle of Sha'ban)",
            arabicTitle = "لَيْلَةُ النِّصْفِ مِنْ شَعْبَانَ",
            hijriDate = "15 Sha'ban 1448H",
            gregorianDate = "January 23, 2027",
            description = "A blessed night for seeking forgiveness, repentance, and preparing the heart for the arrival of holy Ramadan.",
            isSunnahFasting = true,
            isMajorHoliday = false,
            daysRemaining = 143
        ),
        IslamicEvent(
            id = "event_ramadan_start",
            title = "1st of Ramadan (First Day of Fasting)",
            arabicTitle = "أَوَّلُ يَوْمٍ مِنْ رَمَضَانَ الْمُبَارَكِ",
            hijriDate = "1 Ramadan 1448H",
            gregorianDate = "February 7, 2027",
            description = "The beginning of the holy month of fasting, Quran revelation, Tarawih prayers, and immense divine mercy.",
            isSunnahFasting = false,
            isMajorHoliday = true,
            daysRemaining = 158
        ),
        IslamicEvent(
            id = "event_nuzul_quran",
            title = "Nuzul Al-Quran (Revelation of the Holy Quran)",
            arabicTitle = "نُزُولُ الْقُرْآنِ الْكَرِيمِ",
            hijriDate = "17 Ramadan 1448H",
            gregorianDate = "February 23, 2027",
            description = "Marks the commencement of the revelation of the Holy Quran to Prophet Muhammad (ﷺ) in the cave of Hira.",
            isSunnahFasting = false,
            isMajorHoliday = true,
            daysRemaining = 174
        ),
        IslamicEvent(
            id = "event_eid_al_fitr",
            title = "Eid al-Fitr (Hari Raya Aidilfitri)",
            arabicTitle = "عِيدُ الْفِطْرِ الْمُبَارَكُ",
            hijriDate = "1 Shawwal 1448H",
            gregorianDate = "March 9, 2027",
            description = "Joyous celebration of gratitude concluding the holy month of Ramadan, starting with Eid prayer and Zakat al-Fitr.",
            isSunnahFasting = false,
            isMajorHoliday = true,
            daysRemaining = 188
        ),
        IslamicEvent(
            id = "event_day_of_arafah",
            title = "Day of Arafah (Hajj Pinnacle & Fasting)",
            arabicTitle = "يَوْمُ عَرَفَةَ",
            hijriDate = "9 Dhu al-Hijjah 1448H",
            gregorianDate = "May 16, 2027",
            description = "Fasting on this day expiates sins of the previous year and the coming year for non-pilgrims (Sahih Muslim).",
            isSunnahFasting = true,
            isMajorHoliday = true,
            daysRemaining = 256
        ),
        IslamicEvent(
            id = "event_eid_al_adha",
            title = "Eid al-Adha (Hari Raya Aidiladha & Qurban)",
            arabicTitle = "عِيدُ الْأَضْحَى الْمُبَارَكُ",
            hijriDate = "10 Dhu al-Hijjah 1448H",
            gregorianDate = "May 17, 2027",
            description = "The Festival of Sacrifice honouring the devotion of Prophet Ibrahim (A.S.) with the slaughter of sacrificial animals.",
            isSunnahFasting = false,
            isMajorHoliday = true,
            daysRemaining = 257
        )
    )
}
