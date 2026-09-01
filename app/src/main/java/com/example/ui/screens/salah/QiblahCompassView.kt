package com.example.ui.screens.salah

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PrayerTimesRepository
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*
import com.example.util.CompassData
import com.example.util.CompassSensorManager
import com.example.util.CompassSensorState
import com.example.util.QiblahHelper
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblahCompassView(
    prayerTimesRepository: PrayerTimesRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sensorManager = remember { CompassSensorManager(context) }

    DisposableEffect(Unit) {
        sensorManager.startListening()
        onDispose {
            sensorManager.stopListening()
        }
    }

    val compassData by sensorManager.compassData.collectAsState()
    val selectedLocation by prayerTimesRepository.selectedLocation.collectAsState()

    val qiblahBearing = remember(selectedLocation) {
        QiblahHelper.calculateQiblahBearing(selectedLocation.latitude, selectedLocation.longitude).toFloat()
    }
    val distanceKm = remember(selectedLocation) {
        QiblahHelper.calculateDistanceKm(selectedLocation.latitude, selectedLocation.longitude)
    }

    // Relative angle between current heading and Qiblah bearing
    // Positive difference means user needs to turn clockwise
    val relativeAngle = (qiblahBearing - compassData.azimuthDegrees + 360f) % 360f
    val isAligned = abs(if (relativeAngle > 180f) relativeAngle - 360f else relativeAngle) <= 3.5f

    var showCalibrationDialog by remember { mutableStateOf(false) }

    if (showCalibrationDialog) {
        AlertDialog(
            onDismissRequest = { showCalibrationDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Calibrate Compass",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "To ensure maximum precision, wave your device smoothly in a figure-8 motion away from metallic objects and magnets.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "♾️",
                        fontSize = 36.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCalibrationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text("Done")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Qiblah Compass",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = selectedLocation.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald300,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showCalibrationDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Calibrate",
                            tint = GoldLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Emerald950
                )
            )
        },
        containerColor = Emerald950,
        modifier = modifier.fillMaxSize().testTag("qiblah_compass_screen")
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Emerald950, Emerald900, Emerald950)
                    )
                )
        ) {
            SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.07f))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Info Bar: Alignment Status Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isAligned) GoldPrimary.copy(alpha = 0.25f) else Emerald800.copy(alpha = 0.5f),
                    border = BorderStroke(
                        1.dp,
                        if (isAligned) GoldPrimary else Emerald700
                    ),
                    modifier = Modifier.testTag("qiblah_alignment_status")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAligned) "✨" else "🧭",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAligned) "FACING QIBLAH (MAKKAH)" else "ROTATE TO ALIGN WITH KAABA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = if (isAligned) GoldLight else Color.White
                        )
                    }
                }

                // Center Compass Visualizer
                Box(
                    modifier = Modifier
                        .size(310.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Glow when aligned
                    if (isAligned) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(GoldPrimary.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )
                        )
                    }

                    // Rotating Compass Dial Canvas
                    CompassDialCanvas(
                        deviceAzimuth = compassData.azimuthDegrees,
                        qiblahBearing = qiblahBearing,
                        isAligned = isAligned
                    )

                    // Center Hub with Kaaba Icon
                    Surface(
                        shape = CircleShape,
                        color = if (isAligned) GoldPrimary else Emerald900,
                        border = BorderStroke(2.dp, GoldLight),
                        shadowElevation = 6.dp,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🕋",
                                    fontSize = 26.sp
                                )
                            }
                        }
                    }
                }

                // Bottom Qiblah Metric Cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Qiblah Angle Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.85f)),
                            border = BorderStroke(1.dp, Emerald700)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "QIBLAH BEARING",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                                    color = Emerald300
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(java.util.Locale.getDefault(), "%.1f°", qiblahBearing),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = GoldLight
                                )
                                Text(
                                    text = QiblahHelper.getCardinalDirection(qiblahBearing.toDouble()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Distance to Kaaba Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.85f)),
                            border = BorderStroke(1.dp, Emerald700)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "DISTANCE TO KAABA",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                                    color = Emerald300
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(java.util.Locale.getDefault(), "%,.0f km", distanceKm),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Makkah Al-Mukarramah",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Emerald300
                                )
                            }
                        }
                    }

                    // Calibration and sensor status note
                    if (!compassData.isSensorAvailable) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF422006),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Compass hardware sensor unavailable. Theoretical Qiblah angle (${String.format(java.util.Locale.getDefault(), "%.1f°", qiblahBearing)}) displayed.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFEF3C7)
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
private fun CompassDialCanvas(
    deviceAzimuth: Float,
    qiblahBearing: Float,
    isAligned: Boolean
) {
    val animatedDeviceAzimuth by animateFloatAsState(
        targetValue = deviceAzimuth,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "azimuth"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 16.dp.toPx()

        // 1. Draw outer compass dial rotated by -deviceAzimuth
        rotate(-animatedDeviceAzimuth, pivot = center) {
            // Outer dial rim
            drawCircle(
                color = Emerald800,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // Inner subtle ring
            drawCircle(
                color = Emerald700.copy(alpha = 0.4f),
                radius = radius * 0.78f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Degree tick marks every 5 and 30 degrees
            for (deg in 0 until 360 step 5) {
                val isMajor = deg % 30 == 0
                val isCardinal = deg % 90 == 0
                val tickLength = when {
                    isCardinal -> 14.dp.toPx()
                    isMajor -> 10.dp.toPx()
                    else -> 5.dp.toPx()
                }
                val tickColor = when {
                    deg == 0 -> Color(0xFFEF4444) // North Red
                    isCardinal -> GoldLight
                    isMajor -> Emerald300
                    else -> Emerald600.copy(alpha = 0.5f)
                }

                val angleRad = Math.toRadians(deg.toDouble())
                val startX = center.x + (radius - tickLength) * sin(angleRad).toFloat()
                val startY = center.y - (radius - tickLength) * cos(angleRad).toFloat()
                val endX = center.x + radius * sin(angleRad).toFloat()
                val endY = center.y - radius * cos(angleRad).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (isCardinal) 3.dp.toPx() else 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 2. Qiblah Pointer / Kaaba Direction Line from center towards qiblahBearing
            val qiblahRad = Math.toRadians(qiblahBearing.toDouble())
            val qiblahX = center.x + (radius - 8.dp.toPx()) * sin(qiblahRad).toFloat()
            val qiblahY = center.y - (radius - 8.dp.toPx()) * cos(qiblahRad).toFloat()

            // Direction arrow line to Kaaba
            drawLine(
                color = if (isAligned) GoldPrimary else GoldLight.copy(alpha = 0.9f),
                start = center,
                end = Offset(qiblahX, qiblahY),
                strokeWidth = if (isAligned) 4.5.dp.toPx() else 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Kaaba Target Marker circle at perimeter
            drawCircle(
                color = if (isAligned) GoldPrimary else GoldLight,
                radius = 8.dp.toPx(),
                center = Offset(qiblahX, qiblahY)
            )
            drawCircle(
                color = Emerald950,
                radius = 4.dp.toPx(),
                center = Offset(qiblahX, qiblahY)
            )
        }

        // Fixed Top Device Heading indicator (Triangle marker at top)
        val pointerPath = Path().apply {
            moveTo(center.x, center.y - radius - 12.dp.toPx())
            lineTo(center.x - 8.dp.toPx(), center.y - radius)
            lineTo(center.x + 8.dp.toPx(), center.y - radius)
            close()
        }
        drawPath(
            path = pointerPath,
            color = if (isAligned) GoldPrimary else Color.White
        )
    }
}
