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
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { Dispatch } from 'redux';

import { fetchShelters } from '../redux/actions/shelterActions';
import { RootState, RootStackParamList, Shelter } from '../types';
import { formatMileage, formatElevation } from '../utils/utils';

// Types for the navigation and route props
type ShelterListScreenNavigationProp = StackNavigationProp<RootStackParamList, 'ShelterList'>;
type ShelterListScreenRouteProp = RouteProp<RootStackParamList, 'ShelterList'>;

// Shelter list screen component
const ShelterListScreen = () => {
  const dispatch = useDispatch<Dispatch<any>>();
  const navigation = useNavigation<ShelterListScreenNavigationProp>();
  const route = useRoute<ShelterListScreenRouteProp>();
  const { stateId, stateName } = route.params;
  
  // Get shelters from Redux store
  const { shelters, loading, error } = useSelector((state: RootState) => state.shelters);
  const { unitType } = useSelector((state: RootState) => state.settings);
  // Also get states to check if we already have the shelters
  const { states } = useSelector((state: RootState) => state.states);
  
  // Fetch shelters when the component mounts
  useEffect(() => {
    // Check if we already have shelters for this state in our states data
    const currentState = states.find(s => s.stateId === stateId);
    if (currentState && currentState.shelters && currentState.shelters.length > 0) {
      console.log(`Using cached shelters for ${stateName} from states data`);
      // No need to fetch shelters, they're already available
    } else {
      // If not, fetch them
      console.log(`Fetching shelters for ${stateName}`);
      dispatch(fetchShelters(stateId));
    }
  }, [dispatch, stateId, stateName, states]);
  
  // Create a list of shelters to display
  const sheltersToDisplay = React.useMemo(() => {
    // First check if we have shelters from the direct API call
    if (shelters && shelters.length > 0) {
      console.log(`Displaying ${shelters.length} shelters from API call`);
      return shelters;
    }
    
    // If not, check if we have them in the states data
    const currentState = states.find(s => s.stateId === stateId);
    if (currentState && currentState.shelters && currentState.shelters.length > 0) {
      console.log(`Displaying ${currentState.shelters.length} shelters for ${currentState.name} from states data`);
      return currentState.shelters;
    }
    
    // If we don't have shelters yet, return an empty array
    console.log(`No shelters found for state ID: ${stateId}`);
    return [];
  }, [shelters, states, stateId]);

  // Navigate to the shelter detail screen
  const handleShelterPress = (shelterId: number) => {
    navigation.navigate('ShelterDetail', { shelterId });
  };
  
  // Render a shelter item
  const renderShelterItem = ({ item }: { item: Shelter }) => (
    <TouchableOpacity
      style={styles.shelterItem}
      onPress={() => handleShelterPress(item.shelterId)}
    >
      <Text style={styles.shelterName}>{item.name}</Text>
      <View style={styles.shelterDetails}>
        <Text style={styles.shelterMileage}>
          Mile: {formatMileage(item.mileage)}
        </Text>
        <Text style={styles.shelterElevation}>
          Elevation: {formatElevation(item.elevation, unitType)}
        </Text>
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
          onPress={() => dispatch(fetchShelters(stateId))}
        >
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }
  
  // Show message if no shelters are found
  if (sheltersToDisplay.length === 0 && !loading) {
    return (
      <View style={styles.centered}>
        <Text style={styles.noDataText}>No shelters found for this state.</Text>
      </View>
    );
  }
  
  // Render the shelter list
  return (
    <View style={styles.container}>
      <FlatList
        data={sheltersToDisplay}
        renderItem={renderShelterItem}
        keyExtractor={(item) => item.shelterId.toString()}
        contentContainerStyle={styles.listContainer}
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
  listContainer: {
    padding: 10
  },
  shelterItem: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 16,
    marginBottom: 10,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4
  },
  shelterName: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 4
  },
  shelterDetails: {
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  shelterMileage: {
    fontSize: 14,
    color: '#666'
  },
  shelterElevation: {
    fontSize: 14,
    color: '#666'
  },
  errorText: {
    color: '#d32f2f',
    fontSize: 16,
    marginBottom: 20,
    textAlign: 'center'
  },
  noDataText: {
    fontSize: 16,
    color: '#666',
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

export default ShelterListScreen; 