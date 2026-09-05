package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Foundation service provider for Firebase integration in Voxora Muslim Centre.
 *
 * Ensures safe, non-crashing access to Firebase services even when google-services.json
 * is pending configuration or when running in offline/restricted environments.
 */
object VoxoraFirebaseService {
    private const val TAG = "VoxoraFirebase"

    val isFirebaseAvailable: Boolean
        get() = try {
            FirebaseApp.getInstance() != null
        } catch (_: Exception) {
            false
        }

    fun getAuth(): FirebaseAuth? {
        return try {
            if (isFirebaseAvailable) FirebaseAuth.getInstance() else null
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth unavailable: ${e.message}")
            null
        }
    }

    fun getMessaging(): FirebaseMessaging? {
        return try {
            if (isFirebaseAvailable) FirebaseMessaging.getInstance() else null
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseMessaging unavailable: ${e.message}")
            null
        }
    }

    fun getCrashlytics(): FirebaseCrashlytics? {
        return try {
            if (isFirebaseAvailable) FirebaseCrashlytics.getInstance() else null
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseCrashlytics unavailable: ${e.message}")
            null
        }
    }

    fun getAnalytics(context: Context): FirebaseAnalytics? {
        return try {
            if (isFirebaseAvailable) FirebaseAnalytics.getInstance(context.applicationContext) else null
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAnalytics unavailable: ${e.message}")
            null
        }
    }
}
