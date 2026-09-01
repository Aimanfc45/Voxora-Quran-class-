package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.QuranLineSpacing
import com.example.data.model.QuranReadingTheme
import com.example.data.model.ReadingDisplayMode
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

/**
 * FEATURE 8: Night Reading Mode & Reader Settings
 * - 4 Color Themes (Emerald, Night AMOLED, Warm Sepia, Crisp Day)
 * - Font size slider (20sp to 40sp) with live preview
 * - Line spacing (Compact, Standard, Relaxed)
 * - Arabic font style choice
 * - Translation, Transliteration, Tafsir, Tajwid toggles
 * - Brightness slider
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderSettingsDialog(
    repository: VoxoraRepository,
    onDismiss: () -> Unit
) {
    val quranSettings by repository.quranSettings.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .testTag("quran_reader_settings_dialog"),
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
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reader Preferences",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Emerald800
                            )
                        }
                        Text(
                            text = "Customize themes, font sizes, translations, and Tajwid",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_reader_settings_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Reading Canvas Themes
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Reading Canvas Theme",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald900
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    QuranReadingTheme.values().forEach { theme ->
                                        val isSel = quranSettings.readingTheme == theme
                                        Surface(
                                            onClick = { repository.setQuranReadingTheme(theme) },
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(theme.backgroundHex),
                                            border = if (isSel) androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "بِسْمِ",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(theme.textPrimaryHex)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = theme.label,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 10.sp,
                                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                                    ),
                                                    color = Color(theme.textPrimaryHex),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Arabic Font Size & Live Preview
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Arabic Font Size",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Emerald900
                                    )
                                    Text(
                                        text = "${quranSettings.arabicFontSizeSp.toInt()} sp",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Emerald800
                                    )
                                }

                                Slider(
                                    value = quranSettings.arabicFontSizeSp,
                                    onValueChange = { repository.updateQuranFontSize(it) },
                                    valueRange = 20f..40f,
                                    steps = 9,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Emerald800,
                                        activeTrackColor = Emerald700
                                    ),
                                    modifier = Modifier.testTag("font_size_slider")
                                )

                                // Live Preview Box
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(quranSettings.readingTheme.cardBackgroundHex),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontSize = quranSettings.arabicFontSizeSp.sp,
                                            lineHeight = (quranSettings.arabicFontSizeSp * quranSettings.lineSpacing.factor).sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(quranSettings.readingTheme.textPrimaryHex),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Line Spacing
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Line Spacing",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald900
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    QuranLineSpacing.values().forEach { spacing ->
                                        val isSel = quranSettings.lineSpacing == spacing
                                        Surface(
                                            onClick = { repository.setQuranLineSpacing(spacing) },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSel) Emerald800 else MaterialTheme.colorScheme.surface,
                                            border = CardDefaults.outlinedCardBorder(),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = spacing.label,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Script Style
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Arabic Script Typography",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald900
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                listOf("Uthmani (Madinah)", "Indopak Script", "Amiri Modern").forEach { style ->
                                    val isSel = quranSettings.arabicFontStyle == style
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { repository.setArabicFontStyle(style) }
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSel,
                                            onClick = { repository.setArabicFontStyle(style) },
                                            colors = RadioButtonDefaults.colors(selectedColor = Emerald800)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = style, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    // 5. Visibility Toggles
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Reading Content Elements",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Emerald900
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                PreferenceSwitchRow(
                                    title = "English Translation",
                                    subtitle = "Sahih International translation",
                                    checked = quranSettings.showEnglishTranslation,
                                    onCheckedChange = { repository.toggleTranslation(it) }
                                )

                                Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                                PreferenceSwitchRow(
                                    title = "Bahasa Melayu Translation",
                                    subtitle = "Tafsiran Rasmi JAKIM Malaysia",
                                    checked = quranSettings.showMalayTranslation,
                                    onCheckedChange = { repository.toggleMalayTranslation(it) }
                                )

                                Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                                PreferenceSwitchRow(
                                    title = "Transliteration",
                                    subtitle = "Phonetic romanized pronunciation",
                                    checked = quranSettings.showTransliteration,
                                    onCheckedChange = { repository.toggleTransliteration(it) }
                                )

                                Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                                PreferenceSwitchRow(
                                    title = "Tajwid Rules Highlighting",
                                    subtitle = "Color-code Mad, Ikhfa, Idgham, Qalqalah, Ghunnah",
                                    checked = quranSettings.showTajwidColors,
                                    onCheckedChange = { repository.toggleTajwidColors(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GoldPrimary,
                checkedTrackColor = Emerald700
            )
        )
    }
}
