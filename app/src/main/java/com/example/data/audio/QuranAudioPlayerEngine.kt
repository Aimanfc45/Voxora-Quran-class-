package com.example.data.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import com.example.data.api.IQuranAudioService
import com.example.data.model.AudioRepeatMode
import com.example.data.model.QuranAudioState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuranAudioPlayerEngine(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
    private val onVerseChangedListener: ((surahNumber: Int, verseNumber: Int) -> Unit)? = null,
    private val getSurahVerseCount: ((surahNumber: Int) -> Int)? = null
) : IQuranAudioService {

    private val TAG = "QuranAudioEngine"

    private val _audioState = MutableStateFlow(QuranAudioState())
    override val audioState: StateFlow<QuranAudioState> = _audioState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var isPrepared = false

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnPreparedListener { mp ->
                    isPrepared = true
                    val durationSec = (mp.duration / 1000f).coerceAtLeast(1f)
                    _audioState.update {
                        it.copy(
                            isPlaying = true,
                            isLoading = false,
                            errorMessage = null,
                            totalDurationSeconds = durationSec
                        )
                    }
                    applyPlaybackParams()
                    applyVolume()
                    mp.start()
                    startProgressTracker()
                }

                setOnCompletionListener {
                    handleTrackCompletion()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    isPrepared = false
                    _audioState.update {
                        it.copy(
                            isPlaying = false,
                            isLoading = false,
                            errorMessage = "Audio playback failed. Please check internet connection."
                        )
                    }
                    progressJob?.cancel()
                    true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MediaPlayer", e)
        }
    }

    private fun getReciterFolder(reciterName: String): String {
        return when (reciterName) {
            "Abdul Basit Abdul Samad" -> "Abdul_Basit_Murattal_192kbps"
            "Mahmoud Khalil Al-Hussary" -> "Husary_128kbps"
            "Saad Al-Ghamdi" -> "Ghamadi_40kbps"
            else -> "Alafasy_128kbps" // Default: Mishary Rashid Alafasy
        }
    }

    fun buildVerifiedAudioUrl(surah: Int, verse: Int, reciterName: String): String {
        val folder = getReciterFolder(reciterName)
        val sStr = surah.toString().padStart(3, '0')
        val vStr = verse.toString().padStart(3, '0')
        return "https://everyayah.com/data/$folder/$sStr$vStr.mp3"
    }

    override fun playVerse(surahNumber: Int, verseNumber: Int) {
        val currentReciter = _audioState.value.reciterName
        val audioUrl = buildVerifiedAudioUrl(surahNumber, verseNumber, currentReciter)

        _audioState.update {
            it.copy(
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                audioUrl = audioUrl,
                isLoading = true,
                errorMessage = null,
                currentPositionSeconds = 0f
            )
        }

        onVerseChangedListener?.invoke(surahNumber, verseNumber)
        loadAndPlay(audioUrl)
    }

    private fun loadAndPlay(url: String) {
        progressJob?.cancel()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                if (mediaPlayer == null) {
                    withContext(Dispatchers.Main) { initMediaPlayer() }
                }
                mediaPlayer?.let { player ->
                    try {
                        isPrepared = false
                        player.reset()
                        player.setDataSource(url)
                        player.prepareAsync()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error setting data source: $url", e)
                        withContext(Dispatchers.Main) {
                            _audioState.update {
                                it.copy(
                                    isPlaying = false,
                                    isLoading = false,
                                    errorMessage = "Could not load audio. Check your connection."
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in loadAndPlay", e)
                withContext(Dispatchers.Main) {
                    _audioState.update {
                        it.copy(
                            isPlaying = false,
                            isLoading = false,
                            errorMessage = "Audio error: ${e.localizedMessage ?: "Unknown error"}"
                        )
                    }
                }
            }
        }
    }

    override fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
            progressJob?.cancel()
            _audioState.update { it.copy(isPlaying = false) }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing", e)
        }
    }

    override fun resume() {
        try {
            if (isPrepared && mediaPlayer != null) {
                mediaPlayer?.start()
                _audioState.update { it.copy(isPlaying = true, errorMessage = null) }
                startProgressTracker()
            } else {
                val state = _audioState.value
                playVerse(state.surahNumber, state.verseNumber)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming", e)
            val state = _audioState.value
            playVerse(state.surahNumber, state.verseNumber)
        }
    }

    override fun stop() {
        try {
            progressJob?.cancel()
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            isPrepared = false
            mediaPlayer?.reset()
            _audioState.update {
                it.copy(
                    isPlaying = false,
                    isLoading = false,
                    currentPositionSeconds = 0f
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping", e)
        }
    }

    override fun seekTo(positionSeconds: Float) {
        try {
            val clamped = positionSeconds.coerceIn(0f, _audioState.value.totalDurationSeconds)
            if (isPrepared && mediaPlayer != null) {
                val msec = (clamped * 1000).toInt()
                mediaPlayer?.seekTo(msec)
            }
            _audioState.update { it.copy(currentPositionSeconds = clamped) }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking", e)
        }
    }

    override fun setPlaybackSpeed(speed: Float) {
        _audioState.update { it.copy(playbackSpeed = speed) }
        applyPlaybackParams()
    }

    private fun applyPlaybackParams() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isPrepared && mediaPlayer != null) {
                val speed = _audioState.value.playbackSpeed
                val params = mediaPlayer?.playbackParams ?: PlaybackParams()
                params.speed = speed
                mediaPlayer?.playbackParams = params
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to set playback speed", e)
        }
    }

    override fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _audioState.update { it.copy(volume = clamped) }
        applyVolume()
    }

    private fun applyVolume() {
        try {
            val vol = _audioState.value.volume
            mediaPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to set volume", e)
        }
    }

    override fun setRepeatMode(mode: AudioRepeatMode) {
        _audioState.update { it.copy(repeatMode = mode) }
    }

    override fun toggleAutoNext(autoNext: Boolean) {
        _audioState.update { it.copy(autoNextVerse = autoNext) }
    }

    override fun setReciter(reciterName: String) {
        _audioState.update { it.copy(reciterName = reciterName) }
        if (_audioState.value.isPlaying) {
            val state = _audioState.value
            playVerse(state.surahNumber, state.verseNumber)
        }
    }

    override fun nextVerse() {
        val state = _audioState.value
        val totalVersesInSurah = getSurahVerseCount?.invoke(state.surahNumber) ?: 286
        if (state.verseNumber < totalVersesInSurah) {
            playVerse(state.surahNumber, state.verseNumber + 1)
        } else if (state.surahNumber < 114) {
            // Next Surah Verse 1
            playVerse(state.surahNumber + 1, 1)
        } else {
            stop()
        }
    }

    override fun previousVerse() {
        val state = _audioState.value
        if (state.verseNumber > 1) {
            playVerse(state.surahNumber, state.verseNumber - 1)
        } else if (state.surahNumber > 1) {
            val prevSurah = state.surahNumber - 1
            val prevSurahVerseCount = getSurahVerseCount?.invoke(prevSurah) ?: 1
            playVerse(prevSurah, prevSurahVerseCount)
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (isActive) {
                delay(200)
                try {
                    if (isPrepared && mediaPlayer?.isPlaying == true) {
                        val currentPosSec = (mediaPlayer?.currentPosition ?: 0) / 1000f
                        val durationSec = (mediaPlayer?.duration ?: 0) / 1000f
                        _audioState.update {
                            it.copy(
                                currentPositionSeconds = currentPosSec,
                                totalDurationSeconds = if (durationSec > 0) durationSec else it.totalDurationSeconds
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Safe ignore when MediaPlayer state transitions
                }
            }
        }
    }

    private fun handleTrackCompletion() {
        val state = _audioState.value
        val surah = state.surahNumber
        val verse = state.verseNumber
        val totalVersesInSurah = getSurahVerseCount?.invoke(surah) ?: 7

        when (state.repeatMode) {
            AudioRepeatMode.REPEAT_VERSE -> {
                seekTo(0f)
                resume()
            }
            AudioRepeatMode.REPEAT_SURAH -> {
                if (verse < totalVersesInSurah) {
                    playVerse(surah, verse + 1)
                } else {
                    // Loop back to verse 1
                    playVerse(surah, 1)
                }
            }
            AudioRepeatMode.REPEAT_RANGE -> {
                if (verse < state.repeatRangeEnd && verse < totalVersesInSurah) {
                    playVerse(surah, verse + 1)
                } else {
                    playVerse(surah, state.repeatRangeStart)
                }
            }
            AudioRepeatMode.OFF -> {
                if (state.autoNextVerse) {
                    if (verse < totalVersesInSurah) {
                        playVerse(surah, verse + 1)
                    } else if (surah < 114) {
                        playVerse(surah + 1, 1)
                    } else {
                        stop()
                    }
                } else {
                    stop()
                }
            }
        }
    }

    fun release() {
        try {
            progressJob?.cancel()
            mediaPlayer?.release()
            mediaPlayer = null
            isPrepared = false
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        }
    }
}
