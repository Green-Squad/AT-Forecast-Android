import React, { useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createDrawerNavigator } from '@react-navigation/drawer';
import { useSelector, useDispatch } from 'react-redux';
import { Appearance, useColorScheme, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

import { RootStackParamList, RootState, ThemeType } from '../types';
import { setThemeType } from '../redux/actions/settingsActions';

// Import screens
import StateListScreen from '../screens/StateListScreen';
import ShelterListScreen from '../screens/ShelterListScreen';
import ShelterDetailScreen from '../screens/ShelterDetailScreen';
import SettingsScreen from '../screens/SettingsScreen';

// Create navigators
const Stack = createStackNavigator<RootStackParamList>();
const Drawer = createDrawerNavigator();

// Main stack navigator
const MainStack = () => {
  return (
    <Stack.Navigator
      initialRouteName="StateList"
      screenOptions={({ navigation }) => ({
        headerStyle: {
          backgroundColor: '#3f51b5',
        },
        headerTintColor: '#fff',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
        headerRight: () => (
          <TouchableOpacity
            onPress={() => navigation.navigate('Settings')}
            style={{ marginRight: 15 }}
          >
            <Ionicons name="settings-outline" size={24} color="#fff" />
          </TouchableOpacity>
        ),
      })}
    >
      <Stack.Screen
        name="StateList"
        component={StateListScreen}
        options={{ title: 'AT Forecast' }}
      />
      <Stack.Screen
        name="ShelterList"
        component={ShelterListScreen}
        options={({ route }) => ({ title: route.params.stateName })}
      />
      <Stack.Screen
        name="ShelterDetail"
        component={ShelterDetailScreen}
        options={({ route }) => ({ title: 'Shelter Details' })}
      />
    </Stack.Navigator>
  );
};

// Root drawer navigator
const AppNavigator = () => {
  const dispatch = useDispatch();
  const themeType = useSelector((state: RootState) => state.settings.themeType);
  const systemColorScheme = useColorScheme();
  
  // Listen for appearance changes
  useEffect(() => {
    const subscription = Appearance.addChangeListener(({ colorScheme }) => {
      // Only update if the user has selected "System" theme
      if (themeType === ThemeType.SYSTEM) {
        // Force UI refresh when system theme changes
        console.log('System theme changed to:', colorScheme);
      }
    });
    
    return () => {
      subscription.remove();
    };
  }, [themeType]);
  
  // Determine if dark mode should be used
  const isDarkMode = themeType === ThemeType.DARK || 
    (themeType === ThemeType.SYSTEM && systemColorScheme === 'dark');
  
  return (
    <NavigationContainer theme={{ 
      dark: isDarkMode, 
      colors: {
        primary: '#3f51b5',
        background: isDarkMode ? '#121212' : '#f5f5f5',
        card: isDarkMode ? '#1e1e1e' : '#ffffff',
        text: isDarkMode ? '#ffffff' : '#000000',
        border: isDarkMode ? '#333333' : '#e0e0e0',
        notification: '#ff5722'
      }
    }}>
      <Drawer.Navigator
        initialRouteName="Main"
        screenOptions={{
          headerShown: false,
          drawerActiveTintColor: '#3f51b5',
          drawerInactiveTintColor: isDarkMode ? '#ffffff' : '#000000',
          drawerStyle: {
            backgroundColor: isDarkMode ? '#1e1e1e' : '#ffffff',
          }
        }}
      >
        <Drawer.Screen
          name="Main"
          component={MainStack}
          options={{ title: 'AT Forecast' }}
        />
        <Drawer.Screen
          name="Settings"
          component={SettingsScreen}
          options={{ title: 'Settings' }}
        />
      </Drawer.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator; 