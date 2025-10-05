package com.gsnamespace.atforecast.data.remote.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for State from the API.
 */
data class StateDto(
    @SerializedName("state_id")
    @Expose
    val stateId: Int,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("average_high")
    @Expose
    val averageHigh: Int,

    @SerializedName("average_low")
    @Expose
    val averageLow: Int,

    @SerializedName("shelters")
    @Expose
    val shelters: List<ShelterDto>? = null
)
