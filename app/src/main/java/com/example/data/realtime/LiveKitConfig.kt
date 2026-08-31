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
    val serverUrl: String = DEFAULT_SERVER_URL,
    val devTokenServerId: String = DEFAULT_DEV_TOKEN_SERVER_ID,
    val tokenEndpoint: String = "",
    val devToken: String = "",
    val isConfigured: Boolean = true
) {
    companion object {
        const val DEFAULT_SERVER_URL = "wss://voxora-quran-class-2op9ozf4.livekit.cloud"
        const val DEFAULT_DEV_TOKEN_SERVER_ID = "voxoraquranclass-1pdkmx"
        const val DEFAULT_TOKEN_ENDPOINT = ""

        fun createDefault(): LiveKitConfig {
            return LiveKitConfig(
                serverUrl = DEFAULT_SERVER_URL,
                devTokenServerId = DEFAULT_DEV_TOKEN_SERVER_ID,
                tokenEndpoint = "",
                devToken = "",
                isConfigured = true
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
