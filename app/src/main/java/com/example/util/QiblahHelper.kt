package com.example.util

import kotlin.math.*

/**
 * Geometric, Geodesic, and Astronomical calculations for Qiblah,
 * Kaaba coordinates, Great Circle distance, and Compass calculations.
 */
object QiblahHelper {
    // Holy Kaaba Coordinates (Makkah Al-Mukarramah)
    const val MAKKAH_LATITUDE = 21.422487
    const val MAKKAH_LONGITUDE = 39.826206
    const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the true Qiblah azimuth / bearing in degrees [0, 360) from a given GPS coordinate to the Kaaba.
     * Uses the Great Circle forward azimuth formula:
     * θ = atan2(sin(Δλ) * cos(φ2), cos(φ1) * sin(φ2) − sin(φ1) * cos(φ2) * cos(Δλ))
     */
    fun calculateQiblahBearing(latitude: Double, longitude: Double): Double {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(MAKKAH_LATITUDE)
        val deltaLambda = Math.toRadians(MAKKAH_LONGITUDE - longitude)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)

        val bearingRad = atan2(y, x)
        var bearingDeg = Math.toDegrees(bearingRad)
        bearingDeg = (bearingDeg + 360.0) % 360.0

        return bearingDeg
    }

    /**
     * Calculates the Great Circle distance in kilometers between two GPS coordinates using the Haversine formula.
     */
    fun calculateDistanceKm(
        lat1: Double, lon1: Double,
        lat2: Double = MAKKAH_LATITUDE, lon2: Double = MAKKAH_LONGITUDE
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2) + cos(phi1) * cos(phi2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Formats compass degree into cardinal direction (e.g. 292° -> "WNW" / "West-Northwest").
     */
    fun getCardinalDirection(degrees: Double): String {
        val normalized = (degrees % 360.0 + 360.0) % 360.0
        val directions = arrayOf(
            "N", "NNE", "NE", "ENE",
            "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW",
            "W", "WNW", "NW", "NNW"
        )
        val index = ((normalized + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    /**
     * Generates intermediate GPS waypoints along the Great Circle arc from user location to Makkah.
     * Useful for 3D Globe arc rendering.
     */
    fun generateGeodesicArcPoints(
        startLat: Double, startLon: Double,
        endLat: Double = MAKKAH_LATITUDE, endLon: Double = MAKKAH_LONGITUDE,
        numPoints: Int = 30
    ): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        val lat1 = Math.toRadians(startLat)
        val lon1 = Math.toRadians(startLon)
        val lat2 = Math.toRadians(endLat)
        val lon2 = Math.toRadians(endLon)

        val d = 2 * asin(
            sqrt(
                sin((lat1 - lat2) / 2).pow(2) +
                cos(lat1) * cos(lat2) * sin((lon1 - lon2) / 2).pow(2)
            )
        )

        if (d < 1e-6) {
            points.add(Pair(startLat, startLon))
            points.add(Pair(endLat, endLon))
            return points
        }

        for (i in 0..numPoints) {
            val f = i.toDouble() / numPoints.toDouble()
            val a = sin((1 - f) * d) / sin(d)
            val b = sin(f * d) / sin(d)

            val x = a * cos(lat1) * cos(lon1) + b * cos(lat2) * cos(lon2)
            val y = a * cos(lat1) * sin(lon1) + b * cos(lat2) * sin(lon2)
            val z = a * sin(lat1) + b * sin(lat2)

            val latRad = atan2(z, sqrt(x * x + y * y))
            val lonRad = atan2(y, x)

            points.add(Pair(Math.toDegrees(latRad), Math.toDegrees(lonRad)))
        }

        return points
    }
}
