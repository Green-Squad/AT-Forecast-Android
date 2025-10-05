package com.gsnamespace.atforecast.ui.statelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gsnamespace.atforecast.domain.model.State
import com.gsnamespace.atforecast.ui.components.MileageSearchDialog
import com.gsnamespace.atforecast.ui.components.RequestLocationPermission
import com.gsnamespace.atforecast.ui.components.SettingsDialog
import com.gsnamespace.atforecast.ui.components.StateImage
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.size

/**
 * State List Screen - Browse AT states with average temperatures
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateListScreen(
    onNavigateToShelters: (Int?, String, String?) -> Unit,
    onNavigateToShelter: (Int) -> Unit,
    viewModel: StateListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val currentTemperatureUnit by viewModel.currentTemperatureUnit.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var requestLocationPermission by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("ATForecast") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search by mileage"
                        )
                    }
                    IconButton(onClick = {
                        android.util.Log.d("StateListScreen", "GPS button clicked")
                        requestLocationPermission = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Find nearest shelters"
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState !is StateListUiState.Loading,
            onRefresh = { viewModel.refreshStates() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is StateListUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is StateListUiState.Success -> {
                    StateList(
                        states = state.states,
                        onStateClick = { stateId, stateName ->
                            onNavigateToShelters(stateId, stateName, null)
                        }
                    )
                }

                is StateListUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentTemperatureUnit = currentTemperatureUnit,
            currentThemeMode = currentThemeMode,
            onTemperatureUnitChange = { viewModel.setTemperatureUnit(it) },
            onThemeModeChange = { viewModel.setThemeMode(it, context) },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showSearchDialog) {
        MileageSearchDialog(
            onSearch = { mileage ->
                coroutineScope.launch {
                    val shelterId = viewModel.searchByMileage(mileage)
                    if (shelterId != null) {
                        onNavigateToShelter(shelterId)
                    }
                }
            },
            onDismiss = { showSearchDialog = false }
        )
    }

    if (requestLocationPermission) {
        android.util.Log.d("StateListScreen", "Requesting location permission")
        RequestLocationPermission(
            onPermissionGranted = {
                android.util.Log.d("StateListScreen", "Permission granted")
                requestLocationPermission = false
                coroutineScope.launch {
                    Toast.makeText(context, "Finding nearest shelter...", Toast.LENGTH_SHORT).show()
                    android.util.Log.d("StateListScreen", "Calling findNearestShelters")
                    val nearestShelters = viewModel.findNearestShelters()
                    android.util.Log.d("StateListScreen", "Found ${nearestShelters?.size ?: 0} shelters")
                    if (nearestShelters != null && nearestShelters.isNotEmpty()) {
                        // Navigate directly to the nearest shelter
                        android.util.Log.d("StateListScreen", "Navigating to shelter ${nearestShelters.first().shelterId}")
                        onNavigateToShelter(nearestShelters.first().shelterId)
                    } else {
                        Toast.makeText(context, "Could not find nearby shelters", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onPermissionDenied = {
                android.util.Log.d("StateListScreen", "Permission denied")
                requestLocationPermission = false
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun StateList(
    states: List<State>,
    onStateClick: (Int, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
    ) {
        items(states, key = { it.stateId }) { state ->
            StateListItem(
                state = state,
                onClick = { onStateClick(state.stateId, state.name) }
            )
        }
    }
}

@Composable
private fun StateListItem(
    state: State,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // State image
            StateImage(
                imageName = state.imageName,
                contentDescription = state.name,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "High: ${state.averageHigh}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Low: ${state.averageLow}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
