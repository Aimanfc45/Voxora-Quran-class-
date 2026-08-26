package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.*
import com.example.ui.theme.*

enum class QuranViewMode {
    SURAH_CATALOG,
    SURAH_READING,
    JUZ_SELECTION,
    BOOKMARKS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    repository: VoxoraRepository,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val surahs by repository.surahs.collectAsState()
    val juzList by repository.juzList.collectAsState()
    val selectedSurah by repository.selectedSurah.collectAsState()
    val currentVerseIndex by repository.currentVerseIndex.collectAsState()
    val audioState by repository.audioState.collectAsState()
    val quranSettings by repository.quranSettings.collectAsState()
    val bookmarks by repository.bookmarks.collectAsState()
    val lastReadPosition by repository.lastReadPosition.collectAsState()

    var viewMode by remember { mutableStateOf(QuranViewMode.SURAH_READING) }
    var searchQuery by remember { mutableStateOf("") }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAudioDetailSheet by remember { mutableStateOf(false) }
    var showJumpToVerseDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showTajwidGuideDialog by remember { mutableStateOf(false) }
    var showNotesListDialog by remember { mutableStateOf(false) }
    var showNoteDialogForVerse by remember { mutableStateOf<Verse?>(null) }
    var newNoteText by remember { mutableStateOf("") }

    val filteredSurahs = remember(surahs, searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                    it.nameTranslation.contains(searchQuery, ignoreCase = true) ||
                    it.nameArabic.contains(searchQuery) ||
                    it.number.toString() == searchQuery.trim()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    if (viewMode != QuranViewMode.SURAH_READING) {
                        IconButton(
                            onClick = { viewMode = QuranViewMode.SURAH_READING },
                            modifier = Modifier.testTag("quran_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Reading",
                                tint = Emerald700
                            )
                        }
                    }
                },
                title = {
                    if (viewMode == QuranViewMode.SURAH_READING) {
                        Column {
                            Text(
                                text = "${selectedSurah.number}. ${selectedSurah.nameEnglish} (${selectedSurah.nameArabic})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${selectedSurah.nameTranslation} • ${selectedSurah.totalVerses} Verses (${selectedSurah.revelationType})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = when (viewMode) {
                                QuranViewMode.SURAH_CATALOG -> "Surah Directory"
                                QuranViewMode.JUZ_SELECTION -> "Juz (1–30)"
                                QuranViewMode.BOOKMARKS -> "Saved Bookmarks"
                                else -> "Holy Quran"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Emerald700
                        )
                    }
                },
                actions = {
                    // Quran Search Button
                    IconButton(
                        onClick = { showSearchDialog = true },
                        modifier = Modifier.testTag("quran_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Quran",
                            tint = Emerald700
                        )
                    }

                    if (viewMode == QuranViewMode.SURAH_READING) {
                        // Tajwid Rules Guide Button
                        IconButton(
                            onClick = { showTajwidGuideDialog = true },
                            modifier = Modifier.testTag("quran_tajwid_guide_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = "Tajwid Rules",
                                tint = Emerald700
                            )
                        }

                        // Notes Button
                        IconButton(
                            onClick = { showNotesListDialog = true },
                            modifier = Modifier.testTag("quran_notes_list_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EditNote,
                                contentDescription = "Ayah Notes",
                                tint = Emerald700
                            )
                        }

                        // Jump to Verse Button
                        IconButton(
                            onClick = { showJumpToVerseDialog = true },
                            modifier = Modifier.testTag("quran_jump_verse_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Pin,
                                contentDescription = "Jump to Verse",
                                tint = Emerald700
                            )
                        }

                        // Surah Directory Button
                        IconButton(
                            onClick = { viewMode = QuranViewMode.SURAH_CATALOG },
                            modifier = Modifier.testTag("quran_surah_list_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Surah Catalog",
                                tint = Emerald700
                            )
                        }

                        // Display Settings Button
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier.testTag("quran_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FormatSize,
                                contentDescription = "Display Settings",
                                tint = Emerald700
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (viewMode) {
                QuranViewMode.SURAH_READING -> {
                    SurahReadingView(
                        surah = selectedSurah,
                        currentVerseIndex = currentVerseIndex,
                        audioState = audioState,
                        quranSettings = quranSettings,
                        repository = repository,
                        lastReadPosition = lastReadPosition,
                        onOpenCatalog = { viewMode = QuranViewMode.SURAH_CATALOG },
                        onOpenJuz = { viewMode = QuranViewMode.JUZ_SELECTION },
                        onOpenBookmarks = { viewMode = QuranViewMode.BOOKMARKS },
                        onAddNote = { verse ->
                            showNoteDialogForVerse = verse
                            newNoteText = ""
                        },
                        onShowSnackbar = onShowSnackbar
                    )
                }

                QuranViewMode.SURAH_CATALOG -> {
                    SurahCatalogView(
                        surahs = filteredSurahs,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onSurahClick = { surah ->
                            repository.selectSurah(surah.number)
                            viewMode = QuranViewMode.SURAH_READING
                        },
                        onOpenJuz = { viewMode = QuranViewMode.JUZ_SELECTION },
                        onOpenBookmarks = { viewMode = QuranViewMode.BOOKMARKS }
                    )
                }

                QuranViewMode.JUZ_SELECTION -> {
                    JuzSelectionView(
                        juzList = juzList,
                        onJuzClick = { juz ->
                            repository.selectJuz(juz.number)
                            viewMode = QuranViewMode.SURAH_READING
                            onShowSnackbar("Loaded Juz ${juz.number} (${juz.startSurahName})")
                        }
                    )
                }

                QuranViewMode.BOOKMARKS -> {
                    BookmarksListView(
                        bookmarks = bookmarks,
                        onBookmarkClick = { bm ->
                            repository.selectSurah(bm.surahNumber)
                            repository.jumpToVerse(bm.verseNumber)
                            viewMode = QuranViewMode.SURAH_READING
                        },
                        onDeleteBookmark = { bm ->
                            val surah = surahs.find { it.number == bm.surahNumber } ?: selectedSurah
                            val verse = surah.verses.find { it.verseNumber == bm.verseNumber }
                            if (verse != null) {
                                repository.toggleBookmark(surah, verse)
                                onShowSnackbar("Bookmark removed")
                            }
                        }
                    )
                }
            }
        }
    }

    // Display & Font Settings Dialog
    if (showSettingsDialog) {
        QuranSettingsDialog(
            settings = quranSettings,
            repository = repository,
            onDismiss = { showSettingsDialog = false }
        )
    }

    // Full Audio Controls BottomSheet
    if (showAudioDetailSheet) {
        QuranAudioDetailBottomSheet(
            audioState = audioState,
            surahName = selectedSurah.nameEnglish,
            repository = repository,
            onDismiss = { showAudioDetailSheet = false }
        )
    }

    // Quran Full Search Dialog
    if (showSearchDialog) {
        QuranSearchDialog(
            repository = repository,
            onResultClick = { searchResult ->
                repository.selectSurah(searchResult.surahNumber)
                repository.jumpToVerse(searchResult.verseNumber)
                viewMode = QuranViewMode.SURAH_READING
                showSearchDialog = false
            },
            onDismiss = { showSearchDialog = false }
        )
    }

    // Tajwid Rules Guide Dialog
    if (showTajwidGuideDialog) {
        TajwidGuideDialog(
            repository = repository,
            onDismiss = { showTajwidGuideDialog = false }
        )
    }

    // Private Notes List Dialog
    if (showNotesListDialog) {
        QuranNotesListDialog(
            repository = repository,
            onJumpToNote = { note ->
                repository.selectSurah(note.surahNumber)
                repository.jumpToVerse(note.verseNumber)
                viewMode = QuranViewMode.SURAH_READING
                showNotesListDialog = false
            },
            onDismiss = { showNotesListDialog = false }
        )
    }

    // Jump to Verse Dialog
    if (showJumpToVerseDialog) {
        JumpToVerseDialog(
            maxVerses = selectedSurah.totalVerses,
            currentVerse = selectedSurah.verses.getOrNull(currentVerseIndex)?.verseNumber ?: 1,
            onJump = { verseNum ->
                repository.jumpToVerse(verseNum)
                showJumpToVerseDialog = false
            },
            onDismiss = { showJumpToVerseDialog = false }
        )
    }

    // Add Note to Verse Dialog
    if (showNoteDialogForVerse != null) {
        val targetVerse = showNoteDialogForVerse!!
        AlertDialog(
            onDismissRequest = { showNoteDialogForVerse = null },
            title = {
                Text(
                    text = "Add Note — Verse ${targetVerse.verseNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = targetVerse.textArabic,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Emerald700,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = newNoteText,
                        onValueChange = { newNoteText = it },
                        placeholder = { Text("Write personal reflection, Tajwid note, or reminder...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("verse_note_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.addVerseNote(selectedSurah, targetVerse, newNoteText)
                        repository.toggleBookmark(selectedSurah, targetVerse, newNoteText)
                        showNoteDialogForVerse = null
                        onShowSnackbar("Note saved to Bookmarks & Notes")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialogForVerse = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SurahReadingView(
    surah: Surah,
    currentVerseIndex: Int,
    audioState: QuranAudioState,
    quranSettings: QuranSettings,
    repository: VoxoraRepository,
    lastReadPosition: String,
    onOpenCatalog: () -> Unit,
    onOpenJuz: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onAddNote: (Verse) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val listState = rememberLazyListState()

    // Smooth Auto-scroll to active verse when audio is playing or jumping
    LaunchedEffect(audioState.surahNumber, audioState.verseNumber, currentVerseIndex) {
        if (audioState.surahNumber == surah.number && audioState.isPlaying) {
            val targetIndex = (audioState.verseNumber + 1).coerceAtMost(surah.verses.size + 1)
            listState.animateScrollToItem(targetIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Quick Nav & Last Read Strip
        item {
            Surface(
                color = Emerald900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenCatalog() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Last: $lastReadPosition",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = onOpenJuz,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Juz Index", color = Emerald200, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = onOpenBookmarks,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Bookmarks", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Surah Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Emerald800, Emerald950)
                        )
                    )
            ) {
                SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.12f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = surah.nameArabic,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${surah.number}. ${surah.nameEnglish} — ${surah.nameTranslation}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "${surah.revelationType} • ${surah.totalVerses} Verses",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald300
                    )

                    if (surah.number != 9 && surah.number != 1) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.titleLarge,
                            color = GoldLight,
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }

        // Verses List
        if (surah.verses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sample verses for ${surah.nameEnglish} are being generated.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onOpenCatalog,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Text("Browse Available Surahs")
                        }
                    }
                }
            }
        } else {
            items(surah.verses, key = { it.id }) { verse ->
                val isCurrentPlaying = audioState.isPlaying &&
                        audioState.surahNumber == surah.number &&
                        audioState.verseNumber == verse.verseNumber

                val isBookmarked = repository.isVerseBookmarked(surah.number, verse.verseNumber)
                val verseNotes = repository.getNotesForVerse(surah.number, verse.verseNumber)

                VerseItemCard(
                    verse = verse,
                    isCurrentPlaying = isCurrentPlaying,
                    isBookmarked = isBookmarked,
                    hasNotes = verseNotes.isNotEmpty(),
                    quranSettings = quranSettings,
                    onPlayAudio = {
                        repository.playVerseAudio(surah.number, verse.verseNumber)
                    },
                    onToggleBookmark = {
                        val bookmarked = repository.toggleBookmark(surah, verse)
                        onShowSnackbar(if (bookmarked) "Verse ${verse.verseNumber} bookmarked!" else "Bookmark removed")
                    },
                    onAddNote = { onAddNote(verse) }
                )
            }
        }
    }
}

@Composable
private fun VerseItemCard(
    verse: Verse,
    isCurrentPlaying: Boolean,
    isBookmarked: Boolean,
    hasNotes: Boolean,
    quranSettings: QuranSettings,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onAddNote: () -> Unit
) {
    val lineHeightMultiplier = when (quranSettings.lineSpacing) {
        QuranLineSpacing.COMPACT -> 1.4f
        QuranLineSpacing.RELAXED -> 2.0f
        else -> 1.7f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("verse_item_${verse.verseNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying) Emerald900.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isCurrentPlaying) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)) else CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Verse Header Strip (Number + Audio Play + Bookmark + Note Actions)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isCurrentPlaying) GoldPrimary else Emerald100),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${verse.verseNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isCurrentPlaying) Emerald950 else Emerald900
                        )
                    }

                    if (isCurrentPlaying) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldPrimary.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Playing",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldPrimary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    if (hasNotes) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = Emerald50
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Has notes",
                                    tint = Emerald700,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Note", style = MaterialTheme.typography.labelSmall, color = Emerald800, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Dedicated Audio Play Button
                    IconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier.size(34.dp).testTag("verse_play_btn_${verse.verseNumber}")
                    ) {
                        Icon(
                            imageVector = if (isCurrentPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircleOutline,
                            contentDescription = "Play Verse Audio",
                            tint = if (isCurrentPlaying) GoldPrimary else Emerald700,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Bookmark Button
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(34.dp).testTag("verse_bookmark_btn_${verse.verseNumber}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark Verse",
                            tint = if (isBookmarked) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Add Note Button
                    IconButton(
                        onClick = onAddNote,
                        modifier = Modifier.size(34.dp).testTag("verse_note_btn_${verse.verseNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EditNote,
                            contentDescription = "Add Verse Note",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Text
            Text(
                text = verse.textArabic,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = quranSettings.arabicFontSizeSp.sp,
                    lineHeight = (quranSettings.arabicFontSizeSp * lineHeightMultiplier).sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Word-by-Word breakdown if enabled
            if (quranSettings.showWordByWord) {
                val words = remember(verse.textArabic) { verse.textArabic.split(" ").filter { it.isNotBlank() } }
                if (words.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(words) { word ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Emerald50,
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald900,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tajwid Rule Badge if present and Tajwid colors enabled
            if (quranSettings.showTajwidColors && verse.tajwidRuleHighlight != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TajwidRuleBadge(ruleText = verse.tajwidRuleHighlight)
            }

            // Transliteration
            if (quranSettings.showTransliteration) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = verse.transliteration,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = Emerald700
                )
            }

            // English Translation
            if (quranSettings.showTranslation && (quranSettings.showEnglishTranslation || quranSettings.translationLanguage == "English" || quranSettings.readingMode == ReadingDisplayMode.MULTI_TRANSLATION)) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = verse.translationEnglish,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Malay Translation
            if (quranSettings.showMalayTranslation || quranSettings.translationLanguage == "Bahasa Melayu" || quranSettings.readingMode == ReadingDisplayMode.MULTI_TRANSLATION) {
                if (quranSettings.showTranslation && verse.translationMalay.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BM: ${verse.translationMalay}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald800
                    )
                }
            }
        }
    }
}

@Composable
private fun SurahCatalogView(
    surahs: List<Surah>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSurahClick: (Surah) -> Unit,
    onOpenJuz: () -> Unit,
    onOpenBookmarks: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search and filter row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("surah_search_field"),
            placeholder = { Text("Search Surah name or number (e.g. Baqarah, 36, Mulk)...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenJuz,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Juz Index (1-30)")
            }

            OutlinedButton(
                onClick = onOpenBookmarks,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bookmarks", color = Emerald700)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(surahs, key = { it.number }) { surah ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSurahClick(surah) }
                        .testTag("surah_card_${surah.number}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Emerald100),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${surah.number}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald900
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = surah.nameEnglish,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${surah.nameTranslation} • ${surah.totalVerses} Verses (${surah.revelationType})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = surah.nameArabic,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JuzSelectionView(
    juzList: List<JuzInfo>,
    onJuzClick: (JuzInfo) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(juzList) { juz ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onJuzClick(juz) }
                    .testTag("juz_card_${juz.number}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Emerald700,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "Juz ${juz.number}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = juz.nameArabic,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Starts at:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${juz.startSurahName} (v${juz.startVerse})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarksListView(
    bookmarks: List<QuranBookmark>,
    onBookmarkClick: (QuranBookmark) -> Unit,
    onDeleteBookmark: (QuranBookmark) -> Unit
) {
    if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = Emerald300,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No saved bookmarks yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap the bookmark icon or add notes while reading to save verses here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(bookmarks, key = { it.id }) { bm ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookmarkClick(bm) }
                        .testTag("bookmark_item_${bm.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Surah ${bm.surahName} (${bm.surahNumber}:${bm.verseNumber})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald800
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = bm.snippetArabic,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (bm.note.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Emerald50
                                ) {
                                    Text(
                                        text = "Note: ${bm.note}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Emerald900,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { onDeleteBookmark(bm) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Bookmark",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuranSettingsDialog(
    settings: QuranSettings,
    repository: VoxoraRepository,
    onDismiss: () -> Unit
) {
    val languages = listOf("English", "Bahasa Melayu")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.FormatSize, contentDescription = null, tint = Emerald700)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Display & Reading Mode",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Reading Display Preset Modes
                item {
                    Column {
                        Text(
                            text = "Reading Mode Preset",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Emerald800
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ReadingDisplayMode.values().forEach { mode ->
                                val isSelected = settings.readingMode == mode
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { repository.setReadingDisplayMode(mode) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Emerald700 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = when (mode) {
                                                    ReadingDisplayMode.ARABIC_ONLY -> "Mushaf / Arabic Only"
                                                    ReadingDisplayMode.ARABIC_EN -> "Arabic + English Translation"
                                                    ReadingDisplayMode.ARABIC_BM -> "Arabic + Bahasa Melayu"
                                                    ReadingDisplayMode.MULTI_TRANSLATION -> "Arabic + Dual Translations"
                                                    ReadingDisplayMode.ARABIC_TRANSLITERATION -> "Arabic + Transliteration"
                                                },
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Font Size Slider + Live Preview
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Arabic Font Size", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("${settings.arabicFontSizeSp.toInt()} sp", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Emerald700)
                        }
                        Slider(
                            value = settings.arabicFontSizeSp,
                            onValueChange = { repository.updateQuranFontSize(it) },
                            valueRange = 20f..42f,
                            colors = SliderDefaults.colors(
                                thumbColor = Emerald700,
                                activeTrackColor = Emerald700
                            ),
                            modifier = Modifier.testTag("font_size_slider")
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald50,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = settings.arabicFontSizeSp.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Emerald950,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                // Line Spacing Selector
                item {
                    Column {
                        Text("Line Spacing", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            QuranLineSpacing.values().forEach { mode ->
                                val isSelected = settings.lineSpacing == mode
                                ChoicePill(
                                    label = mode.label,
                                    isSelected = isSelected,
                                    onClick = { repository.setLineSpacing(mode) }
                                )
                            }
                        }
                    }
                }

                // Tajwid Colors Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tajwid Color Highlighting", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Color-codes Ghunnah, Idgham, Ikhfa, etc.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.showTajwidColors,
                            onCheckedChange = { repository.toggleTajwidColors(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Emerald700, checkedTrackColor = Emerald200)
                        )
                    }
                }

                // Word by Word Breakdown Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Word-by-Word Translation", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Displays vocabulary cards under each verse", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.showWordByWord,
                            onCheckedChange = { repository.toggleWordByWord(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Emerald700, checkedTrackColor = Emerald200)
                        )
                    }
                }

                // Show Transliteration Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Show Transliteration", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Latin phonetic pronunciation", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.showTransliteration,
                            onCheckedChange = { repository.toggleTransliteration(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Emerald700, checkedTrackColor = Emerald200)
                        )
                    }
                }

                // Translation Language Selector
                item {
                    Column {
                        Text("Default Translation Language", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            languages.forEach { lang ->
                                val isSelected = settings.translationLanguage == lang
                                ChoicePill(
                                    label = lang,
                                    isSelected = isSelected,
                                    onClick = { repository.setTranslationLanguage(lang) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun JumpToVerseDialog(
    maxVerses: Int,
    currentVerse: Int,
    onJump: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var targetVerse by remember { mutableStateOf(currentVerse.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Jump to Verse (1 – $maxVerses)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Verse ${targetVerse.toInt()}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Emerald700
                )
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = targetVerse,
                    onValueChange = { targetVerse = it },
                    valueRange = 1f..maxVerses.toFloat().coerceAtLeast(1f),
                    steps = (maxVerses - 2).coerceAtLeast(0),
                    colors = SliderDefaults.colors(thumbColor = Emerald700, activeTrackColor = Emerald700),
                    modifier = Modifier.testTag("jump_verse_slider")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onJump(targetVerse.toInt()) },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
            ) {
                Text("Go to Verse")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
