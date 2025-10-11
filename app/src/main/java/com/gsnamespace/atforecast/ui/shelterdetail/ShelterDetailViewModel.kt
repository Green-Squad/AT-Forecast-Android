package com.gsnamespace.atforecast.ui.shelterdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gsnamespace.atforecast.data.repository.ShelterRepository
import com.gsnamespace.atforecast.domain.model.DistanceUnit
import com.gsnamespace.atforecast.domain.model.ShelterWithWeather
import com.gsnamespace.atforecast.domain.model.TemperatureUnit
import com.gsnamespace.atforecast.domain.model.ThemeMode
import com.gsnamespace.atforecast.domain.usecase.GetShelterWithWeatherUseCase
import com.gsnamespace.atforecast.domain.usecase.RefreshWeatherIfStaleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Shelter Detail screen.
 * Displays shelter information with 7-day weather forecast.
 */
@HiltViewModel(assistedFactory = ShelterDetailViewModel.Factory::class)
class ShelterDetailViewModel @AssistedInject constructor(
    private val getShelterWithWeatherUseCase: GetShelterWithWeatherUseCase,
    private val refreshWeatherIfStaleUseCase: RefreshWeatherIfStaleUseCase,
    private val shelterRepository: ShelterRepository,
    private val userPreferencesRepository: com.gsnamespace.atforecast.data.preferences.UserPreferencesRepository,
    @Assisted private val shelterId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShelterDetailUiState>(ShelterDetailUiState.Loading)
    val uiState: StateFlow<ShelterDetailUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _hasPreviousShelter = MutableStateFlow(false)
    val hasPreviousShelter: StateFlow<Boolean> = _hasPreviousShelter.asStateFlow()

    private val _hasNextShelter = MutableStateFlow(false)
    val hasNextShelter: StateFlow<Boolean> = _hasNextShelter.asStateFlow()

    private val _previousShelterDistance = MutableStateFlow<Double?>(null)
    val previousShelterDistance: StateFlow<Double?> = _previousShelterDistance.asStateFlow()

    private val _nextShelterDistance = MutableStateFlow<Double?>(null)
    val nextShelterDistance: StateFlow<Double?> = _nextShelterDistance.asStateFlow()

    private val temperatureUnit = userPreferencesRepository.temperatureUnit
    private var hasTriggeredAutoRefresh = false

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

    @AssistedFactory
    interface Factory {
        fun create(shelterId: Int): ShelterDetailViewModel
    }

    init {
        loadShelterWithWeather()
        checkNavigationAvailability()
    }

    /**
     * Check if previous/next shelters are available and calculate distances.
     */
    private fun checkNavigationAvailability() {
        viewModelScope.launch {
            val currentShelter = shelterRepository.getShelterById(shelterId)

            val previousShelter = shelterRepository.getPreviousShelter(shelterId)
            _hasPreviousShelter.value = previousShelter != null

            // Calculate distance to previous shelter if available
            if (previousShelter != null && currentShelter != null) {
                _previousShelterDistance.value = currentShelter.mileage - previousShelter.mileage
            }

            val nextShelter = shelterRepository.getNextShelter(shelterId)
            _hasNextShelter.value = nextShelter != null

            // Calculate distance to next shelter if available
            if (nextShelter != null && currentShelter != null) {
                _nextShelterDistance.value = nextShelter.mileage - currentShelter.mileage
            }
        }
    }

    /**
     * Load shelter with weather data.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadShelterWithWeather() {
        viewModelScope.launch {
            temperatureUnit
                .flatMapLatest { unit ->
                    getShelterWithWeatherUseCase(shelterId, unit)
                }
                .catch { exception ->
                    android.util.Log.e("ShelterDetailViewModel", "Error loading shelter", exception)
                    _uiState.value = ShelterDetailUiState.Error(exception.message ?: "Unknown error")
                }
                .collect { shelterWithWeather ->
                    if (shelterWithWeather != null) {
                        android.util.Log.d("ShelterDetailViewModel", "Loaded shelter with ${shelterWithWeather.dailyWeather.size} daily forecasts")
                        _uiState.value = ShelterDetailUiState.Success(shelterWithWeather)

                        // If no weather data, trigger a refresh (only once, and not during manual refresh)
                        if (shelterWithWeather.dailyWeather.isEmpty() &&
                            !hasTriggeredAutoRefresh &&
                            !_isRefreshing.value) {
                            android.util.Log.d("ShelterDetailViewModel", "No weather data, triggering refresh")
                            hasTriggeredAutoRefresh = true
                            refreshWeather()
                        }
                    } else {
                        _uiState.value = ShelterDetailUiState.Error("Shelter not found")
                    }
                }
        }
    }

    /**
     * Manually refresh weather data.
     */
    fun refreshWeather() {
        viewModelScope.launch {
            android.util.Log.d("ShelterDetailViewModel", "Refreshing weather for shelter $shelterId")
            _isRefreshing.value = true
            try {
                // Match old app: Make two API calls
                // 1. Fast call for current shelter (updates UI)
                // 2. Background call for 100-mile radius (populates cache)
                refreshWeatherIfStaleUseCase(
                    shelterId = shelterId,
                    forceRefresh = true,
                    prefetchInBackground = true
                )
                android.util.Log.d("ShelterDetailViewModel", "Weather refresh completed")
            } catch (e: Exception) {
                android.util.Log.e("ShelterDetailViewModel", "Weather refresh failed", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * Navigate to previous shelter by mileage.
     */
    suspend fun navigateToPrevious(): Int? {
        val previous = shelterRepository.getPreviousShelter(shelterId)
        return previous?.shelterId
    }

    /**
     * Navigate to next shelter by mileage.
     */
    suspend fun navigateToNext(): Int? {
        val next = shelterRepository.getNextShelter(shelterId)
        return next?.shelterId
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
}

/**
 * UI state for Shelter Detail screen.
 */
sealed interface ShelterDetailUiState {
    data object Loading : ShelterDetailUiState
    data class Success(val shelterWithWeather: ShelterWithWeather) : ShelterDetailUiState
    data class Error(val message: String) : ShelterDetailUiState
}
