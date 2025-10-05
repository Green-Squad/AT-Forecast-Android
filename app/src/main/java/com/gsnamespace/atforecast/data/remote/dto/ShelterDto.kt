package com.gsnamespace.atforecast.data.remote.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Shelter from the API.
 */
data class ShelterDto(
    @SerializedName("shelter_id")
    @Expose
    val shelterId: Int,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("mileage")
    @Expose
    val mileage: Double,

    @SerializedName("elevation")
    @Expose
    val elevation: Int?,

    @SerializedName("latt")
    @Expose
    val latitude: Double,

    @SerializedName("long")
    @Expose
    val longitude: Double,

    @SerializedName("daily_weather")
    @Expose
    val dailyWeather: List<DailyWeatherDto>? = null,

    // Not in API response, but needed for local storage
    val stateId: Int? = null
)
