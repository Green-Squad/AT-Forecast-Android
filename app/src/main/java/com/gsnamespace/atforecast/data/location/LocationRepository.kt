package com.gsnamespace.atforecast.data.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing device location using Google Play Services.
 */
@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) {

    /**
     * Get the current device location.
     * Requires location permission to be granted.
     *
     * @return Result containing Location or error
     */
    suspend fun getCurrentLocation(): Result<Location> {
        return try {
            val cancellationTokenSource = CancellationTokenSource()

            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                Result.success(location)
            } else {
                Result.failure(Exception("Unable to get location. Please ensure location services are enabled."))
            }
        } catch (e: SecurityException) {
            Result.failure(Exception("Location permission not granted"))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get location: ${e.message}"))
        }
    }
}
