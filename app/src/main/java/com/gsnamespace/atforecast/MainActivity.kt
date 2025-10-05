package com.gsnamespace.atforecast

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gsnamespace.atforecast.data.preferences.UserPreferencesRepository
import com.gsnamespace.atforecast.domain.model.ThemeMode
import com.gsnamespace.atforecast.navigation.AppNavigation
import com.gsnamespace.atforecast.ui.theme.ATForecastTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Extract deep link shelter ID if present
        val deepLinkShelterId = extractDeepLinkShelterId(intent)

        setContent {
            val themeMode by userPreferencesRepository.themeMode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.SYSTEM
            )
            val systemInDarkTheme = isSystemInDarkTheme()

            val useDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemInDarkTheme
            }

            ATForecastTheme(darkTheme = useDarkTheme) {
                AppNavigation(initialShelterId = deepLinkShelterId)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Handle deep link in a new intent (when app is already running)
        val deepLinkShelterId = extractDeepLinkShelterId(intent)
        if (deepLinkShelterId != null) {
            // Navigation will be handled by AppNavigation recomposition
            recreate()
        }
    }

    /**
     * Extract shelter ID from deep link URI.
     * Expected format: https://www.atforecast.app/shelters/{id}
     */
    private fun extractDeepLinkShelterId(intent: Intent?): Int? {
        val uri = intent?.data ?: return null
        val pathSegments = uri.pathSegments

        // Check if URL matches pattern: /shelters/{id}
        if (pathSegments.size >= 2 && pathSegments[0] == "shelters") {
            return pathSegments[1].toIntOrNull()
        }

        return null
    }
}
