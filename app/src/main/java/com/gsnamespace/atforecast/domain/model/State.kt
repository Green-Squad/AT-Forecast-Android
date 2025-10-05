package com.gsnamespace.atforecast.domain.model

/**
 * Domain model for a US state along the Appalachian Trail.
 * This is the model used in the presentation layer.
 */
data class State(
    val stateId: Int,
    val name: String,
    val averageHigh: Int,  // Temperature (already converted to user's preferred unit)
    val averageLow: Int,   // Temperature (already converted to user's preferred unit)
    val imageName: String  // Derived from name for resource lookup
) {
    companion object {
        /**
         * Convert state name to image resource name.
         * Example: "New York" -> "new_york"
         */
        fun getImageName(name: String): String {
            return name.replace(" ", "_").lowercase()
        }
    }
}
