package com.gsnamespace.atforecast.data.remote.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for HourlyWeather from the API.
 */
data class HourlyWeatherDto(
    @SerializedName("hourly_weather_id")
    @Expose
    val hourlyWeatherId: Int,

    @SerializedName("date")
    @Expose
    val date: String,

    @SerializedName("temp")
    @Expose
    val temp: Int,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("wind")
    @Expose
    val wind: String
)
