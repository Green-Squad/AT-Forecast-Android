import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  FlatList,
  Alert
} from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { ThunkDispatch } from 'redux-thunk';
import { AnyAction } from 'redux';
import { useRoute, RouteProp, useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { Ionicons } from '@expo/vector-icons';

import { fetchShelterDetail, fetchPreviousShelter, fetchNextShelter } from '../redux/actions/shelterActions';
import { RootState, RootStackParamList, DailyWeather, HourlyWeather } from '../types';
import {
  formatTemperature,
  formatElevation,
  formatMileage,
  formatDate,
  formatTime
} from '../utils/utils';

// Types for the navigation and route props
type ShelterDetailScreenRouteProp = RouteProp<RootStackParamList, 'ShelterDetail'>;
type ShelterDetailScreenNavigationProp = StackNavigationProp<RootStackParamList, 'ShelterDetail'>;

// Define the type for our dispatch
type AppDispatch = ThunkDispatch<RootState, unknown, AnyAction>;

// Hourly weather card component
const HourlyWeatherCard = ({ hour, unitType }: { hour: HourlyWeather; unitType: number }) => {
  const temp = hour.temperature !== undefined && hour.temperature !== null ? hour.temperature : 0;
  
  return (
    <View style={styles.hourlyCard}>
      <View style={styles.hourlyCardContent}>
        <Text style={styles.hourlyTime}>{formatTime(hour.time || '')}</Text>
        <View style={styles.hourlyIconContainer}>
          <Ionicons 
            name={hour.icon === 'clear-day' ? 'sunny' : 
                  hour.icon === 'clear-night' ? 'moon' : 
                  hour.icon === 'rain' ? 'rainy' : 
                  hour.icon === 'snow' ? 'snow' : 
                  hour.icon === 'wind' ? 'thunderstorm' : 
                  hour.icon === 'fog' ? 'cloud' : 
                  hour.icon === 'cloudy' ? 'cloudy' : 
                  hour.icon === 'partly-cloudy-day' ? 'partly-sunny' : 
                  hour.icon === 'partly-cloudy-night' ? 'cloudy-night' : 'cloud'} 
            size={20} 
            color="#3f51b5" 
          />
        </View>
      </View>
      <View style={styles.hourlyCardContent}>
        <Text style={styles.hourlyTemp}>{formatTemperature(temp, unitType)}</Text>
        <View style={styles.hourlyDetailsContainer}>
          <Text style={styles.hourlyDetail}>
            <Ionicons name="speedometer" size={12} color="#666" /> {hour.windSpeed || 0} mph
          </Text>
        </View>
      </View>
    </View>
  );
};

// Weather forecast card component
const WeatherForecastCard = ({ 
  day, 
  unitType, 
  isSelected, 
  onSelect 
}: { 
  day: DailyWeather; 
  unitType: number;
  isSelected: boolean;
  onSelect: () => void;
}) => {
  return (
    <TouchableOpacity 
      style={[styles.forecastCard, isSelected && styles.selectedForecastCard]} 
      onPress={onSelect}
    >
      <Text style={styles.forecastDate}>{formatDate(day.date)}</Text>
      <View style={styles.forecastIconContainer}>
        <Ionicons 
          name={day.icon === 'clear-day' ? 'sunny' : 
                day.icon === 'clear-night' ? 'moon' : 
                day.icon === 'rain' ? 'rainy' : 
                day.icon === 'snow' ? 'snow' : 
                day.icon === 'wind' ? 'thunderstorm' : 
                day.icon === 'fog' ? 'cloud' : 
                day.icon === 'cloudy' ? 'cloudy' : 
                day.icon === 'partly-cloudy-day' ? 'partly-sunny' : 
                day.icon === 'partly-cloudy-night' ? 'cloudy-night' : 'cloud'} 
          size={30} 
          color="#3f51b5" 
        />
      </View>
      <Text style={styles.forecastDescription}>{day.description}</Text>
      <View style={styles.forecastTempContainer}>
        <Text style={styles.forecastTempHigh}>
          {formatTemperature(day.high, unitType)}
        </Text>
        <Text style={styles.forecastTempLow}>
          {formatTemperature(day.low, unitType)}
        </Text>
      </View>
      <View style={styles.forecastDetailsContainer}>
        <Text style={styles.forecastDetail}>
          <Ionicons name="speedometer" size={14} color="#666" /> {day.windSpeed} mph
        </Text>
      </View>
    </TouchableOpacity>
  );
};

// Shelter detail screen component
const ShelterDetailScreen = () => {
  const dispatch = useDispatch<AppDispatch>();
  const route = useRoute<ShelterDetailScreenRouteProp>();
  const navigation = useNavigation<ShelterDetailScreenNavigationProp>();
  const { shelterId } = route.params;

  // State to track the selected day for hourly forecast
  const [selectedDayIndex, setSelectedDayIndex] = useState(0);
  // State to track navigation in progress
  const [isNavigating, setIsNavigating] = useState(false);

  // Get shelter from Redux store
  const { selectedShelter: shelter, loading, error, sheltersCache } = useSelector((state: RootState) => state.shelters);
  const { unitType } = useSelector((state: RootState) => state.settings);

  // Fetch shelter details when the component mounts
  useEffect(() => {
    dispatch(fetchShelterDetail(shelterId));
  }, [dispatch, shelterId]);

  // Navigate to previous shelter
  const handlePreviousShelter = async () => {
    if (!shelter || !shelter.mileage) {
      Alert.alert('Navigation Error', 'Cannot navigate: current shelter information is incomplete.');
      return;
    }

    try {
      setIsNavigating(true);
      const previousShelter = await dispatch(fetchPreviousShelter(shelter.mileage));
      if (previousShelter && previousShelter.shelterId) {
        // Reset selected day index when navigating to a different shelter
        setSelectedDayIndex(0);
        // Update navigation params to match new shelter ID
        navigation.setParams({ shelterId: previousShelter.shelterId });
        
        // Save the updated cache
        dispatch({ 
          type: 'SAVE_CACHE_REQUEST', 
          payload: sheltersCache
        });
      } else {
        Alert.alert('Navigation Error', 'No previous shelter found.');
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Could not find previous shelter.';
      Alert.alert('Navigation Error', errorMessage);
      console.error('Error navigating to previous shelter:', error);
    } finally {
      setIsNavigating(false);
    }
  };

  // Navigate to next shelter
  const handleNextShelter = async () => {
    if (!shelter || !shelter.mileage) {
      Alert.alert('Navigation Error', 'Cannot navigate: current shelter information is incomplete.');
      return;
    }

    try {
      setIsNavigating(true);
      const nextShelter = await dispatch(fetchNextShelter(shelter.mileage));
      if (nextShelter && nextShelter.shelterId) {
        // Reset selected day index when navigating to a different shelter
        setSelectedDayIndex(0);
        // Update navigation params to match new shelter ID
        navigation.setParams({ shelterId: nextShelter.shelterId });
        
        // Save the updated cache
        dispatch({ 
          type: 'SAVE_CACHE_REQUEST', 
          payload: sheltersCache
        });
      } else {
        Alert.alert('Navigation Error', 'No next shelter found.');
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Could not find next shelter.';
      Alert.alert('Navigation Error', errorMessage);
      console.error('Error navigating to next shelter:', error);
    } finally {
      setIsNavigating(false);
    }
  };

  // Handle day selection for hourly forecast
  const handleDaySelect = (index: number) => {
    setSelectedDayIndex(index);
  };

  // Show loading indicator while fetching data
  if (loading || isNavigating) {
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
          onPress={() => dispatch(fetchShelterDetail(shelterId))}
        >
          <Text style={styles.retryButtonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  // Show message if no shelter is found
  if (!shelter) {
    return (
      <View style={styles.centered}>
        <Text style={styles.noDataText}>Shelter not found.</Text>
      </View>
    );
  }

  // Get the selected day's hourly weather
  const selectedDay = shelter.dailyWeather && shelter.dailyWeather.length > selectedDayIndex 
    ? shelter.dailyWeather[selectedDayIndex] 
    : null;

  return (
    <View style={styles.container}>
      <ScrollView>
        <View style={styles.shelterHeader}>
          <Text style={styles.shelterName}>{shelter.name}</Text>
          <View style={styles.shelterDetailsContainer}>
            <View style={styles.shelterDetail}>
              <Ionicons name="location" size={16} color="#666" />
              <Text style={styles.shelterDetailText}>
                Mile: {formatMileage(shelter.mileage)}
              </Text>
            </View>
            <View style={styles.shelterDetail}>
              <Ionicons name="trending-up" size={16} color="#666" />
              <Text style={styles.shelterDetailText}>
                Elevation: {formatElevation(shelter.elevation, unitType)}
              </Text>
            </View>
          </View>
        </View>

        <View style={styles.forecastContainer}>
          <Text style={styles.sectionTitle}>Weather Forecast</Text>
          {shelter.dailyWeather && shelter.dailyWeather.length > 0 ? (
            <FlatList
              data={shelter.dailyWeather}
              renderItem={({ item, index }) => (
                <WeatherForecastCard 
                  day={item} 
                  unitType={unitType} 
                  isSelected={index === selectedDayIndex}
                  onSelect={() => handleDaySelect(index)}
                />
              )}
              keyExtractor={(item) => item.date}
              horizontal
              showsHorizontalScrollIndicator={false}
              contentContainerStyle={styles.forecastList}
            />
          ) : (
            <Text style={styles.noDataText}>No forecast data available.</Text>
          )}
        </View>

        {selectedDay && selectedDay.hourlyWeather && selectedDay.hourlyWeather.length > 0 && (
          <View style={styles.hourlyContainer}>
            <Text style={styles.sectionTitle}>
              Hourly Forecast - {formatDate(selectedDay.date)}
            </Text>
            <FlatList
              data={selectedDay.hourlyWeather.slice(0, 10)}
              renderItem={({ item }) => (
                <HourlyWeatherCard hour={item} unitType={unitType} />
              )}
              keyExtractor={(item, index) => `${selectedDay.date}-${index}`}
              scrollEnabled={false}
              showsVerticalScrollIndicator={false}
              contentContainerStyle={styles.hourlyList}
            />
          </View>
        )}

        <View style={styles.navigationContainer}>
          <TouchableOpacity
            style={styles.navigationButton}
            onPress={handlePreviousShelter}
          >
            <Ionicons name="arrow-back" size={24} color="#fff" />
            <Text style={styles.navigationButtonText}>Previous</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.navigationButton}
            onPress={handleNextShelter}
          >
            <Text style={styles.navigationButtonText}>Next</Text>
            <Ionicons name="arrow-forward" size={24} color="#fff" />
          </TouchableOpacity>
        </View>
      </ScrollView>
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
  shelterHeader: {
    backgroundColor: '#fff',
    padding: 20,
    marginBottom: 10,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4
  },
  shelterName: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 10
  },
  shelterDetailsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  shelterDetail: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  shelterDetailText: {
    marginLeft: 5,
    fontSize: 16,
    color: '#666'
  },
  forecastContainer: {
    backgroundColor: '#fff',
    padding: 20,
    marginBottom: 10,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4
  },
  hourlyContainer: {
    backgroundColor: '#fff',
    padding: 20,
    marginBottom: 10,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    maxHeight: 500
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15
  },
  forecastList: {
    paddingBottom: 10
  },
  hourlyList: {
    paddingBottom: 10
  },
  forecastCard: {
    width: 150,
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    padding: 15,
    marginRight: 10,
    elevation: 1,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2
  },
  selectedForecastCard: {
    backgroundColor: '#e3f2fd',
    borderColor: '#2196f3',
    borderWidth: 1
  },
  forecastDate: {
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 5
  },
  forecastIconContainer: {
    alignItems: 'center',
    marginVertical: 10
  },
  forecastDescription: {
    fontSize: 12,
    textAlign: 'center',
    marginBottom: 10,
    color: '#666'
  },
  forecastTempContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10
  },
  forecastTempHigh: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#d32f2f'
  },
  forecastTempLow: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#1976d2'
  },
  forecastDetailsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  forecastDetail: {
    fontSize: 12,
    color: '#666'
  },
  hourlyCard: {
    width: '100%',
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    padding: 10,
    marginBottom: 10,
    elevation: 1,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  hourlyCardContent: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  hourlyTime: {
    fontSize: 14,
    fontWeight: 'bold',
    marginRight: 10
  },
  hourlyIconContainer: {
    marginLeft: 5
  },
  hourlyTemp: {
    fontSize: 14,
    fontWeight: 'bold',
    marginRight: 15
  },
  hourlyDetailsContainer: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  hourlyDetail: {
    fontSize: 12,
    color: '#666'
  },
  navigationContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 20
  },
  navigationButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#3f51b5',
    paddingHorizontal: 15,
    paddingVertical: 10,
    borderRadius: 4
  },
  navigationButtonText: {
    color: '#fff',
    marginHorizontal: 5,
    fontWeight: 'bold'
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
    textAlign: 'center',
    padding: 20
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

export default ShelterDetailScreen; 