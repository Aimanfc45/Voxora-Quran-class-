package com.example.data.audio

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class AudioDownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    FAILED
}

data class SurahDownloadState(
    val surahNumber: Int,
    val reciterName: String,
    val status: AudioDownloadStatus = AudioDownloadStatus.NOT_DOWNLOADED,
    val progressPercent: Int = 0,
    val downloadedVersesCount: Int = 0,
    val totalVersesCount: Int = 7,
    val localDirectoryPath: String? = null,
    val errorMessage: String? = null
)

/**
 * Scalable Offline Audio Download Architecture.
 * Manages local verse audio caching and surah audio packs.
 */
class QuranAudioDownloadManager(
    private val context: Context? = null,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val _downloadStates = MutableStateFlow<Map<String, SurahDownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, SurahDownloadState>> = _downloadStates.asStateFlow()

    private fun getSurahKey(surahNumber: Int, reciterName: String): String {
        return "${surahNumber}_${reciterName.replace(" ", "_")}"
    }

    fun getDownloadStatus(surahNumber: Int, reciterName: String): AudioDownloadStatus {
        val key = getSurahKey(surahNumber, reciterName)
        return _downloadStates.value[key]?.status ?: AudioDownloadStatus.NOT_DOWNLOADED
    }

    fun getLocalVerseAudioFile(surahNumber: Int, verseNumber: Int, reciterName: String): File? {
        if (context == null) return null
        val baseDir = File(context.filesDir, "quran_audio/${reciterName.replace(" ", "_")}/$surahNumber")
        if (!baseDir.exists()) return null
        val vStr = verseNumber.toString().padStart(3, '0')
        val sStr = surahNumber.toString().padStart(3, '0')
        val targetFile = File(baseDir, "$sStr$vStr.mp3")
        return if (targetFile.exists() && targetFile.length() > 0) targetFile else null
    }

    fun startSurahDownload(surahNumber: Int, surahName: String, totalVerses: Int, reciterName: String) {
        val key = getSurahKey(surahNumber, reciterName)
        _downloadStates.update { map ->
            val existing = map[key] ?: SurahDownloadState(
                surahNumber = surahNumber,
                reciterName = reciterName,
                totalVersesCount = totalVerses
            )
            map + (key to existing.copy(
                status = AudioDownloadStatus.DOWNLOADING,
                progressPercent = 0,
                errorMessage = null
            ))
        }

        coroutineScope.launch {
            try {
                // Simulate progressive chunk preparation/download structure
                for (v in 1..totalVerses) {
                    val progress = ((v.toFloat() / totalVerses) * 100).toInt()
                    _downloadStates.update { map ->
                        val current = map[key] ?: return@update map
                        map + (key to current.copy(
                            progressPercent = progress,
                            downloadedVersesCount = v
                        ))
                    }
                }

                _downloadStates.update { map ->
                    val current = map[key] ?: return@update map
                    map + (key to current.copy(
                        status = AudioDownloadStatus.DOWNLOADED,
                        progressPercent = 100,
                        downloadedVersesCount = totalVerses
                    ))
                }
            } catch (e: Exception) {
                _downloadStates.update { map ->
                    val current = map[key] ?: return@update map
                    map + (key to current.copy(
                        status = AudioDownloadStatus.FAILED,
                        errorMessage = "Download failed: ${e.localizedMessage}"
                    ))
                }
            }
        }
    }

    fun cancelDownload(surahNumber: Int, reciterName: String) {
        val key = getSurahKey(surahNumber, reciterName)
        _downloadStates.update { map ->
            map - key
        }
    }

    fun deleteDownloadedSurah(surahNumber: Int, reciterName: String) {
        val key = getSurahKey(surahNumber, reciterName)
        if (context != null) {
            val baseDir = File(context.filesDir, "quran_audio/${reciterName.replace(" ", "_")}/$surahNumber")
            if (baseDir.exists()) {
                baseDir.deleteRecursively()
            }
        }
        _downloadStates.update { map ->
            map - key
        }
    }
}
