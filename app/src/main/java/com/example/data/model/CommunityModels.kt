package com.example.data.model

data class CommunityGroup(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val memberCount: Int,
    val onlineMembersCount: Int = 12,
    val isJoined: Boolean = false,
    val announcements: List<String> = emptyList(),
    val iconEmoji: String = "📖"
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorRole: String = "Student",
    val authorCountry: String = "Malaysia",
    val authorAvatarUrl: String? = null,
    val timeAgo: String,
    val groupName: String? = null,
    val content: String,
    val surahReference: String? = null,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val commentsCount: Int,
    val comments: List<PostComment> = emptyList(),
    val isSaved: Boolean = false,
    val isTeacherPost: Boolean = false,
    val isFollowingAuthor: Boolean = false,
    val category: String = "General" // "Reflection", "Question", "Tajwid Tip", "Hafazan"
)

data class PostComment(
    val id: String,
    val authorName: String,
    val text: String,
    val timeAgo: String,
    val isTeacher: Boolean = false
)

data class UserProfile(
    val name: String = "Ahmed Al-Farsi",
    val username: String = "@ahmed_alfarsi",
    val email: String = "ahmed.farsi@voxora.app",
    val country: String = "Malaysia",
    val flagEmoji: String = "🇲🇾",
    val languages: List<String> = listOf("English", "Bahasa Melayu", "Arabic"),
    val learningLevel: String = "Intermediate (Juz 5)",
    val bio: String = "Passionate about perfecting Tajwid and completing Juz 30 memorization with proper Makharij.",
    val lessonsCompleted: Int = 32,
    val hoursSpent: Float = 48.5f,
    val surahsMemorized: Int = 8,
    val learningStreakDays: Int = 14,
    val isGuest: Boolean = false,
    val dailyGoalMinutes: Int = 20,
    val dailyGoalVerses: Int = 10,
    val juzProgress: Int = 5,
    val classesAttended: Int = 18,
    val avatarEmoji: String = "🧕",
    val totalVersesRead: Int = 420
) {
    val streakCount: Int get() = learningStreakDays
}
