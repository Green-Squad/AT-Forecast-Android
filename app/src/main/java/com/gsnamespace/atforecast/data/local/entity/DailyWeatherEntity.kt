package com.gsnamespace.atforecast.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing daily weather forecast for a shelter.
 */
@Entity(
    tableName = "daily_weather",
    foreignKeys = [
        ForeignKey(
            entity = ShelterEntity::class,
            parentColumns = ["shelterId"],
            childColumns = ["shelterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("shelterId"), Index("weatherDate")]
)
data class DailyWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val dailyWeatherId: Int = 0,
    val shelterId: Int,       // Foreign key to ShelterEntity
    val weatherDate: String,  // Date string (e.g., "2025-10-04")
    val high: Int,            // High temperature
    val low: Int,             // Low temperature
    val description: String,  // Weather description (e.g., "Clear", "Rain")
    val wind: String,         // Wind information
    val updatedAt: Long       // Timestamp in milliseconds
)
