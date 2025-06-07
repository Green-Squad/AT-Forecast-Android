# AT Forecast - React Native

A React Native version of the AT Forecast Android app, providing weather forecasts for shelters along the Appalachian Trail.

## Features

- Display a list of states along the Appalachian Trail
- View shelters within each state
- Weather forecasts for each shelter
- Search for shelters by NOBO mileage
- Find nearby shelters using GPS
- Toggle between day/night modes
- Toggle between Fahrenheit/Celsius units

## Tech Stack

- React Native with Expo
- TypeScript
- Redux for state management
- React Navigation
- Axios for API calls
- Jest for testing

## Getting Started

### Prerequisites

- Node.js (v14 or newer)
- npm or Yarn
- Expo CLI (`npm install -g expo-cli`)
- Expo Go app on your mobile device (for testing)

### Installation and Setup

1. Clone the repository
2. Navigate to the project directory
```
cd react-native
```
3. Install dependencies
```
npm install --legacy-peer-deps
```
4. Create placeholder images (this should happen automatically in the postinstall script)
```
node create-placeholder-images.js
```
5. The API key is hardcoded in the API service files for development purposes
   - In a production environment, this would be handled more securely

### Running the App

Start the development server:
```
npm start
```

You can then:
- Scan the QR code with your phone's camera (iOS) or the Expo Go app (Android)
- Press 'a' to open in an Android emulator
- Press 'i' to open in an iOS simulator (macOS only)

## Project Structure

```
react-native/
├── src/
│   ├── api/             # API service files
│   ├── assets/          # Images, fonts, etc.
│   ├── components/      # Reusable UI components
│   ├── navigation/      # Navigation configuration
│   ├── redux/           # State management
│   │   ├── actions/
│   │   ├── reducers/
│   │   ├── store.ts
│   ├── screens/         # App screens
│   ├── types/           # TypeScript types
│   ├── utils/           # Helper functions
├── App.js               # Main app component
├── app.json             # Expo configuration
└── package.json         # Dependencies
```

## Testing

Run tests with:
```
npm test
```

## Known Issues and Limitations

- The app is currently in development mode and uses placeholder data
- API integration is partially implemented
- Some TypeScript linting errors may be present

## License

This project is licensed under the ISC License.

## Acknowledgments

- Original AT Forecast Android app
- Appalachian Trail Conservancy 