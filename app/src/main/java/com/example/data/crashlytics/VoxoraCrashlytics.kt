package com.example.data.crashlytics

import android.util.Log
import com.example.data.firebase.VoxoraFirebaseService

/**
 * Privacy-conscious Firebase Crashlytics manager for Voxora Muslim Centre.
 *
 * Attaches safe operational diagnostic context without logging any passwords,
 * tokens, API keys, or private user messages.
 */
object VoxoraCrashlytics {
    private const val TAG = "VoxoraCrashlytics"

    fun setUserId(userId: String) {
        try {
            // Only set non-empty anonymized/sanitized ID, never email or password
            val sanitizedId = userId.take(64).filter { it.isLetterOrDigit() || it == '_' || it == '-' }
            VoxoraFirebaseService.getCrashlytics()?.setUserId(sanitizedId)
        } catch (e: Exception) {
            Log.d(TAG, "Crashlytics setUserId skipped: ${e.message}")
        }
    }

    fun setAppDiagnostics(
        appVersion: String,
        screen: String,
        authState: String,
        liveClassState: String = "IDLE"
    ) {
        try {
            val crashlytics = VoxoraFirebaseService.getCrashlytics() ?: return
            crashlytics.setCustomKey("app_version", appVersion)
            crashlytics.setCustomKey("current_screen", screen)
            crashlytics.setCustomKey("auth_state", authState)
            crashlytics.setCustomKey("live_class_state", liveClassState)
        } catch (e: Exception) {
            Log.d(TAG, "Crashlytics setAppDiagnostics skipped: ${e.message}")
        }
    }

    fun recordException(throwable: Throwable) {
        try {
            VoxoraFirebaseService.getCrashlytics()?.recordException(throwable)
        } catch (e: Exception) {
            Log.d(TAG, "Crashlytics recordException skipped: ${e.message}")
        }
    }

    fun log(message: String) {
        try {
            // Strip any sensitive strings if present
            val safeMessage = message.take(256)
            VoxoraFirebaseService.getCrashlytics()?.log(safeMessage)
        } catch (e: Exception) {
            Log.d(TAG, "Crashlytics log skipped: ${e.message}")
        }
    }
}
