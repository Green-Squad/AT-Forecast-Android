package com.gsnamespace.atforecast.domain.model

/**
 * Domain model for daily weather forecast.
 */
data class DailyWeather(
    val dailyWeatherId: Int,
    val shelterId: Int,
    val weatherDate: String,      // Date string (e.g., "2025-10-04")
    val high: Int,                 // Temperature (already converted to user's preferred unit)
    val low: Int,                  // Temperature (already converted to user's preferred unit)
    val description: String,       // Weather description (e.g., "Clear", "Rain")
    val wind: String,              // Wind information
    val hourlyWeather: List<HourlyWeather> = emptyList()  // Hourly forecasts for this day
)
