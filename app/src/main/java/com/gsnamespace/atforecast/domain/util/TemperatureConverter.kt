package com.gsnamespace.atforecast.domain.util

import com.gsnamespace.atforecast.domain.model.TemperatureUnit

/**
 * Utility object for temperature conversions.
 */
object TemperatureConverter {

    /**
     * Convert temperature from Fahrenheit to Celsius.
     */
    fun fahrenheitToCelsius(fahrenheit: Int): Int {
        return ((fahrenheit - 32) * 5.0 / 9.0).toInt()
    }

    /**
     * Convert temperature from Celsius to Fahrenheit.
     */
    fun celsiusToFahrenheit(celsius: Int): Int {
        return (celsius * 9.0 / 5.0 + 32).toInt()
    }

    /**
     * Convert temperature based on user preference.
     * All temperatures in the database are stored in Fahrenheit.
     * This function converts to the target unit if needed.
     *
     * @param temperatureF Temperature in Fahrenheit (from database)
     * @param targetUnit Target temperature unit
     * @return Temperature converted to target unit
     */
    fun convertTemperature(temperatureF: Int, targetUnit: TemperatureUnit): Int {
        return when (targetUnit) {
            TemperatureUnit.FAHRENHEIT -> temperatureF
            TemperatureUnit.CELSIUS -> fahrenheitToCelsius(temperatureF)
        }
    }
}
