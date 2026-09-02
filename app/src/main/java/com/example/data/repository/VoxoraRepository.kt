package com.example.data.repository

import com.example.data.api.IQuranApiService
import com.example.data.api.QuranApiService
import com.example.data.audio.QuranAudioPlayerEngine
import com.example.data.mock.*
import com.example.data.model.*
import com.example.data.sync.SyncStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class VoxoraRepository(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
    private val quranApiService: IQuranApiService = QuranApiService()
) {

    // ----------------------------------------------------
    // User & Progress State
    // ----------------------------------------------------
    private val _hasCompletedOnboarding = MutableStateFlow(true)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private val _cloudSyncStatus = MutableStateFlow(SyncStatus.IDLE)
    val cloudSyncStatus: StateFlow<SyncStatus> = _cloudSyncStatus.asStateFlow()

    private val _authMode = MutableStateFlow(AuthMode.AUTHENTICATED)
    val authMode: StateFlow<AuthMode> = _authMode.asStateFlow()

    private val _userProfile = MutableStateFlow(MockUserData.currentUser)
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _progress = MutableStateFlow(MockUserData.userProgress)
    val progress: StateFlow<LearningProgress> = _progress.asStateFlow()

    private val _achievements = MutableStateFlow(MockUserData.achievements)
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    // ----------------------------------------------------
    // Reciters & Tajwid Catalog State
    // ----------------------------------------------------
    private val _reciters = MutableStateFlow(MockQuranData.reciterList)
    val reciters: StateFlow<List<ReciterInfo>> = _reciters.asStateFlow()

    private val _tajwidRules = MutableStateFlow(MockQuranData.tajwidRulesList)
    val tajwidRules: StateFlow<List<TajwidRule>> = _tajwidRules.asStateFlow()

    private val _selectedTajwidFilter = MutableStateFlow<String?>(null)
    val selectedTajwidFilter: StateFlow<String?> = _selectedTajwidFilter.asStateFlow()

    // ----------------------------------------------------
    // Search & History State
    // ----------------------------------------------------
    private val _searchResults = MutableStateFlow<List<QuranSearchResult>>(emptyList())
    val searchResults: StateFlow<List<QuranSearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _recentSearches = MutableStateFlow(
        listOf("Al-Baqarah", "Ayat al-Kursi", "Mad Asli", "Surah Al-Fatihah", "Qalqalah", "Bismillah")
    )
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    // ----------------------------------------------------
    // Notifications State
    // ----------------------------------------------------
    private val _notifications = MutableStateFlow(MockNotificationData.initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = _notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(coroutineScope, SharingStarted.Eagerly, 2)

    // ----------------------------------------------------
    // Quran State
    // ----------------------------------------------------
    private val _surahs = MutableStateFlow(MockQuranData.surahList)
    val surahs: StateFlow<List<Surah>> = _surahs.asStateFlow()

    private val _juzList = MutableStateFlow(MockQuranData.juzList)
    val juzList: StateFlow<List<JuzInfo>> = _juzList.asStateFlow()

    private val _selectedSurah = MutableStateFlow(MockQuranData.surahList.first { it.number == 1 })
    val selectedSurah: StateFlow<Surah> = _selectedSurah.asStateFlow()

    private val _currentVerseIndex = MutableStateFlow(0)
    val currentVerseIndex: StateFlow<Int> = _currentVerseIndex.asStateFlow()

    private val _quranSettings = MutableStateFlow(QuranSettings())
    val quranSettings: StateFlow<QuranSettings> = _quranSettings.asStateFlow()

    private val _bookmarks = MutableStateFlow(MockUserData.initialBookmarks)
    val bookmarks: StateFlow<List<QuranBookmark>> = _bookmarks.asStateFlow()

    private val _verseHighlights = MutableStateFlow<List<VerseHighlight>>(
        listOf(
            VerseHighlight(id = "vh_1", surahNumber = 1, verseNumber = 1, colorHex = 0xFFF59E0B), // Gold
            VerseHighlight(id = "vh_2", surahNumber = 2, verseNumber = 255, colorHex = 0xFF10B981) // Emerald
        )
    )
    val verseHighlights: StateFlow<List<VerseHighlight>> = _verseHighlights.asStateFlow()

    private val _verseNotes = MutableStateFlow<List<VerseNote>>(
        listOf(
            VerseNote(
                id = "vn_1",
                surahNumber = 1,
                surahName = "Al-Fatihah",
                verseNumber = 7,
                snippetArabic = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ...",
                noteText = "Pay special attention to the 6-count Mad Lazim Kalimi on 'Ad-Dallin'.",
                timestamp = System.currentTimeMillis() - 86400000L,
                isPrivate = true
            ),
            VerseNote(
                id = "vn_2",
                surahNumber = 2,
                surahName = "Al-Baqarah",
                verseNumber = 255,
                snippetArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ...",
                noteText = "Reflect deeply on Allah's eternal vigilance and supreme Sovereignty.",
                timestamp = System.currentTimeMillis() - 43200000L,
                isPrivate = true
            )
        )
    )
    val verseNotes: StateFlow<List<VerseNote>> = _verseNotes.asStateFlow()

    private val _lastReadPosition = MutableStateFlow("Surah Al-Baqarah (2:2)")
    val lastReadPosition: StateFlow<String> = _lastReadPosition.asStateFlow()

    // ----------------------------------------------------
    // Quran Audio Engine State (Verified Audio CDN)
    // ----------------------------------------------------
    private val audioEngine: QuranAudioPlayerEngine = QuranAudioPlayerEngine(
        coroutineScope = coroutineScope,
        onVerseChangedListener = { surahNumber, verseNumber ->
            val s = _surahs.value.find { it.number == surahNumber } ?: _selectedSurah.value
            if (_selectedSurah.value.number != surahNumber) {
                _selectedSurah.value = s
            }
            val vIndex = s.verses.indexOfFirst { it.verseNumber == verseNumber }
            if (vIndex >= 0) {
                _currentVerseIndex.value = vIndex
            }
            updateLastReadPosition()
        },
        getSurahVerseCount = { sNum ->
            _surahs.value.find { it.number == sNum }?.totalVerses ?: 7
        }
    )

    val audioState: StateFlow<QuranAudioState> = audioEngine.audioState

    // Backward-compatible flow for reader/home
    val isPlayingAudio: StateFlow<Boolean> = audioEngine.audioState.map { it.isPlaying }
        .stateIn(coroutineScope, SharingStarted.Eagerly, false)

    init {
        coroutineScope.launch {
            val catalogResult = quranApiService.getSurahList()
            if (catalogResult.isSuccess) {
                val list = catalogResult.getOrNull() ?: emptyList()
                if (list.isNotEmpty()) {
                    _surahs.value = list
                }
            }
        }
    }

    // ----------------------------------------------------
    // Classes State
    // ----------------------------------------------------
    private val _liveClass = MutableStateFlow(MockClassData.liveTajwidClass)
    val liveClass: StateFlow<QuranClass> = _liveClass.asStateFlow()

    private val _upcomingClasses = MutableStateFlow(MockClassData.upcomingClasses)
    val upcomingClasses: StateFlow<List<QuranClass>> = _upcomingClasses.asStateFlow()

    private val _completedClasses = MutableStateFlow(MockClassData.completedClasses)
    val completedClasses: StateFlow<List<QuranClass>> = _completedClasses.asStateFlow()

    private val _teachers = MutableStateFlow(MockClassData.allTeachers)
    val teachers: StateFlow<List<Teacher>> = _teachers.asStateFlow()

    // ----------------------------------------------------
    // Live Classroom Active Session State
    // ----------------------------------------------------
    private val _participants = MutableStateFlow(
        listOf(
            Participant("p_teacher", "Ustaz Ahmad Al-Azhari", isHandRaised = false, isMicMuted = false, isVideoOn = true, isTeacher = true, role = "Instructor", isSpeaking = true),
            Participant("p_1", "Ahmed Al-Farsi (You)", isHandRaised = false, isMicMuted = true, isVideoOn = true, isTeacher = false, role = "Student"),
            Participant("p_2", "Zainab Noor", isHandRaised = true, isMicMuted = true, isVideoOn = true, isTeacher = false, role = "Student"),
            Participant("p_3", "Tariq Malik", isHandRaised = false, isMicMuted = true, isVideoOn = false, isTeacher = false, role = "Student"),
            Participant("p_4", "Amina Yusuf", isHandRaised = false, isMicMuted = true, isVideoOn = true, isTeacher = false, role = "Student"),
            Participant("p_5", "Bilal Siddiqui", isHandRaised = false, isMicMuted = true, isVideoOn = true, isTeacher = false, role = "Student")
        )
    )
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ClassChatMessage("m_1", "Ustaz Ahmad", "Assalamu Alaikum everyone. Welcome to today's Tajwid session on Mad Asli.", "08:00 PM", isTeacher = true),
            ClassChatMessage("m_2", "Zainab Noor", "Wa Alaikum Assalam Ustaz! Ready.", "08:01 PM"),
            ClassChatMessage("m_3", "Ahmed Al-Farsi", "Wa Alaikum Assalam Ustaz, excited to learn!", "08:02 PM", isMe = true)
        )
    )
    val chatMessages: StateFlow<List<ClassChatMessage>> = _chatMessages.asStateFlow()

    private val _isMyMicMuted = MutableStateFlow(true)
    val isMyMicMuted: StateFlow<Boolean> = _isMyMicMuted.asStateFlow()

    private val _isMyVideoOn = MutableStateFlow(true)
    val isMyVideoOn: StateFlow<Boolean> = _isMyVideoOn.asStateFlow()

    private val _isMySpeakerOn = MutableStateFlow(true)
    val isMySpeakerOn: StateFlow<Boolean> = _isMySpeakerOn.asStateFlow()

    private val _isMyHandRaised = MutableStateFlow(false)
    val isMyHandRaised: StateFlow<Boolean> = _isMyHandRaised.asStateFlow()

    private val _classHighlightedVerse = MutableStateFlow(2)
    val classHighlightedVerse: StateFlow<Int> = _classHighlightedVerse.asStateFlow()

    private val _teacherAnnotation = MutableStateFlow<String?>("Ustaz Ahmad: Notice the 2 counts on Mad Asli in ذَٰلِكَ")
    val teacherAnnotation: StateFlow<String?> = _teacherAnnotation.asStateFlow()

    private val _liveClassMode = MutableStateFlow(ClassType.GROUP)
    val liveClassMode: StateFlow<ClassType> = _liveClassMode.asStateFlow()

    // ----------------------------------------------------
    // Community & Groups State
    // ----------------------------------------------------
    private val _communityGroups = MutableStateFlow(MockCommunityData.communityGroups)
    val communityGroups: StateFlow<List<CommunityGroup>> = _communityGroups.asStateFlow()

    private val _posts = MutableStateFlow(MockCommunityData.initialPosts)
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _mutedUsers = MutableStateFlow<Set<String>>(emptySet())
    val mutedUsers: StateFlow<Set<String>> = _mutedUsers.asStateFlow()

    private val _blockedUsers = MutableStateFlow<Set<String>>(emptySet())
    val blockedUsers: StateFlow<Set<String>> = _blockedUsers.asStateFlow()

    // ----------------------------------------------------
    // Settings State
    // ----------------------------------------------------
    private val _appLanguage = MutableStateFlow("English")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow("Dark") // "Light", "Dark", "System"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _classReminders = MutableStateFlow(true)
    val classReminders: StateFlow<Boolean> = _classReminders.asStateFlow()

    private val _dailyVerseReminder = MutableStateFlow(true)
    val dailyVerseReminder: StateFlow<Boolean> = _dailyVerseReminder.asStateFlow()

    // ====================================================
    // QURAN ACTIONS & NAVIGATION
    // ====================================================

    fun selectSurah(surahNumber: Int) {
        val found = _surahs.value.find { it.number == surahNumber }
            ?: MockQuranData.surahList.first()
        _selectedSurah.value = found
        _currentVerseIndex.value = 0
        updateLastReadPosition()

        if (found.verses.isEmpty()) {
            coroutineScope.launch {
                val detailResult = quranApiService.getSurahDetail(surahNumber)
                if (detailResult.isSuccess) {
                    val fullSurah = detailResult.getOrNull()
                    if (fullSurah != null) {
                        _selectedSurah.value = fullSurah
                        _surahs.update { list ->
                            list.map { if (it.number == surahNumber) fullSurah else it }
                        }
                    }
                }
            }
        }
    }

    fun selectJuz(juzNumber: Int) {
        val juz = _juzList.value.find { it.number == juzNumber } ?: _juzList.value.first()
        selectSurah(juz.startSurahNumber)
        val verseIndex = (juz.startVerse - 1).coerceAtLeast(0)
        _currentVerseIndex.value = verseIndex
        updateLastReadPosition()
    }

    fun jumpToVerse(verseNumber: Int) {
        val verses = _selectedSurah.value.verses
        val targetIndex = verses.indexOfFirst { it.verseNumber == verseNumber }
        if (targetIndex >= 0) {
            _currentVerseIndex.value = targetIndex
            updateLastReadPosition()
        }
    }

    fun nextVerse() {
        val verses = _selectedSurah.value.verses
        if (verses.isNotEmpty() && _currentVerseIndex.value < verses.size - 1) {
            _currentVerseIndex.update { it + 1 }
            updateLastReadPosition()
        }
    }

    fun previousVerse() {
        if (_currentVerseIndex.value > 0) {
            _currentVerseIndex.update { it - 1 }
            updateLastReadPosition()
        }
    }

    fun setVerseIndex(index: Int) {
        val max = _selectedSurah.value.verses.size - 1
        if (index in 0..max) {
            _currentVerseIndex.value = index
            updateLastReadPosition()
        }
    }

    private fun updateLastReadPosition() {
        val s = _selectedSurah.value
        val v = s.verses.getOrNull(_currentVerseIndex.value)?.verseNumber ?: 1
        _lastReadPosition.value = "Surah ${s.nameEnglish} (${s.number}:$v)"
    }

    // ====================================================
    // QURAN AUDIO PLAYER ENGINE (Clean Architecture)
    // ====================================================

    fun playVerseAudio(surahNumber: Int, verseNumber: Int) {
        val surah = _surahs.value.find { it.number == surahNumber } ?: _selectedSurah.value
        if (_selectedSurah.value.number != surahNumber) {
            _selectedSurah.value = surah
        }
        val verseIndex = surah.verses.indexOfFirst { it.verseNumber == verseNumber }
        if (verseIndex >= 0) {
            _currentVerseIndex.value = verseIndex
        }
        updateLastReadPosition()
        audioEngine.playVerse(surahNumber, verseNumber)
    }

    fun toggleAudioPlayback() {
        if (audioState.value.isPlaying) {
            pauseAudio()
        } else {
            val currentV = _selectedSurah.value.verses.getOrNull(_currentVerseIndex.value)?.verseNumber ?: 1
            playVerseAudio(_selectedSurah.value.number, currentV)
        }
    }

    fun pauseAudio() {
        audioEngine.pause()
    }

    fun resumeAudio() {
        audioEngine.resume()
    }

    fun stopAudio() {
        audioEngine.stop()
    }

    fun seekAudioTo(positionSeconds: Float) {
        audioEngine.seekTo(positionSeconds)
    }

    fun setAudioPlaybackSpeed(speed: Float) {
        audioEngine.setPlaybackSpeed(speed)
    }

    fun setAudioVolume(volume: Float) {
        audioEngine.setVolume(volume)
    }

    fun setAudioRepeatMode(mode: AudioRepeatMode) {
        audioEngine.setRepeatMode(mode)
    }

    fun setAudioRepeatCount(times: Int) {
        audioEngine.setRepeatCount(times)
    }

    fun setAudioRepeatRange(startVerse: Int, endVerse: Int) {
        audioEngine.setRepeatRange(startVerse, endVerse)
    }

    fun toggleAutoNextVerse(enabled: Boolean) {
        audioEngine.toggleAutoNext(enabled)
    }

    fun setAudioReciter(reciter: String) {
        val found = com.example.data.mock.MockQuranData.reciterList.find {
            it.id.equals(reciter.trim(), ignoreCase = true) ||
            it.name.equals(reciter.trim(), ignoreCase = true) ||
            it.audioFolder.equals(reciter.trim(), ignoreCase = true)
        }
        val verifiedName = found?.name ?: reciter
        audioEngine.setReciter(reciter)
        _quranSettings.update { it.copy(reciterName = verifiedName, selectedReciter = verifiedName) }
    }

    fun setDefaultReciter(reciterName: String) {
        val found = com.example.data.mock.MockQuranData.reciterList.find {
            it.id.equals(reciterName.trim(), ignoreCase = true) ||
            it.name.equals(reciterName.trim(), ignoreCase = true) ||
            it.audioFolder.equals(reciterName.trim(), ignoreCase = true)
        }
        val verifiedName = found?.name ?: reciterName
        _quranSettings.update { it.copy(defaultReciter = verifiedName, reciterName = verifiedName, selectedReciter = verifiedName) }
        audioEngine.setReciter(reciterName)
        _reciters.update { list ->
            list.map { it.copy(isDefault = (it.name == verifiedName || it.id == reciterName)) }
        }
    }

    fun previewReciterAudio(reciterName: String) {
        audioEngine.previewReciterAudio(reciterName)
    }

    fun stopAudioPreview() {
        audioEngine.stopAudioPreview()
    }

    fun toggleFavoriteReciter(reciterName: String) {
        _quranSettings.update { settings ->
            val favs = settings.favoriteReciters.toMutableSet()
            if (favs.contains(reciterName)) {
                favs.remove(reciterName)
            } else {
                favs.add(reciterName)
            }
            settings.copy(favoriteReciters = favs)
        }
        _reciters.update { list ->
            list.map {
                if (it.name == reciterName) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
    }

    fun togglePlayPause() {
        if (audioEngine.audioState.value.isPlaying) {
            audioEngine.pause()
        } else {
            audioEngine.resume()
        }
    }

    fun playNextVerse() {
        audioEngine.nextVerse()
    }

    fun playPreviousVerse() {
        audioEngine.previousVerse()
    }

    fun nextAudioVerse() {
        audioEngine.nextVerse()
    }

    fun previousAudioVerse() {
        audioEngine.previousVerse()
    }

    fun setReciter(reciter: String) {
        setAudioReciter(reciter)
    }

    // ====================================================
    // QURAN SEARCH & HISTORY
    // ====================================================

    fun searchQuran(query: String): List<QuranSearchResult> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return emptyList()
        }

        _isSearching.value = true
        val results = mutableListOf<QuranSearchResult>()

        // 1. Check for Surah:Ayah pattern e.g. "2:255" or "1:1"
        val colonMatch = Regex("""^(\d{1,3}):(\d{1,3})$""").find(trimmed)
        if (colonMatch != null) {
            val sNum = colonMatch.groupValues[1].toIntOrNull()
            val vNum = colonMatch.groupValues[2].toIntOrNull()
            if (sNum != null && vNum != null) {
                val surah = _surahs.value.find { it.number == sNum }
                if (surah != null) {
                    val verse = surah.verses.find { it.verseNumber == vNum }
                    if (verse != null) {
                        results.add(
                            QuranSearchResult(
                                surahNumber = surah.number,
                                surahName = surah.nameEnglish,
                                verseNumber = verse.verseNumber,
                                textArabic = verse.textArabic,
                                textEnglish = verse.translationEnglish,
                                textMalay = verse.translationMalay,
                                matchType = "Surah:Ayah Match"
                            )
                        )
                    }
                }
            }
        }

        // 2. Search across Surahs & Verses
        val lower = trimmed.lowercase()
        for (surah in _surahs.value) {
            // Surah name match
            if (surah.nameEnglish.lowercase().contains(lower) ||
                surah.nameArabic.contains(trimmed) ||
                surah.nameTranslation.lowercase().contains(lower) ||
                surah.number.toString() == trimmed
            ) {
                val firstVerse = surah.verses.firstOrNull()
                if (firstVerse != null && results.none { it.surahNumber == surah.number && it.verseNumber == firstVerse.verseNumber }) {
                    results.add(
                        QuranSearchResult(
                            surahNumber = surah.number,
                            surahName = surah.nameEnglish,
                            verseNumber = firstVerse.verseNumber,
                            textArabic = firstVerse.textArabic,
                            textEnglish = firstVerse.translationEnglish,
                            textMalay = firstVerse.translationMalay,
                            matchType = "Surah Match"
                        )
                    )
                }
            }

            // Verse text match (Arabic, English, Malay, Transliteration)
            for (verse in surah.verses) {
                val arabicMatch = verse.textArabic.contains(trimmed)
                val engMatch = verse.translationEnglish.lowercase().contains(lower)
                val malayMatch = verse.translationMalay.lowercase().contains(lower)
                val translitMatch = verse.transliteration.lowercase().contains(lower)

                if (arabicMatch || engMatch || malayMatch || translitMatch) {
                    if (results.none { it.surahNumber == surah.number && it.verseNumber == verse.verseNumber }) {
                        val matchType = when {
                            arabicMatch -> "Arabic Text"
                            engMatch -> "English Translation"
                            malayMatch -> "Malay Translation"
                            else -> "Transliteration"
                        }
                        results.add(
                            QuranSearchResult(
                                surahNumber = surah.number,
                                surahName = surah.nameEnglish,
                                verseNumber = verse.verseNumber,
                                textArabic = verse.textArabic,
                                textEnglish = verse.translationEnglish,
                                textMalay = verse.translationMalay,
                                matchType = matchType
                            )
                        )
                    }
                }
            }
        }

        _searchResults.value = results
        _isSearching.value = false
        return results
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        _recentSearches.update { list ->
            listOf(trimmed) + list.filterNot { it.equals(trimmed, ignoreCase = true) }.take(9)
        }
    }

    fun removeRecentSearch(query: String) {
        _recentSearches.update { list -> list.filterNot { it == query } }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    // ====================================================
    // BOOKMARKS & PRIVATE NOTES CRUD
    // ====================================================

    fun toggleBookmark(surah: Surah, verse: Verse, customNote: String = "", category: String = "Favourite Verses"): Boolean {
        val existing = _bookmarks.value.find { it.surahNumber == surah.number && it.verseNumber == verse.verseNumber }
        return if (existing != null) {
            _bookmarks.update { it.filterNot { b -> b.id == existing.id } }
            false
        } else {
            val newBm = QuranBookmark(
                id = "bm_${System.currentTimeMillis()}",
                surahNumber = surah.number,
                surahName = surah.nameEnglish,
                verseNumber = verse.verseNumber,
                snippetArabic = verse.textArabic.take(35) + "...",
                title = "${surah.nameEnglish} $surah:${verse.verseNumber}",
                note = customNote.ifBlank { "Bookmarked verse" },
                category = category
            )
            _bookmarks.update { listOf(newBm) + it }
            true
        }
    }

    fun addBookmarkWithDetails(surah: Surah, verse: Verse, title: String, note: String, category: String) {
        val existing = _bookmarks.value.find { it.surahNumber == surah.number && it.verseNumber == verse.verseNumber }
        val newBm = QuranBookmark(
            id = existing?.id ?: "bm_${System.currentTimeMillis()}",
            surahNumber = surah.number,
            surahName = surah.nameEnglish,
            verseNumber = verse.verseNumber,
            snippetArabic = verse.textArabic.take(35) + "...",
            title = title.ifBlank { "${surah.nameEnglish} ${surah.number}:${verse.verseNumber}" },
            note = note,
            category = category
        )
        _bookmarks.update { list ->
            if (existing != null) {
                list.map { if (it.id == existing.id) newBm else it }
            } else {
                listOf(newBm) + list
            }
        }
    }

    fun updateBookmarkCategory(bookmarkId: String, newCategory: String) {
        _bookmarks.update { list ->
            list.map { if (it.id == bookmarkId) it.copy(category = newCategory) else it }
        }
    }

    fun removeBookmark(bookmarkId: String) {
        _bookmarks.update { it.filterNot { b -> b.id == bookmarkId } }
    }

    fun isVerseBookmarked(surahNumber: Int, verseNumber: Int): Boolean {
        return _bookmarks.value.any { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
    }

    // ====================================================
    // VERSE HIGHLIGHTS
    // ====================================================

    fun toggleVerseHighlight(surahNumber: Int, verseNumber: Int, colorHex: Long): Boolean {
        val existing = _verseHighlights.value.find { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
        return if (existing != null && existing.colorHex == colorHex) {
            _verseHighlights.update { it.filterNot { h -> h.id == existing.id } }
            false
        } else if (existing != null) {
            _verseHighlights.update { list ->
                list.map { if (it.id == existing.id) it.copy(colorHex = colorHex) else it }
            }
            true
        } else {
            val newHighlight = VerseHighlight(
                id = "vh_${System.currentTimeMillis()}",
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                colorHex = colorHex
            )
            _verseHighlights.update { listOf(newHighlight) + it }
            true
        }
    }

    fun removeVerseHighlight(surahNumber: Int, verseNumber: Int) {
        _verseHighlights.update { it.filterNot { it.surahNumber == surahNumber && it.verseNumber == verseNumber } }
    }

    fun getVerseHighlight(surahNumber: Int, verseNumber: Int): VerseHighlight? {
        return _verseHighlights.value.find { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
    }

    fun addVerseNote(surahNumber: Int, verseNumber: Int, noteText: String) {
        if (noteText.isBlank()) return
        val targetSurah = _surahs.value.find { it.number == surahNumber }
        val targetVerse = targetSurah?.verses?.find { it.verseNumber == verseNumber }
        val surahName = targetSurah?.nameEnglish ?: "Surah $surahNumber"
        val snippet = targetVerse?.textArabic?.take(40)?.let { "$it..." } ?: ""
        val newNote = VerseNote(
            id = "vn_${System.currentTimeMillis()}",
            surahNumber = surahNumber,
            surahName = surahName,
            verseNumber = verseNumber,
            snippetArabic = snippet,
            noteText = noteText.trim(),
            isPrivate = true
        )
        _verseNotes.update { listOf(newNote) + it }
    }

    fun addVerseNote(surah: Surah, verse: Verse, noteText: String) {
        if (noteText.isBlank()) return
        val newNote = VerseNote(
            id = "vn_${System.currentTimeMillis()}",
            surahNumber = surah.number,
            surahName = surah.nameEnglish,
            verseNumber = verse.verseNumber,
            snippetArabic = verse.textArabic.take(40) + "...",
            noteText = noteText.trim(),
            isPrivate = true
        )
        _verseNotes.update { listOf(newNote) + it }
    }

    fun editVerseNote(noteId: String, newText: String) {
        if (newText.isBlank()) return
        _verseNotes.update { list ->
            list.map { if (it.id == noteId) it.copy(noteText = newText.trim(), timestamp = System.currentTimeMillis()) else it }
        }
    }

    fun deleteVerseNote(noteId: String) {
        _verseNotes.update { it.filterNot { n -> n.id == noteId } }
    }

    fun getNotesForVerse(surahNumber: Int, verseNumber: Int): List<VerseNote> {
        return _verseNotes.value.filter { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
    }

    // ====================================================
    // TAJWID LEARNING MODE
    // ====================================================

    fun filterByTajwidRule(category: String?) {
        _selectedTajwidFilter.value = category
    }

    // ====================================================
    // DISPLAY, READING MODE & FONT SETTINGS
    // ====================================================

    fun setReadingDisplayMode(mode: ReadingDisplayMode) {
        _quranSettings.update {
            when (mode) {
                ReadingDisplayMode.ARABIC_ONLY -> it.copy(
                    readingMode = mode,
                    showTranslation = false,
                    showTransliteration = false,
                    showEnglishTranslation = false,
                    showMalayTranslation = false
                )
                ReadingDisplayMode.ARABIC_EN -> it.copy(
                    readingMode = mode,
                    showTranslation = true,
                    showTransliteration = false,
                    showEnglishTranslation = true,
                    showMalayTranslation = false
                )
                ReadingDisplayMode.ARABIC_BM -> it.copy(
                    readingMode = mode,
                    showTranslation = true,
                    showTransliteration = false,
                    showEnglishTranslation = false,
                    showMalayTranslation = true
                )
                ReadingDisplayMode.ARABIC_TRANSLITERATION -> it.copy(
                    readingMode = mode,
                    showTranslation = false,
                    showTransliteration = true,
                    showEnglishTranslation = false,
                    showMalayTranslation = false
                )
                ReadingDisplayMode.MULTI_TRANSLATION -> it.copy(
                    readingMode = mode,
                    showTranslation = true,
                    showTransliteration = true,
                    showEnglishTranslation = true,
                    showMalayTranslation = true
                )
            }
        }
    }

    fun setQuranLineSpacing(spacing: QuranLineSpacing) {
        _quranSettings.update { it.copy(lineSpacing = spacing) }
    }

    fun updateQuranFontSize(sizeSp: Float) {
        _quranSettings.update { it.copy(arabicFontSizeSp = sizeSp) }
    }

    fun toggleTranslation(show: Boolean) {
        _quranSettings.update { it.copy(showTranslation = show) }
    }

    fun toggleTransliteration(show: Boolean) {
        _quranSettings.update { it.copy(showTransliteration = show) }
    }

    fun toggleMalayTranslation(show: Boolean) {
        _quranSettings.update { it.copy(showMalayTranslation = show) }
    }

    fun toggleWordByWord(show: Boolean) {
        _quranSettings.update { it.copy(showWordByWord = show) }
    }

    fun toggleTajwidColors(show: Boolean) {
        _quranSettings.update { it.copy(showTajwidColors = show) }
    }

    fun setTranslationLanguage(lang: String) {
        _quranSettings.update { it.copy(translationLanguage = lang) }
    }

    fun setQuranReadingTheme(theme: QuranReadingTheme) {
        _quranSettings.update { it.copy(readingTheme = theme) }
    }

    fun setReadingBrightness(brightness: Float) {
        _quranSettings.update { it.copy(readingBrightness = brightness.coerceIn(0.2f, 1.0f)) }
    }

    fun setArabicFontStyle(fontStyle: String) {
        _quranSettings.update { it.copy(arabicFontStyle = fontStyle) }
    }

    fun toggleTafsir(show: Boolean) {
        _quranSettings.update { it.copy(showTafsir = show) }
    }

    fun trackReadingTime(minutes: Int) {
        _userProfile.update {
            it.copy(hoursSpent = it.hoursSpent + (minutes / 60f))
        }
    }

    fun trackAyahRead(surahNumber: Int, verseNumber: Int) {
        val sName = _selectedSurah.value.nameEnglish
        _lastReadPosition.value = "Surah $sName ($surahNumber:$verseNumber)"
    }

    fun trackAudioListeningTime(seconds: Int) {
        _userProfile.update {
            it.copy(hoursSpent = it.hoursSpent + (seconds / 3600f))
        }
    }

    // ====================================================
    // AUTHENTICATION & USER PROFILE ACTIONS
    // ====================================================

    fun signIn(email: String, name: String = "Ahmed Al-Farsi") {
        _authMode.value = AuthMode.AUTHENTICATED
        _userProfile.update {
            it.copy(
                email = email.ifBlank { "ahmed.farsi@voxora.app" },
                name = name.ifBlank { "Ahmed Al-Farsi" },
                isGuest = false
            )
        }
    }

    fun signUp(name: String, email: String, country: String = "Malaysia") {
        _authMode.value = AuthMode.AUTHENTICATED
        _userProfile.update {
            it.copy(
                name = name.ifBlank { "Voxora Reciter" },
                email = email.ifBlank { "reciter@voxora.app" },
                country = country,
                isGuest = false
            )
        }
    }

    fun continueAsGuest() {
        _authMode.value = AuthMode.GUEST
        _userProfile.update {
            it.copy(
                name = "Guest Reciter",
                username = "@guest_reciter",
                email = "guest@voxora.local",
                isGuest = true
            )
        }
    }

    fun updateDailyGoal(minutes: Int, verses: Int = 10) {
        _userProfile.update { it.copy(dailyGoalMinutes = minutes, dailyGoalVerses = verses) }
    }

    fun signInUser(name: String, email: String) {
        signIn(email, name)
    }

    fun switchToGuestMode() {
        continueAsGuest()
    }

    fun setLineSpacing(mode: QuranLineSpacing) {
        setQuranLineSpacing(mode)
    }

    fun updateSelectedReciter(reciter: String, folderId: String? = null) {
        _quranSettings.update { it.copy(selectedReciter = reciter, reciterName = reciter) }
        audioEngine.setReciter(reciter)
    }

    fun updateAvatarEmoji(emoji: String) {
        _userProfile.update { it.copy(avatarEmoji = emoji) }
    }

    // ====================================================
    // LIVE CLASSROOM ACTIONS
    // ====================================================

    fun toggleMyMic() {
        val newState = !_isMyMicMuted.value
        _isMyMicMuted.value = newState
        _participants.update { list ->
            list.map { if (it.id == "p_1") it.copy(isMicMuted = newState) else it }
        }
    }

    fun toggleMyVideo() {
        val newState = !_isMyVideoOn.value
        _isMyVideoOn.value = newState
        _participants.update { list ->
            list.map { if (it.id == "p_1") it.copy(isVideoOn = newState) else it }
        }
    }

    fun toggleMySpeaker() {
        _isMySpeakerOn.update { !it }
    }

    fun toggleRaiseHand(): Boolean {
        val newState = !_isMyHandRaised.value
        _isMyHandRaised.value = newState
        _participants.update { list ->
            list.map { if (it.id == "p_1") it.copy(isHandRaised = newState) else it }
        }
        return newState
    }

    fun setClassHighlightedVerse(verseNum: Int) {
        _classHighlightedVerse.value = verseNum
        _teacherAnnotation.value = "Ustaz Ahmad highlighted Verse $verseNum for recitation assessment."
    }

    fun setLiveClassMode(mode: ClassType) {
        _liveClassMode.value = mode
    }

    fun sendClassChatMessage(text: String) {
        if (text.isBlank()) return
        val newMsg = ClassChatMessage(
            id = "m_${System.currentTimeMillis()}",
            senderName = _userProfile.value.name,
            message = text.trim(),
            timestamp = "Just now",
            isTeacher = false,
            isMe = true
        )
        _chatMessages.update { it + newMsg }
    }

    // ====================================================
    // CLASSES MANAGEMENT & BOOKING
    // ====================================================

    fun joinClassWithInviteCode(code: String): Result<QuranClass> {
        val trimmed = code.trim().uppercase()
        val all = _upcomingClasses.value + _liveClass.value + _completedClasses.value
        val found = all.find { it.inviteCode.equals(trimmed, ignoreCase = true) || trimmed.contains("VOX") || trimmed.contains("786") }
            ?: _liveClass.value

        return Result.success(found)
    }

    fun scheduleNewClass(
        title: String,
        subject: String,
        dateText: String,
        timeText: String,
        type: ClassType,
        level: String = "Intermediate",
        maxParticipants: Int = 15
    ): QuranClass {
        val code = "VOX-${(100..999).random()}"
        val newClass = QuranClass(
            id = "cls_custom_${System.currentTimeMillis()}",
            title = title.trim().ifBlank { "Custom Quran Study Circle" },
            subject = subject.trim().ifBlank { "Tajwid & Recitation" },
            teacher = MockClassData.teacherAhmad,
            type = type,
            status = ClassStatus.UPCOMING,
            level = level,
            dateText = dateText,
            timeText = timeText,
            durationMinutes = if (type == ClassType.ONE_ON_ONE) 45 else 60,
            participantsCount = 1,
            maxParticipants = if (type == ClassType.ONE_ON_ONE) 1 else maxParticipants,
            inviteCode = code,
            inviteLink = "https://voxora.app/class/$code"
        )
        _upcomingClasses.update { listOf(newClass) + it }
        return newClass
    }

    fun bookClassWithTeacher(teacher: Teacher, dateSlot: String, type: ClassType): QuranClass {
        val code = "VOX-${(100..999).random()}"
        val newClass = QuranClass(
            id = "cls_booked_${System.currentTimeMillis()}",
            title = "${if (type == ClassType.ONE_ON_ONE) "1-on-1" else "Group"} Session with ${teacher.name}",
            subject = teacher.specializations.firstOrNull() ?: "Quran Recitation",
            teacher = teacher,
            type = type,
            status = ClassStatus.UPCOMING,
            level = "All Levels",
            dateText = dateSlot,
            timeText = "Scheduled",
            durationMinutes = if (type == ClassType.ONE_ON_ONE) 45 else 60,
            participantsCount = 1,
            maxParticipants = if (type == ClassType.ONE_ON_ONE) 1 else 15,
            inviteCode = code,
            inviteLink = "https://voxora.app/class/$code",
            description = "Personalized recitation and Tajwid coaching session."
        )
        _upcomingClasses.update { listOf(newClass) + it }

        // Trigger local notification
        pushNotification(
            title = "Class Booked Successfully",
            message = "Your session with ${teacher.name} has been booked for $dateSlot.",
            type = NotificationType.UPCOMING_CLASS,
            actionLabel = "View Class"
        )

        return newClass
    }

    // ====================================================
    // COMMUNITY & GROUPS ACTIONS
    // ====================================================

    fun toggleLikePost(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val wasLiked = post.isLiked
                    post.copy(
                        isLiked = !wasLiked,
                        likesCount = if (wasLiked) post.likesCount - 1 else post.likesCount + 1
                    )
                } else post
            }
        }
    }

    fun toggleFollowAuthor(authorName: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.authorName == authorName) {
                    post.copy(isFollowingAuthor = !post.isFollowingAuthor)
                } else post
            }
        }
    }

    fun addCommentToPost(postId: String, commentText: String) {
        if (commentText.isBlank()) return
        val newComment = PostComment(
            id = "c_${System.currentTimeMillis()}",
            authorName = _userProfile.value.name,
            text = commentText.trim(),
            timeAgo = "Just now"
        )
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(
                        commentsCount = post.commentsCount + 1,
                        comments = post.comments + newComment
                    )
                } else post
            }
        }
    }

    fun createPost(content: String, surahRef: String?, groupName: String?, category: String = "Reflection") {
        if (content.isBlank()) return
        val newPost = CommunityPost(
            id = "p_${System.currentTimeMillis()}",
            authorName = _userProfile.value.name,
            authorRole = "Student",
            authorCountry = _userProfile.value.country,
            timeAgo = "Just now",
            groupName = groupName ?: "Global Quran Learners",
            content = content.trim(),
            surahReference = surahRef?.takeIf { it.isNotBlank() },
            likesCount = 0,
            isLiked = false,
            commentsCount = 0,
            category = category
        )
        _posts.update { listOf(newPost) + it }
    }

    fun toggleGroupJoin(groupId: String): Boolean {
        var joined = false
        _communityGroups.update { list ->
            list.map { group ->
                if (group.id == groupId) {
                    val newJoined = !group.isJoined
                    joined = newJoined
                    group.copy(
                        isJoined = newJoined,
                        memberCount = if (newJoined) group.memberCount + 1 else group.memberCount - 1
                    )
                } else group
            }
        }
        return joined
    }

    fun reportContent(targetId: String, reason: String) {
        // Log moderation event
    }

    fun muteUser(userName: String) {
        _mutedUsers.update { it + userName }
    }

    fun blockUser(userName: String) {
        _blockedUsers.update { it + userName }
        _posts.update { list -> list.filterNot { it.authorName == userName } }
    }

    // ====================================================
    // LEARNING PROGRESS ACTIONS
    // ====================================================

    fun logPracticeSession(minutes: Int) {
        _userProfile.update {
            it.copy(
                hoursSpent = it.hoursSpent + (minutes / 60f),
                learningStreakDays = it.learningStreakDays + 1
            )
        }
        _progress.update {
            it.copy(
                quranReadingPercent = (it.quranReadingPercent + 2).coerceAtMost(100),
                tajwidPercent = (it.tajwidPercent + 1).coerceAtMost(100)
            )
        }
        pushNotification(
            title = "Practice Session Logged",
            message = "MashaAllah! You logged $minutes minutes of Quran recitation today.",
            type = NotificationType.ACHIEVEMENT_UNLOCKED
        )
    }

    // ====================================================
    // NOTIFICATIONS ACTIONS
    // ====================================================

    fun markNotificationAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }

    fun clearNotification(id: String) {
        _notifications.update { list ->
            list.filterNot { it.id == id }
        }
    }

    fun pushNotification(title: String, message: String, type: NotificationType, actionLabel: String? = null) {
        val newNotif = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            type = type,
            timestamp = "Just now",
            isRead = false,
            actionLabel = actionLabel
        )
        _notifications.update { listOf(newNotif) + it }
    }

    // ----------------------------------------------------
    // Update System & App Manager
    // ----------------------------------------------------
    val updateManager: com.example.data.update.VoxoraUpdateManager = com.example.data.update.VoxoraUpdateManager()

    // ====================================================
    // SETTINGS ACTIONS
    // ====================================================

    fun setAppLanguage(lang: String) {
        _appLanguage.value = lang
    }

    fun setThemeMode(theme: String) {
        _themeMode.value = theme
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun toggleClassReminders(enabled: Boolean) {
        _classReminders.value = enabled
    }

    fun toggleDailyVerseReminder(enabled: Boolean) {
        _dailyVerseReminder.value = enabled
    }

    fun updateArabicFontSize(size: Float) {
        _quranSettings.update { it.copy(arabicFontSizeSp = size) }
    }

    fun updateArabicFontStyle(style: String) {
        _quranSettings.update { it.copy(arabicFontStyle = style) }
    }

    fun toggleEnglishTranslation(show: Boolean) {
        _quranSettings.update { it.copy(showTranslation = show, showEnglishTranslation = show) }
    }

    fun updateDefaultPlaybackSpeed(speed: Float) {
        _quranSettings.update { it.copy(defaultPlaybackSpeed = speed) }
        audioEngine.setPlaybackSpeed(speed)
    }

    fun updateAutoPlayNextAyah(enabled: Boolean) {
        _quranSettings.update { it.copy(autoPlayNextAyah = enabled) }
    }

    fun updateDefaultRepeatMode(mode: AudioRepeatMode) {
        _quranSettings.update { it.copy(defaultRepeatMode = mode) }
        audioEngine.setRepeatMode(mode)
    }

    fun updateSelectedReciter(reciter: String) {
        setAudioReciter(reciter)
    }

    fun updateUserProfile(
        name: String,
        bio: String,
        country: String,
        level: String = "",
        username: String = "",
        avatarEmoji: String = ""
    ) {
        _userProfile.update {
            it.copy(
                name = name.trim().ifBlank { it.name },
                bio = bio.trim(),
                country = country.trim().ifBlank { it.country },
                learningLevel = if (level.isNotBlank()) level else it.learningLevel,
                username = if (username.isNotBlank()) {
                    if (username.startsWith("@")) username.trim() else "@${username.trim()}"
                } else it.username,
                avatarEmoji = if (avatarEmoji.isNotBlank()) avatarEmoji else it.avatarEmoji
            )
        }
    }

    fun updateProfile(name: String, bio: String, country: String) {
        updateUserProfile(name = name, bio = bio, country = country)
    }

    // ====================================================
    // ONBOARDING & AUTH (Phase 2)
    // ====================================================

    fun completeOnboarding() {
        _hasCompletedOnboarding.value = true
    }

    fun resetOnboarding() {
        _hasCompletedOnboarding.value = false
    }

    fun authenticateUser(email: String, pass: String): Boolean {
        if (email.isBlank()) return false
        val display = email.substringBefore("@").replace(".", " ").capitalize()
        _userProfile.update {
            it.copy(
                email = email.trim(),
                name = if (it.isGuest) display else it.name,
                username = if (it.isGuest) "@${email.substringBefore("@")}" else it.username,
                isGuest = false
            )
        }
        _authMode.value = AuthMode.AUTHENTICATED
        triggerCloudSync()
        return true
    }

    fun createAccountUser(name: String, email: String, pass: String, country: String, level: String): Boolean {
        if (email.isBlank() || name.isBlank()) return false
        _userProfile.update {
            it.copy(
                name = name.trim(),
                email = email.trim(),
                username = "@${email.substringBefore("@").lowercase()}",
                country = country.ifBlank { "Malaysia" },
                learningLevel = level.ifBlank { "Beginner (Juz 1)" },
                isGuest = false
            )
        }
        _authMode.value = AuthMode.AUTHENTICATED
        triggerCloudSync()
        return true
    }

    fun continueAsGuestUser() {
        _userProfile.update {
            it.copy(
                name = "Guest Learner",
                username = "@guest_learner",
                email = "guest@voxora.local",
                isGuest = true
            )
        }
        _authMode.value = AuthMode.GUEST
    }

    fun signOutUser() {
        _userProfile.update {
            it.copy(
                name = "Guest Learner",
                username = "@guest_learner",
                email = "guest@voxora.local",
                isGuest = true
            )
        }
        _authMode.value = AuthMode.GUEST
    }

    // ====================================================
    // AUDIO SLEEP TIMER
    // ====================================================

    fun setAudioSleepTimer(minutes: Int) {
        audioEngine.setSleepTimer(minutes)
    }

    // ====================================================
    // CLOUD SYNC FOUNDATION
    // ====================================================

    fun triggerCloudSync() {
        coroutineScope.launch {
            _cloudSyncStatus.value = SyncStatus.SYNCING
            delay(1200)
            _cloudSyncStatus.value = SyncStatus.SYNCED
            delay(3000)
            _cloudSyncStatus.value = SyncStatus.IDLE
        }
    }
}
