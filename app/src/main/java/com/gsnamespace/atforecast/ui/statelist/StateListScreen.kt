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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.foundation.layout.height
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gsnamespace.atforecast.domain.model.State
import com.gsnamespace.atforecast.ui.components.DistanceUnitDialog
import com.gsnamespace.atforecast.ui.components.MileageSearchDialog
import com.gsnamespace.atforecast.ui.components.RequestLocationPermission
import com.gsnamespace.atforecast.ui.components.StateImage
import com.gsnamespace.atforecast.ui.components.TemperatureUnitDialog
import com.gsnamespace.atforecast.ui.components.ThemeModeDialog
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.gsnamespace.atforecast.R

/**
 * State List Screen - Browse AT states with average temperatures
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateListScreen(
    onNavigateToShelters: (Int?, String, String?) -> Unit,
    onNavigateToShelter: (Int) -> Unit,
    triggerNearestShelter: Boolean = false,
    viewModel: StateListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val currentTemperatureUnit by viewModel.currentTemperatureUnit.collectAsStateWithLifecycle()
    val currentDistanceUnit by viewModel.currentDistanceUnit.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showTemperatureDialog by remember { mutableStateOf(false) }
    var showDistanceDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var showSearchField by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var requestLocationPermission by remember { mutableStateOf(false) }
    var hasTriggeredShortcut by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Trigger nearest shelter search once on initial composition if requested
    androidx.compose.runtime.LaunchedEffect(triggerNearestShelter) {
        if (triggerNearestShelter && !hasTriggeredShortcut) {
            hasTriggeredShortcut = true
            requestLocationPermission = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !isSearchActive,
                            exit = androidx.compose.animation.slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = androidx.compose.animation.core.tween(durationMillis = 150)
                            ) + androidx.compose.animation.fadeOut(
                                animationSpec = androidx.compose.animation.core.tween(durationMillis = 150)
                            )
                        ) {
                            Text("ATForecast")
                        }

                        if (showSearchField) {
                            androidx.compose.material3.TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    val placeholderRes = when (currentDistanceUnit) {
                                        com.gsnamespace.atforecast.domain.model.DistanceUnit.IMPERIAL -> R.string.search_placeholder_miles
                                        com.gsnamespace.atforecast.domain.model.DistanceUnit.METRIC -> R.string.search_placeholder_kilometers
                                    }
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(placeholderRes),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                                ),
                                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                    onSearch = {
                                        val inputValue = searchQuery.toDoubleOrNull()
                                        if (inputValue != null) {
                                            // Convert km to miles if in metric mode
                                            val mileage = when (currentDistanceUnit) {
                                                com.gsnamespace.atforecast.domain.model.DistanceUnit.IMPERIAL -> inputValue
                                                com.gsnamespace.atforecast.domain.model.DistanceUnit.METRIC -> inputValue / 1.60934
                                            }
                                            coroutineScope.launch {
                                                val shelterId = viewModel.searchByMileage(mileage)
                                                if (shelterId != null) {
                                                    onNavigateToShelter(shelterId)
                                                    isSearchActive = false
                                                    showSearchField = false
                                                    searchQuery = ""
                                                } else {
                                                    val unit = when (currentDistanceUnit) {
                                                        com.gsnamespace.atforecast.domain.model.DistanceUnit.IMPERIAL -> "mile"
                                                        com.gsnamespace.atforecast.domain.model.DistanceUnit.METRIC -> "kilometer"
                                                    }
                                                    Toast.makeText(context, "No shelter found at $unit $inputValue", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                ),
                                colors = androidx.compose.material3.TextFieldDefaults.colors(
                                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                navigationIcon = {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isSearchActive,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
                    ) {
                        IconButton(onClick = {
                            isSearchActive = false
                            showSearchField = false
                            searchQuery = ""
                        }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close search"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isSearchActive,
                        exit = androidx.compose.animation.slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 150)
                        ) + androidx.compose.animation.fadeOut(
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 150)
                        )
                    ) {
                        Row {
                            IconButton(onClick = {
                                isSearchActive = true
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(300)
                                    showSearchField = true
                                }
                            }) {
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
                            Box {
                                IconButton(onClick = { showOverflowMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More options"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showOverflowMenu,
                                    onDismissRequest = { showOverflowMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Temperature Unit") },
                                        onClick = {
                                            showOverflowMenu = false
                                            showTemperatureDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Distance Unit") },
                                        onClick = {
                                            showOverflowMenu = false
                                            showDistanceDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Theme") },
                                        onClick = {
                                            showOverflowMenu = false
                                            showThemeDialog = true
                                        }
                                    )
                                }
                            }
                        }
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

    if (showTemperatureDialog) {
        TemperatureUnitDialog(
            currentUnit = currentTemperatureUnit,
            onUnitChange = { viewModel.setTemperatureUnit(it) },
            onDismiss = { showTemperatureDialog = false }
        )
    }

    if (showDistanceDialog) {
        DistanceUnitDialog(
            currentUnit = currentDistanceUnit,
            onUnitChange = { viewModel.setDistanceUnit(it) },
            onDismiss = { showDistanceDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeModeDialog(
            currentMode = currentThemeMode,
            onModeChange = { viewModel.setThemeMode(it, context) },
            onDismiss = { showThemeDialog = false }
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
