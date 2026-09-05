package com.example.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.firebase.VoxoraFirebaseService
import kotlinx.coroutines.tasks.await

/**
 * Production Notification and FCM manager for Voxora Muslim Centre.
 *
 * Configures notification channels, requests system permissions, securely stores
 * registration tokens without UI exposure, and routes incoming FCM push payloads
 * to system notifications with deep links back to MainActivity.
 */
object VoxoraNotificationManager {
    private const val TAG = "VoxoraNotification"

    const val CHANNEL_PRAYER = "voxora_prayer_channel"
    const val CHANNEL_LIVE_CLASS = "voxora_live_class_channel"
    const val CHANNEL_QURAN = "voxora_quran_reminders_channel"
    const val CHANNEL_ANNOUNCEMENTS = "voxora_announcements_channel"

    private const val PREFS_NAME = "voxora_fcm_secure_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_PRAYER,
                    "Prayer Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Timely Adhan and Salah reminders"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_LIVE_CLASS,
                    "Live Class & Invitations",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Teacher session broadcasts and student recitation invitations"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_QURAN,
                    "Quran & Dhikr Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily reading goals and remembrance reflections"
                },
                NotificationChannel(
                    CHANNEL_ANNOUNCEMENTS,
                    "Voxora Announcements",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Community updates, Ramadan and Islamic Calendar news"
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    suspend fun registerFcmToken(context: Context): String? {
        return try {
            val messaging = VoxoraFirebaseService.getMessaging() ?: return null
            val token = messaging.token.await()
            if (!token.isNullOrBlank()) {
                saveToken(context, token)
            }
            token
        } catch (e: Exception) {
            Log.d(TAG, "FCM registration not yet available: ${e.message}")
            null
        }
    }

    fun saveToken(context: Context, token: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d(TAG, "FCM Token securely persisted for internal push routing.")
    }

    fun getPersistedToken(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    fun showPushNotification(
        context: Context,
        id: Int,
        channelId: String,
        title: String,
        body: String,
        actionDestination: String? = null
    ) {
        if (!areNotificationsEnabled(context)) {
            Log.d(TAG, "Notifications are disabled by user, skipping presentation.")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (!actionDestination.isNullOrBlank()) {
                putExtra("destination", actionDestination)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (e: SecurityException) {
            Log.w(TAG, "Notification permission missing: ${e.message}")
        }
    }
}
