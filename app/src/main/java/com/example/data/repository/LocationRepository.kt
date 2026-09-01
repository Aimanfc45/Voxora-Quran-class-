package com.example.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.model.MalaysianZone
import com.example.data.model.MalaysianZonesCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.math.*

/**
 * Handles Android device location detection and Malaysian Prayer Zone mapping.
 */
class LocationRepository(private val context: Context) {

    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    suspend fun getDeviceCoordinates(): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) return@withContext null

        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return@withContext null

            // 1. Try last known location from GPS or Network
            val gpsLocation = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: SecurityException) { null }
            } else null

            val networkLocation = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                try { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: SecurityException) { null }
            } else null

            val bestLocation = when {
                gpsLocation != null && networkLocation != null -> {
                    if (gpsLocation.time > networkLocation.time) gpsLocation else networkLocation
                }
                gpsLocation != null -> gpsLocation
                networkLocation != null -> networkLocation
                else -> null
            }

            if (bestLocation != null) {
                return@withContext Pair(bestLocation.latitude, bestLocation.longitude)
            }

            // 2. Request single fresh update if possible
            return@withContext requestSingleFreshLocation(locationManager)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun requestSingleFreshLocation(locationManager: LocationManager): Pair<Double, Double>? {
        return suspendCancellableCoroutine { continuation ->
            val provider = when {
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                else -> null
            }

            if (provider == null) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationManager.removeUpdates(this)
                    if (continuation.isActive) {
                        continuation.resume(Pair(location.latitude, location.longitude))
                    }
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }

            try {
                locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                continuation.invokeOnCancellation {
                    try { locationManager.removeUpdates(listener) } catch (_: Exception) {}
                }
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }

    /**
     * Finds the closest Malaysian Zone from GPS coordinates using Haversine formula.
     */
    fun findClosestZone(lat: Double, lng: Double): MalaysianZone {
        return MalaysianZonesCatalog.zones.minByOrNull { zone ->
            haversineDistanceKm(lat, lng, zone.latitude, zone.longitude)
        } ?: MalaysianZonesCatalog.zones[0]
    }

    private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun getAllZones(): List<MalaysianZone> = MalaysianZonesCatalog.zones

    fun getAllZonesGroupedByState(): Map<String, List<MalaysianZone>> {
        return MalaysianZonesCatalog.zones.groupBy { it.state }
    }
}
