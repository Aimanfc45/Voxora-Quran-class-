package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val badgeIcon: String,
    val features: List<String>,
    val arabicAccent: String,
    val iconBackground: List<Color>
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: (goToAuth: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingStep(
            title = "VOXORA QURAN",
            subtitle = "Your Quran. Your Journey.",
            description = "Read the Quran with a clean modern experience.",
            badgeIcon = "📖",
            features = listOf(
                "Quran Reader",
                "Search",
                "Bookmarks",
                "Notes"
            ),
            arabicAccent = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            iconBackground = listOf(Emerald800, Emerald950)
        ),
        OnboardingStep(
            title = "LISTEN & LEARN",
            subtitle = "Perfect Your Recitation",
            description = "Listen and improve your recitation.",
            badgeIcon = "🎧",
            features = listOf(
                "Multiple Qaris",
                "Quran Audio",
                "Tajwid",
                "Learning tools"
            ),
            arabicAccent = "وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",
            iconBackground = listOf(Emerald700, DeepEmerald950)
        ),
        OnboardingStep(
            title = "BUILD YOUR SALAH",
            subtitle = "Build Better Prayer Habits",
            description = "Build better prayer habits with accurate schedules and guide.",
            badgeIcon = "🕌",
            features = listOf(
                "Prayer Times",
                "Salah Mode",
                "Step-by-step guidance"
            ),
            arabicAccent = "وَأَقِيمُوا الصَّلَاةَ",
            iconBackground = listOf(Emerald800, Emerald900)
        ),
        OnboardingStep(
            title = "LEARN TOGETHER",
            subtitle = "Live Community & Teachers",
            description = "Learn Quran with others in virtual classrooms.",
            badgeIcon = "🎓",
            features = listOf(
                "Quran Classes",
                "Live Classes",
                "Teacher/Student experience"
            ),
            arabicAccent = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            iconBackground = listOf(Emerald700, Emerald950)
        ),
        OnboardingStep(
            title = "MAKE IT YOURS",
            subtitle = "Track Your Daily Growth",
            description = "Track your Quran journey and build lasting habits.",
            badgeIcon = "✨",
            features = listOf(
                "Daily Goals",
                "Streak",
                "Learning Progress"
            ),
            arabicAccent = "فَاسْتَبِقُوا الْخَيْرَاتِ",
            iconBackground = listOf(Emerald800, GoldDark)
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
        Box(modifier = Modifier.fillMaxSize()) {
            SubtleIslamicPattern(
                modifier = Modifier.fillMaxSize(),
                patternColor = GoldPrimary.copy(alpha = 0.05f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header: Back & Skip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier.testTag("onboarding_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Page",
                                tint = Gold400
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    // Top brand accent
                    Text(
                        text = "VOXORA",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = Gold500.copy(alpha = 0.7f)
                        )
                    )

                    // Skip button
                    if (pagerState.currentPage < pages.size - 1) {
                        TextButton(
                            onClick = { onFinishOnboarding(true) },
                            modifier = Modifier.testTag("onboarding_skip_button")
                        ) {
                            Text(
                                text = "Skip",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold400
                                )
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }

                // Pager Content
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { pageIndex ->
                    val item = pages[pageIndex]
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Badge Icon Container
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Brush.linearGradient(item.iconBackground))
                                .border(2.dp, Gold500.copy(alpha = 0.6f), RoundedCornerShape(28.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.badgeIcon,
                                fontSize = 42.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Arabic Accent Banner
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Emerald900.copy(alpha = 0.75f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Gold500.copy(alpha = 0.35f))
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

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.5.sp,
                                color = Gold400
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Emerald100.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Feature checklist
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .border(1.dp, Emerald700.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item.features.forEach { feature ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Gold500),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DeepEmerald950,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = feature,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
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
                    // Modern Page Indicator Dots
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold500,
                                    contentColor = DeepEmerald950
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Next",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Final Screen: Get Started button
                        Button(
                            onClick = { onFinishOnboarding(true) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("onboarding_get_started_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gold500,
                                contentColor = DeepEmerald950
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Get Started",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
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
