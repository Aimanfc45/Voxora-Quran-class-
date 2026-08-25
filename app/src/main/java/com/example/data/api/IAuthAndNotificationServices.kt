package com.example.data.api

import com.example.data.model.AppNotification
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface prepared for Authentication Service (Firebase Auth / Custom JWT backend).
 */
interface IAuthService {
    val currentUser: StateFlow<UserProfile>
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun register(name: String, email: String, password: String): Result<UserProfile>
    suspend fun logout()
    suspend fun updateProfile(name: String, bio: String, country: String): Result<UserProfile>
}

/**
 * Interface prepared for Notification Service (FCM Push / Local Notifications).
 */
interface INotificationService {
    val notifications: StateFlow<List<AppNotification>>
    val unreadCount: StateFlow<Int>

    fun markAsRead(notificationId: String)
    fun markAllAsRead()
    fun clearNotification(notificationId: String)
    fun sendLocalNotification(notification: AppNotification)
}
