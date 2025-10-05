package com.gsnamespace.atforecast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gsnamespace.atforecast.data.local.entity.HourlyWeatherEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing HourlyWeather data from the database.
 */
@Dao
interface HourlyWeatherDao {

    @Query("SELECT * FROM hourly_weather WHERE dailyWeatherId = :dailyWeatherId ORDER BY date ASC")
    fun getHourlyWeatherForDay(dailyWeatherId: Int): Flow<List<HourlyWeatherEntity>>

    @Query("SELECT * FROM hourly_weather WHERE dailyWeatherId = :dailyWeatherId ORDER BY date ASC")
    suspend fun getHourlyWeatherForDaySync(dailyWeatherId: Int): List<HourlyWeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeather: List<HourlyWeatherEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleHourlyWeather(hourlyWeather: HourlyWeatherEntity)

    @Query("DELETE FROM hourly_weather WHERE dailyWeatherId = :dailyWeatherId")
    suspend fun deleteHourlyWeatherForDay(dailyWeatherId: Int)

    @Query("DELETE FROM hourly_weather")
    suspend fun deleteAllHourlyWeather()
}
