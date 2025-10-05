package com.gsnamespace.atforecast.data.remote

import com.gsnamespace.atforecast.data.remote.dto.ShelterDto
import com.gsnamespace.atforecast.data.remote.dto.StateDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for ATForecast backend.
 * Base URL: https://www.atforecast.app/
 */
interface ATForecastApiService {

    /**
     * Fetch all states with optional shelter data.
     * @param includeShelters Whether to include shelter data in response
     * @param apiKey API key for authentication
     */
    @GET("index.json")
    suspend fun getStates(
        @Query("include_shelters") includeShelters: Boolean,
        @Query("api_key") apiKey: String
    ): List<StateDto>

    /**
     * Fetch shelter with weather forecast data.
     * @param id Shelter ID
     * @param apiKey API key for authentication
     * @param distMiles Optional distance in miles for mass data fetch within radius
     * @return List of shelters (single shelter or multiple if distMiles specified)
     */
    @GET("shelters/{id}.json")
    suspend fun getShelter(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("dist_miles") distMiles: Int? = null
    ): List<ShelterDto>
}
