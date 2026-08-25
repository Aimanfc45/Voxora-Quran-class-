package com.example.data.mock

import com.example.data.model.AppNotification
import com.example.data.model.NotificationType

object MockNotificationData {
    val initialNotifications: List<AppNotification> = listOf(
        AppNotification(
            id = "notif_1",
            title = "Upcoming Live Class in 15 mins",
            message = "Ustaz Ahmad Shakir's Tajwid — Rules of Noon Sakinah starts at 08:00 PM.",
            type = NotificationType.UPCOMING_CLASS,
            timestamp = "15m ago",
            isRead = false,
            actionLabel = "Join Class"
        ),
        AppNotification(
            id = "notif_2",
            title = "Ustaz Ahmad sent feedback",
            message = "Great improvement on your Qalqalah articulation in Surah Al-Ikhlas! Check notes.",
            type = NotificationType.TEACHER_MESSAGE,
            timestamp = "1h ago",
            isRead = false,
            actionLabel = "View Notes"
        ),
        AppNotification(
            id = "notif_3",
            title = "New Discussion in Tajwid Beginners",
            message = "Fatima posted: 'How do you differentiate between Idgham Ma'al Ghunnah and Bila Ghunnah?'",
            type = NotificationType.GROUP_ACTIVITY,
            timestamp = "3h ago",
            isRead = true,
            actionLabel = "View Group"
        ),
        AppNotification(
            id = "notif_4",
            title = "Daily Verse Reminder",
            message = "Don't forget your daily recitation today. Maintain your 14-day streak!",
            type = NotificationType.CLASS_REMINDER,
            timestamp = "6h ago",
            isRead = true,
            actionLabel = "Open Quran"
        ),
        AppNotification(
            id = "notif_5",
            title = "Achievement Unlocked! 🏆",
            message = "You unlocked the '14-Day Streak' badge for consistent daily Quran study.",
            type = NotificationType.ACHIEVEMENT_UNLOCKED,
            timestamp = "1d ago",
            isRead = true,
            actionLabel = "View Achievements"
        )
    )
}
