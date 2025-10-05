package com.gsnamespace.atforecast.data.repository

import com.gsnamespace.atforecast.BuildConfig
import com.gsnamespace.atforecast.data.local.dao.ShelterDao
import com.gsnamespace.atforecast.data.local.dao.StateDao
import com.gsnamespace.atforecast.data.local.entity.StateEntity
import com.gsnamespace.atforecast.data.mapper.toEntities
import com.gsnamespace.atforecast.data.mapper.toEntity
import com.gsnamespace.atforecast.data.remote.ATForecastApiService
import com.gsnamespace.atforecast.data.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for State data.
 * Implements offline-first architecture: always show cached data, fetch fresh data in background.
 */
@Singleton
class StateRepository @Inject constructor(
    private val stateDao: StateDao,
    private val shelterDao: ShelterDao,
    private val apiService: ATForecastApiService
) {

    /**
     * Get all states from the database as a Flow.
     * Returns cached data immediately for offline support.
     */
    fun getStates(): Flow<List<StateEntity>> {
        return stateDao.getAllStates()
    }

    /**
     * Get all states with network fetch.
     * Emits Loading -> Success/Error states.
     */
    fun getStatesWithRefresh(): Flow<Resource<List<StateEntity>>> = flow {
        // Emit loading with cached data
        emit(Resource.Loading())

        // Emit current cached data immediately
        stateDao.getAllStates().collect { cachedStates ->
            if (cachedStates.isNotEmpty()) {
                emit(Resource.Loading(cachedStates))
            }
        }

        // Fetch fresh data from network
        try {
            refreshStates(includeShelters = true)
            // After refresh, emit success with new data
            stateDao.getAllStates().map { states ->
                emit(Resource.Success(states))
            }
        } catch (e: Exception) {
            // On error, emit error but keep cached data
            stateDao.getAllStates().map { states ->
                emit(Resource.Error(e.message ?: "Unknown error", states))
            }
        }
    }

    /**
     * Refresh states from the API and save to database.
     * @param includeShelters Whether to include shelter data in the response
     */
    suspend fun refreshStates(includeShelters: Boolean = true) {
        try {
            val response = apiService.getStates(
                includeShelters = includeShelters,
                apiKey = BuildConfig.ATFORECAST_API_KEY
            )

            // Convert and save states
            val stateEntities = response.toEntities()
            stateDao.insertStates(stateEntities)

            // If shelters included, save them too
            if (includeShelters) {
                response.forEach { stateDto ->
                    stateDto.shelters?.let { shelters ->
                        val shelterEntities = shelters.map { it.toEntity(stateDto.stateId) }
                        shelterDao.insertShelters(shelterEntities)
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Check if states data is stale and needs refresh.
     * @param maxAgeHours Maximum age in hours before data is considered stale
     */
    suspend fun isStale(maxAgeHours: Int = 24): Boolean {
        val states = stateDao.getAllStates()
        // This is a simplified check - in production, track last update time
        return false // Implement based on timestamp checking
    }
}
