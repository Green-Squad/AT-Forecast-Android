package com.gsnamespace.atforecast.data.mapper

import com.gsnamespace.atforecast.data.local.entity.DailyWeatherEntity
import com.gsnamespace.atforecast.data.local.entity.HourlyWeatherEntity
import com.gsnamespace.atforecast.data.remote.dto.DailyWeatherDto
import com.gsnamespace.atforecast.data.remote.dto.HourlyWeatherDto

/**
 * Converts DailyWeatherDto from API to DailyWeatherEntity for database storage.
 */
fun DailyWeatherDto.toEntity(): DailyWeatherEntity {
    return DailyWeatherEntity(
        dailyWeatherId = 0,  // Let Room auto-generate the ID
        shelterId = shelterId,
        weatherDate = weatherDate,
        high = high,
        low = low,
        description = description,
        wind = wind,
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Converts list of DailyWeatherDtos to list of DailyWeatherEntities.
 */
fun List<DailyWeatherDto>.toDailyWeatherEntities(): List<DailyWeatherEntity> {
    return map { it.toEntity() }
}

/**
 * Converts HourlyWeatherDto from API to HourlyWeatherEntity for database storage.
 * @param dailyWeatherId The parent DailyWeather ID from the database (auto-generated)
 */
fun HourlyWeatherDto.toEntity(dailyWeatherId: Int): HourlyWeatherEntity {
    return HourlyWeatherEntity(
        hourlyWeatherId = 0,  // Let Room auto-generate the ID
        dailyWeatherId = dailyWeatherId,
        date = date,
        temp = temp,
        description = description,
        wind = wind
    )
}

/**
 * Converts list of HourlyWeatherDtos to list of HourlyWeatherEntities.
 */
fun List<HourlyWeatherDto>.toHourlyWeatherEntities(dailyWeatherId: Int): List<HourlyWeatherEntity> {
    return map { it.toEntity(dailyWeatherId) }
}
