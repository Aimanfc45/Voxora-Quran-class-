package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ReciterInfo
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

/**
 * FEATURE 1: Modern Qari Audio Hub
 * - Search reciter (by Name, Arabic Name, Country)
 * - Reciter List with authentic photos/badges and country flags
 * - Favorite reciter toggle
 * - Set as default reciter indicator & action
 * - Preview audio sample before confirming
 * - Select reciter (updates audio engine seamlessly)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QariAudioHubDialog(
    repository: VoxoraRepository,
    currentReciterName: String,
    onSelectReciter: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val recitersList by repository.reciters.collectAsState()
    val audioState by repository.audioState.collectAsState()
    val quranSettings by repository.quranSettings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("All") } // "All", "Favorites", "Saudi Arabia", "Egypt", "Kuwait"

    val filteredReciters = remember(searchQuery, selectedFilterTab, recitersList, quranSettings.favoriteReciters) {
        val q = searchQuery.trim().lowercase()
        recitersList.filter { reciter ->
            val matchesQuery = q.isBlank() ||
                    reciter.name.lowercase().contains(q) ||
                    reciter.arabicName.contains(searchQuery.trim()) ||
                    reciter.country.lowercase().contains(q)

            val matchesTab = when (selectedFilterTab) {
                "Favorites" -> quranSettings.favoriteReciters.contains(reciter.name)
                "Saudi Arabia" -> reciter.country.contains("Saudi", ignoreCase = true)
                "Egypt" -> reciter.country.contains("Egypt", ignoreCase = true)
                "Kuwait" -> reciter.country.contains("Kuwait", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesTab
        }.sortedWith(
            compareByDescending<ReciterInfo> { it.name == quranSettings.defaultReciter }
                .thenByDescending { quranSettings.favoriteReciters.contains(it.name) }
        )
    }

    Dialog(
        onDismissRequest = {
            repository.stopAudioPreview()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .testTag("qari_audio_hub_dialog"),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Qari Audio Hub",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Emerald800
                            )
                        }
                        Text(
                            text = "Verified master Qaris with studio-grade Murattal recitations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            repository.stopAudioPreview()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("close_qari_hub_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_reciter_field"),
                    placeholder = { Text("Search by Qari name, country, or Arabic...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Emerald600,
                        unfocusedBorderColor = Emerald200
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Tabs (All, Favorites, Countries)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tabs = listOf(
                        "All" to "All (${recitersList.size})",
                        "Favorites" to "Favorites ⭐ (${quranSettings.favoriteReciters.size})",
                        "Saudi Arabia" to "Saudi Arabia 🇸🇦",
                        "Egypt" to "Egypt 🇪🇬",
                        "Kuwait" to "Kuwait 🇰🇼"
                    )
                    items(tabs) { (key, label) ->
                        val isSelected = selectedFilterTab == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterTab = key },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald700,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reciters List
                if (filteredReciters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.PersonSearch,
                                contentDescription = null,
                                tint = Emerald300,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Qari found matching \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredReciters, key = { it.id }) { reciter ->
                            val isCurrentSelected = reciter.name == currentReciterName
                            val isDefaultReciter = reciter.name == quranSettings.defaultReciter
                            val isFavorite = quranSettings.favoriteReciters.contains(reciter.name)
                            val isPreviewingThis = audioState.isPreviewPlaying && audioState.previewReciterName == reciter.name

                            QariCardItem(
                                reciter = reciter,
                                isSelected = isCurrentSelected,
                                isDefault = isDefaultReciter,
                                isFavorite = isFavorite,
                                isPreviewing = isPreviewingThis,
                                onSelect = {
                                    repository.stopAudioPreview()
                                    onSelectReciter(reciter.name)
                                },
                                onToggleFavorite = {
                                    repository.toggleFavoriteReciter(reciter.name)
                                },
                                onSetDefault = {
                                    repository.setDefaultReciter(reciter.name)
                                },
                                onTogglePreview = {
                                    if (isPreviewingThis) {
                                        repository.stopAudioPreview()
                                    } else {
                                        repository.previewReciterAudio(reciter.name)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QariCardItem(
    reciter: ReciterInfo,
    isSelected: Boolean,
    isDefault: Boolean,
    isFavorite: Boolean,
    isPreviewing: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSetDefault: () -> Unit,
    onTogglePreview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("qari_item_${reciter.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Emerald50 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar / Flag Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Emerald800 else Emerald100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reciter.flagEmoji,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Metadata
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = reciter.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = reciter.arabicName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = GoldPrimary
                        )
                        Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${reciter.country} (${reciter.style})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Favorite Star
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite Reciter",
                        tint = if (isFavorite) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description summary
            Text(
                text = reciter.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row (Preview, Set Default, Select)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio Preview Button
                OutlinedButton(
                    onClick = onTogglePreview,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isPreviewing) GoldPrimary else Emerald800
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPreviewing) "Stop Sample" else "Preview Audio",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Default Reciter Toggle Button
                    if (!isDefault) {
                        TextButton(
                            onClick = onSetDefault,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Set Default",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald700
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Emerald800,
                            modifier = Modifier.height(28.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Default",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Select Button
                    Button(
                        onClick = onSelect,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Emerald800 else GoldPrimary,
                            contentColor = if (isSelected) Color.White else Emerald950
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (isSelected) "Active" else "Select",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
