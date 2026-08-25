package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mock.MockQuranData
import com.example.data.model.QuranBookmark
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.CategoryPill
import com.example.ui.components.TajwidRuleBadge
import com.example.ui.theme.*

enum class QuranViewMode {
    SURAH_CATALOG,
    SURAH_READING,
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
    val isPlayingAudio by repository.isPlayingAudio.collectAsState()
    val quranSettings by repository.quranSettings.collectAsState()
    val bookmarks by repository.bookmarks.collectAsState()
    val lastReadPosition by repository.lastReadPosition.collectAsState()

    var viewMode by remember { mutableStateOf(QuranViewMode.SURAH_READING) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Surah, 1: Juz, 2: Bookmarks
    var searchQuery by remember { mutableStateOf("") }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val filteredSurahs = remember(surahs, searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                    it.nameTranslation.contains(searchQuery, ignoreCase = true) ||
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
                    if (viewMode == QuranViewMode.SURAH_READING) {
                        IconButton(
                            onClick = { viewMode = QuranViewMode.SURAH_CATALOG },
                            modifier = Modifier.testTag("quran_back_to_catalog_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Surah List",
                                tint = Emerald700
                            )
                        }
                    }
                },
                title = {
                    if (viewMode == QuranViewMode.SURAH_READING) {
                        Column {
                            Text(
                                text = "${selectedSurah.number}. ${selectedSurah.nameEnglish}",
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
                            text = "Holy Quran",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Emerald700
                        )
                    }
                },
                actions = {
                    if (viewMode == QuranViewMode.SURAH_READING) {
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
        },
        bottomBar = {
            if (viewMode == QuranViewMode.SURAH_READING) {
                // Interactive Audio & Navigation Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Verse ${currentVerseIndex + 1} of ${selectedSurah.verses.size}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald700
                                )
                                Text(
                                    text = "Reciter: ${quranSettings.reciterName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        repository.previousVerse()
                                    },
                                    enabled = currentVerseIndex > 0,
                                    modifier = Modifier.testTag("quran_prev_verse_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipPrevious,
                                        contentDescription = "Previous Verse"
                                    )
                                }

                                FilledIconButton(
                                    onClick = {
                                        repository.toggleAudioPlayback()
                                        if (!isPlayingAudio) {
                                            onShowSnackbar("Playing recitation for Verse ${currentVerseIndex + 1}...")
                                        } else {
                                            onShowSnackbar("Audio paused")
                                        }
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("quran_play_audio_button"),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = Emerald700,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlayingAudio) "Pause" else "Play",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        repository.nextVerse()
                                    },
                                    enabled = currentVerseIndex < selectedSurah.verses.size - 1,
                                    modifier = Modifier.testTag("quran_next_verse_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Next Verse"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (viewMode) {
                QuranViewMode.SURAH_CATALOG -> {
                    QuranCatalogView(
                        surahs = filteredSurahs,
                        juzList = juzList,
                        selectedTab = selectedTab,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onTabChange = { selectedTab = it },
                        lastReadPosition = lastReadPosition,
                        onSelectSurah = { surahNum ->
                            repository.selectSurah(surahNum)
                            viewMode = QuranViewMode.SURAH_READING
                        },
                        onSelectJuz = { juzNum ->
                            val juz = juzList.find { it.number == juzNum }
                            if (juz != null) {
                                val s = surahs.find { it.nameEnglish == juz.startSurahName } ?: surahs.first()
                                repository.selectSurah(s.number)
                                viewMode = QuranViewMode.SURAH_READING
                            }
                        },
                        bookmarks = bookmarks,
                        onSelectBookmark = { bm ->
                            repository.selectSurah(bm.surahNumber)
                            viewMode = QuranViewMode.SURAH_READING
                        }
                    )
                }

                QuranViewMode.SURAH_READING -> {
                    QuranReaderView(
                        surah = selectedSurah,
                        currentVerseIndex = currentVerseIndex,
                        isPlayingAudio = isPlayingAudio,
                        settings = quranSettings,
                        isBookmarked = { verseNum ->
                            repository.isVerseBookmarked(selectedSurah.number, verseNum)
                        },
                        onToggleBookmark = { verse ->
                            val added = repository.toggleBookmark(selectedSurah, verse)
                            onShowSnackbar(if (added) "Bookmarked Verse ${verse.verseNumber}" else "Removed Bookmark")
                        },
                        onSelectVerse = { idx ->
                            repository.setVerseIndex(idx)
                        }
                    )
                }

                QuranViewMode.BOOKMARKS -> {
                    BookmarksListView(
                        bookmarks = bookmarks,
                        onSelectBookmark = { bm ->
                            repository.selectSurah(bm.surahNumber)
                            viewMode = QuranViewMode.SURAH_READING
                        }
                    )
                }
            }
        }
    }

    // Display Settings Sheet Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = "Quran Display Settings",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Emerald700
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Arabic Font Size (${quranSettings.arabicFontSizeSp.toInt()} sp)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = quranSettings.arabicFontSizeSp,
                        onValueChange = { repository.updateQuranFontSize(it) },
                        valueRange = 20f..40f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = Emerald700,
                            activeTrackColor = Emerald700
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Translation", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = quranSettings.showTranslation,
                            onCheckedChange = { repository.toggleTranslation(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Transliteration", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = quranSettings.showTransliteration,
                            onCheckedChange = { repository.toggleTransliteration(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Translation Language", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("English", "Bahasa Melayu", "Arabic").forEach { lang ->
                            FilterChip(
                                selected = quranSettings.translationLanguage == lang,
                                onClick = { repository.setTranslationLanguage(lang) },
                                label = { Text(lang, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Apply")
                }
            }
        )
    }
}

@Composable
private fun QuranCatalogView(
    surahs: List<Surah>,
    juzList: List<com.example.data.model.JuzInfo>,
    selectedTab: Int,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onTabChange: (Int) -> Unit,
    lastReadPosition: String,
    onSelectSurah: (Int) -> Unit,
    onSelectJuz: (Int) -> Unit,
    bookmarks: List<com.example.data.model.QuranBookmark>,
    onSelectBookmark: (com.example.data.model.QuranBookmark) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Last Read Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onSelectSurah(2) },
            color = Emerald800
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LAST READ POSITION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lastReadPosition,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                FilledTonalButton(
                    onClick = { onSelectSurah(2) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GoldPrimary,
                        contentColor = Emerald900
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Resume", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("quran_search_field"),
            placeholder = { Text("Search Surah name, number, or English translation...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        // Tabs: Surah / Juz / Bookmarks
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Emerald700
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabChange(0) },
                text = { Text("Surah (${surahs.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabChange(1) },
                text = { Text("Juz (${juzList.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { onTabChange(2) },
                text = { Text("Saved (${bookmarks.size})", fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(surahs) { surah ->
                        SurahListItem(
                            surah = surah,
                            onClick = { onSelectSurah(surah.number) }
                        )
                    }
                }
            }

            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(juzList) { juz ->
                        JuzListItem(
                            juz = juz,
                            onClick = { onSelectJuz(juz.number) }
                        )
                    }
                }
            }

            2 -> {
                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔖", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Bookmarks Yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Tap the bookmark icon next to any verse while reading to save it here.",
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookmarks) { bm ->
                            BookmarkCard(
                                bookmark = bm,
                                onClick = { onSelectBookmark(bm) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahListItem(
    surah: Surah,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("surah_item_${surah.number}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Surah number octagon badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
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

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = surah.nameEnglish,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${surah.nameTranslation} • ${surah.totalVerses} verses",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = surah.nameArabic,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Emerald700
                    )
                )
                Text(
                    text = surah.revelationType,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun JuzListItem(
    juz: com.example.data.model.JuzInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${juz.number}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldOnContainer
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Juz ${juz.number}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Starts at ${juz.startSurahName} (Verse ${juz.startVerse})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "${juz.totalVerses} Verses",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Emerald700
            )
        }
    }
}

@Composable
private fun BookmarkCard(
    bookmark: com.example.data.model.QuranBookmark,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Surah ${bookmark.surahName} (${bookmark.surahNumber}:${bookmark.verseNumber})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Emerald700
                )
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = GoldPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bookmark.snippetArabic,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            if (bookmark.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bookmark.note,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

@Composable
private fun QuranReaderView(
    surah: Surah,
    currentVerseIndex: Int,
    isPlayingAudio: Boolean,
    settings: com.example.data.model.QuranSettings,
    isBookmarked: (Int) -> Boolean,
    onToggleBookmark: (Verse) -> Unit,
    onSelectVerse: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bismillah Header (except for Surah 9)
        if (surah.number != 9 && surah.number != 1) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald800,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        itemsIndexed(surah.verses) { index, verse ->
            val isCurrentPlaying = isPlayingAudio && index == currentVerseIndex
            val bookmarked = isBookmarked(verse.verseNumber)

            VerseReadingCard(
                verse = verse,
                settings = settings,
                isActive = index == currentVerseIndex,
                isAudioPlaying = isCurrentPlaying,
                isBookmarked = bookmarked,
                onSelect = { onSelectVerse(index) },
                onToggleBookmark = { onToggleBookmark(verse) }
            )
        }
    }
}

@Composable
private fun VerseReadingCard(
    verse: Verse,
    settings: com.example.data.model.QuranSettings,
    isActive: Boolean,
    isAudioPlaying: Boolean,
    isBookmarked: Boolean,
    onSelect: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelect() }
            .testTag("verse_card_${verse.verseNumber}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Emerald50 else MaterialTheme.colorScheme.surface
        ),
        border = if (isActive) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Emerald600)
        ) else CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Verse Header: Number Badge & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isActive) Emerald700 else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${verse.verseNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (verse.tajwidRuleHighlight != null) {
                        TajwidRuleBadge(ruleText = verse.tajwidRuleHighlight)
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Arabic Text
            Text(
                text = verse.textArabic,
                fontSize = settings.arabicFontSizeSp.sp,
                lineHeight = (settings.arabicFontSizeSp * 1.7f).sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            if (settings.showTransliteration) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = verse.transliteration,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Emerald800
                    )
                )
            }

            if (settings.showTranslation) {
                Spacer(modifier = Modifier.height(8.dp))
                val translationText = when (settings.translationLanguage) {
                    "Bahasa Melayu" -> verse.translationMalay
                    "Arabic" -> verse.textArabic
                    else -> verse.translationEnglish
                }
                Text(
                    text = translationText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BookmarksListView(
    bookmarks: List<QuranBookmark>,
    onSelectBookmark: (QuranBookmark) -> Unit
) {
    if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔖", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Bookmarks Yet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Tap the bookmark icon next to any verse while reading to save it here.",
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookmarks) { bm ->
                BookmarkCard(
                    bookmark = bm,
                    onClick = { onSelectBookmark(bm) }
                )
            }
        }
    }
}

