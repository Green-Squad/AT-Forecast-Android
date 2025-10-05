package com.gsnamespace.atforecast.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gsnamespace.atforecast.data.local.dao.DailyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.HourlyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.ShelterDao
import com.gsnamespace.atforecast.data.local.dao.StateDao
import com.gsnamespace.atforecast.data.local.entity.DailyWeatherEntity
import com.gsnamespace.atforecast.data.local.entity.HourlyWeatherEntity
import com.gsnamespace.atforecast.data.local.entity.ShelterEntity
import com.gsnamespace.atforecast.data.local.entity.StateEntity

/**
 * Room database for ATForecast app.
 * Stores states, shelters, and weather forecast data for offline-first architecture.
 */
@Database(
    entities = [
        StateEntity::class,
        ShelterEntity::class,
        DailyWeatherEntity::class,
        HourlyWeatherEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ATForecastDatabase : RoomDatabase() {

    abstract fun stateDao(): StateDao
    abstract fun shelterDao(): ShelterDao
    abstract fun dailyWeatherDao(): DailyWeatherDao
    abstract fun hourlyWeatherDao(): HourlyWeatherDao

    companion object {
        const val DATABASE_NAME = "atforecast_database"
    }
}
