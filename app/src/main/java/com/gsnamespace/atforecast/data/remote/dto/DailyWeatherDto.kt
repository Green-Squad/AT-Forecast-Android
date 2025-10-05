package com.gsnamespace.atforecast.data.remote.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for DailyWeather from the API.
 */
data class DailyWeatherDto(
    @SerializedName("daily_weather_id")
    @Expose
    val dailyWeatherId: Int,

    @SerializedName("weather_date")
    @Expose
    val weatherDate: String,

    @SerializedName("high")
    @Expose
    val high: Int,

    @SerializedName("low")
    @Expose
    val low: Int,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("wind")
    @Expose
    val wind: String,

    @SerializedName("shelter_id")
    @Expose
    val shelterId: Int,

    @SerializedName("hourly_weather")
    @Expose
    val hourlyWeather: List<HourlyWeatherDto>? = null
)
