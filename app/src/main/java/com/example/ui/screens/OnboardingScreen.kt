package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val headline: String,
    val description: String,
    val badgeIcon: String,
    val features: List<String>,
    val arabicAccent: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: (goToAuth: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = "VOXORA QURAN",
            headline = "Learn. Recite. Grow.",
            description = "Experience the Holy Quran with crystal-clear Uthmani calligraphy, verified multi-lingual translations, and personalized reading journeys.",
            badgeIcon = "📖",
            features = listOf(
                "Authentic Medina Uthmani Calligraphy",
                "English & Bahasa Melayu Translations",
                "Smart Verse-by-Verse & Mushaf Modes"
            ),
            arabicAccent = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        ),
        OnboardingPage(
            title = "AUDIO & TAJWID",
            headline = "Listen & Perfect Your Recitation",
            description = "Immerse yourself in soul-stirring recitations from world-renowned Qaris with powerful verse looping, audio repeat, and Tajwid mastery.",
            badgeIcon = "🎧",
            features = listOf(
                "World-Renowned Certified Qaris",
                "Customizable Verse Repeat & Range Practice",
                "Color-Coded Tajwid Rules with Audio Guidance"
            ),
            arabicAccent = "وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا"
        ),
        OnboardingPage(
            title = "LIVE VIRTUAL HALAQAH",
            headline = "Learn Live with Certified Instructors",
            description = "Connect in real-time with certified Quran teachers and classmates worldwide with synchronized Quran sheets, hand-raising, and feedback.",
            badgeIcon = "🎓",
            features = listOf(
                "Real-time Synchronized Ayah Navigation",
                "Hand-Raising & Recitation Queue System",
                "Live Teacher Tajwid Feedback & Assessments"
            ),
            arabicAccent = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        color = DeepEmerald950
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = { onFinishOnboarding(false) },
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Gold400
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(38.dp))
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val item = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Badge Icon Container
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Emerald800, Emerald900)
                                )
                            )
                            .border(2.dp, Gold500.copy(alpha = 0.5f), RoundedCornerShape(32.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.badgeIcon,
                            fontSize = 48.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Arabic Accent Banner
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Emerald900.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold500.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = item.arabicAccent,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gold400
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Gold400
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.headline,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Emerald100.copy(alpha = 0.85f),
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feature checklist
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.features.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Gold500),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = DeepEmerald950,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation & Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isSelected) Gold500 else Emerald800)
                        )
                    }
                }

                // Main Action Buttons
                if (pagerState.currentPage < pages.size - 1) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_next_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = DeepEmerald950)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Next",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onFinishOnboarding(true) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_get_started_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = DeepEmerald950)
                        ) {
                            Text(
                                text = "Get Started & Sign In",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        OutlinedButton(
                            onClick = { onFinishOnboarding(false) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("onboarding_guest_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Emerald400),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text(
                                text = "Continue as Guest",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}
