package com.gsnamespace.atforecast.ui.shelterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gsnamespace.atforecast.data.location.LocationRepository
import com.gsnamespace.atforecast.data.preferences.UserPreferencesRepository
import com.gsnamespace.atforecast.data.repository.ShelterRepository
import com.gsnamespace.atforecast.domain.mapper.toDomainShelters
import com.gsnamespace.atforecast.domain.model.Shelter
import com.gsnamespace.atforecast.domain.model.TemperatureUnit
import com.gsnamespace.atforecast.domain.model.ThemeMode
import com.gsnamespace.atforecast.domain.usecase.GetNearestSheltersUseCase
import com.gsnamespace.atforecast.domain.usecase.SearchShelterByMileageUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Shelter List screen.
 * Displays shelters for a specific state.
 */
@HiltViewModel(assistedFactory = ShelterListViewModel.Factory::class)
class ShelterListViewModel @AssistedInject constructor(
    private val shelterRepository: ShelterRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val searchShelterByMileageUseCase: SearchShelterByMileageUseCase,
    private val getNearestSheltersUseCase: GetNearestSheltersUseCase,
    private val locationRepository: LocationRepository,
    @Assisted("stateId") private val stateId: Int?,
    @Assisted("stateName") val stateName: String,
    @Assisted("shelterIds") private val shelterIds: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShelterListUiState>(ShelterListUiState.Loading)
    val uiState: StateFlow<ShelterListUiState> = _uiState.asStateFlow()

    val currentTemperatureUnit: StateFlow<TemperatureUnit> = userPreferencesRepository.temperatureUnit
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TemperatureUnit.FAHRENHEIT
        )

    val currentThemeMode: StateFlow<ThemeMode> = userPreferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("stateId") stateId: Int?,
            @Assisted("stateName") stateName: String,
            @Assisted("shelterIds") shelterIds: String?
        ): ShelterListViewModel
    }

    init {
        loadShelters()
    }

    /**
     * Load shelters for the state or specific shelter IDs.
     */
    private fun loadShelters() {
        viewModelScope.launch {
            if (shelterIds != null) {
                // Load specific shelters by IDs (nearest shelters mode)
                loadSpecificShelters(shelterIds)
            } else if (stateId != null) {
                // Load shelters by state
                shelterRepository.getSheltersByState(stateId)
                    .catch { exception ->
                        _uiState.value = ShelterListUiState.Error(exception.message ?: "Unknown error")
                    }
                    .collect { shelters ->
                        _uiState.value = ShelterListUiState.Success(
                            shelters = shelters.toDomainShelters()
                        )
                    }
            }
        }
    }

    /**
     * Load specific shelters by IDs (for nearest shelters).
     */
    private suspend fun loadSpecificShelters(shelterIdsString: String) {
        try {
            val shelterIds = shelterIdsString.split(",").mapNotNull { it.toIntOrNull() }
            val shelters = shelterIds.mapNotNull { shelterId ->
                shelterRepository.getShelterById(shelterId)
            }
            _uiState.value = ShelterListUiState.Success(
                shelters = shelters.toDomainShelters()
            )
        } catch (e: Exception) {
            _uiState.value = ShelterListUiState.Error(e.message ?: "Failed to load shelters")
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
                android.util.Log.e("ShelterListViewModel", "Location error: ${locationResult.exceptionOrNull()?.message}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("ShelterListViewModel", "Error finding nearest shelters", e)
            null
        }
    }
}

/**
 * UI state for Shelter List screen.
 */
sealed interface ShelterListUiState {
    data object Loading : ShelterListUiState
    data class Success(val shelters: List<Shelter>) : ShelterListUiState
    data class Error(val message: String) : ShelterListUiState
}
