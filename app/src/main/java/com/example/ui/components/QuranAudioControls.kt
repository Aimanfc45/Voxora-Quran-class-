package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.AudioDownloadStatus
import com.example.data.model.AudioRepeatMode
import com.example.data.model.QuranAudioState
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

/**
 * Sticky Mini Audio Player shown when audio is loaded/playing across the app.
 */
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
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Emerald950,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Mini progress indicator bar
            val progress = if (audioState.totalDurationSeconds > 0) {
                (audioState.currentPositionSeconds / audioState.totalDurationSeconds).coerceIn(0f, 1f)
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
                    progress = { progress },
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
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = audioState.errorMessage ?: "Audio playback error",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            onClick = onTogglePlay,
                            shape = RoundedCornerShape(6.dp),
                            color = GoldPrimary
                        ) {
                            Text(
                                text = "Retry",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Emerald950,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Main Mini Player Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Info Section
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
                                modifier = Modifier.size(18.dp),
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

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$surahName • Ayah ${audioState.verseNumber}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${audioState.reciterName} • ${formatTime(audioState.currentPositionSeconds)} / ${formatTime(audioState.totalDurationSeconds)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald200,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onPreviousVerse,
                        modifier = Modifier
                            .size(38.dp)
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
                            .size(40.dp)
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
                            .size(38.dp)
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

/**
 * Full Quran Audio Detail Bottom Sheet.
 * Complete with authentic reciters picker, smooth scrubber, speed selection, repeat modes, and error recovery.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAudioDetailBottomSheet(
    audioState: QuranAudioState,
    surahName: String,
    repository: VoxoraRepository,
    onDismiss: () -> Unit
) {
    var showReciterPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
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
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Reciter Badge & Selector Button
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                onClick = { showReciterPicker = true },
                shape = RoundedCornerShape(16.dp),
                color = Emerald800.copy(alpha = 0.7f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = audioState.reciterName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Error notice banner with Retry
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = audioState.errorMessage ?: "Playback failed. Please retry.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
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

            Spacer(modifier = Modifier.height(20.dp))

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
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldLight
                    )
                    Text(
                        text = formatTime(audioState.totalDurationSeconds),
                        style = MaterialTheme.typography.labelMedium,
                        color = Emerald200
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("audio_repeat_mode_button")
                ) {
                    Icon(
                        imageVector = when (audioState.repeatMode) {
                            AudioRepeatMode.OFF -> Icons.Outlined.Repeat
                            AudioRepeatMode.REPEAT_VERSE -> Icons.Filled.RepeatOne
                            AudioRepeatMode.REPEAT_SURAH -> Icons.Filled.Repeat
                            AudioRepeatMode.REPEAT_RANGE -> Icons.Filled.Loop
                        },
                        contentDescription = "Repeat Mode",
                        tint = if (audioState.repeatMode != AudioRepeatMode.OFF) GoldPrimary else Emerald300,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Previous Verse
                IconButton(
                    onClick = { repository.previousAudioVerse() },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("audio_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Verse",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
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
                        .size(52.dp)
                        .testTag("audio_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Verse",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Stop Audio
                IconButton(
                    onClick = { repository.stopAudio() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("audio_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Audio",
                        tint = Emerald300,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Speed Control Section (Responsive Grid of 5 chips)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Emerald900.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Playback Speed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Text(
                        text = "${audioState.playbackSpeed}x ${if (audioState.playbackSpeed == 1.0f) "(Normal)" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        val isSelected = audioState.playbackSpeed == speed
                        Surface(
                            onClick = { repository.setAudioPlaybackSpeed(speed) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else Emerald950,
                            border = if (isSelected) null else CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${speed}x",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Emerald950 else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Continuous Playback & Auto-Next Row
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Emerald900.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Continuous Playback",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Automatically play next ayah when completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald200
                        )
                    }
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

            // Volume Slider Row
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { repository.setAudioVolume(0f) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeMute,
                        contentDescription = "Mute Volume",
                        tint = Emerald300,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Slider(
                    value = audioState.volume,
                    onValueChange = { repository.setAudioVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldPrimary,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = Emerald900
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .testTag("audio_volume_slider")
                )
                IconButton(
                    onClick = { repository.setAudioVolume(1f) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Max Volume",
                        tint = Emerald300,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Comprehensive Reciters Picker Dialog
    if (showReciterPicker) {
        QariAudioHubDialog(
            currentReciterName = audioState.reciterName,
            repository = repository,
            onSelectReciter = { name ->
                repository.setAudioReciter(name)
                showReciterPicker = false
            },
            onDismiss = { showReciterPicker = false }
        )
    }
}

/**
 * Reciters Selection Dialog with search and favorites.
 */
@Composable
fun RecitersSelectionDialog(
    currentReciterName: String,
    repository: VoxoraRepository,
    onSelectReciter: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val recitersList by repository.reciters.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredReciters = remember(searchQuery, recitersList) {
        val q = searchQuery.trim().lowercase()
        val list = if (q.isBlank()) recitersList else recitersList.filter {
            it.name.lowercase().contains(q) ||
            it.arabicName.contains(searchQuery.trim()) ||
            it.country.lowercase().contains(q)
        }
        list.sortedByDescending { it.isFavorite }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Select Quran Reciter",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Authentic Murattal recitations from verified master Qaris",
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
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("reciter_search_input"),
                    placeholder = { Text("Search reciter or country...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Emerald700)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Reciters list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(filteredReciters) { reciter ->
                        val isSelected = currentReciterName == reciter.name
                        Surface(
                            onClick = { onSelectReciter(reciter.name) },
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
                                        Text(
                                            text = reciter.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface
                                        )
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
            TextButton(onClick = onDismiss) {
                Text("Done", color = Emerald700, fontWeight = FontWeight.Bold)
            }
        }
    )
}

private fun formatTime(seconds: Float): String {
    val totalSec = seconds.toInt().coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format("%02d:%02d", min, sec)
}
