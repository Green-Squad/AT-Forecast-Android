package com.gsnamespace.atforecast.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a shelter on the Appalachian Trail.
 */
@Entity(
    tableName = "shelters",
    foreignKeys = [
        ForeignKey(
            entity = StateEntity::class,
            parentColumns = ["stateId"],
            childColumns = ["stateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stateId"), Index("mileage")]
)
data class ShelterEntity(
    @PrimaryKey
    val shelterId: Int,
    val name: String,
    val mileage: Double,      // NOBO mile marker (max 2500)
    val elevation: Int?,      // Elevation in feet
    val latitude: Double,
    val longitude: Double,
    val stateId: Int          // Foreign key to StateEntity
)
