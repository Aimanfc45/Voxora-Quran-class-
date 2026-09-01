package com.example.ui.screens.salah

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class SalahIntroPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val iconEmoji: String,
    val featurePills: List<String>
)

val salahIntroPages = listOf(
    SalahIntroPage(
        title = "SALAH MODE",
        subtitle = "Learn your prayer, one step at a time.",
        description = "Discover the spiritual depth of Salah with authentic step-by-step guidance tailored for every Muslim, from beginners to experienced reciters.",
        iconEmoji = "🕌",
        featurePills = listOf("Fajr to Isha", "Canonical Raka'ats", "Khushoo' Reminders")
    ),
    SalahIntroPage(
        title = "UNDERSTAND EVERY STEP",
        subtitle = "Authentic recitations & clear translations.",
        description = "Master each movement and dua with crystal-clear Arabic calligraphy, phonetic transliteration, Bahasa Melayu, English, and posture instructions.",
        iconEmoji = "📖",
        featurePills = listOf("Arabic Text", "Transliteration", "Malay & English", "Audio Cue Guidance")
    ),
    SalahIntroPage(
        title = "STAY CONNECTED",
        subtitle = "Live prayer times, Qiblah & 3D Map.",
        description = "Never miss a prayer with verified JAKIM prayer schedules, accurate real-time compass Qiblah finder, and interactive 3D spherical geodesic maps.",
        iconEmoji = "🧭",
        featurePills = listOf("JAKIM Verified", "Real-Time Compass", "3D Kaaba Map", "Daily Tracker")
    )
)

@Composable
fun SalahModeIntro(
    onFinishIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { salahIntroPages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Emerald950, Emerald900, Emerald950)
                )
            )
            .testTag("salah_intro_screen")
    ) {
        SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.08f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = "VOXORA SALAH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = GoldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                TextButton(
                    onClick = onFinishIntro,
                    modifier = Modifier.testTag("salah_intro_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Emerald300
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Horizontal Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = salahIntroPages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Feature Icon Orb
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(GoldPrimary.copy(alpha = 0.35f), Emerald800.copy(alpha = 0.5f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = page.iconEmoji, fontSize = 48.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldLight
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = page.subtitle,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Emerald200.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        page.featurePills.forEach { pill ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Emerald800.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = pill,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = Emerald100,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Pager Indicator Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                repeat(salahIntroPages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) GoldPrimary else Emerald700.copy(alpha = 0.5f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button: Next or Get Started
            val isLastPage = pagerState.currentPage == salahIntroPages.size - 1

            Button(
                onClick = {
                    if (isLastPage) {
                        onFinishIntro()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("salah_intro_next_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Emerald950
                )
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
