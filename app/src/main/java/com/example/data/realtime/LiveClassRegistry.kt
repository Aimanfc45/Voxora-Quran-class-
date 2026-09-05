package com.example.data.realtime

import java.util.concurrent.ConcurrentHashMap

/**
 * Data representation of an active Quran live class.
 */
data class ActiveClassInfo(
    val code: String,              // Human-friendly code, e.g. "VX-7K29P"
    val roomName: String,          // LiveKit room name, e.g. "voxora_class_vx7k29p"
    val className: String,         // e.g. "Tajwid Interactive Session"
    val topic: String,             // e.g. "Mad Asli"
    val classType: String,         // "GROUP" or "1-ON-1"
    val hostName: String,          // Authenticated Teacher's name
    val hostIdentity: String,      // Technical participant identity
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Registry and deterministic resolver for Voxora Live Classes.
 * Guarantees that Teacher and Student entering the same Class Code
 * resolve to the exact SAME LiveKit room name across devices.
 */
object LiveClassRegistry {
    private val activeClasses = ConcurrentHashMap<String, ActiveClassInfo>()

    init {
        val defaultCode = "VX-7K29P"
        activeClasses[defaultCode] = ActiveClassInfo(
            code = defaultCode,
            roomName = classCodeToRoomName(defaultCode),
            className = "Tajwid & Makharij Interactive Class",
            topic = "Rules of Nun Sakinah & Tanwin",
            classType = "GROUP",
            hostName = "Quran Instructor",
            hostIdentity = "teacher_main"
        )
    }

    fun getAllActiveClasses(): List<ActiveClassInfo> = activeClasses.values.toList()

    /**
     * Generates a 5-character non-ambiguous uppercase code prefixed with "VX-".
     * Avoids confusing characters (0, O, 1, I).
     */
    fun generateClassCode(): String {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val randomPart = (1..5).map { chars.random() }.joinToString("")
        return "VX-$randomPart"
    }

    /**
     * Normalizes user-entered class code:
     * - Strips spaces, punctuation, dashes
     * - Uppercases
     * - Adds "VX-" prefix
     */
    fun normalizeClassCode(input: String): String {
        val clean = input.trim().uppercase().replace("[^A-Z0-9]".toRegex(), "")
        if (clean.isBlank()) return ""
        val withoutVx = clean.removePrefix("VX")
        return if (withoutVx.isNotBlank()) "VX-$withoutVx" else "VX-$clean"
    }

    /**
     * Derives deterministic LiveKit room name from a class code.
     * Guaranteed identical across all devices for the same code.
     */
    fun classCodeToRoomName(code: String): String {
        val clean = normalizeClassCode(code).replace("[^A-Z0-9]".toRegex(), "").lowercase()
        return "voxora_class_$clean"
    }

    /**
     * Registers an active class created by a teacher.
     */
    fun registerClass(info: ActiveClassInfo) {
        val normalized = normalizeClassCode(info.code)
        activeClasses[normalized] = info.copy(code = normalized)
    }

    /**
     * Resolves a class code to its active class info.
     * If the class was created on another device (Phone A) and is joined on Phone B,
     * this creates a deterministic resolution to the exact same LiveKit room.
     * Real-time metadata (name, topic, teacher) will sync immediately via LiveKit data channel.
     */
    fun resolveClass(rawCode: String): ActiveClassInfo? {
        val normalized = normalizeClassCode(rawCode)
        if (normalized.length < 5) return null // Must be at least VX-XXX

        val existing = activeClasses[normalized]
        if (existing != null) {
            return existing
        }

        // Cross-device deterministic fallback
        val roomName = classCodeToRoomName(normalized)
        val crossDeviceInfo = ActiveClassInfo(
            code = normalized,
            roomName = roomName,
            className = "Live Quran & Tajwid Class",
            topic = "Interactive Tajwid Session",
            classType = "GROUP",
            hostName = "Teacher / Host",
            hostIdentity = "teacher_remote"
        )
        activeClasses[normalized] = crossDeviceInfo
        return crossDeviceInfo
    }

    /**
     * Unregisters a class when the teacher ends it.
     */
    fun removeClass(code: String) {
        val normalized = normalizeClassCode(code)
        activeClasses.remove(normalized)
    }

    /**
     * Clears all local registered classes.
     */
    fun clear() {
        activeClasses.clear()
    }
}
