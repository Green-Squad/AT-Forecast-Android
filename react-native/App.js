import React from 'react';
import { registerRootComponent } from 'expo';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { Provider } from 'react-redux';
import { StatusBar } from 'expo-status-bar';

import store from './src/redux/store';
import AppNavigator from './src/navigation';
import AppInitializer from './src/components/AppInitializer';

// Main app component
const App = () => {
  return (
    <Provider store={store}>
      <SafeAreaProvider>
        <StatusBar style="auto" />
        <AppInitializer />
        <AppNavigator />
      </SafeAreaProvider>
    </Provider>
  );
};

// Register as the root component
registerRootComponent(App);

export default App; 