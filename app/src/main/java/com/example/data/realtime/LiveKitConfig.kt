package com.example.data.realtime

/**
 * Configuration for LiveKit Real-Time WebRTC Audio/Video & Data Channels.
 *
 * SECURITY NOTICE:
 * Server API secrets and JWT private signing keys MUST NEVER be hardcoded
 * or included in the Android application package.
 *
 * For Production:
 * Point [tokenEndpoint] to your secure backend (e.g., https://api.voxora.app/livekit/token)
 * which validates user authentication (e.g. Bearer token) and issues signed LiveKit JWTs.
 *
 * For Development & Testing:
 * Set [devServerUrl] and optionally supply a test token generated via LiveKit CLI or Console.
 */
data class LiveKitConfig(
    val serverUrl: String = "",
    val tokenEndpoint: String = "",
    val devToken: String = "",
    val isConfigured: Boolean = false
) {
    companion object {
        // Default environment placeholders
        const val DEFAULT_SERVER_URL = "wss://voxora-live.livekit.cloud"
        const val DEFAULT_TOKEN_ENDPOINT = "https://api.voxora.app/api/v1/livekit/token"

        fun createDefault(): LiveKitConfig {
            return LiveKitConfig(
                serverUrl = "",
                tokenEndpoint = "",
                devToken = "",
                isConfigured = false
            )
        }
    }
}

enum class ConnectionQualityLevel {
    EXCELLENT,
    GOOD,
    POOR,
    RECONNECTING,
    DISCONNECTED,
    UNCONFIGURED
}

enum class ClassroomRole {
    TEACHER,
    STUDENT
}
