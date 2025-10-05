package com.gsnamespace.atforecast.domain.usecase

import com.gsnamespace.atforecast.data.local.dao.DailyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.HourlyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.ShelterDao
import com.gsnamespace.atforecast.domain.mapper.createShelterWithWeather
import com.gsnamespace.atforecast.domain.model.ShelterWithWeather
import com.gsnamespace.atforecast.domain.model.TemperatureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case for getting shelter with weather forecast data.
 * Combines data from multiple DAOs and converts to domain model.
 */
class GetShelterWithWeatherUseCase @Inject constructor(
    private val shelterDao: ShelterDao,
    private val dailyWeatherDao: DailyWeatherDao,
    private val hourlyWeatherDao: HourlyWeatherDao
) {

    /**
     * Get shelter with weather data as a Flow.
     * Automatically updates when database changes.
     *
     * @param shelterId The shelter ID
     * @param temperatureUnit User's preferred temperature unit
     * @return Flow of ShelterWithWeather or null if not found
     */
    operator fun invoke(
        shelterId: Int,
        temperatureUnit: TemperatureUnit
    ): Flow<ShelterWithWeather?> {
        return combine(
            shelterDao.getShelterByIdFlow(shelterId),
            dailyWeatherDao.getDailyWeatherForShelter(shelterId)
        ) { shelter, dailyWeather ->
            if (shelter == null) {
                return@combine null
            }

            // Fetch hourly weather for each daily forecast
            val hourlyWeatherMap = dailyWeather.associate { daily ->
                val hourly = hourlyWeatherDao.getHourlyWeatherForDaySync(daily.dailyWeatherId)
                daily.dailyWeatherId to hourly
            }

            createShelterWithWeather(
                shelter = shelter,
                dailyWeather = dailyWeather,
                hourlyWeatherMap = hourlyWeatherMap,
                temperatureUnit = temperatureUnit
            )
        }
    }
}
