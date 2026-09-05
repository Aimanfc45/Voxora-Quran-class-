package com.example.data.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.data.firebase.VoxoraFirebaseService
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Privacy-conscious Firebase Analytics integration for Voxora Muslim Centre.
 *
 * Tracks high-level product navigation and feature engagement without capturing
 * personal data, passwords, tokens, or private messages.
 */
object VoxoraAnalytics {
    private const val TAG = "VoxoraAnalytics"

    fun logAppOpen(context: Context) {
        logEvent(context, FirebaseAnalytics.Event.APP_OPEN, null)
    }

    fun logAuthGoogleSuccess(context: Context) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "google")
        }
        logEvent(context, "auth_google_success", bundle)
    }

    fun logAuthGoogleFailed(context: Context, reason: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "google")
            putString("failure_reason", reason.take(100))
        }
        logEvent(context, "auth_google_failed", bundle)
    }

    fun logAuthEmailSuccess(context: Context) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "email")
        }
        logEvent(context, FirebaseAnalytics.Event.LOGIN, bundle)
    }

    fun logAuthSignOut(context: Context) {
        logEvent(context, "auth_sign_out", null)
    }

    fun logQuranOpened(context: Context) {
        logEvent(context, "quran_opened", null)
    }

    fun logSalahOpened(context: Context) {
        logEvent(context, "salah_opened", null)
    }

    fun logDhikrOpened(context: Context) {
        logEvent(context, "dhikr_opened", null)
    }

    fun logLearningOpened(context: Context) {
        logEvent(context, "learning_opened", null)
    }

    fun logLiveClassJoined(context: Context, role: String) {
        val bundle = Bundle().apply {
            putString("role", role.lowercase())
        }
        logEvent(context, "live_class_joined", bundle)
    }

    fun logProfileOpened(context: Context) {
        logEvent(context, "profile_opened", null)
    }

    fun logModeSelected(context: Context, modeName: String) {
        val bundle = Bundle().apply {
            putString("mode_name", modeName.take(50))
        }
        logEvent(context, "mode_selected", bundle)
    }

    private fun logEvent(context: Context, eventName: String, params: Bundle?) {
        try {
            val analytics = VoxoraFirebaseService.getAnalytics(context) ?: return
            analytics.logEvent(eventName, params)
        } catch (e: Exception) {
            Log.d(TAG, "Analytics event $eventName skipped: ${e.message}")
        }
    }
}
