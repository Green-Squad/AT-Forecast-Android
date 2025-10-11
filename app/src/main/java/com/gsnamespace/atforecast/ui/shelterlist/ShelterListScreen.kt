package com.gsnamespace.atforecast.ui.shelterlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gsnamespace.atforecast.R
import com.gsnamespace.atforecast.domain.model.Shelter
import com.gsnamespace.atforecast.ui.components.DistanceUnitDialog
import com.gsnamespace.atforecast.ui.components.MileageSearchDialog
import com.gsnamespace.atforecast.ui.components.RequestLocationPermission
import com.gsnamespace.atforecast.ui.components.TemperatureUnitDialog
import com.gsnamespace.atforecast.ui.components.ThemeModeDialog
import kotlinx.coroutines.launch

/**
 * Shelter List Screen - Browse shelters for a specific state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterListScreen(
    stateId: Int?,
    stateName: String,
    shelterIds: String?,
    onNavigateBack: () -> Unit,
    onNavigateToShelterDetail: (Int) -> Unit,
    viewModel: ShelterListViewModel = hiltViewModel(
        creationCallback = { factory: ShelterListViewModel.Factory ->
            factory.create(stateId, stateName, shelterIds)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                            Text(viewModel.stateName)
                        }

                        if (showSearchField) {
                            androidx.compose.material3.TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(R.string.search_placeholder),
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
                                        val mileage = searchQuery.toDoubleOrNull()
                                        if (mileage != null) {
                                            coroutineScope.launch {
                                                val shelterId = viewModel.searchByMileage(mileage)
                                                if (shelterId != null) {
                                                    onNavigateToShelterDetail(shelterId)
                                                    isSearchActive = false
                                                    showSearchField = false
                                                    searchQuery = ""
                                                } else {
                                                    Toast.makeText(context, "No shelter found at mile $mileage", Toast.LENGTH_SHORT).show()
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
                        visible = !isSearchActive,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close search"
                            )
                        }
                    }
                },
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
                            IconButton(onClick = { requestLocationPermission = true }) {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ShelterListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ShelterListUiState.Success -> {
                    if (state.shelters.isEmpty()) {
                        Text(
                            text = "No shelters found",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        ShelterList(
                            shelters = state.shelters,
                            distanceUnit = currentDistanceUnit,
                            onShelterClick = onNavigateToShelterDetail
                        )
                    }
                }

                is ShelterListUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
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
        RequestLocationPermission(
            onPermissionGranted = {
                requestLocationPermission = false
                coroutineScope.launch {
                    Toast.makeText(context, "Finding nearest shelter...", Toast.LENGTH_SHORT).show()
                    val nearestShelters = viewModel.findNearestShelters()
                    if (nearestShelters != null && nearestShelters.isNotEmpty()) {
                        // Navigate to first nearest shelter
                        onNavigateToShelterDetail(nearestShelters.first().shelterId)
                    } else {
                        Toast.makeText(context, "Could not find nearby shelters", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onPermissionDenied = {
                requestLocationPermission = false
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ShelterList(
    shelters: List<Shelter>,
    distanceUnit: com.gsnamespace.atforecast.domain.model.DistanceUnit,
    onShelterClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
    ) {
        items(shelters, key = { it.shelterId }) { shelter ->
            ShelterListItem(
                shelter = shelter,
                distanceUnit = distanceUnit,
                onClick = { onShelterClick(shelter.shelterId) }
            )
        }
    }
}

@Composable
private fun ShelterListItem(
    shelter: Shelter,
    distanceUnit: com.gsnamespace.atforecast.domain.model.DistanceUnit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = shelter.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                val distanceValue = com.gsnamespace.atforecast.domain.util.UnitConverter.formatDistance(shelter.mileage, distanceUnit)
                val distanceLabel = com.gsnamespace.atforecast.domain.util.UnitConverter.getDistanceUnitLabel(distanceUnit)
                Text(
                    text = "$distanceValue $distanceLabel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                shelter.elevation?.let { elevation ->
                    Spacer(modifier = Modifier.width(16.dp))
                    val elevationValue = com.gsnamespace.atforecast.domain.util.UnitConverter.formatElevation(elevation, distanceUnit)
                    val elevationLabel = com.gsnamespace.atforecast.domain.util.UnitConverter.getElevationUnitLabel(distanceUnit)
                    Text(
                        text = "Elevation: $elevationValue $elevationLabel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
