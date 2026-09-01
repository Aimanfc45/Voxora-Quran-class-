package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TajwidRule
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TajwidGuideDialog(
    repository: VoxoraRepository,
    onDismiss: () -> Unit
) {
    val tajwidRules by repository.tajwidRules.collectAsState()
    val quranSettings by repository.quranSettings.collectAsState()
    var selectedRuleDetail by remember { mutableStateOf<TajwidRule?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp)
                .testTag("tajwid_guide_dialog"),
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
                        Text(
                            text = "Tajwid Rules Guide",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Emerald800
                        )
                        Text(
                            text = "Color-coded recitation rules & pronunciation tips",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tajwid Color Toggle Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald50)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tajwid Highlighting in Reader",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Emerald900
                            )
                            Text(
                                text = if (quranSettings.showTajwidColors) "Enabled — colors visible in Ayah cards" else "Disabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald700
                            )
                        }
                        Switch(
                            checked = quranSettings.showTajwidColors,
                            onCheckedChange = { repository.toggleTajwidColors(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Emerald700,
                                checkedTrackColor = Emerald200
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Core Rules Reference (${tajwidRules.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tajwidRules, key = { it.id }) { rule ->
                        val ruleColor = Color(rule.colorHex)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRuleDetail = rule }
                                .testTag("tajwid_rule_card_${rule.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(ruleColor)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = rule.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Text(
                                        text = rule.arabicName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldDark,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = rule.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = ruleColor.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = rule.category,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = ruleColor,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        if (rule.harakatCount.isNotBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = GoldPrimary.copy(alpha = 0.18f)
                                            ) {
                                                Text(
                                                    text = rule.harakatCount,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Emerald950,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = rule.exampleArabic,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Emerald800
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog for Individual Rule
    selectedRuleDetail?.let { r ->
        val ruleColor = Color(r.colorHex)
        AlertDialog(
            onDismissRequest = { selectedRuleDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(ruleColor))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${r.name} (${r.arabicName})", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = r.description, style = MaterialTheme.typography.bodyMedium)
                    if (r.harakatCount.isNotBlank()) {
                        Surface(shape = RoundedCornerShape(8.dp), color = GoldPrimary.copy(alpha = 0.2f)) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Duration / Elongation: ", fontWeight = FontWeight.Bold, color = Emerald950, style = MaterialTheme.typography.labelMedium)
                                Text(r.harakatCount, fontWeight = FontWeight.SemiBold, color = Emerald900, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = Emerald50) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Pronunciation Tip:", fontWeight = FontWeight.Bold, color = Emerald900, style = MaterialTheme.typography.labelMedium)
                            Text(r.pronunciationTip, style = MaterialTheme.typography.bodySmall, color = Emerald800)
                        }
                    }
                    Text("Rule Summary:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text(r.ruleSummary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Example in Quran (${r.verseReference}):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text("${r.exampleArabic} — ${r.exampleTransliteration}", style = MaterialTheme.typography.titleMedium, color = Emerald700)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedRuleDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Got It")
                }
            }
        )
    }
}
