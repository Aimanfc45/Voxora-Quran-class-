package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.PrayerType
import com.example.data.model.SalahDataCatalog
import com.example.data.model.SalahStep
import com.example.ui.components.VoxoraHeaderBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalahModeScreen(
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPrayer by remember { mutableStateOf(PrayerType.FAJR) }
    val steps = remember { SalahDataCatalog.canonicalSteps }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = steps.getOrElse(currentStepIndex) { steps.first() }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("salah_mode_screen"),
        topBar = {
            VoxoraHeaderBar(
                title = "Salah Mode (Foundation)",
                subtitle = "Step-by-step guidance & postures for canonical prayers",
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("salah_mode_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Prayer Selection Selector
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SELECT PRAYER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Emerald800,
                            letterSpacing = 1.sp
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(PrayerType.values()) { prayer ->
                            val isSelected = selectedPrayer == prayer
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) Emerald800 else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Gold500 else MaterialTheme.colorScheme.outlineVariant
                                ),
                                shadowElevation = if (isSelected) 3.dp else 1.dp,
                                modifier = Modifier
                                    .clickable {
                                        selectedPrayer = prayer
                                        currentStepIndex = 0
                                    }
                                    .testTag("prayer_select_${prayer.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(prayer.iconEmoji, fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = prayer.englishName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = "${prayer.rakaatCount} Raka'at",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Gold400 else Emerald700,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Prayer Summary Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${selectedPrayer.englishName} Prayer",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Gold500
                                ) {
                                    Text(
                                        text = "${selectedPrayer.rakaatCount} Raka'at",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepEmerald950
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedPrayer.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Emerald100.copy(alpha = 0.85f),
                                    lineHeight = 16.sp
                                )
                            )
                        }
                        Text(
                            text = selectedPrayer.arabicName,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gold400
                            ),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }

            // 3. Step Progress Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP ${currentStepIndex + 1} OF ${steps.size}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald800,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = currentStep.titleEnglish,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Step Indicator Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        steps.forEachIndexed { idx, _ ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        when {
                                            idx == currentStepIndex -> Gold500
                                            idx < currentStepIndex -> Emerald700
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .clickable { currentStepIndex = idx }
                            )
                        }
                    }
                }
            }

            // 4. Main Step Guide Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("salah_step_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header of the Step
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Emerald800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentStep.stepCode,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Gold400
                                        )
                                    )
                                }

                                Column {
                                    Text(
                                        text = currentStep.titleEnglish,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = currentStep.transliteration,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = Emerald700
                                        )
                                    )
                                }
                            }

                            Text(
                                text = currentStep.titleArabic,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald900
                                )
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Posture Instruction
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    tint = Emerald800,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Posture: ${currentStep.postureName}",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    )
                                )
                            }
                            Text(
                                text = currentStep.postureDescription,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                            )
                        }

                        // Arabic Recitation Box
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFBFBF9),
                            border = BorderStroke(1.dp, Gold500.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = currentStep.arabicDua,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = ArabicQuranFontFamily,
                                        fontSize = 22.sp,
                                        lineHeight = 36.sp,
                                        textAlign = TextAlign.Center,
                                        color = DeepEmerald950
                                    )
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Emerald50
                                ) {
                                    Text(
                                        text = currentStep.transliteration,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            color = Emerald900,
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Translations
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column {
                                Text(
                                    text = "English Translation:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald800
                                    )
                                )
                                Text(
                                    text = currentStep.translationEnglish,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                )
                            }

                            Column {
                                Text(
                                    text = "Terjemahan Bahasa Melayu:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald800
                                    )
                                )
                                Text(
                                    text = currentStep.translationMalay,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }

                        // Focus Tip / Khushoo'
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "Khushoo' Reflection:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    )
                                    Text(
                                        text = currentStep.focusTip,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF78350F),
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Step Navigation Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentStepIndex > 0) currentStepIndex--
                        },
                        enabled = currentStepIndex > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("salah_prev_step_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Previous")
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStepIndex < steps.size - 1) {
                                currentStepIndex++
                            } else {
                                onShowSnackbar("Completed all steps of ${selectedPrayer.englishName} Salah!")
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("salah_next_step_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800, contentColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (currentStepIndex < steps.size - 1) "Next Step" else "Complete Salah",
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (currentStepIndex < steps.size - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 6. Roadmap / Coming Soon Notice
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Emerald900.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Emerald700.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Salah Mode Roadmap",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold400
                                )
                            )
                            Text(
                                text = "Audible high-res recitations, certified Tajwid guides, and 3D postural animations will be released in subsequent updates.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Emerald100.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
