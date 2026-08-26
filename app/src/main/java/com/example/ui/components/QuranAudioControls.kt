package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioRepeatMode
import com.example.data.model.QuranAudioState
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

@Composable
fun QuranMiniAudioPlayer(
    audioState: QuranAudioState,
    surahName: String,
    onExpandControls: () -> Unit,
    onTogglePlay: () -> Unit,
    onNextVerse: () -> Unit,
    onPreviousVerse: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExpandControls() }
            .testTag("quran_mini_audio_player"),
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        color = Emerald900,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Mini progress indicator bar
            val progress = if (audioState.totalDurationSeconds > 0) {
                audioState.currentPositionSeconds / audioState.totalDurationSeconds
            } else 0f

            if (audioState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = GoldPrimary,
                    trackColor = Emerald800
                )
            } else {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = GoldPrimary,
                    trackColor = Emerald800
                )
            }

            // Error notice banner if any
            if (audioState.errorMessage != null) {
                Surface(
                    color = Color(0xFF7F1D1D),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = audioState.errorMessage,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Tap to Retry",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldLight,
                            modifier = Modifier.clickable { onTogglePlay() }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (audioState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = GoldPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "$surahName • Ayah ${audioState.verseNumber}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "${audioState.reciterName} • ${formatTime(audioState.currentPositionSeconds)} / ${formatTime(audioState.totalDurationSeconds)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald200
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onPreviousVerse,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mini_player_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Verse",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                            .testTag("mini_player_play_button")
                    ) {
                        if (audioState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Emerald950,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                                tint = Emerald950
                            )
                        }
                    }

                    IconButton(
                        onClick = onNextVerse,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mini_player_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Verse",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("mini_player_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Player",
                            tint = Emerald300
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAudioDetailBottomSheet(
    audioState: QuranAudioState,
    surahName: String,
    repository: VoxoraRepository,
    onDismiss: () -> Unit
) {
    val recitersList by repository.reciters.collectAsState()
    var showReciterPicker by remember { mutableStateOf(false) }
    var reciterSearchQuery by remember { mutableStateOf("") }
    var showRepeatCountDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Info
            Text(
                text = "QURAN AUDIO RECITATION",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$surahName — Ayah ${audioState.verseNumber}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            // Reciter Badge & Selector Button
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                onClick = { showReciterPicker = true },
                shape = RoundedCornerShape(16.dp),
                color = Emerald800.copy(alpha = 0.6f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = audioState.reciterName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Emerald200,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Error notice banner
            if (audioState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFF7F1D1D),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = audioState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { repository.resumeAudio() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Text("Retry", color = Emerald950, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seek Bar & Timers
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = audioState.currentPositionSeconds,
                    onValueChange = { repository.seekAudioTo(it) },
                    valueRange = 0f..audioState.totalDurationSeconds.coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = GoldPrimary,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = Emerald900
                    ),
                    modifier = Modifier.testTag("audio_seek_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(audioState.currentPositionSeconds),
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald200
                    )
                    Text(
                        text = formatTime(audioState.totalDurationSeconds),
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald200
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Repeat Mode Button
                IconButton(
                    onClick = {
                        val nextMode = when (audioState.repeatMode) {
                            AudioRepeatMode.OFF -> AudioRepeatMode.REPEAT_VERSE
                            AudioRepeatMode.REPEAT_VERSE -> AudioRepeatMode.REPEAT_SURAH
                            AudioRepeatMode.REPEAT_SURAH -> AudioRepeatMode.REPEAT_RANGE
                            AudioRepeatMode.REPEAT_RANGE -> AudioRepeatMode.OFF
                        }
                        repository.setAudioRepeatMode(nextMode)
                    },
                    modifier = Modifier.testTag("audio_repeat_mode_button")
                ) {
                    Icon(
                        imageVector = when (audioState.repeatMode) {
                            AudioRepeatMode.OFF -> Icons.Outlined.Repeat
                            AudioRepeatMode.REPEAT_VERSE -> Icons.Filled.RepeatOne
                            AudioRepeatMode.REPEAT_SURAH -> Icons.Filled.Repeat
                            AudioRepeatMode.REPEAT_RANGE -> Icons.Filled.Loop
                        },
                        contentDescription = "Repeat Mode",
                        tint = if (audioState.repeatMode != AudioRepeatMode.OFF) GoldPrimary else Emerald300
                    )
                }

                // Previous Verse
                IconButton(
                    onClick = { repository.previousAudioVerse() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("audio_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Verse",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play / Pause FAB
                FloatingActionButton(
                    onClick = { repository.toggleAudioPlayback() },
                    containerColor = GoldPrimary,
                    contentColor = Emerald950,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("audio_play_pause_button")
                ) {
                    if (audioState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Emerald950,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Next Verse
                IconButton(
                    onClick = { repository.nextAudioVerse() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("audio_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Verse",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Stop Audio
                IconButton(
                    onClick = { repository.stopAudio() },
                    modifier = Modifier.testTag("audio_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Audio",
                        tint = Emerald300
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Speed & Volume Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Emerald900.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback Speed Chips
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Speed:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald200
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        val isSelected = audioState.playbackSpeed == speed
                        Surface(
                            onClick = { repository.setAudioPlaybackSpeed(speed) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else Color.Transparent,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Emerald950 else Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Auto Advance Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Auto-Next",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald200
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = audioState.autoNextVerse,
                        onCheckedChange = { repository.toggleAutoNextVerse(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldPrimary,
                            checkedTrackColor = Emerald700
                        ),
                        modifier = Modifier.testTag("audio_autonext_switch")
                    )
                }
            }

            // Repeat Configuration Banner (if active)
            if (audioState.repeatMode == AudioRepeatMode.REPEAT_VERSE) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Repeat Ayah ${audioState.verseNumber}:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldLight
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1 to "1x", 3 to "3x", 5 to "5x", 999 to "Loop ∞").forEach { (count, label) ->
                            val isSel = audioState.repeatCountSetting == count
                            Surface(
                                onClick = { repository.setAudioRepeatCount(count) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) GoldPrimary else Emerald900
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) Emerald950 else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Volume Slider
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeMute,
                    contentDescription = null,
                    tint = Emerald300,
                    modifier = Modifier.size(20.dp)
                )
                Slider(
                    value = audioState.volume,
                    onValueChange = { repository.setAudioVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Emerald300,
                        activeTrackColor = Emerald300,
                        inactiveTrackColor = Emerald900
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .testTag("audio_volume_slider")
                )
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = Emerald300,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Comprehensive 10 Reciters Picker Dialog
    if (showReciterPicker) {
        val filteredReciters = remember(reciterSearchQuery, recitersList) {
            val q = reciterSearchQuery.trim().lowercase()
            val list = if (q.isBlank()) recitersList else recitersList.filter {
                it.name.lowercase().contains(q) ||
                it.arabicName.contains(reciterSearchQuery.trim()) ||
                it.country.lowercase().contains(q)
            }
            list.sortedByDescending { it.isFavorite }
        }

        AlertDialog(
            onDismissRequest = { showReciterPicker = false },
            title = {
                Column {
                    Text(
                        text = "Select Quran Reciter",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "10 verified master Qaris with high-fidelity Murattal audio",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = reciterSearchQuery,
                        onValueChange = { reciterSearchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("reciter_search_input"),
                        placeholder = { Text("Search reciter or country...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Emerald700)
                        },
                        trailingIcon = {
                            if (reciterSearchQuery.isNotBlank()) {
                                IconButton(onClick = { reciterSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Reciters list
                    androidx.compose.foundation.lazy.LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(filteredReciters.size) { idx ->
                            val reciter = filteredReciters[idx]
                            val isSelected = audioState.reciterName == reciter.name
                            Surface(
                                onClick = {
                                    repository.setAudioReciter(reciter.name)
                                    showReciterPicker = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Emerald100 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = reciter.flagEmoji,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = reciter.name,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                    ),
                                                    color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${reciter.arabicName} • ${reciter.style} (${reciter.bitRate})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSelected) Emerald800 else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { repository.toggleFavoriteReciter(reciter.name) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (reciter.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                contentDescription = "Favorite",
                                                tint = if (reciter.isFavorite) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = Emerald700,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReciterPicker = false }) {
                    Text("Done", color = Emerald700, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

private fun formatTime(seconds: Float): String {
    val totalSec = seconds.toInt().coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format("%02d:%02d", min, sec)
}
