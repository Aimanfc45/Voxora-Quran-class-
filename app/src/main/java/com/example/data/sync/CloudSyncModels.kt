package com.example.data.sync

import com.example.data.model.QuranBookmark
import com.example.data.model.QuranSettings
import com.example.data.model.VerseNote

enum class SyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE,
    ERROR
}

data class SyncDataPayload(
    val userId: String,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val bookmarks: List<QuranBookmark>,
    val notes: List<VerseNote>,
    val lastReadPosition: String,
    val streakCount: Int,
    val totalVersesRead: Int,
    val selectedReciterId: String,
    val quranSettings: QuranSettings
)

/**
 * Cloud Sync Service Interface.
 *
 * BACKEND REQUIREMENT:
 * In production, connect this service with a secure cloud persistence layer:
 * - Firebase Cloud Firestore / Realtime Database
 * - Google Cloud SQL / Supabase REST & WebSockets
 * - Custom gRPC or GraphQL Sync Server
 *
 * All user data is currently persisted locally using local storage and StateFlow
 * while maintaining 100% data integrity between sessions.
 */
interface ICloudSyncService {
    suspend fun uploadUserData(payload: SyncDataPayload): Result<Long>
    suspend fun downloadUserData(userId: String): Result<SyncDataPayload?>
    suspend fun getSyncStatus(): SyncStatus
}
