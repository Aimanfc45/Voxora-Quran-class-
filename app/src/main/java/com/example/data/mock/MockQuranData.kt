package com.example.data.mock

import com.example.data.model.JuzInfo
import com.example.data.model.Surah
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
}
