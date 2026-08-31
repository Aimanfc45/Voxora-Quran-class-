package com.example.data.api

import com.example.data.model.AudioRepeatMode
import com.example.data.model.QuranAudioState
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface prepared for real Quran audio streaming (e.g., EveryAyah, Quran.com Audio CDN, or MediaPlayer).
 */
interface IQuranAudioService {
    val audioState: StateFlow<QuranAudioState>

    fun playVerse(surahNumber: Int, verseNumber: Int)
    fun pause()
    fun resume()
    fun stop()
    fun nextVerse()
    fun previousVerse()
    fun seekTo(positionSeconds: Float)
    fun setPlaybackSpeed(speed: Float)
    fun setVolume(volume: Float)
    fun setRepeatMode(mode: AudioRepeatMode)
    fun setRepeatCount(times: Int)
    fun setRepeatRange(start: Int, end: Int)
    fun toggleAutoNext(autoNext: Boolean)
    fun setReciter(reciterName: String)
    fun setSleepTimer(minutes: Int)
}
