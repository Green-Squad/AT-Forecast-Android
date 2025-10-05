package com.gsnamespace.atforecast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gsnamespace.atforecast.data.local.entity.DailyWeatherEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing DailyWeather data from the database.
 */
@Dao
interface DailyWeatherDao {

    @Query("SELECT * FROM daily_weather WHERE shelterId = :shelterId ORDER BY weatherDate ASC")
    fun getDailyWeatherForShelter(shelterId: Int): Flow<List<DailyWeatherEntity>>

    @Query("SELECT * FROM daily_weather WHERE dailyWeatherId = :dailyWeatherId")
    suspend fun getDailyWeatherById(dailyWeatherId: Int): DailyWeatherEntity?

    @Query("SELECT * FROM daily_weather WHERE shelterId = :shelterId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestDailyWeather(shelterId: Int): DailyWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(dailyWeather: List<DailyWeatherEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleDailyWeather(dailyWeather: DailyWeatherEntity): Long

    @Query("DELETE FROM daily_weather WHERE shelterId = :shelterId")
    suspend fun deleteDailyWeatherForShelter(shelterId: Int)

    @Query("DELETE FROM daily_weather")
    suspend fun deleteAllDailyWeather()

    @Transaction
    suspend fun replaceWeatherForShelter(shelterId: Int, weather: List<DailyWeatherEntity>) {
        deleteDailyWeatherForShelter(shelterId)
        insertDailyWeather(weather)
    }
}
