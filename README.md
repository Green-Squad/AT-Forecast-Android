# ATForecast Android

**ATForecast** is a modern Android weather application for hikers on the Appalachian Trail. Built with Jetpack Compose and Material 3, it provides 7-day weather forecasts for trail shelters with full offline support.

## 🌟 Features

- **Trail Shelter Directory**: Browse shelters organized by state with detailed information (mileage, elevation, coordinates)
- **7-Day Weather Forecasts**: Detailed daily and hourly weather for each shelter via OpenWeather API
- **Offline-First Architecture**: All weather data cached locally for hikers without connectivity
- **GPS Location**: Find nearest shelters based on your current location
- **Mileage Search**: Quickly jump to a shelter by NOBO mile marker
- **Customizable Settings**: Temperature units (F/C) and theme mode (Light/Dark/System)
- **Deep Linking**: Direct links to shelter details from web

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Hilt dependency injection
- **Navigation**: Navigation 3 (experimental alpha) with type-safe routes
- **Database**: Room for offline-first data persistence
- **Networking**: Retrofit + Gson
- **Location**: Google Play Services Location
- **Image Loading**: Coil for shelter and state images
- **Preferences**: DataStore (with SharedPreferences for startup theme)
- **Icons**: Material Symbols for weather and UI elements

## 📋 Prerequisites

- Android Studio (latest stable or Canary for best Compose alpha compatibility)
- Android SDK 36
- JDK 11+
- Minimum device API level: 31 (Android 12)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/ATForecast-Android.git
cd ATForecast-Android
```

### 2. Configure API Key

The ATForecast backend API key must be configured in `local.properties`:

1. Open or create `local.properties` in the project root
2. Add the following line:
   ```properties
   atforecast.api.key=YOUR_API_KEY_HERE
   ```

**Note**: `local.properties` is git-ignored for security. Never commit API keys to the repository.

### 3. Build and Run

1. Open the project in Android Studio
2. Let Gradle sync and download dependencies
3. Build and run on an emulator (API 31+) or physical device

## 🏗️ Project Structure

```
ATForecast-Android/
├── app/
│   ├── src/main/java/com/gsnamespace/atforecast/
│   │   ├── data/              # Data layer (Room, Retrofit, Repositories)
│   │   ├── domain/            # Domain models and use cases
│   │   ├── di/                # Hilt dependency injection modules
│   │   ├── navigation/        # Navigation 3 setup and routes
│   │   ├── ui/                # UI layer (screens, components, theme)
│   │   ├── MainActivity.kt
│   │   └── MyApplication.kt
│   ├── schemas/               # Room database schemas
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml     # Dependency version catalog
└── CLAUDE.md                  # AI assistant instructions
```

## 🔧 Build Commands

```bash
# Build the app
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Assemble debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

## 📱 App Architecture

### Navigation Pattern
- Uses **Navigation 3** (experimental) with declarative routes
- Type-safe navigation via Kotlin serialization
- ViewModel lifecycle scoped to navigation entries

### Data Flow
- **Offline-first**: Always show cached data, fetch fresh data in background
- **Cache strategy**: Weather expires after 2 hours, shelter metadata after 24 hours
- **Prefetching**: Automatically fetches weather for shelters within 100-mile radius

### API Integration
- **Base URL**: `https://www.atforecast.app/`
- **Endpoints**:
  - `GET /index.json` - Fetch all states with optional shelter data
  - `GET /shelters/{id}.json` - Fetch shelter with 7-day weather forecast

## 🎨 Material 3 Theme

Custom blue theme based on the original ATForecast brand:
- **Light Mode**: Primary blue header (`#2980b9`) with white text
- **Dark Mode**: Dark header (`#1E1E1E`) with off-white text (`#E8E8E8`)
- Dynamic theme switching with system-matching splash screen
- Seamless theme changes without activity recreation

## ✨ Key Features Implemented

### UI & Navigation
- **Navigation 3**: Modern type-safe navigation with ViewModel scoping to nav entries
- **Inline Search**: Animated header search with numeric keyboard for mileage lookup
- **Pull-to-Refresh**: Smart refresh with rate limiting (30min for shelter details)
- **Previous/Next Navigation**: Seamless shelter browsing with Previous/Next buttons
- **Single Card Expansion**: Only one weather card expanded at a time

### Weather Display
- **Material Symbols Icons**: Clear, clouds, rain, snow, fog, and extreme weather icons
- **7-Day Forecasts**: Daily cards with expandable hourly breakdowns
- **Smart Date Formatting**: "Today", "Tomorrow", or day names for weather dates
- **Temperature Units**: Fahrenheit/Celsius conversion with persistent preferences

### Location Features
- **GPS Navigation**: Find nearest shelters based on current location
- **State Images**: All 14 AT state images integrated with Coil
- **Mileage-Based Search**: Quick jump to any shelter by mile marker

### Performance & Offline
- **Offline-First**: All data cached locally with smart prefetching
- **100-Mile Prefetch**: Automatically loads weather for nearby shelters
- **Stale Data Detection**: 2-hour weather expiration with automatic refresh

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the [MIT License](LICENSE.md).

## 🙏 Acknowledgments

- Weather data powered by OpenWeather API
- Trail data courtesy of the Appalachian Trail community
- Original app by Green Squad
