module.exports = {
  name: 'AT Forecast',
  slug: 'atforecast',
  version: '1.0.0',
  orientation: 'portrait',
  icon: './src/assets/icon.png',
  userInterfaceStyle: 'automatic',
  owner: "greensquad",
  splash: {
    image: './src/assets/splash.png',
    resizeMode: 'contain',
    backgroundColor: '#ffffff'
  },
  assetBundlePatterns: [
    '**/*'
  ],
  ios: {
    supportsTablet: true,
    bundleIdentifier: 'com.greensquad.atforecast2'
  },
  android: {
    adaptiveIcon: {
      foregroundImage: './src/assets/adaptive-icon.png',
      backgroundColor: '#ffffff'
    },
    package: 'com.greensquad.atforecast2',
    permissions: [
      'ACCESS_FINE_LOCATION',
      'ACCESS_COARSE_LOCATION'
    ]
  },
  web: {
    favicon: './src/assets/favicon.png'
  },
  extra: {
    eas: {
      projectId: "6f679c4a-c528-47b3-b935-423b108b34cd"
    }
  }
}; 