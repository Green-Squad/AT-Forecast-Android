package com.gsnamespace.atforecast

import android.app.Application
import android.app.UiModeManager
import android.content.SharedPreferences
import com.gsnamespace.atforecast.data.preferences.UserPreferencesRepository
import com.gsnamespace.atforecast.domain.model.ThemeMode
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // Apply theme preference before any Activity starts
        // This ensures the splash screen displays with the correct theme
        applyThemePreference()
    }

    private fun applyThemePreference() {
        val themeModeString = sharedPreferences.getString(
            UserPreferencesRepository.THEME_MODE_PREF_KEY,
            ThemeMode.SYSTEM.name
        )

        val themeMode = try {
            ThemeMode.valueOf(themeModeString ?: ThemeMode.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }

        android.util.Log.d("MyApplication", "Theme mode from SharedPreferences: $themeMode")

        val nightMode = when (themeMode) {
            ThemeMode.LIGHT -> UiModeManager.MODE_NIGHT_NO
            ThemeMode.DARK -> UiModeManager.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> UiModeManager.MODE_NIGHT_AUTO
        }

        android.util.Log.d("MyApplication", "Setting night mode: $nightMode")

        // Use UiModeManager for proper splash screen integration (minSdk is 31)
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        uiModeManager.setApplicationNightMode(nightMode)
    }
}