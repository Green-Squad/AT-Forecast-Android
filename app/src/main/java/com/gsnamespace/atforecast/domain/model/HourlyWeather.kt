package com.gsnamespace.atforecast.domain.model

/**
 * Domain model for hourly weather forecast.
 */
data class HourlyWeather(
    val hourlyWeatherId: Int,
    val dailyWeatherId: Int,
    val date: String,          // Date/time string
    val temp: Int,             // Temperature (already converted to user's preferred unit)
    val description: String,   // Weather description
    val wind: String           // Wind information
)
