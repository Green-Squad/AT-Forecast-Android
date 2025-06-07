import React, { useEffect } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator
} from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { ThunkDispatch } from 'redux-thunk';
import { AnyAction } from 'redux';
import { Ionicons } from '@expo/vector-icons';

import { fetchStates } from '../redux/actions/stateActions';
import { RootState, RootStackParamList, State } from '../types';
import { formatTemperature } from '../utils/utils';
import API from '../api/api';

// Define the type for navigation prop
type StateListScreenNavigationProp = StackNavigationProp<RootStackParamList, 'StateList'>;

// Define the type for our dispatch
type AppDispatch = ThunkDispatch<RootState, unknown, AnyAction>;

// Hardcoded API key
const API_KEY = "jLEy94zVGv";

// State list screen component
const StateListScreen = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigation = useNavigation<StateListScreenNavigationProp>();
  
  // Get states from Redux store
  const { states, loading, error } = useSelector((state: RootState) => state.states);
  
  // Use effect to fetch states when component mounts
  useEffect(() => {
    dispatch(fetchStates());
  }, [dispatch]);
  
  // Navigate to shelters screen
  const handleStatePress = (state: State) => {
    navigation.navigate('ShelterList', { stateId: state.stateId, stateName: state.name });
  };
  
  // Render each state item
  const renderStateItem = ({ item }: { item: State }) => (
    <TouchableOpacity
      style={styles.stateItem}
      onPress={() => handleStatePress(item)}
    >
      <Text style={styles.stateName}>{item.name}</Text>
      <View style={styles.stateWeather}>
        <Text style={styles.stateTemp}>
          {item.averageHigh !== undefined ? `${item.averageHigh}°F / ${item.averageLow}°F` : 'Weather data unavailable'}
        </Text>
        <Ionicons name="chevron-forward" size={20} color="#666" />
      </View>
    </TouchableOpacity>
  );
  
  // Show loading indicator while fetching data
  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#3f51b5" />
      </View>
    );
  }
  
  // Show error message if fetch failed
  if (error) {
    return (
      <View style={styles.centered}>
        <Text style={styles.errorText}>Error: {error}</Text>
        <TouchableOpacity
          style={styles.retryButton}
          onPress={() => dispatch(fetchStates())}
        >
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }
  
  return (
    <View style={styles.container}>
      <FlatList
        data={states}
        renderItem={renderStateItem}
        keyExtractor={(item) => item.stateId.toString()}
        contentContainerStyle={styles.stateList}
      />
    </View>
  );
};

// Styles
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5'
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20
  },
  stateList: {
    padding: 10
  },
  stateItem: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 15,
    marginBottom: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4
  },
  stateName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333'
  },
  stateWeather: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  stateTemp: {
    fontSize: 14,
    color: '#666',
    marginRight: 5
  },
  errorText: {
    fontSize: 16,
    color: '#d32f2f',
    marginBottom: 20,
    textAlign: 'center'
  },
  retryButton: {
    backgroundColor: '#3f51b5',
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 4
  },
  retryButtonText: {
    color: '#fff',
    fontWeight: 'bold'
  }
});

export default StateListScreen; 