package com.gsnamespace.atforecast.ui.statelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gsnamespace.atforecast.data.location.LocationRepository
import com.gsnamespace.atforecast.data.preferences.UserPreferencesRepository
import com.gsnamespace.atforecast.data.repository.StateRepository
import com.gsnamespace.atforecast.domain.mapper.toDomainStates
import com.gsnamespace.atforecast.domain.model.DistanceUnit
import com.gsnamespace.atforecast.domain.model.Shelter
import com.gsnamespace.atforecast.domain.model.State
import com.gsnamespace.atforecast.domain.model.TemperatureUnit
import com.gsnamespace.atforecast.domain.model.ThemeMode
import com.gsnamespace.atforecast.domain.usecase.GetNearestSheltersUseCase
import com.gsnamespace.atforecast.domain.usecase.SearchShelterByMileageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the State List screen.
 * Displays all states along the Appalachian Trail with average temperatures.
 */
@HiltViewModel
class StateListViewModel @Inject constructor(
    private val stateRepository: StateRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val searchShelterByMileageUseCase: SearchShelterByMileageUseCase,
    private val getNearestSheltersUseCase: GetNearestSheltersUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StateListUiState>(StateListUiState.Loading)
    val uiState: StateFlow<StateListUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val temperatureUnit = userPreferencesRepository.temperatureUnit

    private var lastRefreshTime: Long = 0L
    private val MINIMUM_REFRESH_INTERVAL = 60 * 60 * 1000L // 1 hour in milliseconds

    val currentTemperatureUnit: StateFlow<TemperatureUnit> = userPreferencesRepository.temperatureUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TemperatureUnit.FAHRENHEIT
        )

    val currentDistanceUnit: StateFlow<DistanceUnit> = userPreferencesRepository.distanceUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DistanceUnit.IMPERIAL
        )

    val currentThemeMode: StateFlow<ThemeMode> = userPreferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    init {
        loadStates()
    }

    /**
     * Load states from the repository.
     */
    private fun loadStates() {
        viewModelScope.launch {
            combine(
                stateRepository.getStates(),
                temperatureUnit
            ) { states, unit ->
                states to unit
            }
                .catch { exception ->
                    _uiState.value = StateListUiState.Error(exception.message ?: "Unknown error")
                }
                .collect { (states, unit) ->
                    if (states.isEmpty()) {
                        // No cached data, trigger refresh
                        refreshStates()
                    } else {
                        _uiState.value = StateListUiState.Success(
                            states = states.toDomainStates(unit)
                        )
                    }
                }
        }
    }

    /**
     * Refresh states from the API.
     * Rate-limited to once per hour to avoid excessive API calls.
     */
    fun refreshStates() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastRefresh = currentTime - lastRefreshTime

            // Check if enough time has passed since last refresh
            if (timeSinceLastRefresh < MINIMUM_REFRESH_INTERVAL && lastRefreshTime > 0) {
                android.util.Log.d("StateListViewModel", "States refreshed recently (${timeSinceLastRefresh / 1000}s ago), skipping refresh")
                _isRefreshing.value = false
                return@launch
            }

            _isRefreshing.value = true
            try {
                android.util.Log.d("StateListViewModel", "Refreshing states and shelters from API")
                stateRepository.refreshStates(includeShelters = true)
                lastRefreshTime = currentTime
            } catch (e: Exception) {
                android.util.Log.e("StateListViewModel", "Failed to refresh states", e)
                _uiState.value = StateListUiState.Error(e.message ?: "Failed to refresh states")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * Update the temperature unit preference.
     */
    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            userPreferencesRepository.setTemperatureUnit(unit)
        }
    }

    /**
     * Update the distance unit preference.
     */
    fun setDistanceUnit(unit: DistanceUnit) {
        viewModelScope.launch {
            userPreferencesRepository.setDistanceUnit(unit)
        }
    }

    /**
     * Update the theme mode preference.
     */
    fun setThemeMode(mode: ThemeMode, context: android.content.Context) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(mode, context)
        }
    }

    /**
     * Search for a shelter by mileage marker.
     * Returns the shelter ID if found, null otherwise.
     */
    suspend fun searchByMileage(mileage: Double): Int? {
        return searchShelterByMileageUseCase(mileage)
            .getOrNull()
            ?.shelterId
    }

    /**
     * Find nearest shelters based on current GPS location.
     * Returns list of nearest shelters or null if location unavailable.
     */
    suspend fun findNearestShelters(): List<Shelter>? {
        return try {
            val locationResult = locationRepository.getCurrentLocation()
            val location = locationResult.getOrNull()

            if (location != null) {
                val sheltersResult = getNearestSheltersUseCase(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    maxResults = 10
                )
                sheltersResult.getOrNull()
            } else {
                // Location error
                android.util.Log.e("StateListViewModel", "Location error: ${locationResult.exceptionOrNull()?.message}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("StateListViewModel", "Error finding nearest shelters", e)
            null
        }
    }
}

/**
 * UI state for State List screen.
 */
sealed interface StateListUiState {
    data object Loading : StateListUiState
    data class Success(val states: List<State>) : StateListUiState
    data class Error(val message: String) : StateListUiState
}
