package com.gsnamespace.atforecast.data.preferences

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gsnamespace.atforecast.domain.model.DistanceUnit
import com.gsnamespace.atforecast.domain.model.TemperatureUnit
import com.gsnamespace.atforecast.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user preferences using DataStore.
 * Theme preference is also stored in SharedPreferences for synchronous access during app startup.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")
        private val DISTANCE_UNIT_KEY = stringPreferencesKey("distance_unit")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        const val THEME_MODE_PREF_KEY = "theme_mode"
    }

    /**
     * Get the current temperature unit preference.
     */
    val temperatureUnit: Flow<TemperatureUnit> = dataStore.data.map { preferences ->
        val unitString = preferences[TEMPERATURE_UNIT_KEY] ?: TemperatureUnit.FAHRENHEIT.name
        try {
            TemperatureUnit.valueOf(unitString)
        } catch (e: IllegalArgumentException) {
            TemperatureUnit.FAHRENHEIT
        }
    }

    /**
     * Get the current distance unit preference.
     */
    val distanceUnit: Flow<DistanceUnit> = dataStore.data.map { preferences ->
        val unitString = preferences[DISTANCE_UNIT_KEY] ?: DistanceUnit.IMPERIAL.name
        try {
            DistanceUnit.valueOf(unitString)
        } catch (e: IllegalArgumentException) {
            DistanceUnit.IMPERIAL
        }
    }

    /**
     * Get the current theme mode preference.
     */
    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        val modeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(modeString)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    /**
     * Set the temperature unit preference.
     */
    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] = unit.name
        }
    }

    /**
     * Set the distance unit preference.
     */
    suspend fun setDistanceUnit(unit: DistanceUnit) {
        dataStore.edit { preferences ->
            preferences[DISTANCE_UNIT_KEY] = unit.name
        }
    }

    /**
     * Set the theme mode preference.
     * Writes to both DataStore (for Flow) and SharedPreferences (for early startup access).
     * Also updates UiModeManager immediately so the next app launch uses the correct splash screen theme.
     */
    suspend fun setThemeMode(mode: ThemeMode, context: android.content.Context) {
        // Write to DataStore for Flow-based observation
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }

        // Write to SharedPreferences for synchronous startup access
        sharedPreferences.edit().putString(THEME_MODE_PREF_KEY, mode.name).apply()

        // Update UiModeManager immediately so it takes effect on next app restart
        val nightMode = when (mode) {
            ThemeMode.LIGHT -> android.app.UiModeManager.MODE_NIGHT_NO
            ThemeMode.DARK -> android.app.UiModeManager.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> android.app.UiModeManager.MODE_NIGHT_AUTO
        }
        val uiModeManager = context.getSystemService(android.content.Context.UI_MODE_SERVICE) as android.app.UiModeManager
        uiModeManager.setApplicationNightMode(nightMode)
    }
}
