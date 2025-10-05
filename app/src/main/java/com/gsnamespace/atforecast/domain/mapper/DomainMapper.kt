package com.gsnamespace.atforecast.domain.mapper

import com.gsnamespace.atforecast.data.local.entity.DailyWeatherEntity
import com.gsnamespace.atforecast.data.local.entity.HourlyWeatherEntity
import com.gsnamespace.atforecast.data.local.entity.ShelterEntity
import com.gsnamespace.atforecast.data.local.entity.StateEntity
import com.gsnamespace.atforecast.domain.model.DailyWeather
import com.gsnamespace.atforecast.domain.model.HourlyWeather
import com.gsnamespace.atforecast.domain.model.Shelter
import com.gsnamespace.atforecast.domain.model.ShelterWithWeather
import com.gsnamespace.atforecast.domain.model.State
import com.gsnamespace.atforecast.domain.model.TemperatureUnit
import com.gsnamespace.atforecast.domain.util.TemperatureConverter

/**
 * Convert StateEntity to State domain model.
 * @param temperatureUnit User's preferred temperature unit
 */
fun StateEntity.toDomain(temperatureUnit: TemperatureUnit): State {
    return State(
        stateId = stateId,
        name = name,
        averageHigh = TemperatureConverter.convertTemperature(averageHigh, temperatureUnit),
        averageLow = TemperatureConverter.convertTemperature(averageLow, temperatureUnit),
        imageName = State.getImageName(name)
    )
}

/**
 * Convert list of StateEntities to list of State domain models.
 * States are sorted by stateId to maintain geographic order along the AT.
 */
fun List<StateEntity>.toDomainStates(temperatureUnit: TemperatureUnit): List<State> {
    return map { it.toDomain(temperatureUnit) }.sortedBy { it.stateId }
}

/**
 * Convert ShelterEntity to Shelter domain model.
 */
fun ShelterEntity.toDomain(): Shelter {
    return Shelter(
        shelterId = shelterId,
        name = name,
        mileage = mileage,
        elevation = elevation,
        latitude = latitude,
        longitude = longitude,
        stateId = stateId
    )
}

/**
 * Convert list of ShelterEntities to list of Shelter domain models.
 * Shelters are sorted by mileage to maintain geographic order along the AT.
 */
fun List<ShelterEntity>.toDomainShelters(): List<Shelter> {
    return map { it.toDomain() }.sortedBy { it.mileage }
}

/**
 * Convert DailyWeatherEntity to DailyWeather domain model.
 * @param temperatureUnit User's preferred temperature unit
 * @param hourlyWeather List of hourly weather for this day
 */
fun DailyWeatherEntity.toDomain(
    temperatureUnit: TemperatureUnit,
    hourlyWeather: List<HourlyWeather> = emptyList()
): DailyWeather {
    return DailyWeather(
        dailyWeatherId = dailyWeatherId,
        shelterId = shelterId,
        weatherDate = weatherDate,
        high = TemperatureConverter.convertTemperature(high, temperatureUnit),
        low = TemperatureConverter.convertTemperature(low, temperatureUnit),
        description = description,
        wind = wind,
        hourlyWeather = hourlyWeather
    )
}

/**
 * Convert HourlyWeatherEntity to HourlyWeather domain model.
 * @param temperatureUnit User's preferred temperature unit
 */
fun HourlyWeatherEntity.toDomain(temperatureUnit: TemperatureUnit): HourlyWeather {
    return HourlyWeather(
        hourlyWeatherId = hourlyWeatherId,
        dailyWeatherId = dailyWeatherId,
        date = date,
        temp = TemperatureConverter.convertTemperature(temp, temperatureUnit),
        description = description,
        wind = wind
    )
}

/**
 * Convert list of HourlyWeatherEntities to list of HourlyWeather domain models.
 */
fun List<HourlyWeatherEntity>.toDomainHourlyWeather(temperatureUnit: TemperatureUnit): List<HourlyWeather> {
    return map { it.toDomain(temperatureUnit) }
}

/**
 * Create ShelterWithWeather domain model from shelter and weather entities.
 */
fun createShelterWithWeather(
    shelter: ShelterEntity,
    dailyWeather: List<DailyWeatherEntity>,
    hourlyWeatherMap: Map<Int, List<HourlyWeatherEntity>>,
    temperatureUnit: TemperatureUnit
): ShelterWithWeather {
    val dailyWeatherDomain = dailyWeather.map { daily ->
        val hourly = hourlyWeatherMap[daily.dailyWeatherId]?.toDomainHourlyWeather(temperatureUnit) ?: emptyList()
        daily.toDomain(temperatureUnit, hourly)
    }

    val lastUpdated = dailyWeather.maxOfOrNull { it.updatedAt } ?: 0L

    return ShelterWithWeather(
        shelter = shelter.toDomain(),
        dailyWeather = dailyWeatherDomain,
        lastUpdated = lastUpdated
    )
}
