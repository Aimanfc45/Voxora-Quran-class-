package com.example.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * Single source of truth for Voxora Application Versioning.
 */
object AppVersion {
    const val VERSION_NAME = "1.4.0"
    const val VERSION_CODE = 4
    const val PHASE = "Phase 2 — Update 1.4"
    const val BUILD_DATE = "September 2026"
    const val CODENAME = "Voxora Real Data & Prayer Times Suite"
}

data class UpdateInfo(
    val versionName: String,
    val versionCode: Int,
    val releaseDate: String,
    val isCritical: Boolean = false,
    val releaseNotes: List<String>,
    val downloadUrl: String? = null,
    val packageSizeMb: Double = 18.4,
    val minSupportedVersionCode: Int = 1
)

sealed interface UpdateCheckStatus {
    object Idle : UpdateCheckStatus
    object Checking : UpdateCheckStatus
    data class UpdateAvailable(val updateInfo: UpdateInfo) : UpdateCheckStatus
    object UpToDate : UpdateCheckStatus
    data class Downloading(val progressPercent: Int, val bytesDownloaded: Long, val totalBytes: Long) : UpdateCheckStatus
    data class ReadyToInstall(val updateInfo: UpdateInfo, val localPackageFile: File?) : UpdateCheckStatus
    data class Error(val message: String) : UpdateCheckStatus
}

interface IUpdateChecker {
    suspend fun checkForUpdates(currentVersionCode: Int): UpdateInfo?
}

interface IUpdateDownloader {
    suspend fun downloadUpdate(
        updateInfo: UpdateInfo,
        onProgress: (progressPercent: Int, downloaded: Long, total: Long) -> Unit
    ): File?
}

/**
 * Default update provider implementation.
 * Ready to connect to backend server, Firebase Remote Config, or GitHub Releases API.
 */
class DefaultUpdateChecker : IUpdateChecker {
    override suspend fun checkForUpdates(currentVersionCode: Int): UpdateInfo? {
        // Simulated network check with latest remote manifest
        delay(600)
        val latestRemoteCode = 4 // Current up to date version
        if (latestRemoteCode > currentVersionCode) {
            return UpdateInfo(
                versionName = "1.4.0",
                versionCode = 4,
                releaseDate = "September 2026",
                isCritical = false,
                releaseNotes = listOf(
                    "✨ Real-time Malaysian JAKIM Prayer Times & Live Countdown Clock",
                    "🕌 GPS Auto-Detection & All 60+ Malaysian Prayer Zones Catalog",
                    "🎙️ Robust Qari Audio Engine with ID-based folder resolution",
                    "⚙️ Modernized Profile & Settings with granular preferences",
                    "🚀 APK Release v1.4.0 with optimized memory & performance"
                ),
                downloadUrl = "https://voxora.app/download/voxora-v1.4.0.apk",
                packageSizeMb = 18.9
            )
        }
        return null
    }
}

class DefaultUpdateDownloader(private val context: Context? = null) : IUpdateDownloader {
    override suspend fun downloadUpdate(
        updateInfo: UpdateInfo,
        onProgress: (progressPercent: Int, downloaded: Long, total: Long) -> Unit
    ): File? {
        val totalBytes = (updateInfo.packageSizeMb * 1024 * 1024).toLong()
        var downloaded = 0L
        val chunkSize = totalBytes / 20

        for (step in 1..20) {
            delay(120)
            downloaded = (chunkSize * step).coerceAtMost(totalBytes)
            val percent = ((downloaded.toDouble() / totalBytes) * 100).toInt()
            onProgress(percent, downloaded, totalBytes)
        }

        return if (context != null) {
            val updateDir = File(context.cacheDir, "updates")
            if (!updateDir.exists()) updateDir.mkdirs()
            val apkFile = File(updateDir, "voxora-update-v${updateInfo.versionName}.apk")
            if (!apkFile.exists()) {
                apkFile.writeBytes(ByteArray(1024)) // Safe placeholder file
            }
            apkFile
        } else null
    }
}

/**
 * Central Update Manager orchestrating version checks, notifications, downloads, and installation requests.
 */
class VoxoraUpdateManager(
    private val context: Context? = null,
    private val updateChecker: IUpdateChecker = DefaultUpdateChecker(),
    private val updateDownloader: IUpdateDownloader = DefaultUpdateDownloader(context),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val _status = MutableStateFlow<UpdateCheckStatus>(UpdateCheckStatus.Idle)
    val status: StateFlow<UpdateCheckStatus> = _status.asStateFlow()

    private val _latestUpdateInfo = MutableStateFlow<UpdateInfo?>(null)
    val latestUpdateInfo: StateFlow<UpdateInfo?> = _latestUpdateInfo.asStateFlow()

    fun checkForUpdates(isManual: Boolean = false) {
        if (_status.value is UpdateCheckStatus.Checking || _status.value is UpdateCheckStatus.Downloading) return

        _status.value = UpdateCheckStatus.Checking
        coroutineScope.launch {
            try {
                val update = updateChecker.checkForUpdates(AppVersion.VERSION_CODE)
                if (update != null && update.versionCode > AppVersion.VERSION_CODE) {
                    _latestUpdateInfo.value = update
                    _status.value = UpdateCheckStatus.UpdateAvailable(update)
                } else {
                    _latestUpdateInfo.value = null
                    _status.value = UpdateCheckStatus.UpToDate
                }
            } catch (e: Exception) {
                _status.value = UpdateCheckStatus.Error("Failed to check for updates: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Helper to preview what an update dialog looks like for testing/demonstration purposes.
     */
    fun showSimulatedUpdateAvailable() {
        val sampleUpdate = UpdateInfo(
            versionName = "1.3.0",
            versionCode = 4,
            releaseDate = "September 2026",
            isCritical = false,
            releaseNotes = listOf(
                "🎙️ Enhanced Crystal-Clear Live Audio Classrooms",
                "📈 Advanced Tajwid Mastery Tracker & Certificate",
                "🕌 Enhanced Offline Mushaf Reader with Indopak & Amiri Scripts",
                "⚡ Faster Audio Streaming and Memory Optimizations"
            ),
            downloadUrl = "https://voxora.app/download/voxora-v1.3.0.apk",
            packageSizeMb = 19.2
        )
        _latestUpdateInfo.value = sampleUpdate
        _status.value = UpdateCheckStatus.UpdateAvailable(sampleUpdate)
    }

    fun startDownload(updateInfo: UpdateInfo) {
        if (_status.value is UpdateCheckStatus.Downloading) return

        _status.value = UpdateCheckStatus.Downloading(0, 0, (updateInfo.packageSizeMb * 1024 * 1024).toLong())
        coroutineScope.launch {
            try {
                val file = updateDownloader.downloadUpdate(updateInfo) { percent, downloaded, total ->
                    _status.value = UpdateCheckStatus.Downloading(percent, downloaded, total)
                }
                _status.value = UpdateCheckStatus.ReadyToInstall(updateInfo, file)
            } catch (e: Exception) {
                _status.value = UpdateCheckStatus.Error("Download failed: ${e.localizedMessage}")
            }
        }
    }

    fun triggerAndroidInstallation(file: File?): Boolean {
        if (context == null || file == null || !file.exists()) return false

        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    } catch (e: Exception) {
                        Uri.fromFile(file)
                    }
                } else {
                    Uri.fromFile(file)
                }
                setDataAndType(uri, "application/vnd.android.package-archive")
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun dismissUpdate() {
        _status.value = UpdateCheckStatus.Idle
    }
}
