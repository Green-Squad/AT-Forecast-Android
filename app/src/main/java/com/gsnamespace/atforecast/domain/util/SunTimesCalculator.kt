package com.gsnamespace.atforecast.domain.util

import org.shredzone.commons.suncalc.SunTimes
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility object for calculating sunrise and sunset times for a given location.
 */
object SunTimesCalculator {

    // Most of the Appalachian Trail is in the Eastern Time Zone
    private val EASTERN_TIMEZONE = ZoneId.of("America/New_York")

    // 12-hour time format with AM/PM
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a")

    /**
     * Calculate sunrise and sunset times for a given location on the current date.
     *
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return Pair of formatted sunrise and sunset times (e.g., "6:45 AM" to "8:30 PM")
     *         Returns null for either value if the sun doesn't rise or set on this date
     */
    fun calculateSunTimes(latitude: Double, longitude: Double): Pair<String?, String?> {
        val today = ZonedDateTime.now(EASTERN_TIMEZONE)

        val sunTimes = SunTimes.compute()
            .on(today)
            .at(latitude, longitude)
            .execute()

        val sunrise = sunTimes.rise?.let {
            it.withZoneSameInstant(EASTERN_TIMEZONE).format(TIME_FORMATTER)
        }

        val sunset = sunTimes.set?.let {
            it.withZoneSameInstant(EASTERN_TIMEZONE).format(TIME_FORMATTER)
        }

        return Pair(sunrise, sunset)
    }
}
