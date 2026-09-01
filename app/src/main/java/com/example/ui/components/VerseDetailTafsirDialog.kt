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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.mock.MockQuranData
import com.example.data.model.*
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

/**
 * FEATURE 2: Translation & Tafsir Information Panel
 * FEATURE 3: Notes & Color Highlights
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseDetailTafsirDialog(
    surah: Surah,
    verse: Verse,
    repository: VoxoraRepository,
    onDismiss: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val highlights by repository.verseHighlights.collectAsState()
    val bookmarks by repository.bookmarks.collectAsState()
    val notes by repository.verseNotes.collectAsState()
    val audioState by repository.audioState.collectAsState()
    val quranSettings by repository.quranSettings.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Translations, 1: Tafsir, 2: Highlights & Note
    var personalNoteText by remember { mutableStateOf("") }
    var isEditingNote by remember { mutableStateOf(false) }

    val currentHighlight = remember(highlights, surah.number, verse.verseNumber) {
        highlights.find { it.surahNumber == surah.number && it.verseNumber == verse.verseNumber }
    }

    val isBookmarked = remember(bookmarks, surah.number, verse.verseNumber) {
        bookmarks.any { it.surahNumber == surah.number && it.verseNumber == verse.verseNumber }
    }

    val verseNotesList = remember(notes, surah.number, verse.verseNumber) {
        notes.filter { it.surahNumber == surah.number && it.verseNumber == verse.verseNumber }
    }

    val tafsirData = remember(surah.number, verse.verseNumber) {
        MockQuranData.getTafsirForVerse(surah.number, verse.verseNumber)
    }

    val isPlayingThis = audioState.isPlaying && audioState.surahNumber == surah.number && audioState.verseNumber == verse.verseNumber

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .testTag("verse_detail_tafsir_dialog"),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Emerald800,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "${surah.number}:${verse.verseNumber}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "${surah.nameEnglish} (${surah.nameArabic})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Emerald900
                            )
                        }
                        Text(
                            text = "Ayah Details, Verified Translations & Tafsir",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_verse_detail_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar (Play Audio, Bookmark, Highlight Indicator)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Emerald50,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play / Pause Button
                        Button(
                            onClick = onPlayAudio,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingThis) GoldPrimary else Emerald800,
                                contentColor = if (isPlayingThis) Emerald950 else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlayingThis) "Playing Audio" else "Play Recitation",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Bookmark toggle
                            IconButton(
                                onClick = { repository.toggleBookmark(surah, verse) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) GoldPrimary else Emerald800
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Emerald800
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Translations", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Tafsir", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Highlights & Notes", fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Always show Arabic Text Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentHighlight != null) Color(currentHighlight.colorHex).copy(alpha = 0.2f) else Emerald950
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Text(
                                    text = verse.textArabic,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = quranSettings.arabicFontSizeSp.sp,
                                        lineHeight = (quranSettings.arabicFontSizeSp * quranSettings.lineSpacing.factor).sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (currentHighlight != null) MaterialTheme.colorScheme.onSurface else GoldLight,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (verse.transliteration.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = verse.transliteration,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        ),
                                        color = if (currentHighlight != null) MaterialTheme.colorScheme.onSurfaceVariant else Emerald200
                                    )
                                }
                            }
                        }
                    }

                    // TAB 0: Translations
                    if (selectedTab == 0) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🇬🇧", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "English (Sahih International)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Emerald900
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = verse.translationEnglish,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🇲🇾", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Bahasa Melayu (Tafsiran JAKIM)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Emerald900
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = verse.translationMalay,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // TAB 1: Tafsir & Commentary
                    if (selectedTab == 1) {
                        if (tafsirData != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Emerald50)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Ringkasan Tafsir (Bahasa Melayu)",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Emerald900
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Emerald800
                                            ) {
                                                Text(
                                                    text = "Sahih / JAKIM",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = GoldLight,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = tafsirData.tafsirSummaryMalay,
                                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Divider(color = Emerald200)
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "English Exegesis Summary",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Emerald900
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = tafsirData.tafsirSummaryEnglish,
                                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        if (tafsirData.keyThemes.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Key Themes:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Emerald800
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                tafsirData.keyThemes.forEach { theme ->
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = GoldPrimary.copy(alpha = 0.2f)
                                                    ) {
                                                        Text(
                                                            text = "# $theme",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                            color = Emerald900,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Verified religious integrity notice
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Tafsir Volume Coming Soon",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Emerald900
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Full classical multi-volume Tafsir Ibn Kathir & Al-Jalalayn for this specific Ayah is being indexed from verified scholastic editions.",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // TAB 2: Highlights & Private Notes
                    if (selectedTab == 2) {
                        // Highlight Colors Palette
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Ayah Highlight Color",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Emerald900
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val colors = listOf(
                                            0xFFF59E0B to "Gold",
                                            0xFF10B981 to "Emerald",
                                            0xFF06B6D4 to "Cyan",
                                            0xFFF43F5E to "Coral",
                                            0xFF8B5CF6 to "Purple"
                                        )
                                        colors.forEach { (hex, name) ->
                                            val isSel = currentHighlight?.colorHex == hex
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(hex))
                                                    .clickable { repository.toggleVerseHighlight(surah.number, verse.verseNumber, hex) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSel) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Clear button
                                        if (currentHighlight != null) {
                                            IconButton(
                                                onClick = { repository.removeVerseHighlight(surah.number, verse.verseNumber) }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.FormatColorReset,
                                                    contentDescription = "Clear Highlight",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Add Personal Note Section
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Emerald700,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Add Personal Reflection / Note (On-Device)",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Emerald900
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = personalNoteText,
                                        onValueChange = { personalNoteText = it },
                                        placeholder = { Text("Write your reflection, memorization cue or Ustaz notes...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("verse_note_input_field"),
                                        shape = RoundedCornerShape(12.dp),
                                        minLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (personalNoteText.isNotBlank()) {
                                                repository.addVerseNote(surah, verse, personalNoteText)
                                                personalNoteText = ""
                                            }
                                        },
                                        enabled = personalNoteText.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Save Reflection")
                                    }
                                }
                            }
                        }

                        // Saved Notes for this Ayah
                        if (verseNotesList.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Saved Reflections for this Ayah (${verseNotesList.size})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald900
                                )
                            }
                            items(verseNotesList, key = { it.id }) { noteItem ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Emerald50)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = noteItem.noteText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        IconButton(
                                            onClick = { repository.deleteVerseNote(noteItem.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete Note",
                                                tint = Color.Red.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
