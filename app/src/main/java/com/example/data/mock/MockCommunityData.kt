package com.example.data.mock

import com.example.data.model.*

object MockCommunityData {

    val communityGroups = listOf(
        CommunityGroup(
            id = "grp_1",
            name = "Global Quran Learners",
            category = "General",
            description = "A worldwide welcoming circle for brothers and sisters studying Quran recitation, Tajwid, and reflections together.",
            memberCount = 14200,
            isJoined = true,
            iconEmoji = "🌍",
            announcements = listOf(
                "Weekly Tajwid live recitation circle every Saturday at 8 PM UTC.",
                "Share your daily Quran reading goal in the comments below!"
            )
        ),
        CommunityGroup(
            id = "grp_2",
            name = "Juz 30 Challenge",
            category = "Memorization",
            description = "Daily accountability and mutual testing for brothers and sisters memorizing and revising Juz 'Amma.",
            memberCount = 8950,
            isJoined = true,
            iconEmoji = "🏆",
            announcements = listOf(
                "Week 4 target: Surah Al-Alaq & At-Tin revision. Test with your partner by Friday!"
            )
        ),
        CommunityGroup(
            id = "grp_3",
            name = "Malaysia Quran Learners",
            category = "Regional",
            description = "Komuniti pembelajaran Al-Quran khas untuk pelajar di Malaysia. Kelas Talaqqi & Musyafahah secara langsung.",
            memberCount = 6320,
            isJoined = false,
            iconEmoji = "🇲🇾",
            announcements = listOf(
                "Sesi Daurah Tajwid Asas bersama Ustaz jemputan setiap Ahad malam."
            )
        ),
        CommunityGroup(
            id = "grp_4",
            name = "Tajwid Beginners",
            category = "Tajwid",
            description = "Step-by-step guidance for beginners starting from Makharij (letter articulation) to rules of Noon/Meem Sakinah.",
            memberCount = 11400,
            isJoined = true,
            iconEmoji = "📖",
            announcements = listOf(
                "Tip of the day: Watch the position of your tongue tip during Qalqalah Kubra."
            )
        ),
        CommunityGroup(
            id = "grp_5",
            name = "Hafazan Community",
            category = "Memorization",
            description = "Techniques, revision schedules, and memory retention strategies for Quran memorizers.",
            memberCount = 5780,
            isJoined = false,
            iconEmoji = "✨",
            announcements = listOf(
                "New memory retention tracker worksheet uploaded in resources."
            )
        )
    )

    val initialPosts = listOf(
        CommunityPost(
            id = "p_1",
            authorName = "Ustaz Ahmad Al-Azhari",
            authorRole = "Teacher",
            authorCountry = "Egypt / Malaysia",
            timeAgo = "2h ago",
            groupName = "Tajwid Beginners",
            content = "Tip for today: In Surah Al-Fatihah verse 7, make sure to hold the elongation for 'Walad-Daaalleen' (وَلَا الضَّالِّينَ) for a full 6 counts (Mad Lazim Kalimi Muthaqqal). Notice the heavy shaddah on the Lam right after the long Alif!",
            surahReference = "Surah Al-Fatihah (1:7)",
            likesCount = 84,
            isLiked = true,
            commentsCount = 12,
            isTeacherPost = true,
            comments = listOf(
                PostComment("c_1", "Ahmed Al-Farsi", "JazakAllahu Khairan Ustaz! Practiced this in class today.", "1h ago"),
                PostComment("c_2", "Maryam Farooq", "Very helpful explanation, thank you!", "45m ago")
            )
        ),
        CommunityPost(
            id = "p_2",
            authorName = "Sarah binti Ridzwan",
            authorRole = "Student",
            authorCountry = "Malaysia",
            timeAgo = "4h ago",
            groupName = "Juz 30 Challenge",
            content = "Alhamdulillah just completed revising Surah An-Nas through Surah Al-Ikhlas with proper Qalqalah rules! Having daily teacher feedback on Voxora is such a blessing.",
            surahReference = "Surah Al-Ikhlas — An-Nas",
            likesCount = 45,
            isLiked = false,
            commentsCount = 6,
            comments = listOf(
                PostComment("c_3", "Ustazah Fatima", "MashaAllah Sarah, keep up the strong consistency!", "3h ago", isTeacher = true),
                PostComment("c_4", "Zayd Ibrahim", "BarakAllahu feeki, great milestone!", "2h ago")
            )
        ),
        CommunityPost(
            id = "p_3",
            authorName = "Omar Khalid",
            authorRole = "Student",
            authorCountry = "United Kingdom",
            timeAgo = "6h ago",
            groupName = "Global Quran Learners",
            content = "Who else is attending Ustaz Ahmad's Live Tajwid class today? Looking forward to the interactive recitation practice on Surah Al-Baqarah.",
            surahReference = "Surah Al-Baqarah (2:1-5)",
            likesCount = 29,
            isLiked = false,
            commentsCount = 8,
            comments = listOf(
                PostComment("c_5", "Ahmed Al-Farsi", "I'll be there! See you in the live room.", "5h ago")
            )
        )
    )
}
