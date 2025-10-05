package com.gsnamespace.atforecast.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a US state along the Appalachian Trail.
 */
@Entity(tableName = "states")
data class StateEntity(
    @PrimaryKey
    val stateId: Int,
    val name: String,
    val averageHigh: Int,  // Temperature in Fahrenheit
    val averageLow: Int,   // Temperature in Fahrenheit
    val updatedAt: Long    // Timestamp in milliseconds
)
