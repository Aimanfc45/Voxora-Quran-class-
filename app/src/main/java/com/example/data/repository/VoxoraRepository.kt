package com.example.data.repository

import com.example.data.mock.*
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VoxoraRepository {

    // User & Progress State
    private val _userProfile = MutableStateFlow(MockUserData.currentUser)
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _progress = MutableStateFlow(MockUserData.userProgress)
    val progress: StateFlow<LearningProgress> = _progress.asStateFlow()

    private val _achievements = MutableStateFlow(MockUserData.achievements)
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    // Quran State
    private val _surahs = MutableStateFlow(MockQuranData.surahList)
    val surahs: StateFlow<List<Surah>> = _surahs.asStateFlow()

    private val _juzList = MutableStateFlow(MockQuranData.juzList)
    val juzList: StateFlow<List<JuzInfo>> = _juzList.asStateFlow()

    private val _selectedSurah = MutableStateFlow(MockQuranData.surahList.first { it.number == 1 })
    val selectedSurah: StateFlow<Surah> = _selectedSurah.asStateFlow()

    private val _currentVerseIndex = MutableStateFlow(0)
    val currentVerseIndex: StateFlow<Int> = _currentVerseIndex.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _quranSettings = MutableStateFlow(QuranSettings())
    val quranSettings: StateFlow<QuranSettings> = _quranSettings.asStateFlow()

    private val _bookmarks = MutableStateFlow(MockUserData.initialBookmarks)
    val bookmarks: StateFlow<List<QuranBookmark>> = _bookmarks.asStateFlow()

    private val _lastReadPosition = MutableStateFlow("Surah Al-Baqarah (2:2)")
    val lastReadPosition: StateFlow<String> = _lastReadPosition.asStateFlow()

    // Classes State
    private val _liveClass = MutableStateFlow(MockClassData.liveTajwidClass)
    val liveClass: StateFlow<QuranClass> = _liveClass.asStateFlow()

    private val _upcomingClasses = MutableStateFlow(MockClassData.upcomingClasses)
    val upcomingClasses: StateFlow<List<QuranClass>> = _upcomingClasses.asStateFlow()

    private val _completedClasses = MutableStateFlow(MockClassData.completedClasses)
    val completedClasses: StateFlow<List<QuranClass>> = _completedClasses.asStateFlow()

    private val _teachers = MutableStateFlow(MockClassData.allTeachers)
    val teachers: StateFlow<List<Teacher>> = _teachers.asStateFlow()

    // Live Classroom Active Session State
    private val _participants = MutableStateFlow(MockClassData.initialParticipants)
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    private val _chatMessages = MutableStateFlow(MockClassData.initialChatMessages)
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

    // Community State
    private val _communityGroups = MutableStateFlow(MockCommunityData.communityGroups)
    val communityGroups: StateFlow<List<CommunityGroup>> = _communityGroups.asStateFlow()

    private val _posts = MutableStateFlow(MockCommunityData.initialPosts)
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    // Settings State
    private val _appLanguage = MutableStateFlow("English")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow("System") // "Light", "Dark", "System"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _classReminders = MutableStateFlow(true)
    val classReminders: StateFlow<Boolean> = _classReminders.asStateFlow()

    private val _dailyVerseReminder = MutableStateFlow(true)
    val dailyVerseReminder: StateFlow<Boolean> = _dailyVerseReminder.asStateFlow()

    // Quran Actions
    fun selectSurah(surahNumber: Int) {
        val found = _surahs.value.find { it.number == surahNumber }
            ?: MockQuranData.surahList.first()
        _selectedSurah.value = found
        _currentVerseIndex.value = 0
        _isPlayingAudio.value = false
        _lastReadPosition.value = "Surah ${found.nameEnglish} (1:1)"
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

    fun toggleAudioPlayback() {
        _isPlayingAudio.update { !it }
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

    fun setTranslationLanguage(lang: String) {
        _quranSettings.update { it.copy(translationLanguage = lang) }
    }

    fun toggleBookmark(surah: Surah, verse: Verse): Boolean {
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
                snippetArabic = verse.textArabic.take(30) + "...",
                note = "Bookmarked verse"
            )
            _bookmarks.update { listOf(newBm) + it }
            true
        }
    }

    fun isVerseBookmarked(surahNumber: Int, verseNumber: Int): Boolean {
        return _bookmarks.value.any { it.surahNumber == surahNumber && it.verseNumber == verseNumber }
    }

    // Live Class Actions
    fun toggleMyMic() {
        _isMyMicMuted.update { !it }
        _participants.update { list ->
            list.map { if (it.id == "p_1") it.copy(isMicMuted = _isMyMicMuted.value) else it }
        }
    }

    fun toggleMyVideo() {
        _isMyVideoOn.update { !it }
        _participants.update { list ->
            list.map { if (it.id == "p_1") it.copy(isVideoOn = _isMyVideoOn.value) else it }
        }
    }

    fun toggleMySpeaker() {
        _isMySpeakerOn.update { !it }
    }

    fun toggleRaiseHand() {
        val newState = !_isMyHandRaised.value
        _isMyHandRaised.value = newState
        _participants.update { list ->
            list.map { if (it.id == "p_1") it.copy(isHandRaised = newState) else it }
        }
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
            isMe = true
        )
        _chatMessages.update { it + newMsg }
    }

    // Community Actions
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

    fun createPost(content: String, surahRef: String?, groupName: String?) {
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
            commentsCount = 0
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
        // Mock moderation log
    }

    fun muteUser(userName: String) {
        // Mock moderation mute
    }

    // Teacher & Class Booking
    fun bookClass(teacher: Teacher, dateSlot: String, type: ClassType): QuranClass {
        val newClass = QuranClass(
            id = "cls_booked_${System.currentTimeMillis()}",
            title = "${if (type == ClassType.ONE_ON_ONE) "1-on-1" else "Group"} Class with ${teacher.name}",
            subject = teacher.specializations.firstOrNull() ?: "Quran Recitation",
            teacher = teacher,
            type = type,
            status = ClassStatus.UPCOMING,
            dateText = dateSlot,
            timeText = "Scheduled",
            durationMinutes = if (type == ClassType.ONE_ON_ONE) 45 else 60,
            participantsCount = 1,
            maxParticipants = if (type == ClassType.ONE_ON_ONE) 1 else 15,
            description = "Personalized recitation and Tajwid coaching session."
        )
        _upcomingClasses.update { listOf(newClass) + it }
        return newClass
    }

    fun scheduleNewClass(title: String, subject: String, dateText: String, timeText: String, type: ClassType) {
        val newClass = QuranClass(
            id = "cls_custom_${System.currentTimeMillis()}",
            title = title,
            subject = subject,
            teacher = MockClassData.teacherAhmad,
            type = type,
            status = ClassStatus.UPCOMING,
            dateText = dateText,
            timeText = timeText,
            durationMinutes = 60,
            participantsCount = 1,
            maxParticipants = 15
        )
        _upcomingClasses.update { listOf(newClass) + it }
    }

    // Settings Updates
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

    fun setReciterName(reciter: String) {
        _quranSettings.update { it.copy(reciterName = reciter) }
    }

    fun updateProfile(name: String, bio: String, country: String) {
        _userProfile.update {
            it.copy(
                name = name,
                bio = bio,
                country = country
            )
        }
    }

    fun updateUserProfile(name: String, bio: String, country: String) {
        updateProfile(name, bio, country)
    }
}
