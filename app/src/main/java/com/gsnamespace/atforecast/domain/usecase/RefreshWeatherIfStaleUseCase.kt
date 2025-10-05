package com.gsnamespace.atforecast.domain.usecase

import com.gsnamespace.atforecast.data.local.dao.DailyWeatherDao
import com.gsnamespace.atforecast.data.repository.ShelterRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Use case for refreshing weather data if it's stale.
 * Weather data is considered stale after 2 hours.
 * Manual refreshes are rate-limited to once every 30 minutes.
 */
class RefreshWeatherIfStaleUseCase @Inject constructor(
    private val shelterRepository: ShelterRepository,
    private val dailyWeatherDao: DailyWeatherDao
) {

    companion object {
        private val WEATHER_STALE_THRESHOLD = 2.hours.inWholeMilliseconds
        private val MINIMUM_REFRESH_INTERVAL = 30.minutes.inWholeMilliseconds
    }

    /**
     * Check if weather data for a shelter is stale and refresh if needed.
     *
     * @param shelterId The shelter ID to check and refresh
     * @param forceRefresh If true, refresh regardless of staleness (but still respects minimum interval)
     * @param prefetchInBackground If true, also prefetch weather for nearby shelters (100 mile radius) in background
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        shelterId: Int,
        forceRefresh: Boolean = false,
        prefetchInBackground: Boolean = false
    ): Result<Unit> {
        return try {
            // Check if weather was updated too recently
            if (!canRefresh(shelterId)) {
                android.util.Log.d("RefreshWeatherIfStale", "Weather updated recently, skipping refresh")
                return Result.success(Unit)
            }

            val shouldRefresh = forceRefresh || isWeatherStale(shelterId)

            if (shouldRefresh) {
                android.util.Log.d("RefreshWeatherIfStale", "Refreshing shelter $shelterId, prefetchInBackground=$prefetchInBackground")

                // First call: Refresh weather for current shelter only (fast, updates UI)
                shelterRepository.refreshShelterWeather(
                    shelterId = shelterId,
                    distMiles = null
                )

                // Second call: Prefetch nearby shelters in background (doesn't block UI)
                if (prefetchInBackground) {
                    android.util.Log.d("RefreshWeatherIfStale", "Starting background prefetch for 100-mile radius")
                    shelterRepository.prefetchNearbySheltersInBackground(
                        shelterId = shelterId,
                        distMiles = 100
                    )
                } else {
                    android.util.Log.d("RefreshWeatherIfStale", "Skipping background prefetch (prefetchInBackground=false)")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if enough time has passed since last update to allow a refresh.
     * Prevents refreshing more than once every 30 minutes.
     */
    private suspend fun canRefresh(shelterId: Int): Boolean {
        val latestWeather = dailyWeatherDao.getLatestDailyWeather(shelterId)
            ?: return true // No weather data, allow refresh

        val currentTime = System.currentTimeMillis()
        val timeSinceUpdate = currentTime - latestWeather.updatedAt

        return timeSinceUpdate > MINIMUM_REFRESH_INTERVAL
    }

    /**
     * Check if weather data is stale for a given shelter.
     */
    private suspend fun isWeatherStale(shelterId: Int): Boolean {
        val latestWeather = dailyWeatherDao.getLatestDailyWeather(shelterId)
            ?: return true // No weather data, needs refresh

        val currentTime = System.currentTimeMillis()
        val timeSinceUpdate = currentTime - latestWeather.updatedAt

        return timeSinceUpdate > WEATHER_STALE_THRESHOLD
    }
}
