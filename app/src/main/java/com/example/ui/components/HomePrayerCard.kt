package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerCountdownState
import com.example.data.model.PrayerName
import com.example.data.model.PrayerSlot
import com.example.ui.theme.*

@Composable
fun HomePrayerCard(
    prayerState: PrayerCountdownState,
    onViewAllPrayerTimes: () -> Unit,
    onOpenSalahMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("home_prayer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Emerald100)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: PRAYER TIMES tag + Live Device Clock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Emerald800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTimeFilled,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PRAYER TIMES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Emerald800
                        )
                        Text(
                            text = prayerState.schedule.locationName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Live ticking digital clock badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Emerald50,
                    border = BorderStroke(1.dp, Emerald200)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Emerald600)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prayerState.currentTimeFormatted,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = Emerald900
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Container: Current Prayer & Next Prayer Countdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Emerald900, Emerald950)
                        )
                    )
            ) {
                SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.08f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "NEXT PRAYER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = prayerState.nextPrayer.englishName,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(${prayerState.nextPrayer.time12})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Emerald200
                                )
                            }
                        }

                        // Countdown Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldPrimary.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.HourglassTop,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = prayerState.formattedCountdown,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = GoldLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prayer Countdown Progress
                    LinearProgressIndicator(
                        progress = { prayerState.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GoldPrimary,
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = prayerState.currentPrayer?.let { "Current: ${it.englishName} (${it.time12})" } ?: "Night Time",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald300
                        )
                        Text(
                            text = prayerState.currentDateFormatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Horizontal Prayer Schedule Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                prayerState.schedule.slots.forEach { slot ->
                    PrayerSlotChip(
                        slot = slot,
                        onClick = onViewAllPrayerTimes
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewAllPrayerTimes,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_view_all_prayer_times"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Emerald800
                    ),
                    border = BorderStroke(1.dp, Emerald300)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "All Times",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Button(
                    onClick = onOpenSalahMode,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("btn_open_salah_mode"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald800,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Open Salah Mode",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerSlotChip(
    slot: PrayerSlot,
    onClick: () -> Unit
) {
    val isCurrentOrNext = slot.isCurrent || slot.isNext
    val bgColor = when {
        slot.isCurrent -> Emerald800
        slot.isNext -> GoldContainer
        slot.isPassed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }

    val contentColor = when {
        slot.isCurrent -> Color.White
        slot.isNext -> GoldDark
        slot.isPassed -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp)
    ) {
        Text(
            text = slot.name.englishName.take(3),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isCurrentOrNext) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
            ),
            color = contentColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = slot.time24,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isCurrentOrNext) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp,
                fontFeatureSettings = "tnum"
            ),
            color = contentColor
        )
    }
}
