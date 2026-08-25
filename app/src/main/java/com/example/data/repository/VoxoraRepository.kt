package com.example.data.repository

import com.example.data.api.IQuranApiService
import com.example.data.api.QuranApiService
import com.example.data.audio.QuranAudioPlayerEngine
import com.example.data.mock.*
import com.example.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class VoxoraRepository(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
    private val quranApiService: IQuranApiService = QuranApiService()
) {

    // ----------------------------------------------------
    // User & Progress State
    // ----------------------------------------------------
    private val _userProfile = MutableStateFlow(MockUserData.currentUser)
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _progress = MutableStateFlow(MockUserData.userProgress)
    val progress: StateFlow<LearningProgress> = _progress.asStateFlow()

    private val _achievements = MutableStateFlow(MockUserData.achievements)
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

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

    private val _verseNotes = MutableStateFlow<List<VerseNote>>(emptyList())
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

    fun toggleAutoNextVerse(enabled: Boolean) {
        audioEngine.toggleAutoNext(enabled)
    }

    fun setAudioReciter(reciter: String) {
        audioEngine.setReciter(reciter)
        _quranSettings.update { it.copy(reciterName = reciter, selectedReciter = reciter) }
    }

    fun nextAudioVerse() {
        audioEngine.nextVerse()
    }

    fun previousAudioVerse() {
        audioEngine.previousVerse()
    }

    // ====================================================
    // BOOKMARKS & NOTES
    // ====================================================

    fun toggleBookmark(surah: Surah, verse: Verse, customNote: String = ""): Boolean {
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
                note = customNote.ifBlank { "Bookmarked verse" }
            )
            _bookmarks.update { listOf(newBm) + it }
            true
        }
    }

    fun isVerseBookmarked(surahNumber: Int, verseNumber: Int): Boolean {
        return _bookmarks.value.any { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
    }

    fun addVerseNote(surahNumber: Int, verseNumber: Int, noteText: String) {
        if (noteText.isBlank()) return
        val newNote = VerseNote(
            id = "vn_${System.currentTimeMillis()}",
            surahNumber = surahNumber,
            verseNumber = verseNumber,
            noteText = noteText.trim()
        )
        _verseNotes.update { listOf(newNote) + it }
    }

    fun getNotesForVerse(surahNumber: Int, verseNumber: Int): List<VerseNote> {
        return _verseNotes.value.filter { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
    }

    // ====================================================
    // DISPLAY & FONT SETTINGS
    // ====================================================

    fun updateQuranFontSize(sizeSp: Float) {
        _quranSettings.update { it.copy(arabicFontSizeSp = sizeSp) }
    }

    fun toggleTranslation(show: Boolean) {
        _quranSettings.update { it.copy(showTranslation = show) }
    }

    fun toggleTransliteration(show: Boolean) {
        _quranSettings.update { it.copy(showTransliteration = show) }
    }

    fun setTranslationLanguage(lang: String) {
        _quranSettings.update { it.copy(translationLanguage = lang) }
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

    fun toggleEnglishTranslation(show: Boolean) {
        _quranSettings.update { it.copy(showTranslation = show, showEnglishTranslation = show) }
    }

    fun toggleWordByWord(show: Boolean) {
        _quranSettings.update { it.copy(showWordByWord = show) }
    }

    fun updateSelectedReciter(reciter: String) {
        _quranSettings.update { it.copy(selectedReciter = reciter, reciterName = reciter) }
        audioEngine.setReciter(reciter)
    }

    fun updateUserProfile(name: String, bio: String, country: String, level: String = "") {
        _userProfile.update {
            it.copy(
                name = name.trim().ifBlank { it.name },
                bio = bio.trim(),
                country = country.trim().ifBlank { it.country },
                learningLevel = if (level.isNotBlank()) level else it.learningLevel
            )
        }
    }

    fun updateProfile(name: String, bio: String, country: String) {
        updateUserProfile(name, bio, country)
    }
}
