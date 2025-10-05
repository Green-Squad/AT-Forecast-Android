package com.gsnamespace.atforecast.data.repository

import androidx.room.withTransaction
import com.gsnamespace.atforecast.BuildConfig
import com.gsnamespace.atforecast.data.local.ATForecastDatabase
import com.gsnamespace.atforecast.data.local.dao.DailyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.HourlyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.ShelterDao
import com.gsnamespace.atforecast.data.local.entity.ShelterEntity
import com.gsnamespace.atforecast.data.mapper.toDailyWeatherEntities
import com.gsnamespace.atforecast.data.mapper.toHourlyWeatherEntities
import com.gsnamespace.atforecast.data.remote.ATForecastApiService
import com.gsnamespace.atforecast.data.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Repository for Shelter data.
 * Implements offline-first architecture with weather forecast caching.
 */
@Singleton
class ShelterRepository @Inject constructor(
    private val database: ATForecastDatabase,
    private val shelterDao: ShelterDao,
    private val dailyWeatherDao: DailyWeatherDao,
    private val hourlyWeatherDao: HourlyWeatherDao,
    private val apiService: ATForecastApiService
) {
    private val isPrefetchInProgress = java.util.concurrent.atomic.AtomicBoolean(false)

    /**
     * Get shelters for a specific state.
     */
    fun getSheltersByState(stateId: Int): Flow<List<ShelterEntity>> {
        return shelterDao.getSheltersByState(stateId)
    }

    /**
     * Get all shelters.
     */
    fun getAllShelters(): Flow<List<ShelterEntity>> {
        return shelterDao.getAllShelters()
    }

    /**
     * Get shelter by ID.
     */
    suspend fun getShelterById(shelterId: Int): ShelterEntity? {
        return shelterDao.getShelterById(shelterId)
    }

    /**
     * Get shelter by ID as Flow.
     */
    fun getShelterByIdFlow(shelterId: Int): Flow<ShelterEntity?> {
        return shelterDao.getShelterByIdFlow(shelterId)
    }

    /**
     * Refresh weather data for a shelter.
     * @param shelterId The shelter ID to refresh
     * @param distMiles Optional: fetch weather for shelters within this radius (for prefetching)
     */
    suspend fun refreshShelterWeather(shelterId: Int, distMiles: Int? = null) {
        try {
            android.util.Log.d("ShelterRepository", "Fetching weather for shelter $shelterId")
            val response = apiService.getShelter(
                id = shelterId,
                apiKey = BuildConfig.ATFORECAST_API_KEY,
                distMiles = distMiles
            )

            android.util.Log.d("ShelterRepository", "Received ${response.size} shelter(s) from API")

            // Process each shelter in response
            response.forEach { shelterDto ->
                android.util.Log.d("ShelterRepository", "Processing shelter ${shelterDto.shelterId}, has ${shelterDto.dailyWeather?.size ?: 0} daily forecasts")

                // Save daily and hourly weather in a single transaction
                shelterDto.dailyWeather?.let { dailyWeatherList ->
                    val dailyEntities = dailyWeatherList.toDailyWeatherEntities()

                    // Use database transaction to ensure atomic save of daily + hourly weather
                    database.withTransaction {
                        // Delete old weather (cascade deletes hourly weather due to foreign key)
                        dailyWeatherDao.deleteDailyWeatherForShelter(shelterDto.shelterId)

                        // Insert daily weather
                        val insertedIds = dailyWeatherDao.insertDailyWeather(dailyEntities)
                        android.util.Log.d("ShelterRepository", "Inserted ${insertedIds.size} daily weather records for shelter ${shelterDto.shelterId}")

                        // Save hourly weather for each daily forecast
                        var totalHourlyCount = 0
                        dailyWeatherList.forEachIndexed { index, dailyWeatherDto ->
                            dailyWeatherDto.hourlyWeather?.let { hourlyWeatherList ->
                                val dailyWeatherId = insertedIds[index].toInt()
                                val hourlyEntities = hourlyWeatherList.toHourlyWeatherEntities(dailyWeatherId)
                                android.util.Log.d("ShelterRepository", "Inserting ${hourlyEntities.size} hourly weather records for daily weather $dailyWeatherId (index $index)")
                                hourlyWeatherDao.insertHourlyWeather(hourlyEntities)
                                totalHourlyCount += hourlyEntities.size
                            } ?: android.util.Log.d("ShelterRepository", "No hourly weather for daily weather index $index")
                        }
                        android.util.Log.d("ShelterRepository", "Total hourly weather records inserted: $totalHourlyCount")
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Search for shelter by mileage marker.
     * Finds the closest shelter to the given mileage.
     */
    suspend fun searchByMileage(mileage: Double): ShelterEntity? {
        return shelterDao.findShelterByMileage(mileage)
    }

    /**
     * Find nearest shelters based on GPS coordinates.
     * Returns shelters sorted by distance (closest first).
     */
    suspend fun findNearestShelters(
        latitude: Double,
        longitude: Double,
        maxResults: Int = 5
    ): List<ShelterEntity> {
        android.util.Log.d("ShelterRepository", "Finding shelters near lat=$latitude, lon=$longitude")

        // Use a bounding box query first to limit candidates
        // Approximately 1 degree = 69 miles at this latitude
        val degreeOffset = 2.0 // ~138 mile radius

        val candidates = shelterDao.getSheltersInBounds(
            minLat = latitude - degreeOffset,
            maxLat = latitude + degreeOffset,
            minLon = longitude - degreeOffset,
            maxLon = longitude + degreeOffset
        )

        android.util.Log.d("ShelterRepository", "Found ${candidates.size} candidate shelters in bounds")

        // Calculate actual distances using Haversine formula
        val sheltersWithDistance = candidates.map { shelter ->
            val distance = haversine(
                lat1 = latitude,
                lon1 = longitude,
                lat2 = shelter.latitude,
                lon2 = shelter.longitude
            )
            shelter to distance
        }

        // Sort by distance and return top results
        val nearest = sheltersWithDistance
            .sortedBy { it.second }
            .take(maxResults)
            .map { it.first }

        android.util.Log.d("ShelterRepository", "Returning ${nearest.size} nearest shelters")
        return nearest
    }

    /**
     * Prefetch weather for nearby shelters in background.
     * This runs in a separate coroutine to avoid blocking the UI.
     * Matches old app behavior of making a second API call with 100-mile radius.
     * Only one prefetch operation can run at a time to avoid duplicate API calls.
     */
    fun prefetchNearbySheltersInBackground(shelterId: Int, distMiles: Int) {
        // Check if a prefetch is already in progress
        if (!isPrefetchInProgress.compareAndSet(false, true)) {
            android.util.Log.d("ShelterRepository", "Background prefetch already in progress, skipping")
            return
        }

        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                android.util.Log.d("ShelterRepository", "Background prefetch: Fetching weather for shelters within $distMiles miles of shelter $shelterId")
                refreshShelterWeather(shelterId, distMiles)
                android.util.Log.d("ShelterRepository", "Background prefetch completed")
            } catch (e: Exception) {
                android.util.Log.e("ShelterRepository", "Background prefetch failed", e)
                // Don't propagate errors from background prefetch
            } finally {
                // Reset the flag so future prefetches can run
                isPrefetchInProgress.set(false)
            }
        }
    }

    /**
     * Get previous shelter (by mileage).
     */
    suspend fun getPreviousShelter(currentShelterId: Int): ShelterEntity? {
        return shelterDao.getPreviousShelter(currentShelterId)
    }

    /**
     * Get next shelter (by mileage).
     */
    suspend fun getNextShelter(currentShelterId: Int): ShelterEntity? {
        return shelterDao.getNextShelter(currentShelterId)
    }

    /**
     * Calculate distance between two coordinates using Haversine formula.
     * Returns distance in kilometers.
     */
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6372.8 // Earth radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val radLat1 = Math.toRadians(lat1)
        val radLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(radLat1) * cos(radLat2)
        val c = 2 * asin(sqrt(a))

        return R * c
    }
}
