package com.example.data.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Service to receive real Firebase Cloud Messaging push events and handle token refresh.
 *
 * NOTE: Never displays fake push notifications; only responds to authentic remote messages
 * dispatched to the device by the Voxora backend.
 */
class VoxoraFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received.")
        VoxoraNotificationManager.saveToken(applicationContext, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Voxora Muslim Centre"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "New notification from your Islamic learning journey."

        val type = remoteMessage.data["type"] ?: "announcement"
        val channelId = when (type) {
            "prayer", "salah" -> VoxoraNotificationManager.CHANNEL_PRAYER
            "live_class", "class_invite" -> VoxoraNotificationManager.CHANNEL_LIVE_CLASS
            "quran", "dhikr" -> VoxoraNotificationManager.CHANNEL_QURAN
            else -> VoxoraNotificationManager.CHANNEL_ANNOUNCEMENTS
        }

        val destination = remoteMessage.data["destination"]

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        VoxoraNotificationManager.showPushNotification(
            context = applicationContext,
            id = notificationId,
            channelId = channelId,
            title = title,
            body = body,
            actionDestination = destination
        )
    }

    companion object {
        private const val TAG = "VoxoraFcmService"
    }
}
