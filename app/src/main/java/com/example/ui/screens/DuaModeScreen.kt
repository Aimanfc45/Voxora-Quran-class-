package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.repository.EcosystemRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaModeScreen(
    ecosystemRepository: EcosystemRepository,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val duas by ecosystemRepository.duas.collectAsState()
    val selectedCategory by ecosystemRepository.selectedDuaCategory.collectAsState()
    val searchQuery by ecosystemRepository.duaSearchQuery.collectAsState()

    var showOnlyBookmarks by remember { mutableStateOf(false) }

    val filteredDuas = remember(duas, selectedCategory, searchQuery, showOnlyBookmarks) {
        duas.filter { dua ->
            val matchesCategory = selectedCategory == null || dua.category == selectedCategory
            val matchesBookmarks = !showOnlyBookmarks || dua.isBookmarked
            val matchesQuery = searchQuery.isBlank() ||
                    dua.title.contains(searchQuery, ignoreCase = true) ||
                    dua.transliteration.contains(searchQuery, ignoreCase = true) ||
                    dua.translation.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesBookmarks && matchesQuery
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "VOXORA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldPrimary
                        )
                        Text(
                            text = "Dua Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("dua_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Muslim Centre"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showOnlyBookmarks = !showOnlyBookmarks },
                        modifier = Modifier.testTag("dua_filter_bookmarks")
                    ) {
                        Icon(
                            imageVector = if (showOnlyBookmarks) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmarks Filter",
                            tint = if (showOnlyBookmarks) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { ecosystemRepository.setDuaSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("dua_search_input"),
                    placeholder = { Text("Search Dua by keyword, situation, meaning...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { ecosystemRepository.setDuaSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Emerald700,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )
            }

            // 2. Categories Horizontal Carousel
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null && !showOnlyBookmarks,
                            onClick = {
                                showOnlyBookmarks = false
                                ecosystemRepository.selectDuaCategory(null)
                            },
                            label = { Text("✨ All Duas (${duas.size})") }
                        )
                    }

                    DuaCategory.values().forEach { category ->
                        item {
                            FilterChip(
                                selected = selectedCategory == category && !showOnlyBookmarks,
                                onClick = {
                                    showOnlyBookmarks = false
                                    ecosystemRepository.selectDuaCategory(category)
                                },
                                label = { Text("${category.iconEmoji} ${category.displayName}") }
                            )
                        }
                    }
                }
            }

            // 3. Duas Result Cards List
            if (filteredDuas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🤲", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No supplications found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching with a different keyword or resetting filters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredDuas) { dua ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("dua_card_${dua.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Emerald100
                                ) {
                                    Text(
                                        text = "${dua.category.iconEmoji} ${dua.category.displayName}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Emerald800,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Bookmark Button
                                    IconButton(
                                        onClick = {
                                            ecosystemRepository.toggleBookmarkDua(dua.id)
                                            val action = if (!dua.isBookmarked) "Bookmarked" else "Removed bookmark for"
                                            onShowSnackbar("$action ${dua.title}")
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (dua.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (dua.isBookmarked) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Share Button
                                    IconButton(
                                        onClick = {
                                            val shareText = "${dua.title}\n\n${dua.arabicText}\n\n\"${dua.transliteration}\"\n\nTranslation: ${dua.translation}\n\nReference: ${dua.reference}\n— Shared via Voxora Muslim Centre"
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "Share Supplication"))
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Copy Button
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Dua", "${dua.title}\n${dua.arabicText}\n${dua.translation}\n${dua.reference}")
                                            clipboard.setPrimaryClip(clip)
                                            onShowSnackbar("Copied to clipboard")
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = dua.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (dua.occasion.isNotBlank()) {
                                Text(
                                    text = "Occasion: ${dua.occasion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Emerald700
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Arabic Display Box
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Emerald50.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = dua.arabicText,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = 22.sp,
                                        lineHeight = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Emerald950,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = dua.transliteration,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = Emerald800
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = dua.translation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Reference: ${dua.reference}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
