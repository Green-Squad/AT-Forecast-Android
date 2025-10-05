package com.gsnamespace.atforecast.domain.model

/**
 * Domain model for an Appalachian Trail shelter.
 */
data class Shelter(
    val shelterId: Int,
    val name: String,
    val mileage: Double,      // NOBO mile marker
    val elevation: Int?,      // Elevation in feet
    val latitude: Double,
    val longitude: Double,
    val stateId: Int
)
