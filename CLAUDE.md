# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ATForecast is an Android application built using Jetpack Compose with Material 3 expressive features. The project is currently in active development and uses experimental/alpha versions of AndroidX Navigation 3 libraries to leverage the latest navigation patterns.

**Package name**: `com.gsnamespace.atforecast`

## Key Technologies

- **Jetpack Compose** with alpha version of Compose BOM for Material 3 expressive features
- **Navigation 3**: Using the experimental Navigation 3 library (alpha-10) with type-safe navigation via Kotlin serialization
- **Hilt**: Dependency injection framework
- **MVVM Architecture**: ViewModels scoped to navigation entries using `rememberViewModelStoreNavEntryDecorator`
- **Room**: Local database for offline-first data persistence
- **Retrofit + Gson**: Network layer for API communication
- **Coil**: Image loading for state and shelter images
- **DataStore + SharedPreferences**: User preferences (dual storage for theme to support splash screen)
- **Material Symbols**: Icon library for weather and UI elements

## Build Commands

```bash
# Build the app
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests com.gsnamespace.atforecast.ExampleUnitTest

# Assemble debug APK
./gradlew assembleDebug

# Install debug APK on connected device
./gradlew installDebug
```

## Architecture

### Navigation Setup

The app uses **Navigation 3** (experimental alpha library) with a declarative navigation pattern:

- **Navigation routes** are defined as serializable data objects in `AppNavigation.kt` (e.g., `HomeRoute`, `DetailsRoute`)
- Navigation is managed via `rememberNavBackStack()` which maintains the back stack
- `NavDisplay` composable renders the current destination with three critical decorators applied in order:
  1. `rememberSceneSetupNavEntryDecorator()` - Scene management
  2. `rememberSavedStateNavEntryDecorator()` - State preservation
  3. `rememberViewModelStoreNavEntryDecorator()` - ViewModel lifecycle management tied to nav entries
- Screen composables are registered using `entryProvider { entry<Route> { ... } }`

**Important**: The order of decorators in `NavDisplay` matters. ViewModelStore decorator must come after the other decorators to ensure proper ViewModel scoping to navigation entries.

### Dependency Injection

- `MyApplication.kt` is annotated with `@HiltAndroidApp` and handles theme initialization on startup
- `MainActivity.kt` is annotated with `@AndroidEntryPoint` and configured with `android:configChanges="uiMode"` to prevent recreation on theme change
- ViewModels are annotated with `@HiltViewModel` and injected via `hiltViewModel()` in composables
- Custom Hilt modules in `di/` package:
  - `DatabaseModule`: Provides Room database and DAOs
  - `NetworkModule`: Provides Retrofit and API service
  - `DataStoreModule`: Provides DataStore and SharedPreferences for user preferences
  - `LocationModule`: Provides FusedLocationProviderClient for GPS features

### Screen Structure

Each feature follows a consistent pattern:
- **Screen composable**: UI layer (e.g., `StateListScreen.kt`, `ShelterDetailScreen.kt`)
- **ViewModel**: Business logic layer (e.g., `StateListViewModel.kt`, `ShelterDetailViewModel.kt`)
- **UiState**: Sealed class/interface for representing screen state (Loading, Success, Error)
- ViewModels are automatically scoped to their navigation entry lifecycle

Main screens:
- `StateListScreen`: Browse AT states with average temperatures (home screen)
- `ShelterListScreen`: Browse shelters for a specific state
- `ShelterDetailScreen`: View shelter info with 7-day weather forecast

## Important Configuration Details

### AndroidX Snapshot Repository

The project uses a specific AndroidX snapshot build for experimental Navigation 3 features:

```kotlin
maven {
    url = uri("https://androidx.dev/snapshots/builds/13508953/artifacts/repository")
}
```

This is configured in both `pluginManagement` and `dependencyResolutionManagement` sections of `settings.gradle.kts`.

### Compilation Settings

- **compileSdk**: 36
- **minSdk**: 31
- **targetSdk**: 36
- **Java compatibility**: Java 11
- **Kotlin JVM target**: 11

## Development Guidelines

### Adding New Screens

1. Create a new route object in `AppNavigation.kt`:
   ```kotlin
   @Serializable
   data object NewRoute : NavKey
   ```

2. Create screen composable and ViewModel in a new package (e.g., `newscreen/`)

3. Register the entry in `AppNavigation.kt`:
   ```kotlin
   entry<NewRoute> {
       NewScreen(viewModel = hiltViewModel())
   }
   ```

4. Navigate by adding the route to the back stack: `backStack.add(NewRoute)`

### ViewModel Lifecycle

ViewModels are scoped to navigation entries, not the Activity. This means:
- ViewModels are created when navigating to a screen
- ViewModels are cleared when the screen is removed from the back stack
- Each navigation entry has its own ViewModel instance

### Navigation 3 vs Traditional Navigation

This project uses the experimental Navigation 3 library, which differs significantly from Navigation Compose (traditional):
- Uses `NavDisplay` instead of `NavHost`
- Uses `rememberNavBackStack()` instead of `rememberNavController()`
- Decorators replace traditional navigation scaffolding
- Type-safe routes via Kotlin serialization instead of string-based routes

## Data Layer Architecture

### Repositories
- `StateRepository`: Manages state data with offline-first pattern
- `ShelterRepository`: Manages shelter data and weather forecasts
- `UserPreferencesRepository`: Manages user settings (theme, temperature units)
  - Uses dual storage: DataStore for reactive updates + SharedPreferences for startup access
  - Calls `UiModeManager.setApplicationNightMode()` to ensure splash screen matches theme

### Use Cases
- `RefreshStatesUseCase`: Fetches fresh state data with staleness detection
- `RefreshWeatherIfStaleUseCase`: Smart weather refresh with 30-minute rate limit
- `SearchShelterByMileageUseCase`: Finds shelter by NOBO mile marker
- `FindNearestSheltersUseCase`: GPS-based shelter discovery
- `PrefetchNearbyWeatherUseCase`: Proactive weather loading for 100-mile radius

### Database (Room)
- `ATForecastDatabase`: Main database with version 1 schema
- DAOs: `StateDao`, `ShelterDao`, `DailyWeatherDao`, `HourlyWeatherDao`
- Entities use `@Embedded` and `@Relation` for complex relationships
- Foreign keys enforce referential integrity

## UI Patterns

### Theme & Styling
- Material 3 color scheme with custom blue theme (`#2980b9`)
- Light mode: Primary blue header with white text
- Dark mode: Dark header (`#1E1E1E`) with off-white text (`#E8E8E8`)
- Theme changes don't recreate activity (handled by `android:configChanges="uiMode"`)
- Splash screen automatically matches theme via `UiModeManager`

### Animations
- Sequential slide-out animations for inline search (buttons slide right, title slides left)
- Pull-to-refresh with smart loading state coordination
- Expandable weather cards with `AnimatedVisibility`
- Fixed-height containers prevent layout shifts during animations

### String Resources
All user-facing strings use string resources for localization:
- `R.string.search_placeholder`: "Enter mile marker…"
- `R.string.finding_nearest_shelter`: "Finding nearest shelter…"
- `R.string.no_shelter_found_at_mile`: "No shelter found at mile %1$s"
- See `values/strings.xml` for complete list

## Common Patterns & Best Practices

### Inline Search Implementation
Both StateListScreen and ShelterListScreen use inline search with:
- Numeric keyboard (`KeyboardType.Number`)
- Search IME action (`ImeAction.Search`)
- Sequential animations (300ms delay between exit and entry)
- Fixed height (56dp) to prevent header expansion
- Transparent TextField background with proper color theming

### ViewModel Creation with Factory
Some ViewModels require parameters (e.g., shelterId):
```kotlin
viewModel: ShelterDetailViewModel = hiltViewModel(
    creationCallback = { factory: ShelterDetailViewModel.Factory ->
        factory.create(shelterId)
    }
)
```

### Pull-to-Refresh Pattern
Prevent duplicate loading indicators:
```kotlin
PullToRefreshBox(
    isRefreshing = isRefreshing && uiState !is StateListUiState.Loading,
    onRefresh = { viewModel.refreshWeather() },
    // ...
)
```

### Single Card Expansion
Lift expansion state to parent for coordination:
```kotlin
var expandedCardIndex by remember { mutableStateOf(0) }
itemsIndexed(items) { index, item ->
    Card(
        expanded = expandedCardIndex == index,
        onExpandChange = { expandedCardIndex = index }
    )
}
```
