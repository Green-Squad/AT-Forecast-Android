package com.gsnamespace.atforecast.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

/**
 * Navigation routes for ATForecast app.
 */

/**
 * State list screen - Browse states along the AT
 */
@Serializable
data object StateListRoute : NavKey

/**
 * Shelter list screen - Browse shelters for a specific state
 * @param stateId The state ID (null for nearest shelters mode)
 * @param stateName The state name for display
 * @param shelterIds Comma-separated list of shelter IDs for nearest shelters mode
 */
@Serializable
data class ShelterListRoute(
    val stateId: Int?,
    val stateName: String,
    val shelterIds: String? = null
) : NavKey

/**
 * Shelter detail screen - View shelter info and weather forecast
 * @param shelterId The shelter ID
 */
@Serializable
data class ShelterDetailRoute(
    val shelterId: Int
) : NavKey

/**
 * Main navigation setup for the app.
 * Implements Navigation 3 with type-safe routes and ViewModel scoping.
 *
 * @param initialShelterId Optional shelter ID from deep link to navigate directly to shelter detail
 */
@Composable
fun AppNavigation(initialShelterId: Int? = null) {
    val initialRoute: NavKey = if (initialShelterId != null) {
        ShelterDetailRoute(initialShelterId)
    } else {
        StateListRoute
    }

    val backStack = rememberNavBackStack(initialRoute)

    NavDisplay(
        entryDecorators = listOf(
            // Scene management
            rememberSceneSetupNavEntryDecorator(),
            // State preservation
            rememberSavedStateNavEntryDecorator(),
            // ViewModel lifecycle tied to nav entries
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<StateListRoute> {
                com.gsnamespace.atforecast.ui.statelist.StateListScreen(
                    onNavigateToShelters = { stateId, stateName, shelterIds ->
                        backStack.add(ShelterListRoute(stateId, stateName, shelterIds))
                    },
                    onNavigateToShelter = { shelterId ->
                        backStack.add(ShelterDetailRoute(shelterId))
                    }
                )
            }

            entry<ShelterListRoute> { route ->
                com.gsnamespace.atforecast.ui.shelterlist.ShelterListScreen(
                    stateId = route.stateId,
                    stateName = route.stateName,
                    shelterIds = route.shelterIds,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToShelterDetail = { shelterId ->
                        backStack.add(ShelterDetailRoute(shelterId))
                    }
                )
            }

            entry<ShelterDetailRoute> { route ->
                com.gsnamespace.atforecast.ui.shelterdetail.ShelterDetailScreen(
                    shelterId = route.shelterId,
                    onNavigateBack = {
                        // Pop all shelter detail screens until we get back to the shelter list
                        while (backStack.lastOrNull() is ShelterDetailRoute) {
                            backStack.removeLastOrNull()
                        }
                    },
                    onNavigateToShelter = { shelterId ->
                        backStack.add(ShelterDetailRoute(shelterId))
                    }
                )
            }
        }
    )
}
