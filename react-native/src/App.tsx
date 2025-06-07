import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Provider, useDispatch } from 'react-redux';
import { Dispatch } from 'redux';
import { TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import store from './redux/store';
import { loadCache } from './redux/actions/cacheActions';
import { RootStackParamList } from './types';

// Import screens
import StateListScreen from './screens/StateListScreen';
import ShelterListScreen from './screens/ShelterListScreen';
import ShelterDetailScreen from './screens/ShelterDetailScreen';
import SettingsScreen from './screens/SettingsScreen';

// Create stack navigator
const Stack = createStackNavigator<RootStackParamList>();

// App initialization component
const AppInitializer = () => {
  const dispatch = useDispatch<Dispatch<any>>();

  useEffect(() => {
    // Load cache from AsyncStorage on app start
    dispatch(loadCache());
  }, [dispatch]);

  return null;
};

// Main app component
const App = () => {
  return (
    <Provider store={store}>
      <AppInitializer />
      <NavigationContainer>
        <Stack.Navigator 
          initialRouteName="StateList"
          screenOptions={({ navigation }) => ({
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
            options={{ title: 'States' }}
          />
          <Stack.Screen 
            name="ShelterList" 
            component={ShelterListScreen} 
            options={({ route }) => ({ title: route.params.stateName || 'Shelters' })}
          />
          <Stack.Screen 
            name="ShelterDetail" 
            component={ShelterDetailScreen} 
            options={{ title: 'Shelter Details' }}
          />
          <Stack.Screen 
            name="Settings" 
            component={SettingsScreen} 
            options={{ title: 'Settings' }}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </Provider>
  );
};

export default App; 