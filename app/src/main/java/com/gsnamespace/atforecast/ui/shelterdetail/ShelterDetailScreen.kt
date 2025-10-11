package com.gsnamespace.atforecast.ui.shelterdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import kotlinx.coroutines.launch
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
import androidx.compose.foundation.layout.size
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gsnamespace.atforecast.domain.model.DailyWeather
import com.gsnamespace.atforecast.domain.model.HourlyWeather
import com.gsnamespace.atforecast.ui.components.DistanceUnitDialog
import com.gsnamespace.atforecast.ui.components.TemperatureUnitDialog
import com.gsnamespace.atforecast.ui.components.ThemeModeDialog
import com.gsnamespace.atforecast.ui.components.WeatherIcon
import com.gsnamespace.atforecast.domain.model.Shelter
import com.gsnamespace.atforecast.domain.model.ShelterWithWeather
import com.gsnamespace.atforecast.domain.util.DateFormatter

/**
 * Shelter Detail Screen - View shelter info and 7-day weather forecast
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterDetailScreen(
    shelterId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToShelter: (Int) -> Unit = {},
    viewModel: ShelterDetailViewModel = hiltViewModel(
        creationCallback = { factory: ShelterDetailViewModel.Factory ->
            factory.create(shelterId)
        }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val hasPreviousShelter by viewModel.hasPreviousShelter.collectAsStateWithLifecycle()
    val hasNextShelter by viewModel.hasNextShelter.collectAsStateWithLifecycle()
    val previousShelterDistance by viewModel.previousShelterDistance.collectAsStateWithLifecycle()
    val nextShelterDistance by viewModel.nextShelterDistance.collectAsStateWithLifecycle()
    val currentTemperatureUnit by viewModel.currentTemperatureUnit.collectAsStateWithLifecycle()
    val currentDistanceUnit by viewModel.currentDistanceUnit.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showTemperatureDialog by remember { mutableStateOf(false) }
    var showDistanceDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val state = uiState) {
                            is ShelterDetailUiState.Success -> state.shelterWithWeather.shelter.name
                            else -> "Shelter Details"
                        },
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
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
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState !is ShelterDetailUiState.Loading,
            onRefresh = { viewModel.refreshWeather() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ShelterDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ShelterDetailUiState.Success -> {
                    ShelterDetailContent(
                        shelterWithWeather = state.shelterWithWeather,
                        distanceUnit = currentDistanceUnit,
                        previousShelterDistance = previousShelterDistance,
                        nextShelterDistance = nextShelterDistance,
                        hasPreviousShelter = hasPreviousShelter,
                        hasNextShelter = hasNextShelter,
                        onNavigateToPrevious = {
                            coroutineScope.launch {
                                val previousShelterId = viewModel.navigateToPrevious()
                                previousShelterId?.let { onNavigateToShelter(it) }
                            }
                        },
                        onNavigateToNext = {
                            coroutineScope.launch {
                                val nextShelterId = viewModel.navigateToNext()
                                nextShelterId?.let { onNavigateToShelter(it) }
                            }
                        }
                    )
                }

                is ShelterDetailUiState.Error -> {
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
}

@Composable
private fun ShelterDetailContent(
    shelterWithWeather: ShelterWithWeather,
    distanceUnit: com.gsnamespace.atforecast.domain.model.DistanceUnit,
    previousShelterDistance: Double?,
    nextShelterDistance: Double?,
    hasPreviousShelter: Boolean,
    hasNextShelter: Boolean,
    onNavigateToPrevious: () -> Unit,
    onNavigateToNext: () -> Unit
) {
    var expandedCardIndex by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Navigation buttons
        item {
            NavigationButtons(
                hasPrevious = hasPreviousShelter,
                hasNext = hasNextShelter,
                previousDistance = previousShelterDistance,
                nextDistance = nextShelterDistance,
                distanceUnit = distanceUnit,
                onPreviousClick = onNavigateToPrevious,
                onNextClick = onNavigateToNext
            )
        }

        // Shelter info header
        item {
            ShelterInfoCard(
                shelter = shelterWithWeather.shelter,
                distanceUnit = distanceUnit,
                lastUpdated = shelterWithWeather.lastUpdated
            )
        }

        // Weather forecast section
        item {
            Text(
                text = "7-Day Forecast",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Daily weather cards
        itemsIndexed(shelterWithWeather.dailyWeather, key = { _, item -> item.dailyWeatherId }) { index, dailyWeather ->
            DailyWeatherCard(
                dailyWeather = dailyWeather,
                distanceUnit = distanceUnit,
                expanded = expandedCardIndex == index,
                onExpandChange = { expandedCardIndex = index }
            )
        }

        // Empty state if no weather
        if (shelterWithWeather.dailyWeather.isEmpty()) {
            item {
                Text(
                    text = "No weather data available. Pull to refresh.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ShelterInfoCard(
    shelter: Shelter,
    distanceUnit: com.gsnamespace.atforecast.domain.model.DistanceUnit,
    lastUpdated: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    val distanceValue = com.gsnamespace.atforecast.domain.util.UnitConverter.formatDistance(shelter.mileage, distanceUnit)
                    val distanceLabel = when (distanceUnit) {
                        com.gsnamespace.atforecast.domain.model.DistanceUnit.IMPERIAL -> "Mile"
                        com.gsnamespace.atforecast.domain.model.DistanceUnit.METRIC -> "Kilometer"
                    }
                    InfoRow(label = distanceLabel, value = distanceValue)
                }
                shelter.elevation?.let { elevation ->
                    Column(modifier = Modifier.weight(1f)) {
                        val elevationValue = com.gsnamespace.atforecast.domain.util.UnitConverter.formatElevation(elevation, distanceUnit)
                        val elevationLabel = com.gsnamespace.atforecast.domain.util.UnitConverter.getElevationUnitLabel(distanceUnit)
                        InfoRow(label = "Elevation", value = "$elevationValue $elevationLabel")
                    }
                }
            }

            // Only show last updated timestamp if weather data exists
            if (lastUpdated > 0) {
                val relativeTime = android.text.format.DateUtils.getRelativeTimeSpanString(
                    lastUpdated,
                    System.currentTimeMillis(),
                    android.text.format.DateUtils.MINUTE_IN_MILLIS
                ).toString()

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Last updated $relativeTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DailyWeatherCard(
    dailyWeather: DailyWeather,
    distanceUnit: com.gsnamespace.atforecast.domain.model.DistanceUnit,
    expanded: Boolean,
    onExpandChange: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onExpandChange() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Daily summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Weather icon
                WeatherIcon(
                    description = dailyWeather.description,
                    contentDescription = dailyWeather.description,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = DateFormatter.formatDailyDate(dailyWeather.weatherDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dailyWeather.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "H: ${dailyWeather.high}°",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "L: ${dailyWeather.low}°",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            // Hourly weather (expandable)
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Hourly Forecast",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    dailyWeather.hourlyWeather.forEach { hourly ->
                        HourlyWeatherRow(
                            hourlyWeather = hourly,
                            distanceUnit = distanceUnit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (dailyWeather.hourlyWeather.isEmpty()) {
                        Text(
                            text = "No hourly data available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyWeatherRow(
    hourlyWeather: HourlyWeather,
    distanceUnit: com.gsnamespace.atforecast.domain.model.DistanceUnit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeatherIcon(
            description = hourlyWeather.description,
            contentDescription = hourlyWeather.description,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = DateFormatter.formatHourlyTime(hourlyWeather.date),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Column(modifier = Modifier.weight(1.5f)) {
            Text(
                text = hourlyWeather.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val windSpeed = com.gsnamespace.atforecast.domain.util.UnitConverter.formatWindSpeed(hourlyWeather.wind, distanceUnit)
            Text(
                text = windSpeed,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Text(
            text = "${hourlyWeather.temp}°",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun NavigationButtons(
    hasPrevious: Boolean,
    hasNext: Boolean,
    previousDistance: Double?,
    nextDistance: Double?,
    distanceUnit: com.gsnamespace.atforecast.domain.model.DistanceUnit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (hasPrevious) {
                Button(
                    onClick = onPreviousClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous shelter"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (previousDistance != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SOBO")
                            val distanceValue = com.gsnamespace.atforecast.domain.util.UnitConverter.formatDistance(previousDistance, distanceUnit)
                            val distanceLabel = com.gsnamespace.atforecast.domain.util.UnitConverter.getDistanceUnitLabel(distanceUnit)
                            Text("$distanceValue $distanceLabel")
                        }
                    } else {
                        Text("Previous")
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            if (hasNext) {
                Button(
                    onClick = onNextClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (nextDistance != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("NOBO")
                            val distanceValue = com.gsnamespace.atforecast.domain.util.UnitConverter.formatDistance(nextDistance, distanceUnit)
                            val distanceLabel = com.gsnamespace.atforecast.domain.util.UnitConverter.getDistanceUnitLabel(distanceUnit)
                            Text("$distanceValue $distanceLabel")
                        }
                    } else {
                        Text("Next")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next shelter"
                    )
                }
            }
        }
    }
}
