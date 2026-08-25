package com.example.data.model

enum class NotificationType {
    UPCOMING_CLASS,
    TEACHER_MESSAGE,
    GROUP_ACTIVITY,
    CLASS_REMINDER,
    ACHIEVEMENT_UNLOCKED
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionLabel: String? = null,
    val relatedId: String? = null
)
