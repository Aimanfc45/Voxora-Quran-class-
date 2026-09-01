package com.example.data.mock

import com.example.data.model.JuzInfo
import com.example.data.model.ReciterInfo
import com.example.data.model.Surah
import com.example.data.model.TajwidRule
import com.example.data.model.Verse

object MockQuranData {

    val surahList: List<Surah> = listOf(
        Surah(
            number = 1,
            nameArabic = "الفَاتِحَة",
            nameEnglish = "Al-Fatihah",
            nameTranslation = "The Opening",
            revelationType = "Meccan",
            totalVerses = 7,
            verses = listOf(
                Verse(
                    id = "1:1",
                    surahNumber = 1,
                    verseNumber = 1,
                    textArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    transliteration = "Bismillāhir-Raḥmānir-Raḥīm",
                    translationEnglish = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                    translationMalay = "Dengan nama Allah, Yang Maha Pemurah, lagi Maha Mengasihani.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Mad Asli on الرَّحْمَٰنِ"
                ),
                Verse(
                    id = "1:2",
                    surahNumber = 1,
                    verseNumber = 2,
                    textArabic = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    transliteration = "Al-ḥamdu lillāhi Rabbil-ʿālamīn",
                    translationEnglish = "[All] praise is [due] to Allah, Lord of the worlds -",
                    translationMalay = "Segala puji tertentu bagi Allah, Tuhan sekalian alam.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Idh-har Qamari on الْحَمْدُ"
                ),
                Verse(
                    id = "1:3",
                    surahNumber = 1,
                    verseNumber = 3,
                    textArabic = "الرَّحْمَٰنِ الرَّحِيمِ",
                    transliteration = "Ar-Raḥmānir-Raḥīm",
                    translationEnglish = "The Entirely Merciful, the Especially Merciful,",
                    translationMalay = "Yang Maha Pemurah, lagi Maha Mengasihani.",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Idgham Shamsi on الرَّحْمَٰنِ"
                ),
                Verse(
                    id = "1:4",
                    surahNumber = 1,
                    verseNumber = 4,
                    textArabic = "مَالِكِ يَوْمِ الدِّينِ",
                    transliteration = "Māliki yawmid-dīn",
                    translationEnglish = "Sovereign of the Day of Recompense.",
                    translationMalay = "Yang Menguasai pemerintahan hari Pembalasan.",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Mad Tabee'i on مَالِكِ"
                ),
                Verse(
                    id = "1:5",
                    surahNumber = 1,
                    verseNumber = 5,
                    textArabic = "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                    transliteration = "Iyyāka naʿbudu wa-iyyāka nastaʿīn",
                    translationEnglish = "It is You we worship and You we ask for help.",
                    translationMalay = "Engkaulah sahaja yang kami sembah dan kepada Engkaulah sahaja kami memohon pertolongan.",
                    audioDurationSeconds = 7,
                    tajwidRuleHighlight = "Shaddah articulation on إِيَّاكَ"
                ),
                Verse(
                    id = "1:6",
                    surahNumber = 1,
                    verseNumber = 6,
                    textArabic = "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                    transliteration = "Ihdinaṣ-ṣirāṭal-mustaqīm",
                    translationEnglish = "Guide us to the straight path -",
                    translationMalay = "Tunjukilah kami jalan yang lurus.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Makhraj of Ṣad vs Sin"
                ),
                Verse(
                    id = "1:7",
                    surahNumber = 1,
                    verseNumber = 7,
                    textArabic = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                    transliteration = "Ṣirāṭallaḏhīna anʿamta ʿalayhim ghayril-maghḍūbi ʿalayhim walāḍ-ḍāllīn",
                    translationEnglish = "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
                    translationMalay = "Iaitu jalan orang-orang yang Engkau telah kurniakan nikmat kepada mereka, bukan (jalan) orang-orang yang dimurkai dan bukan pula (jalan) orang-orang yang sesat.",
                    audioDurationSeconds = 12,
                    tajwidRuleHighlight = "Mad Lazim Kalimi Muthaqqal on الضَّالِّينَ (6 Harakat)"
                )
            )
        ),
        Surah(
            number = 2,
            nameArabic = "البَقَرَة",
            nameEnglish = "Al-Baqarah",
            nameTranslation = "The Cow",
            revelationType = "Medinan",
            totalVerses = 286,
            verses = listOf(
                Verse(
                    id = "2:1",
                    surahNumber = 2,
                    verseNumber = 1,
                    textArabic = "الم",
                    transliteration = "Alif-Lām-Mīm",
                    translationEnglish = "Alif, Lam, Meem.",
                    translationMalay = "Alif, Laam, Miim.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Mad Lazim Harfi Muthaqqal (6 Harakat)"
                ),
                Verse(
                    id = "2:2",
                    surahNumber = 2,
                    verseNumber = 2,
                    textArabic = "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ",
                    transliteration = "Zālikal-kitābu lā rayba fīh, hudal-lil-muttaqīn",
                    translationEnglish = "This is the Book about which there is no doubt, a guidance for those conscious of Allah -",
                    translationMalay = "Kitab Al-Quran ini, tidak ada sebarang keraguan padanya; petunjuk bagi orang-orang yang bertaqwa.",
                    audioDurationSeconds = 8,
                    tajwidRuleHighlight = "Tanween Idgham Bila Ghunnah on هُدًى لِّلْمُتَّقِينَ"
                ),
                Verse(
                    id = "2:3",
                    surahNumber = 2,
                    verseNumber = 3,
                    textArabic = "الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنفِقُونَ",
                    transliteration = "Alladhīna yu'minūna bil-ghaybi wa yuqīmūnaṣ-ṣalāta wa mimmā razaqnāhum yunfiqūn",
                    translationEnglish = "Who believe in the unseen, establish prayer, and spend out of what We have provided for them,",
                    translationMalay = "Iaitu orang-orang yang beriman kepada perkara yang ghaib, mendirikan sembahyang, dan mendermakan sebahagian dari rezeki yang Kami berikan kepada mereka.",
                    audioDurationSeconds = 10,
                    tajwidRuleHighlight = "Ikhfa Haqiqi on يُنفِقُونَ"
                ),
                Verse(
                    id = "2:4",
                    surahNumber = 2,
                    verseNumber = 4,
                    textArabic = "وَالَّذِينَ يُؤْمِنُونَ بِمَا أُنزِلَ إِلَيْكَ وَمَا أُنزِلَ مِن قَبْلِكَ وَبِالْآخِرَةِ هُمْ يُوقِنُونَ",
                    transliteration = "Walladhīna yu'minūna bimā unzila ilayka wa mā unzila min qablika wa bil-ākhirati hum yūqinūn",
                    translationEnglish = "And who believe in what has been revealed to you, [O Muhammad], and what was revealed before you, and of the Hereafter they are certain [in faith].",
                    translationMalay = "Dan mereka yang beriman kepada kitab yang diturunkan kepadamu dan kitab-kitab yang diturunkan sebelummu, serta yakin akan adanya hari akhirat.",
                    audioDurationSeconds = 11,
                    tajwidRuleHighlight = "Mad Ja'iz Munfasil on بِمَا أُنزِلَ"
                ),
                Verse(
                    id = "2:5",
                    surahNumber = 2,
                    verseNumber = 5,
                    textArabic = "أُولَٰئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰئِكَ هُمُ الْمُفْلِحُونَ",
                    transliteration = "Ulā'ika 'alā hudam-mir-Rabbihim wa ulā'ika humul-mufliḥūn",
                    translationEnglish = "Those are upon [right] guidance from their Lord, and it is those who are the successful.",
                    translationMalay = "Mereka itulah yang tetap mendapat petunjuk dari Tuhan mereka, dan merekalah orang-orang yang berjaya.",
                    audioDurationSeconds = 9,
                    tajwidRuleHighlight = "Mad Wajib Muttasil on أُولَٰئِكَ (4-5 Harakat)"
                ),
                Verse(
                    id = "2:255",
                    surahNumber = 2,
                    verseNumber = 255,
                    textArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                    transliteration = "Allāhu lā ilāha illā Huwal-Ḥayyul-Qayyūm...",
                    translationEnglish = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep...",
                    translationMalay = "Allah, tiada Tuhan melainkan Dia, Yang Tetap Hidup, Yang Kekal selama-lamanya Mentadbirkan...",
                    audioDurationSeconds = 24,
                    tajwidRuleHighlight = "Ayat Al-Kursi — Comprehensive Tajwid"
                )
            )
        ),
        Surah(
            number = 36,
            nameArabic = "يس",
            nameEnglish = "Ya-Sin",
            nameTranslation = "Ya-Sin",
            revelationType = "Meccan",
            totalVerses = 83,
            verses = listOf(
                Verse(
                    id = "36:1",
                    surahNumber = 36,
                    verseNumber = 1,
                    textArabic = "يس",
                    transliteration = "Yā-Sīn",
                    translationEnglish = "Ya, Seen.",
                    translationMalay = "Yaa, Siin.",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Mad Lazim Harfi Mukhaffaf (6 Harakat on Sin)"
                ),
                Verse(
                    id = "36:2",
                    surahNumber = 36,
                    verseNumber = 2,
                    textArabic = "وَالْقُرْآنِ الْحَكِيمِ",
                    transliteration = "Wal-Qur'ānil-ḥakīm",
                    translationEnglish = "By the wise Qur'an.",
                    translationMalay = "Demi Al-Quran yang mengandungi hikmat-hikmat dan kebenaran yang tetap kukuh.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Idh-har Qamari on الْقُرْآنِ"
                ),
                Verse(
                    id = "36:3",
                    surahNumber = 36,
                    verseNumber = 3,
                    textArabic = "إِنَّكَ لَمِنَ الْمُرْسَلِينَ",
                    transliteration = "Innaka laminal-mursalīn",
                    translationEnglish = "Indeed you, [O Muhammad], are from among the messengers,",
                    translationMalay = "Sesungguhnya engkau (wahai Muhammad) adalah seorang Rasul dari Rasul-rasul yang telah diutus.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Ghunnah Mushaddadah on إِنَّكَ"
                ),
                Verse(
                    id = "36:4",
                    surahNumber = 36,
                    verseNumber = 4,
                    textArabic = "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ",
                    transliteration = "'Alā ṣirāṭim-mustaqīm",
                    translationEnglish = "On a straight path.",
                    translationMalay = "Turut menjalani jalan yang lurus.",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Idgham Ma'al Ghunnah on صِرَاطٍ مُّسْتَقِيمٍ"
                )
            )
        ),
        Surah(
            number = 67,
            nameArabic = "المُلْك",
            nameEnglish = "Al-Mulk",
            nameTranslation = "The Sovereignty",
            revelationType = "Meccan",
            totalVerses = 30,
            verses = listOf(
                Verse(
                    id = "67:1",
                    surahNumber = 67,
                    verseNumber = 1,
                    textArabic = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                    transliteration = "Tabārakal-ladhī biyadihil-mulku wa huwa 'alā kulli shay'in qadīr",
                    translationEnglish = "Blessed is He in whose hand is dominion, and He is over all things competent -",
                    translationMalay = "Maha Berkat (serta Maha Tinggilah kelebihan) Tuhan yang menguasai pemerintahan (dunia dan akhirat); dan memanglah Ia Maha Kuasa atas tiap-tiap sesuatu.",
                    audioDurationSeconds = 9,
                    tajwidRuleHighlight = "Ikhfa Haqiqi on شَيْءٍ قَدِيرٌ"
                ),
                Verse(
                    id = "67:2",
                    surahNumber = 67,
                    verseNumber = 2,
                    textArabic = "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ",
                    transliteration = "Alladhī khalaqal-mawta wal-ḥayāta liyabluwakum ayyukum aḥsanu 'amalā, wa huwal-'azīzul-ghafūr",
                    translationEnglish = "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -",
                    translationMalay = "Dia lah yang telah mentakdirkan adanya mati dan hidup untuk menguji dan menzahirkan keadaan kamu: siapakah di antara kamu yang lebih baik amalnya.",
                    audioDurationSeconds = 11,
                    tajwidRuleHighlight = "Qalqalah Sughra on لِيَبْلُوَكُمْ"
                )
            )
        ),
        Surah(
            number = 112,
            nameArabic = "الإِخْلَاص",
            nameEnglish = "Al-Ikhlas",
            nameTranslation = "The Sincerity",
            revelationType = "Meccan",
            totalVerses = 4,
            verses = listOf(
                Verse(
                    id = "112:1",
                    surahNumber = 112,
                    verseNumber = 1,
                    textArabic = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                    transliteration = "Qul huwal-lāhu aḥad",
                    translationEnglish = "Say, \"He is Allah, [who is] One,",
                    translationMalay = "Katakanlah (wahai Muhammad): (Tuhanku) ialah Allah Yang Maha Esa.",
                    audioDurationSeconds = 4,
                    tajwidRuleHighlight = "Qalqalah Kubra on أَحَدٌ"
                ),
                Verse(
                    id = "112:2",
                    surahNumber = 112,
                    verseNumber = 2,
                    textArabic = "اللَّهُ الصَّمَدُ",
                    transliteration = "Allāhuṣ-ṣamad",
                    translationEnglish = "Allah, the Eternal Refuge.",
                    translationMalay = "Allah Yang menjadi tumpuan sekalian makhluk untuk memohon sebarang hajat.",
                    audioDurationSeconds = 4,
                    tajwidRuleHighlight = "Qalqalah Kubra on الصَّمَدُ"
                ),
                Verse(
                    id = "112:3",
                    surahNumber = 112,
                    verseNumber = 3,
                    textArabic = "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                    transliteration = "Lam yalid wa lam yūlad",
                    translationEnglish = "He neither begets nor is born,",
                    translationMalay = "Ia tiada beranak, dan Ia pula tidak diperanakkan.",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Idh-har Shafawi on لَمْ يَلِدْ"
                ),
                Verse(
                    id = "112:4",
                    surahNumber = 112,
                    verseNumber = 4,
                    textArabic = "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
                    transliteration = "Wa lam yakul-lahū kufuwan aḥad",
                    translationEnglish = "Nor is there to Him any equivalent.\"",
                    translationMalay = "Dan tidak ada sesiapapun yang serupa dengan-Nya.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Idgham Bila Ghunnah on يَكُن لَّهُ"
                )
            )
        ),
        Surah(
            number = 113,
            nameArabic = "الفَلَق",
            nameEnglish = "Al-Falaq",
            nameTranslation = "The Daybreak",
            revelationType = "Meccan",
            totalVerses = 5,
            verses = listOf(
                Verse(
                    id = "113:1",
                    surahNumber = 113,
                    verseNumber = 1,
                    textArabic = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ",
                    transliteration = "Qul a'ūdhu birabbil-falaq",
                    translationEnglish = "Say, \"I seek refuge in the Lord of daybreak",
                    translationMalay = "Katakanlah (wahai Muhammad): Aku berlindung kepada (Allah) Tuhan yang menciptakan sekalian makhluk,",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Qalqalah Kubra on الْفَلَقِ"
                ),
                Verse(
                    id = "113:2",
                    surahNumber = 113,
                    verseNumber = 2,
                    textArabic = "مِن شَرِّ مَا خَلَقَ",
                    transliteration = "Min sharri mā khalaq",
                    translationEnglish = "From the evil of that which He created",
                    translationMalay = "Dari bahaya benda-benda yang Ia ciptakan;",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Ikhfa Haqiqi on مِن شَرِّ"
                ),
                Verse(
                    id = "113:3",
                    surahNumber = 113,
                    verseNumber = 3,
                    textArabic = "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ",
                    transliteration = "Wa min sharri ghāsiqin idhā waqab",
                    translationEnglish = "And from the evil of darkness when it settles",
                    translationMalay = "Dan dari bahaya gelap apabila ia masuk;",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Idh-har Halqi on غَاسِقٍ إِذَا"
                ),
                Verse(
                    id = "113:4",
                    surahNumber = 113,
                    verseNumber = 4,
                    textArabic = "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ",
                    transliteration = "Wa min sharrin-naffāthāti fīl-'uqad",
                    translationEnglish = "And from the evil of the blowers in knots",
                    translationMalay = "Dan dari bahaya sekalian wanita-wanita penyihir yang meniup pada simpulan-simpulan;",
                    audioDurationSeconds = 7,
                    tajwidRuleHighlight = "Ghunnah on النَّفَّاثَاتِ"
                ),
                Verse(
                    id = "113:5",
                    surahNumber = 113,
                    verseNumber = 5,
                    textArabic = "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
                    transliteration = "Wa min sharri ḥāsidin idhā ḥasad",
                    translationEnglish = "And from the evil of an envier when he envies.\"",
                    translationMalay = "Dan dari bahaya orang yang dengki apabila ia melakukan dengkinya.",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Makhraj of Ḥaa in حَاسِدٍ"
                )
            )
        ),
        Surah(
            number = 114,
            nameArabic = "النَّاس",
            nameEnglish = "An-Nas",
            nameTranslation = "Mankind",
            revelationType = "Meccan",
            totalVerses = 6,
            verses = listOf(
                Verse(
                    id = "114:1",
                    surahNumber = 114,
                    verseNumber = 1,
                    textArabic = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                    transliteration = "Qul a'ūdhu birabbin-nās",
                    translationEnglish = "Say, \"I seek refuge in the Lord of mankind,",
                    translationMalay = "Katakanlah (wahai Muhammad): Aku berlindung kepada (Allah) Pemelihara sekalian manusia.",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Ghunnah Mushaddadah on النَّاسِ"
                ),
                Verse(
                    id = "114:2",
                    surahNumber = 114,
                    verseNumber = 2,
                    textArabic = "مَلِكِ النَّاسِ",
                    transliteration = "Malikin-nās",
                    translationEnglish = "The Sovereign of mankind,",
                    translationMalay = "Yang Menguasai sekalian manusia,",
                    audioDurationSeconds = 4,
                    tajwidRuleHighlight = "Mad Tabee'i on مَلِكِ"
                ),
                Verse(
                    id = "114:3",
                    surahNumber = 114,
                    verseNumber = 3,
                    textArabic = "إِلَٰهِ النَّاسِ",
                    transliteration = "Ilāhin-nās",
                    translationEnglish = "The God of mankind,",
                    translationMalay = "Tuhan yang berhak disembah oleh sekalian manusia,",
                    audioDurationSeconds = 4,
                    tajwidRuleHighlight = "Mad Tabee'i on إِلَٰهِ"
                ),
                Verse(
                    id = "114:4",
                    surahNumber = 114,
                    verseNumber = 4,
                    textArabic = "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ",
                    transliteration = "Min sharril-waswāsil-khannās",
                    translationEnglish = "From the evil of the retreating whisperer -",
                    translationMalay = "Dari kejahatan pembisik yang timbul tenggelam,",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Ikhfa Haqiqi on مِن شَرِّ"
                ),
                Verse(
                    id = "114:5",
                    surahNumber = 114,
                    verseNumber = 5,
                    textArabic = "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ",
                    transliteration = "Alladhī yuwaswisu fī ṣudūrin-nās",
                    translationEnglish = "Who whispers into the breasts of mankind -",
                    translationMalay = "Yang melemparkan bisikan dan hasutannya ke dalam dada manusia,",
                    audioDurationSeconds = 6,
                    tajwidRuleHighlight = "Makhraj of Ṣad in صُدُورِ"
                ),
                Verse(
                    id = "114:6",
                    surahNumber = 114,
                    verseNumber = 6,
                    textArabic = "مِنَ الْجِنَّةِ وَالنَّاسِ",
                    transliteration = "Minal-jinnati wan-nās",
                    translationEnglish = "From among the jinn and mankind.\"",
                    translationMalay = "(Iaitu pembisik dan penghasut) dari kalangan jin dan manusia.",
                    audioDurationSeconds = 5,
                    tajwidRuleHighlight = "Ghunnah Mushaddadah on الْجِنَّةِ and النَّاسِ"
                )
            )
        ),
        Surah(3, "آلِ عِمْرَان", "Ali 'Imran", "Family of Imran", "Medinan", 200),
        Surah(4, "النِّسَاء", "An-Nisa", "The Women", "Medinan", 176),
        Surah(5, "المَائِدَة", "Al-Ma'idah", "The Table Spread", "Medinan", 120),
        Surah(6, "الأَنْعَام", "Al-An'am", "The Cattle", "Meccan", 165),
        Surah(7, "الأَعْرَاف", "Al-A'raf", "The Heights", "Meccan", 206),
        Surah(8, "الأَنْفَال", "Al-Anfal", "The Spoils of War", "Medinan", 75),
        Surah(9, "التَّوْبَة", "At-Tawbah", "The Repentance", "Medinan", 129),
        Surah(10, "يُونُس", "Yunus", "Jonah", "Meccan", 109),
        Surah(18, "الكَهْف", "Al-Kahf", "The Cave", "Meccan", 110),
        Surah(55, "الرَّحْمَٰن", "Ar-Rahman", "The Beneficent", "Medinan", 78),
        Surah(56, "الوَاقِعَة", "Al-Waqi'ah", "The Inevitable", "Meccan", 96),
        Surah(78, "النَّبَأ", "An-Naba", "The Tidings", "Meccan", 40)
    )

    val all114SurahsCatalog: List<Surah> = listOf(
        Surah(1, "الفَاتِحَة", "Al-Fatihah", "The Opening", "Meccan", 7),
        Surah(2, "البَقَرَة", "Al-Baqarah", "The Cow", "Medinan", 286),
        Surah(3, "آلِ عِمْرَان", "Ali 'Imran", "Family of Imran", "Medinan", 200),
        Surah(4, "النِّسَاء", "An-Nisa", "The Women", "Medinan", 176),
        Surah(5, "المَائِدَة", "Al-Ma'idah", "The Table Spread", "Medinan", 120),
        Surah(6, "الأَنْعَام", "Al-An'am", "The Cattle", "Meccan", 165),
        Surah(7, "الأَعْرَاف", "Al-A'raf", "The Heights", "Meccan", 206),
        Surah(8, "الأَنْفَال", "Al-Anfal", "The Spoils of War", "Medinan", 75),
        Surah(9, "التَّوْبَة", "At-Tawbah", "The Repentance", "Medinan", 129),
        Surah(10, "يُونُس", "Yunus", "Jonah", "Meccan", 109),
        Surah(11, "هُود", "Hud", "Hud", "Meccan", 123),
        Surah(12, "يُوسُف", "Yusuf", "Joseph", "Meccan", 111),
        Surah(13, "الرَّعْد", "Ar-Ra'd", "The Thunder", "Medinan", 43),
        Surah(14, "إِبْرَاهِيم", "Ibrahim", "Abraham", "Meccan", 52),
        Surah(15, "الحِجْر", "Al-Hijr", "The Rocky Tract", "Meccan", 99),
        Surah(16, "النَّحْل", "An-Nahl", "The Bee", "Meccan", 128),
        Surah(17, "الإِسْرَاء", "Al-Isra", "The Night Journey", "Meccan", 111),
        Surah(18, "الكَهْف", "Al-Kahf", "The Cave", "Meccan", 110),
        Surah(19, "مَرْيَم", "Maryam", "Mary", "Meccan", 98),
        Surah(20, "طه", "Taha", "Ta-Ha", "Meccan", 135),
        Surah(21, "الأَنْبِيَاء", "Al-Anbiya", "The Prophets", "Meccan", 112),
        Surah(22, "الحَجّ", "Al-Hajj", "The Pilgrimage", "Medinan", 78),
        Surah(23, "المُؤْمِنُون", "Al-Mu'minun", "The Believers", "Meccan", 118),
        Surah(24, "النُّور", "An-Nur", "The Light", "Medinan", 64),
        Surah(25, "الفُرْقَان", "Al-Furqan", "The Criterion", "Meccan", 77),
        Surah(26, "الشُّعَرَاء", "Ash-Shu'ara", "The Poets", "Meccan", 227),
        Surah(27, "النَّمْل", "An-Naml", "The Ant", "Meccan", 93),
        Surah(28, "القَصَص", "Al-Qasas", "The Stories", "Meccan", 88),
        Surah(29, "العَنْكَبُوت", "Al-'Ankabut", "The Spider", "Meccan", 69),
        Surah(30, "الرُّوم", "Ar-Rum", "The Romans", "Meccan", 60),
        Surah(31, "لُقْمَان", "Luqman", "Luqman", "Meccan", 34),
        Surah(32, "السَّجْدَة", "As-Sajdah", "The Prostration", "Meccan", 30),
        Surah(33, "الأَحْزَاب", "Al-Ahzab", "The Combined Forces", "Medinan", 73),
        Surah(34, "سَبَأ", "Saba", "Sheba", "Meccan", 54),
        Surah(35, "فَاطِر", "Fatir", "Originator", "Meccan", 45),
        Surah(36, "يس", "Ya-Sin", "Ya Sin", "Meccan", 83),
        Surah(37, "الصَّافَّات", "As-Saffat", "Those who set the Ranks", "Meccan", 182),
        Surah(38, "ص", "Sad", "The Letter Sad", "Meccan", 88),
        Surah(39, "الزُّمَر", "Az-Zumar", "The Troops", "Meccan", 75),
        Surah(40, "غَافِر", "Ghafir", "The Forgiver", "Meccan", 85),
        Surah(41, "فُصِّلَت", "Fussilat", "Explained in Detail", "Meccan", 54),
        Surah(42, "الشُّورَى", "Ash-Shura", "The Consultation", "Meccan", 53),
        Surah(43, "الزُّخْرُف", "Az-Zukhruf", "The Ornaments of Gold", "Meccan", 89),
        Surah(44, "الدُّخَان", "Ad-Dukhan", "The Smoke", "Meccan", 59),
        Surah(45, "الجَاثِيَة", "Al-Jathiyah", "The Crouching", "Meccan", 37),
        Surah(46, "الأَحْقَاف", "Al-Ahqaf", "The Wind-Curved Sandhills", "Meccan", 35),
        Surah(47, "مُحَمَّد", "Muhammad", "Muhammad", "Medinan", 38),
        Surah(48, "الفَتْح", "Al-Fath", "The Victory", "Medinan", 29),
        Surah(49, "الحُجُرَات", "Al-Hujurat", "The Rooms", "Medinan", 18),
        Surah(50, "ق", "Qaf", "The Letter Qaf", "Meccan", 45),
        Surah(51, "الذَّارِيَات", "Adh-Dhariyat", "The Winnowing Winds", "Meccan", 60),
        Surah(52, "الطُّور", "At-Tur", "The Mount", "Meccan", 49),
        Surah(53, "النَّجْم", "An-Najm", "The Star", "Meccan", 62),
        Surah(54, "القَمَر", "Al-Qamar", "The Moon", "Meccan", 55),
        Surah(55, "الرَّحْمَٰن", "Ar-Rahman", "The Beneficent", "Medinan", 78),
        Surah(56, "الوَاقِعَة", "Al-Waqi'ah", "The Inevitable", "Meccan", 96),
        Surah(57, "الحَدِيد", "Al-Hadid", "The Iron", "Medinan", 29),
        Surah(58, "المُجَادِلَة", "Al-Mujadila", "The Pleading Woman", "Medinan", 22),
        Surah(59, "الحَشْر", "Al-Hashr", "The Exile", "Medinan", 24),
        Surah(60, "المُمْتَحَنَة", "Al-Mumtahanah", "She that is to be examined", "Medinan", 13),
        Surah(61, "الصَّفّ", "As-Saff", "The Ranks", "Medinan", 14),
        Surah(62, "الجُمُعَة", "Al-Jumu'ah", "The Congregation, Friday", "Medinan", 11),
        Surah(63, "المُنَافِقُون", "Al-Munafiqun", "The Hypocrites", "Medinan", 11),
        Surah(64, "التَّغَابُن", "At-Taghabun", "The Mutual Disillusion", "Medinan", 18),
        Surah(65, "الطَّلَاق", "At-Talaq", "The Divorce", "Medinan", 12),
        Surah(66, "التَّحْرِيم", "At-Tahrim", "The Prohibition", "Medinan", 12),
        Surah(67, "المُلْك", "Al-Mulk", "The Sovereignty", "Meccan", 30),
        Surah(68, "القَلَم", "Al-Qalam", "The Pen", "Meccan", 52),
        Surah(69, "الحَاقَّة", "Al-Haqqah", "The Reality", "Meccan", 52),
        Surah(70, "المَعَارِج", "Al-Ma'arij", "The Ascending Stairways", "Meccan", 44),
        Surah(71, "نُوح", "Nuh", "Noah", "Meccan", 28),
        Surah(72, "الجِنّ", "Al-Jinn", "The Jinn", "Meccan", 28),
        Surah(73, "المُزَّمِّل", "Al-Muzzammil", "The Enshrouded One", "Meccan", 20),
        Surah(74, "المُدَّثِّر", "Al-Muddaththir", "The Cloaked One", "Meccan", 56),
        Surah(75, "القِيَامَة", "Al-Qiyamah", "The Resurrection", "Meccan", 40),
        Surah(76, "الإِنْسَان", "Al-Insan", "Man", "Medinan", 31),
        Surah(77, "المُرْسَلَات", "Al-Mursalat", "The Emissaries", "Meccan", 50),
        Surah(78, "النَّبَأ", "An-Naba", "The Tidings", "Meccan", 40),
        Surah(79, "النَّازِعَات", "An-Nazi'at", "Those who drag forth", "Meccan", 46),
        Surah(80, "عَبَسَ", "'Abasa", "He frowned", "Meccan", 42),
        Surah(81, "التَّكْوِير", "At-Takwir", "The Overthrowing", "Meccan", 29),
        Surah(82, "الانْفِطَار", "Al-Infitar", "The Cleaving", "Meccan", 19),
        Surah(83, "المُطَفِّفِين", "Al-Mutaffifin", "Defrauding", "Meccan", 36),
        Surah(84, "الانْشِقَاق", "Al-Inshiqaq", "The Splitting Open", "Meccan", 25),
        Surah(85, "البُرُوج", "Al-Buruj", "The Mansions of the Stars", "Meccan", 22),
        Surah(86, "الطَّارِق", "At-Tariq", "The Morning Star", "Meccan", 17),
        Surah(87, "الأَعْلَى", "Al-A'la", "The Most High", "Meccan", 19),
        Surah(88, "الغَاشِيَة", "Al-Ghashiyah", "The Overwhelming", "Meccan", 26),
        Surah(89, "الفَجْر", "Al-Fajr", "The Dawn", "Meccan", 30),
        Surah(90, "البَلَد", "Al-Balad", "The City", "Meccan", 20),
        Surah(91, "الشَّمْس", "Ash-Shams", "The Sun", "Meccan", 15),
        Surah(92, "اللَّيْل", "Al-Layl", "The Night", "Meccan", 21),
        Surah(93, "الضُّحَى", "Ad-Duha", "The Morning Hours", "Meccan", 11),
        Surah(94, "الشَّرْح", "Ash-Sharh", "The Relief", "Meccan", 8),
        Surah(95, "التِّين", "At-Tin", "The Fig", "Meccan", 8),
        Surah(96, "العَلَق", "Al-'Alaq", "The Clot", "Meccan", 19),
        Surah(97, "القَدْر", "Al-Qadr", "The Power", "Meccan", 5),
        Surah(98, "البَيِّنَة", "Al-Bayyinah", "The Clear Proof", "Medinan", 8),
        Surah(99, "الزَّلْزَلَة", "Az-Zalzalah", "The Earthquake", "Medinan", 8),
        Surah(100, "العَادِيَات", "Al-'Adiyat", "The Courser", "Meccan", 11),
        Surah(101, "القَارِعَة", "Al-Qari'ah", "The Calamity", "Meccan", 11),
        Surah(102, "التَّكَاثُر", "At-Takathur", "The Rivalry in world increase", "Meccan", 8),
        Surah(103, "العَصْر", "Al-'Asr", "The Declining Day", "Meccan", 3),
        Surah(104, "الهُمَزَة", "Al-Humazah", "The Traducer", "Meccan", 9),
        Surah(105, "الفِيل", "Al-Fil", "The Elephant", "Meccan", 5),
        Surah(106, "قُرَيْش", "Quraysh", "Quraysh", "Meccan", 4),
        Surah(107, "المَاعُون", "Al-Ma'un", "The Small Kindness", "Meccan", 7),
        Surah(108, "الكَوْثَر", "Al-Kawthar", "The Abundance", "Meccan", 3),
        Surah(109, "الكَافِرُون", "Al-Kafirun", "The Disbelievers", "Meccan", 6),
        Surah(110, "النَّصْر", "An-Nasr", "The Divine Support", "Medinan", 3),
        Surah(111, "المَسَد", "Al-Masad", "The Palm Fiber", "Meccan", 5),
        Surah(112, "الإِخْلَاص", "Al-Ikhlas", "The Sincerity", "Meccan", 4),
        Surah(113, "الفَلَق", "Al-Falaq", "The Daybreak", "Meccan", 5),
        Surah(114, "النَّاس", "An-Nas", "Mankind", "Meccan", 6)
    )

    val juzList: List<JuzInfo> = listOf(
        JuzInfo(1, "الم", 1, "Al-Fatihah", 1, 148),
        JuzInfo(2, "سَيَقُولُ", 2, "Al-Baqarah", 142, 111),
        JuzInfo(3, "تِلْكَ الرُّسُلُ", 2, "Al-Baqarah", 253, 126),
        JuzInfo(4, "لَنْ تَنَالُوا", 3, "Ali 'Imran", 93, 131),
        JuzInfo(5, "وَالْمُحْصَنَاتُ", 4, "An-Nisa", 24, 124),
        JuzInfo(6, "لَا يُحِبُّ اللَّهُ", 4, "An-Nisa", 148, 110),
        JuzInfo(7, "وَإِذَا سَمِعُوا", 5, "Al-Ma'idah", 82, 149),
        JuzInfo(8, "وَلَوْ أَنَّنَا", 6, "Al-An'am", 111, 142),
        JuzInfo(9, "قَالَ الْمَلَأُ", 7, "Al-A'raf", 88, 159),
        JuzInfo(10, "وَاعْلَمُوا", 8, "Al-Anfal", 41, 127),
        JuzInfo(11, "يَعْتَذِرُونَ", 9, "At-Tawbah", 93, 151),
        JuzInfo(12, "وَمَا مِنْ دَابَّةٍ", 11, "Hud", 6, 170),
        JuzInfo(13, "وَمَا أُبَرِّئُ", 12, "Yusuf", 53, 154),
        JuzInfo(14, "رُبَمَا", 15, "Al-Hijr", 1, 227),
        JuzInfo(15, "سُبْحَانَ الَّذِي", 17, "Al-Isra", 1, 185),
        JuzInfo(16, "قَالَ أَلَمْ", 18, "Al-Kahf", 75, 269),
        JuzInfo(17, "اقْتَرَبَ لِلنَّاسِ", 21, "Al-Anbiya", 1, 190),
        JuzInfo(18, "قَدْ أَفْلَحَ", 23, "Al-Mu'minun", 1, 202),
        JuzInfo(19, "وَقَالَ الَّذِينَ", 25, "Al-Furqan", 21, 339),
        JuzInfo(20, "أَمَّنْ خَلَقَ", 27, "An-Naml", 56, 171),
        JuzInfo(21, "اتْلُ مَا أُوحِيَ", 29, "Al-'Ankabut", 46, 178),
        JuzInfo(22, "وَمَنْ يَقْنُتْ", 33, "Al-Ahzab", 31, 169),
        JuzInfo(23, "وَمَا لِيَ", 36, "Ya-Sin", 28, 357),
        JuzInfo(24, "فَمَنْ أَظْلَمُ", 39, "Az-Zumar", 32, 175),
        JuzInfo(25, "إِلَيْهِ يُرَدُّ", 41, "Fussilat", 47, 246),
        JuzInfo(26, "حم", 46, "Al-Ahqaf", 1, 195),
        JuzInfo(27, "قَالَ فَمَا خَطْبُكُمْ", 51, "Adh-Dhariyat", 31, 399),
        JuzInfo(28, "قَدْ سَمِعَ اللَّهُ", 58, "Al-Mujadila", 1, 137),
        JuzInfo(29, "تَبَارَكَ الَّذِي", 67, "Al-Mulk", 1, 431),
        JuzInfo(30, "عَمَّ يَتَسَاءَلُونَ", 78, "An-Naba", 1, 564)
    )

    val reciterList: List<ReciterInfo> = listOf(
        ReciterInfo(
            id = "sudais",
            name = "Abdul Rahman Al-Sudais",
            arabicName = "عبد الرحمن السديس",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Abdurrahmaan_As-Sudais_192kbps",
            bitRate = "192kbps",
            isFavorite = true,
            description = "Chief Imam and Khatib of the Grand Mosque in Makkah (Masjid al-Haram). Globally beloved voice with impassioned cadence."
        ),
        ReciterInfo(
            id = "juhany",
            name = "Sheikh Abdullah Al-Juhani",
            arabicName = "عبد الله عواد الجهني",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Abdullaah_3awwaad_Al-Juhaynee_128kbps",
            bitRate = "128kbps",
            isFavorite = true,
            description = "Imam of the Grand Mosque in Makkah (Masjid al-Haram). Authentic Murattal recordings with crystal-clear Tajwid and soothing resonance."
        ),
        ReciterInfo(
            id = "alafasy",
            name = "Mishary Rashid Alafasy",
            arabicName = "مشاري بن راشد العفاسي",
            country = "Kuwait",
            flagEmoji = "🇰🇼",
            style = "Murattal",
            audioFolder = "Alafasy_128kbps",
            bitRate = "128kbps",
            isFavorite = true,
            description = "Imam of the Grand Mosque of Kuwait. Renowned for resonant, melodic and deeply emotional recitation."
        ),
        ReciterInfo(
            id = "abdul_basit",
            name = "Abdul Basit Abdus-Samad",
            arabicName = "عبد الباسط عبد الصمد",
            country = "Egypt",
            flagEmoji = "🇪🇬",
            style = "Murattal",
            audioFolder = "Abdul_Basit_Murattal_192kbps",
            bitRate = "192kbps",
            isFavorite = true,
            description = "Legendary Egyptian Qari known as the 'Golden Throat'. Unrivaled breath control and classic Tajwid precision."
        ),
        ReciterInfo(
            id = "husary",
            name = "Mahmoud Khalil Al-Husary",
            arabicName = "محمود خليل الحصري",
            country = "Egypt",
            flagEmoji = "🇪🇬",
            style = "Murattal",
            audioFolder = "Husary_128kbps",
            bitRate = "128kbps",
            isFavorite = false,
            description = "Pioneer of modern Tajwid teaching recordings. Strict adherence to Makharij and Ahkam rules."
        ),
        ReciterInfo(
            id = "minshawi",
            name = "Mohamed Siddiq Al-Minshawi",
            arabicName = "محمد صديق المنشاوي",
            country = "Egypt",
            flagEmoji = "🇪🇬",
            style = "Murattal",
            audioFolder = "Minshawy_Murattal_128kbps",
            bitRate = "128kbps",
            isFavorite = false,
            description = "One of the quadrumvirate of Egyptian recitation masters. Revered for sorrowful, deeply moving spiritual cadence."
        ),
        ReciterInfo(
            id = "ghamdi",
            name = "Saad Al-Ghamdi",
            arabicName = "سعد الغامدي",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Ghamadi_40kbps",
            bitRate = "40kbps",
            isFavorite = false,
            description = "Prominent Saudi Imam and scholar from Dammam with clear, steady pace ideal for Quran learners."
        ),
        ReciterInfo(
            id = "muaiqly",
            name = "Maher Al-Muaiqly",
            arabicName = "ماهر المعيقلي",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Maher_AlMuaiqly_64kbps",
            bitRate = "64kbps",
            isFavorite = false,
            description = "Celebrated Imam of the Grand Mosque in Makkah (Masjid al-Haram) known for stirring Taraweeh prayers."
        ),
        ReciterInfo(
            id = "dosari",
            name = "Yasser Al-Dosari",
            arabicName = "ياسر الدوسري",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Yasser_Ad-Dussary_128kbps",
            bitRate = "128kbps",
            isFavorite = false,
            description = "Imam at Masjid al-Haram with a unique, powerful, and deeply captivating melodic voice."
        ),
        ReciterInfo(
            id = "shuraim",
            name = "Saud Ash-Shuraim",
            arabicName = "سعود الشريم",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Saood_ash-Shuraym_128kbps",
            bitRate = "128kbps",
            isFavorite = false,
            description = "Former Imam and Khatib of the Grand Mosque in Makkah. Fast-paced, disciplined Murattal recitation."
        ),
        ReciterInfo(
            id = "shatri",
            name = "Abu Bakr Ash-Shatri",
            arabicName = "أبو بكر الشاطري",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Abu_Bakr_Ash-Shaatree_128kbps",
            bitRate = "128kbps",
            isFavorite = false,
            description = "Renowned Saudi Qari with warm, harmonious, and highly meditative recitation style."
        ),
        ReciterInfo(
            id = "qatami",
            name = "Nasser Al-Qatami",
            arabicName = "ناصر القطامي",
            country = "Saudi Arabia",
            flagEmoji = "🇸🇦",
            style = "Murattal",
            audioFolder = "Nasser_Alqatami_128kbps",
            bitRate = "128kbps",
            isFavorite = false,
            description = "Imam of the Princess Latifa Mosque in Riyadh. Master of emotional pacing and crystal-clear pronunciation."
        )
    )

    val tajwidRulesList: List<TajwidRule> = listOf(
        TajwidRule(
            id = "mad_asli",
            name = "Al-Madd Al-Asli (Tabee'i)",
            arabicName = "المَدُّ الأَصْلِيُّ (الطَّبِيعِي)",
            category = "Mad",
            colorHex = 0xFFD97706, // Amber / Orange
            harakatCount = "2 Harakat (Counts)",
            description = "Natural elongation of vowel letters (Alif, Waw, Ya) without a Hamzah or Sukun following it.",
            ruleSummary = "Held for 2 Harakat (counts). Natural flow.",
            exampleArabic = "قَالَ • نُوحِيهَا",
            exampleTransliteration = "Qāla • Nūḥīhā",
            verseReference = "Al-Baqarah 2:2",
            pronunciationTip = "Do not shorten below 2 counts nor stretch beyond. Natural opening of mouth.",
            hasVerifiedAudio = true
        ),
        TajwidRule(
            id = "mad_lazim",
            name = "Al-Madd Al-Lazim",
            arabicName = "المَدُّ اللَّازِم",
            category = "Mad",
            colorHex = 0xFFDC2626, // Red
            harakatCount = "6 Harakat (Mandatory)",
            description = "Compulsory elongation caused by an original Sukun or Shaddah following a Madd letter.",
            ruleSummary = "Must be elongated for 6 Harakat (counts) unconditionally.",
            exampleArabic = "وَلَا الضَّالِّينَ • الحَاقَّةُ",
            exampleTransliteration = "Wa-lāḍ-ḍāllīn • Al-Ḥāqqah",
            verseReference = "Al-Fatihah 1:7",
            pronunciationTip = "Sustain the vowel smoothly for 6 full beats before applying the Shaddah.",
            hasVerifiedAudio = true
        ),
        TajwidRule(
            id = "ikhfa_haqiqi",
            name = "Ikhfa' Haqiqi",
            arabicName = "الإِخْفَاءُ الحَقِيقِي",
            category = "Ikhfa",
            colorHex = 0xFF059669, // Emerald
            harakatCount = "2 Counts (Nasal Ghunnah)",
            description = "Concealment of Nun Sakinah or Tanween when followed by any of the 15 Ikhfa letters with Ghunnah.",
            ruleSummary = "Hold nasal Ghunnah for 2 counts near the letter's articulation point.",
            exampleArabic = "مِن قَبْلِكُم • أَنزَلْنَا",
            exampleTransliteration = "Min qablikum • Anzalnā",
            verseReference = "Al-Baqarah 2:4",
            pronunciationTip = "Position tongue near the next letter's Makhraj while vibrating air through the nasal passage.",
            hasVerifiedAudio = true
        ),
        TajwidRule(
            id = "idgham_ghunnah",
            name = "Idgham Ma'al Ghunnah",
            arabicName = "الإِدْغَامُ بِغُنَّة",
            category = "Idgham",
            colorHex = 0xFF2563EB, // Blue
            harakatCount = "2 Counts (Nasal Ghunnah)",
            description = "Merging of Nun Sakinah or Tanween into one of the four letters (ي ن م و - Yanmu) with a 2-count nasal Ghunnah.",
            ruleSummary = "Merge sound completely with clear 2-count nasal resonance.",
            exampleArabic = "مَن يَقُولُ • هُدًى لِّلْمُتَّقِينَ",
            exampleTransliteration = "May-yaqūlu • Hudal-lilmuttaqīn",
            verseReference = "Al-Baqarah 2:8",
            pronunciationTip = "Blend the Nun into the next letter seamlessly while sustaining the nasal hum.",
            hasVerifiedAudio = true
        ),
        TajwidRule(
            id = "iqlab",
            name = "Iqlab (Conversion)",
            arabicName = "الإِقْلَاب",
            category = "Iqlab",
            colorHex = 0xFF7C3AED, // Purple
            harakatCount = "2 Counts (Nasal Ghunnah)",
            description = "Converting Nun Sakinah or Tanween into a hidden Meem with Ghunnah when followed by the letter Ba (ب).",
            ruleSummary = "Transform Nun to Mim with 2 counts Ghunnah and light lip contact.",
            exampleArabic = "مِن بَعْدِ • عَلِيمٌ بِذَاتِ الصُّدُورِ",
            exampleTransliteration = "Mim-ba'di • 'Alīmum bi-dhātis-ṣudūr",
            verseReference = "Al-Baqarah 2:27",
            pronunciationTip = "Close lips gently without pressing tightly while maintaining nasal vibration.",
            hasVerifiedAudio = true
        ),
        TajwidRule(
            id = "qalqalah",
            name = "Qalqalah (Echo / Bouncing)",
            arabicName = "القَلْقَلَة",
            category = "Qalqalah",
            colorHex = 0xFF0284C7, // Cyan
            harakatCount = "Echo Release",
            description = "Vibration or echoing sound made when pronouncing the 5 Qalqalah letters (ق ط ب ج د - Quṭb Jad) with Sukun.",
            ruleSummary = "Distinct echo release on Sukun. Stronger (Kubra) when stopping.",
            exampleArabic = "الفَلَقِ • أَحَدٌ • يَجْعَلُونَ",
            exampleTransliteration = "Al-Falaq • Aḥad • Yaj'alūn",
            verseReference = "Al-Ikhlas 112:1",
            pronunciationTip = "Quickly bounce off the articulation point without adding any vowel sound.",
            hasVerifiedAudio = true
        ),
        TajwidRule(
            id = "ghunnah_mushaddadah",
            name = "Ghunnah Musyaddadah",
            arabicName = "الغُنَّةُ المُشَدَّدَة",
            category = "Ghunnah",
            colorHex = 0xFF10B981, // Teal
            harakatCount = "2 Counts (Mandatory)",
            description = "Compulsory nasalization of 2 counts on Nun with Shaddah (نّ) and Meem with Shaddah (مّ).",
            ruleSummary = "Full 2 Harakat nasal resonance from the nasal cavity (Khayshum).",
            exampleArabic = "إِنَّ • ثُمَّ • النَّاسِ",
            exampleTransliteration = "Inna • Thumma • An-Nās",
            verseReference = "An-Nas 114:1",
            pronunciationTip = "Hold the letter firmly with clear acoustic resonance from the nose.",
            hasVerifiedAudio = true
        )
    )

    val verifiedTafsirList: List<com.example.data.model.VerseTafsir> = listOf(
        com.example.data.model.VerseTafsir(
            surahNumber = 1,
            verseNumber = 1,
            surahName = "Al-Fatihah",
            textArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            transliteration = "Bismillāhir-Raḥmānir-Raḥīm",
            translationMalay = "Dengan nama Allah, Yang Maha Pemurah, lagi Maha Mengasihani.",
            translationEnglish = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
            tafsirSummaryMalay = "Ayat pembukaan yang mengajar hamba agar memulakan setiap urusan dengan memohon keberkatan Nama Allah, yang rahmat-Nya meliputi seluruh makhluk di dunia dan dikhususkan untuk orang beriman di akhirat.",
            tafsirSummaryEnglish = "The opening verse instructing the servant to commence every action by seeking blessings through Allah's name, whose mercy encompasses all creation.",
            sourceName = "Tafsir Ringkas JAKIM & Tafsir Ibn Kathir",
            keyThemes = listOf("Tawhid", "Divine Mercy", "Supplication"),
            revelationContext = "Meccan revelation at the beginning of the Prophet's mission."
        ),
        com.example.data.model.VerseTafsir(
            surahNumber = 1,
            verseNumber = 2,
            surahName = "Al-Fatihah",
            textArabic = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
            transliteration = "Al-ḥamdu lillāhi Rabbil-ʿālamīn",
            translationMalay = "Segala puji tertentu bagi Allah, Tuhan sekalian alam.",
            translationEnglish = "[All] praise is [due] to Allah, Lord of the worlds -",
            tafsirSummaryMalay = "Pujian mutlak yang sempurna hanya layak bagi Allah Ta'ala semata-mata, Pencipta, Pemelihara, dan Pentadbir seluruh alam semesta.",
            tafsirSummaryEnglish = "Absolute and complete praise belongs solely to Allah, the Creator, Sustainer, and Nourisher of all realms of existence.",
            sourceName = "Tafsir Ringkas JAKIM & Tafsir Ibn Kathir",
            keyThemes = listOf("Gratitude", "Lordship (Rububiyyah)", "Universal Praise")
        ),
        com.example.data.model.VerseTafsir(
            surahNumber = 1,
            verseNumber = 7,
            surahName = "Al-Fatihah",
            textArabic = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
            transliteration = "Ṣirāṭallaḏhīna anʿamta ʿalayhim ghayril-maghḍūbi ʿalayhim walāḍ-ḍāllīn",
            translationMalay = "Iaitu jalan orang-orang yang Engkau telah kurniakan nikmat kepada mereka, bukan jalan orang-orang yang dimurkai dan bukan pula jalan orang-orang yang sesat.",
            translationEnglish = "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
            tafsirSummaryMalay = "Permohonan hidayah berterusan ke jalan para Nabi, Siddiqin, Syuhada dan Solihin, serta dijauhkan daripada jalan yang dimurkai dan jalan kesesatan.",
            tafsirSummaryEnglish = "Supplication for steadfast guidance upon the path of the righteous prophets and saints, avoiding the paths of disobedience and misguidance.",
            sourceName = "Tafsir Ringkas JAKIM & Tafsir Ibn Kathir",
            keyThemes = listOf("Hidayah", "Steadfastness", "The Right Path")
        ),
        com.example.data.model.VerseTafsir(
            surahNumber = 2,
            verseNumber = 255,
            surahName = "Al-Baqarah",
            textArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ...",
            transliteration = "Allāhu lā ilāha illā huwal-Ḥayyul-Qayyūm...",
            translationMalay = "Allah, tiada Tuhan melainkan Dia, Yang Tetap Hidup, Yang Kekal selama-lamanya mentadbirkan sekalian makhluk-Nya. Tiada mengantuk dan tiada tidur...",
            translationEnglish = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep...",
            tafsirSummaryMalay = "Ayat al-Kursi, ayat paling agung dalam Al-Quran yang menghimpunkan 5 sifat ketuhanan tertinggi: Keesaan, Hidup yang Mutlak (Al-Hayy), Pemelihara yang Berdiri Sendiri (Al-Qayyum), Pengetahuan yang meliputi segalanya, dan Kekuasaan mutlak di langit dan bumi.",
            tafsirSummaryEnglish = "Ayat al-Kursi (The Verse of the Throne), the greatest verse in the Holy Quran, expounding Allah's absolute oneness, eternal sovereignty, all-encompassing knowledge, and supreme majesty.",
            sourceName = "Tafsir Ibn Kathir & Sahih International",
            keyThemes = listOf("Tawhid", "Ayat al-Kursi", "Divine Majesty", "Protection"),
            revelationContext = "Medinan revelation, praised extensively in authentic Sunnah."
        ),
        com.example.data.model.VerseTafsir(
            surahNumber = 112,
            verseNumber = 1,
            surahName = "Al-Ikhlas",
            textArabic = "قُلْ هُوَ اللَّهُ أَحَدٌ",
            transliteration = "Qul Huwal-Lāhu Aḥad",
            translationMalay = "Katakanlah (wahai Muhammad): Dialah Allah, Yang Maha Esa.",
            translationEnglish = "Say, 'He is Allah, [who is] One.'",
            tafsirSummaryMalay = "Penegasan tauhid yang tulen dan suci. Allah Maha Esa dalam Zat-Nya, sifat-sifat-Nya, dan perbuatan-Nya, tiada sekutu atau tandingan bagi-Nya.",
            tafsirSummaryEnglish = "Declaration of absolute monotheism. Allah is uniquely One in His Essence, Attributes, and Actions, without partners or equals.",
            sourceName = "Tafsir Ringkas JAKIM",
            keyThemes = listOf("Tawhid Al-Khalis", "Purity of Faith")
        )
    )

    fun getTafsirForVerse(surahNumber: Int, verseNumber: Int): com.example.data.model.VerseTafsir? {
        return verifiedTafsirList.find { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
    }
}
