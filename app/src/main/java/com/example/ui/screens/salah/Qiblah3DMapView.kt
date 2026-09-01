package com.example.ui.screens.salah

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PrayerTimesRepository
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*
import com.example.util.QiblahHelper
import java.util.Locale
import kotlin.math.*

enum class MapProjectionMode {
    GLOBE_3D,
    GEODESIC_FLAT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Qiblah3DMapView(
    prayerTimesRepository: PrayerTimesRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLocation by prayerTimesRepository.selectedLocation.collectAsState()

    val userLat = selectedLocation.latitude
    val userLon = selectedLocation.longitude
    val kaabaLat = QiblahHelper.MAKKAH_LATITUDE
    val kaabaLon = QiblahHelper.MAKKAH_LONGITUDE

    val qiblahBearing = remember(selectedLocation) {
        QiblahHelper.calculateQiblahBearing(userLat, userLon)
    }
    val distanceKm = remember(selectedLocation) {
        QiblahHelper.calculateDistanceKm(userLat, userLon)
    }

    var projectionMode by remember { mutableStateOf(MapProjectionMode.GLOBE_3D) }

    // Globe rotation state (Yaw / Longitude offset and Pitch / Latitude offset)
    val midLon = ((userLon + kaabaLon) / 2.0).toFloat()
    val midLat = ((userLat + kaabaLat) / 2.0).toFloat()

    var globeRotX by remember { mutableFloatStateOf(midLat) }
    var globeRotY by remember { mutableFloatStateOf(midLon) }
    var globeScale by remember { mutableFloatStateOf(1.0f) }

    // Continuous subtle animated rotation for atmosphere
    val infiniteTransition = rememberInfiniteTransition(label = "globe_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Qiblah 3D Great Circle Map",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Interactive Spherical Navigation to Kaaba",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald300
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
                    IconButton(
                        onClick = {
                            globeRotX = midLat
                            globeRotY = midLon
                            globeScale = 1.0f
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset View",
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
        modifier = modifier.fillMaxSize().testTag("qiblah_3d_map_screen")
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
            SubtleIslamicPattern(patternColor = GoldPrimary.copy(alpha = 0.06f))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Mode Toggle & Info Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Emerald900,
                        border = BorderStroke(1.dp, Emerald700)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            FilledTonalButton(
                                onClick = { projectionMode = MapProjectionMode.GLOBE_3D },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (projectionMode == MapProjectionMode.GLOBE_3D) Emerald700 else Color.Transparent,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("3D Globe", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            FilledTonalButton(
                                onClick = { projectionMode = MapProjectionMode.GEODESIC_FLAT },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (projectionMode == MapProjectionMode.GEODESIC_FLAT) Emerald700 else Color.Transparent,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Flat Arc", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    // Touch Drag Helper Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Emerald800.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Drag to Rotate",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = GoldLight
                            )
                        }
                    }
                }

                // Interactive 3D Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Emerald900.copy(alpha = 0.4f))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                globeRotY += dragAmount.x * 0.4f
                                globeRotX = (globeRotX - dragAmount.y * 0.4f).coerceIn(-75f, 75f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = (size.minDimension / 2f - 24.dp.toPx()) * globeScale

                        if (projectionMode == MapProjectionMode.GLOBE_3D) {
                            draw3DGlobe(
                                center = center,
                                radius = radius,
                                rotX = globeRotX,
                                rotY = globeRotY,
                                userLat = userLat,
                                userLon = userLon,
                                kaabaLat = kaabaLat,
                                kaabaLon = kaabaLon,
                                pulseAlpha = pulseAlpha
                            )
                        } else {
                            drawFlatGeodesicMap(
                                center = center,
                                width = size.width - 32.dp.toPx(),
                                height = size.height - 32.dp.toPx(),
                                userLat = userLat,
                                userLon = userLon,
                                kaabaLat = kaabaLat,
                                kaabaLon = kaabaLon,
                                pulseAlpha = pulseAlpha
                            )
                        }
                    }

                    // Zoom Controls overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledIconButton(
                            onClick = { globeScale = (globeScale + 0.15f).coerceAtMost(1.8f) },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Emerald800)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White)
                        }

                        FilledIconButton(
                            onClick = { globeScale = (globeScale - 0.15f).coerceAtLeast(0.6f) },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Emerald800)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White)
                        }
                    }
                }

                // Bottom Coordinate & Geodesic Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald900.copy(alpha = 0.9f)),
                    border = BorderStroke(1.dp, Emerald700)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "GEODESIC GREAT CIRCLE NAVIGATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = GoldLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${selectedLocation.name} ➔ Makkah",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldPrimary.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, GoldPrimary)
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f° %s", qiblahBearing, QiblahHelper.getCardinalDirection(qiblahBearing)),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("📍 Your Origin", style = MaterialTheme.typography.labelSmall, color = Emerald300)
                                Text(
                                    text = String.format(Locale.getDefault(), "%.4f° N, %.4f° E", userLat, userLon),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📏 Distance", style = MaterialTheme.typography.labelSmall, color = Emerald300)
                                Text(
                                    text = String.format(Locale.getDefault(), "%,.0f km", distanceKm),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldLight
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("🕋 Holy Kaaba", style = MaterialTheme.typography.labelSmall, color = Emerald300)
                                Text(
                                    text = "21.4225° N, 39.8262° E",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders an orthographic 3D spherical globe with interactive longitude and latitude rotation,
 * lat/lon graticules, user marker, Kaaba marker, and the Great Circle geodesic arc trajectory.
 */
private fun DrawScope.draw3DGlobe(
    center: Offset,
    radius: Float,
    rotX: Float,
    rotY: Float,
    userLat: Double,
    userLon: Double,
    kaabaLat: Double,
    kaabaLon: Double,
    pulseAlpha: Float
) {
    // 1. Globe sphere background
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Emerald800.copy(alpha = 0.9f), Emerald950),
            center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
            radius = radius * 1.3f
        ),
        radius = radius,
        center = center
    )

    // Outer atmosphere glow
    drawCircle(
        color = GoldPrimary.copy(alpha = 0.15f * pulseAlpha),
        radius = radius + 8.dp.toPx(),
        center = center,
        style = Stroke(width = 4.dp.toPx())
    )

    // Globe border
    drawCircle(
        color = Emerald600,
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    // 2. Draw latitude parallels (every 30 degrees)
    for (lat in -60..60 step 30) {
        val latRad = Math.toRadians(lat.toDouble())
        val rLat = radius * cos(latRad).toFloat()
        val yOffset = -radius * sin(latRad).toFloat()

        val yRot = yOffset * cos(Math.toRadians(rotX.toDouble())).toFloat()
        val zRot = yOffset * sin(Math.toRadians(rotX.toDouble())).toFloat()

        if (zRot <= 0) { // visible on front hemisphere
            val parallelWidth = rLat * 2f
            val parallelHeight = abs(rLat * sin(Math.toRadians(rotX.toDouble())).toFloat()) * 2f
            drawOval(
                color = Emerald700.copy(alpha = 0.3f),
                topLeft = Offset(center.x - parallelWidth / 2f, center.y + yRot - parallelHeight / 2f),
                size = androidx.compose.ui.geometry.Size(parallelWidth, parallelHeight),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }

    // 3. Draw longitude meridians (every 45 degrees)
    for (lon in 0 until 360 step 45) {
        val relLon = (lon - rotY + 360f) % 360f
        val lonRad = Math.toRadians(relLon.toDouble())
        val xFactor = sin(lonRad).toFloat()
        val zFactor = cos(lonRad).toFloat()

        if (zFactor > 0) {
            val meridianWidth = radius * 2f * abs(xFactor)
            drawOval(
                color = Emerald700.copy(alpha = 0.35f),
                topLeft = Offset(center.x - meridianWidth / 2f, center.y - radius),
                size = androidx.compose.ui.geometry.Size(meridianWidth, radius * 2f),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }

    // 4. Project coordinates to 3D sphere surface
    fun projectLatLon(lat: Double, lon: Double): Triple<Float, Float, Boolean> {
        val phi = Math.toRadians(lat)
        val lambda = Math.toRadians(lon - rotY)
        val rotXRad = Math.toRadians(rotX.toDouble())

        // Standard 3D spherical coordinates
        val x0 = radius * cos(phi) * sin(lambda)
        val y0 = -radius * sin(phi)
        val z0 = radius * cos(phi) * cos(lambda)

        // Rotate around X axis (pitch)
        val yRot = y0 * cos(rotXRad) - z0 * sin(rotXRad)
        val zRot = y0 * sin(rotXRad) + z0 * cos(rotXRad)
        val xRot = x0

        val screenX = center.x + xRot.toFloat()
        val screenY = center.y + yRot.toFloat()
        val isVisible = zRot > 0 // Front face

        return Triple(screenX, screenY, isVisible)
    }

    // 5. Draw Great Circle Geodesic Arc Waypoints
    val arcPoints = QiblahHelper.generateGeodesicArcPoints(userLat, userLon, kaabaLat, kaabaLon, numPoints = 40)
    val projectedPath = Path()
    var isPathStarted = false

    arcPoints.forEach { (ptLat, ptLon) ->
        val (sx, sy, isVis) = projectLatLon(ptLat, ptLon)
        if (isVis) {
            if (!isPathStarted) {
                projectedPath.moveTo(sx, sy)
                isPathStarted = true
            } else {
                projectedPath.lineTo(sx, sy)
            }
        } else {
            isPathStarted = false
        }
    }

    // Draw Great Circle Arc Line
    drawPath(
        path = projectedPath,
        color = GoldPrimary,
        style = Stroke(
            width = 3.5.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
        )
    )

    // 6. Draw User Marker
    val (userX, userY, userVis) = projectLatLon(userLat, userLon)
    if (userVis) {
        drawCircle(
            color = Emerald300,
            radius = 7.dp.toPx(),
            center = Offset(userX, userY)
        )
        drawCircle(
            color = Color.White,
            radius = 4.dp.toPx(),
            center = Offset(userX, userY)
        )
    }

    // 7. Draw Kaaba / Makkah Marker
    val (kaabaX, kaabaY, kaabaVis) = projectLatLon(kaabaLat, kaabaLon)
    if (kaabaVis) {
        // Pulsing Gold Halo
        drawCircle(
            color = GoldPrimary.copy(alpha = 0.35f * pulseAlpha),
            radius = 16.dp.toPx(),
            center = Offset(kaabaX, kaabaY)
        )
        drawCircle(
            color = GoldPrimary,
            radius = 9.dp.toPx(),
            center = Offset(kaabaX, kaabaY)
        )
        drawCircle(
            color = Emerald950,
            radius = 4.dp.toPx(),
            center = Offset(kaabaX, kaabaY)
        )
    }
}

/**
 * Flat Geodesic Projection Map
 */
private fun DrawScope.drawFlatGeodesicMap(
    center: Offset,
    width: Float,
    height: Float,
    userLat: Double,
    userLon: Double,
    kaabaLat: Double,
    kaabaLon: Double,
    pulseAlpha: Float
) {
    val left = center.x - width / 2f
    val top = center.y - height / 2f

    // Background Grid Map container
    drawRoundRect(
        color = Emerald950,
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(width, height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx())
    )

    // Graticule grid
    for (i in 1..4) {
        val y = top + (height / 5f) * i
        drawLine(
            color = Emerald800.copy(alpha = 0.4f),
            start = Offset(left, y),
            end = Offset(left + width, y),
            strokeWidth = 1.dp.toPx()
        )
    }
    for (i in 1..6) {
        val x = left + (width / 7f) * i
        drawLine(
            color = Emerald800.copy(alpha = 0.4f),
            start = Offset(x, top),
            end = Offset(x, top + height),
            strokeWidth = 1.dp.toPx()
        )
    }

    // Flat coordinates mapping (Centered around Middle East / SE Asia: Lat -10..40, Lon 30..120)
    val minLat = -10.0
    val maxLat = 45.0
    val minLon = 25.0
    val maxLon = 125.0

    fun mapFlat(lat: Double, lon: Double): Offset {
        val normX = ((lon - minLon) / (maxLon - minLon)).toFloat().coerceIn(0f, 1f)
        val normY = (1.0f - ((lat - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0f, 1f)
        return Offset(left + normX * width, top + normY * height)
    }

    val arcPoints = QiblahHelper.generateGeodesicArcPoints(userLat, userLon, kaabaLat, kaabaLon, numPoints = 40)
    val flatPath = Path()
    arcPoints.forEachIndexed { idx, (lat, lon) ->
        val pt = mapFlat(lat, lon)
        if (idx == 0) flatPath.moveTo(pt.x, pt.y) else flatPath.lineTo(pt.x, pt.y)
    }

    // Geodesic curve
    drawPath(
        path = flatPath,
        color = GoldPrimary,
        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
    )

    // Markers
    val userPt = mapFlat(userLat, userLon)
    drawCircle(color = Emerald300, radius = 8.dp.toPx(), center = userPt)
    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = userPt)

    val kaabaPt = mapFlat(kaabaLat, kaabaLon)
    drawCircle(color = GoldPrimary.copy(alpha = 0.4f * pulseAlpha), radius = 16.dp.toPx(), center = kaabaPt)
    drawCircle(color = GoldPrimary, radius = 9.dp.toPx(), center = kaabaPt)
    drawCircle(color = Emerald950, radius = 4.dp.toPx(), center = kaabaPt)
}
