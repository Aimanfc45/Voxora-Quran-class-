package com.example.data.api

import com.example.data.model.ClassChatMessage
import com.example.data.model.Participant
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface prepared for real-time video/audio classroom integration (LiveKit, Agora, or WebRTC).
 */
interface IRealtimeClassService {
    val participants: StateFlow<List<Participant>>
    val chatMessages: StateFlow<List<ClassChatMessage>>
    val isMicMuted: StateFlow<Boolean>
    val isVideoOn: StateFlow<Boolean>
    val isHandRaised: StateFlow<Boolean>

    fun joinClass(classId: String, participantName: String)
    fun leaveClass()
    fun toggleMic()
    fun toggleVideo()
    fun toggleHandRaise()
    fun sendChatMessage(message: String)
    fun setTeacherHighlightedVerse(verseNumber: Int)
}
