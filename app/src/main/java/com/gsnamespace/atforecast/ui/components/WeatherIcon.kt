package com.gsnamespace.atforecast.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.gsnamespace.atforecast.R

/**
 * Display a weather icon based on the weather description.
 * Uses Material Symbols vector drawables for weather conditions.
 */
@Composable
fun WeatherIcon(
    description: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val lowerDescription = description.lowercase()

    val iconRes = when {
        lowerDescription.contains("clear") || lowerDescription.contains("sun") -> R.drawable.sunny
        lowerDescription.contains("rain") || lowerDescription.contains("drizzle") || lowerDescription.contains("shower") -> R.drawable.rainy
        lowerDescription.contains("snow") || lowerDescription.contains("sleet") || lowerDescription.contains("ice") -> R.drawable.severe_cold
        lowerDescription.contains("fog") || lowerDescription.contains("mist") || lowerDescription.contains("haze") -> R.drawable.foggy
        lowerDescription.contains("tornado") || lowerDescription.contains("hurricane") || lowerDescription.contains("cyclone") -> R.drawable.cyclone
        lowerDescription.contains("storm") || lowerDescription.contains("thunder") -> R.drawable.thunderstorm
        lowerDescription.contains("cloud") || lowerDescription.contains("overcast") -> R.drawable.cloud
        else -> R.drawable.cloud // Default to cloud for unknown conditions
    }

    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
