package com.gsnamespace.atforecast.domain.model

/**
 * Domain model combining shelter information with weather forecast.
 * Used for the shelter detail screen.
 */
data class ShelterWithWeather(
    val shelter: Shelter,
    val dailyWeather: List<DailyWeather>,
    val lastUpdated: Long  // Timestamp of last weather update
)
