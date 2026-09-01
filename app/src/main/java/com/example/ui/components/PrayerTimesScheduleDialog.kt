package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.MalaysianZone
import com.example.data.model.MalaysianZonesCatalog
import com.example.data.model.PrayerCountdownState
import com.example.data.model.PrayerSlot
import com.example.data.repository.PrayerTimesRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScheduleDialog(
    prayerTimesRepository: PrayerTimesRepository,
    onDismiss: () -> Unit,
    onNavigateToSalahMode: () -> Unit
) {
    val context = LocalContext.current
    val prayerState by prayerTimesRepository.prayerState.collectAsState()
    val selectedLocation by prayerTimesRepository.selectedLocation.collectAsState()

    var showZoneSelector by remember { mutableStateOf(false) }
    var zoneSearchQuery by remember { mutableStateOf("") }
    var showLocationPermissionRationale by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            prayerTimesRepository.detectCurrentGpsLocation()
        }
    }

    if (showLocationPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionRationale = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Emerald800)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enable Location", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Allow location access to get accurate prayer times for your current area.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLocationPermissionRationale = false
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text("Allow Location")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLocationPermissionRationale = false
                        showZoneSelector = true
                    }
                ) {
                    Text("Choose Location Manually", color = Emerald800)
                }
            }
        )
    }

    val filteredZones = remember(zoneSearchQuery) {
        if (zoneSearchQuery.isBlank()) {
            MalaysianZonesCatalog.zones
        } else {
            MalaysianZonesCatalog.zones.filter {
                it.code.contains(zoneSearchQuery, ignoreCase = true) ||
                it.state.contains(zoneSearchQuery, ignoreCase = true) ||
                it.description.contains(zoneSearchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Prayer Times & Schedule",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${prayerState.currentDateFormatted} • ${prayerState.schedule.hijriFormatted}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { prayerTimesRepository.refreshPrayerTimes() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh prayer times",
                            tint = Emerald800
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Cache & JAKIM status line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${prayerState.schedule.lastUpdatedFormatted} • Using cached prayer times",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location Selector Pill Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showZoneSelector = !showZoneSelector },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Emerald50
                ),
                border = BorderStroke(1.dp, Emerald200)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Emerald800),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedLocation.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Emerald950,
                                maxLines = 1
                            )
                            Text(
                                text = "Zone: ${selectedLocation.zoneCode} • ${selectedLocation.state}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald700
                            )
                        }
                    }

                    TextButton(onClick = { showZoneSelector = !showZoneSelector }) {
                        Text(
                            text = if (showZoneSelector) "Close" else "Change",
                            color = Emerald800,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expandable Zone / Location Picker
            if (showZoneSelector) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // GPS Auto Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedLocation.isAutoLocation) Emerald100 else Color.Transparent)
                                .clickable {
                                    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    if (fine) {
                                        prayerTimesRepository.detectCurrentGpsLocation()
                                        showZoneSelector = false
                                    } else {
                                        showLocationPermissionRationale = true
                                    }
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = Emerald800,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Use Automatic Location (GPS)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Emerald900
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Zone Search Field
                        OutlinedTextField(
                            value = zoneSearchQuery,
                            onValueChange = { zoneSearchQuery = it },
                            placeholder = { Text("Search zone (e.g. WLY01, Selangor, Johor)", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (zoneSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { zoneSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Emerald700,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Malaysian JAKIM Zones (${filteredZones.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        LazyColumn {
                            items(filteredZones) { zone ->
                                val isSelected = selectedLocation.zoneCode == zone.code
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Emerald100 else Color.Transparent)
                                        .clickable {
                                            prayerTimesRepository.selectMalaysianZone(zone)
                                            showZoneSelector = false
                                        }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${zone.code} - ${zone.state}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = zone.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Emerald700,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prayer Slots List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                prayerState.schedule.slots.forEach { slot ->
                    PrayerSlotRow(slot = slot)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Guidance & Salah Mode CTA
            Button(
                onClick = {
                    onDismiss()
                    onNavigateToSalahMode()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("dialog_btn_salah_mode"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Emerald800,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    tint = GoldPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Launch Step-by-Step Salah Guide",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun PrayerSlotRow(slot: PrayerSlot) {
    val isHighlighted = slot.isCurrent || slot.isNext
    val cardBg = when {
        slot.isCurrent -> Emerald800
        slot.isNext -> GoldContainer
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    val primaryTextColor = when {
        slot.isCurrent -> Color.White
        slot.isNext -> GoldDark
        else -> MaterialTheme.colorScheme.onSurface
    }

    val secondaryTextColor = when {
        slot.isCurrent -> Emerald200
        slot.isNext -> GoldDark.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = if (slot.isNext) BorderStroke(1.dp, GoldPrimary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = slot.iconEmoji,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = slot.englishName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = primaryTextColor
                        )
                        if (slot.isCurrent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldPrimary
                            ) {
                                Text(
                                    text = "NOW",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = Emerald950,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else if (slot.isNext) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Emerald800
                            ) {
                                Text(
                                    text = "NEXT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${slot.malayName} • ${slot.arabicName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryTextColor
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = slot.time12,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = primaryTextColor
                )
                Text(
                    text = "${slot.time24} hrs",
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor
                )
            }
        }
    }
}
