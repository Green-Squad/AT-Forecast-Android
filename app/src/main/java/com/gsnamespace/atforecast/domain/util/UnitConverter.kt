package com.gsnamespace.atforecast.domain.util

import com.gsnamespace.atforecast.domain.model.DistanceUnit

/**
 * Utility functions for converting between imperial and metric units.
 */
object UnitConverter {

    private const val MILES_TO_KM = 1.60934
    private const val MPH_TO_KMH = 1.60934
    private const val FEET_TO_METERS = 0.3048

    /**
     * Convert miles to kilometers or return miles based on unit preference.
     */
    fun formatDistance(miles: Double, unit: DistanceUnit): String {
        return when (unit) {
            DistanceUnit.IMPERIAL -> String.format("%.1f", miles)
            DistanceUnit.METRIC -> String.format("%.1f", miles * MILES_TO_KM)
        }
    }

    /**
     * Get the distance unit label.
     */
    fun getDistanceUnitLabel(unit: DistanceUnit): String {
        return when (unit) {
            DistanceUnit.IMPERIAL -> "mi"
            DistanceUnit.METRIC -> "km"
        }
    }

    /**
     * Convert elevation from feet to meters or return feet based on unit preference.
     */
    fun formatElevation(feet: Int, unit: DistanceUnit): String {
        return when (unit) {
            DistanceUnit.IMPERIAL -> feet.toString()
            DistanceUnit.METRIC -> String.format("%.0f", feet * FEET_TO_METERS)
        }
    }

    /**
     * Get the elevation unit label.
     */
    fun getElevationUnitLabel(unit: DistanceUnit): String {
        return when (unit) {
            DistanceUnit.IMPERIAL -> "ft"
            DistanceUnit.METRIC -> "m"
        }
    }

    /**
     * Parse and format wind speed string.
     * Expected input format: "X mph" or similar
     * Output format: "X mph" or "X km/h" based on preference
     */
    fun formatWindSpeed(windString: String, unit: DistanceUnit): String {
        if (unit == DistanceUnit.IMPERIAL) {
            return windString // Already in mph
        }

        // Parse mph value and convert to km/h
        val mphRegex = """(\d+\.?\d*)\s*mph""".toRegex(RegexOption.IGNORE_CASE)
        val match = mphRegex.find(windString)

        return if (match != null) {
            val mph = match.groupValues[1].toDoubleOrNull() ?: return windString
            val kmh = mph * MPH_TO_KMH
            String.format("%.0f km/h", kmh)
        } else {
            windString // Return as-is if format doesn't match
        }
    }
}
