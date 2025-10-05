package com.gsnamespace.atforecast.domain.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    private val dailyFormatter = DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy", Locale.US)
    private val hourlyFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

    /**
     * Formats a date string to "Friday May 12, 2025" format.
     * Expected input format: "2025-05-12T00:00:00.000-04:00"
     */
    fun formatDailyDate(dateString: String): String {
        return try {
            val dateTime = ZonedDateTime.parse(dateString)
            dateTime.format(dailyFormatter)
        } catch (e: Exception) {
            dateString // Return original if parsing fails
        }
    }

    /**
     * Formats a datetime string to "5:00 PM" format.
     * Expected input format: "2025-05-12T17:00:00.000-04:00"
     */
    fun formatHourlyTime(dateTimeString: String): String {
        return try {
            val dateTime = ZonedDateTime.parse(dateTimeString)
            dateTime.format(hourlyFormatter)
        } catch (e: Exception) {
            dateTimeString // Return original if parsing fails
        }
    }
}
