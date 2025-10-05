package com.gsnamespace.atforecast.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing hourly weather forecast within a daily forecast.
 */
@Entity(
    tableName = "hourly_weather",
    foreignKeys = [
        ForeignKey(
            entity = DailyWeatherEntity::class,
            parentColumns = ["dailyWeatherId"],
            childColumns = ["dailyWeatherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dailyWeatherId")]
)
data class HourlyWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val hourlyWeatherId: Int = 0,
    val dailyWeatherId: Int,  // Foreign key to DailyWeatherEntity
    val date: String,         // Date/time string
    val temp: Int,            // Temperature
    val description: String,  // Weather description
    val wind: String          // Wind information
)
