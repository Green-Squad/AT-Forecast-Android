import { Dispatch } from 'redux';
import {
  FETCH_SHELTERS_REQUEST,
  FETCH_SHELTERS_SUCCESS,
  FETCH_SHELTERS_FAILURE,
  FETCH_SHELTER_DETAIL_REQUEST,
  FETCH_SHELTER_DETAIL_SUCCESS,
  FETCH_SHELTER_DETAIL_FAILURE,
  SEARCH_SHELTER_BY_MILEAGE_REQUEST,
  SEARCH_SHELTER_BY_MILEAGE_SUCCESS,
  SEARCH_SHELTER_BY_MILEAGE_FAILURE,
  SEARCH_NEAREST_SHELTERS_REQUEST,
  SEARCH_NEAREST_SHELTERS_SUCCESS,
  SEARCH_NEAREST_SHELTERS_FAILURE,
  SET_SELECTED_SHELTER,
  CLEAR_SELECTED_SHELTER
} from './types';
import API from '../../api/api';
import { Shelter } from '../../types';
import { saveCache } from './cacheActions';

// Helper function to safely parse wind data
const parseWindData = (windString: any) => {
  if (!windString || typeof windString !== 'string') {
    return { speed: 0, direction: 'N' };
  }

  const parts = windString.split(' ');
  return {
    speed: parseFloat(parts[0] || '0'),
    direction: parts[2] || 'N'
  };
};

// Action to fetch shelters for a specific state
export const fetchShelters = (stateId: number) => {
  return async (dispatch: Dispatch) => {
    dispatch({ type: FETCH_SHELTERS_REQUEST });

    try {
      console.log(`Fetching shelters for state ID: ${stateId}`);
      
      // Fetch all states with shelters included
      const statesData = await API.getStates(true);
      
      // Find the selected state in the raw API response (using 'any' type to handle raw API data)
      const selectedState = statesData.find((s: any) => parseInt(s.state_id) === stateId);
      
      if (selectedState && selectedState.shelters && selectedState.shelters.length > 0) {
        console.log(`Found ${selectedState.shelters.length} shelters for state: ${selectedState.name}`);
        
        // Transform the shelter data to match our app's data structure
        const transformedShelters = selectedState.shelters.map((shelter: any) => ({
          shelterId: shelter.shelter_id,
          name: shelter.name,
          mileage: parseFloat(shelter.mileage),
          elevation: shelter.elevation,
          latitude: parseFloat(shelter.latt || shelter.latitude || 0),
          longitude: parseFloat(shelter.long || shelter.longitude || 0),
          dailyWeather: shelter.daily_weather || []
        }));
        
        dispatch({
          type: FETCH_SHELTERS_SUCCESS,
          payload: transformedShelters
        });
      } else {
        console.error(`No shelters found for state ID: ${stateId}`);
        throw new Error('No shelters found for this state');
      }
    } catch (error) {
      dispatch({
        type: FETCH_SHELTERS_FAILURE,
        payload: error instanceof Error ? error.message : 'An unknown error occurred'
      });
    }
  };
};

// Helper function to get next or previous shelter from cache
const findAdjacentShelterFromCache = (shelters: Shelter[], currentMileage: number, findNext: boolean): Shelter | null => {
  if (!shelters || shelters.length === 0) return null;
  
  // Ensure we have valid mileage values
  const validShelters = shelters.filter(s => s.mileage !== undefined && !isNaN(s.mileage));
  if (validShelters.length === 0) return null;
  
  // Filter shelters based on mileage
  const filteredShelters = findNext 
    ? validShelters.filter(s => s.mileage > currentMileage) 
    : validShelters.filter(s => s.mileage < currentMileage);
  
  if (filteredShelters.length === 0) return null;
  
  // Sort by mileage
  return findNext 
    ? filteredShelters.sort((a, b) => a.mileage - b.mileage)[0] // Get closest next shelter
    : filteredShelters.sort((a, b) => b.mileage - a.mileage)[0]; // Get closest previous shelter
};

// Action to fetch a specific shelter by ID
export const fetchShelterDetail = (shelterId: number) => {
  return async (dispatch: Dispatch, getState: any) => {
    dispatch({ type: FETCH_SHELTER_DETAIL_REQUEST });

    try {
      // Check if shelter exists in cache
      const state = getState();
      const cachedShelter = state.shelters.sheltersCache[shelterId];
      
      // If we have the shelter in cache with dailyWeather data, use it
      if (cachedShelter && cachedShelter.dailyWeather && cachedShelter.dailyWeather.length > 0) {
        console.log('Using cached shelter data for ID:', shelterId);
        
        dispatch({
          type: FETCH_SHELTER_DETAIL_SUCCESS,
          payload: cachedShelter
        });
        
        return cachedShelter;
      }
      
      // Otherwise, fetch from API
      console.log('Fetching shelter data from API for ID:', shelterId);
      const sheltersData = await API.getShelter(shelterId);
      
      if (sheltersData && sheltersData.length > 0) {
        // Transform the shelter data to match our app's data structure
        // Using 'any' type since we're dealing with raw API data
        const shelterData: any = sheltersData[0];
        const transformedShelter = {
          shelterId: shelterData.shelter_id,
          name: shelterData.name,
          mileage: parseFloat(shelterData.mileage),
          elevation: shelterData.elevation,
          latitude: parseFloat(shelterData.latt || shelterData.latitude || 0),
          longitude: parseFloat(shelterData.long || shelterData.longitude || 0),
          dailyWeather: shelterData.daily_weather ? shelterData.daily_weather.map((weather: any) => {
            const windData = parseWindData(weather.wind);
            return {
              dailyWeatherId: weather.daily_weather_id,
              date: weather.weather_date,
              high: weather.high,
              low: weather.low,
              description: weather.description,
              icon: 'default-icon', // Add default icon or map from API
              precipitation: 0, // Add default or map from API
              windSpeed: windData.speed,
              windDirection: windData.direction,
              hourlyWeather: weather.hourly_weather ? weather.hourly_weather.map((hourly: any) => {
                const hourlyWindData = parseWindData(hourly.wind);
                return {
                  hourlyWeatherId: hourly.hourly_weather_id,
                  time: hourly.time || hourly.weather_time || hourly.date,
                  temperature: hourly.temperature || parseInt(hourly.temp) || 0,
                  description: hourly.description || '',
                  icon: hourly.icon || 'cloud',
                  precipitation: hourly.precipitation || 0,
                  windSpeed: hourlyWindData.speed,
                  windDirection: hourlyWindData.direction
                };
              }) : []
            };
          }) : []
        };
        
        dispatch({
          type: FETCH_SHELTER_DETAIL_SUCCESS,
          payload: transformedShelter
        });
        
        // Save updated cache to storage
        const updatedCache = {
          ...state.shelters.sheltersCache,
          [transformedShelter.shelterId]: transformedShelter
        };
        dispatch({ 
          type: 'SAVE_CACHE_REQUEST', 
          payload: updatedCache 
        });
        
        return transformedShelter;
      } else {
        throw new Error('Shelter not found');
      }
    } catch (error) {
      dispatch({
        type: FETCH_SHELTER_DETAIL_FAILURE,
        payload: error instanceof Error ? error.message : 'An unknown error occurred'
      });
      return null;
    }
  };
};

// Action to search for a shelter by mileage
export const searchShelterByMileage = (mileage: number) => {
  return async (dispatch: Dispatch) => {
    dispatch({ type: SEARCH_SHELTER_BY_MILEAGE_REQUEST });

    try {
      const shelterData = await API.findShelterByMileage(mileage);
      
      if (shelterData) {
        // Transform the shelter data to match our app's data structure
        const rawShelter = shelterData as any;
        const transformedShelter = {
          shelterId: rawShelter.shelter_id,
          name: rawShelter.name,
          mileage: parseFloat(rawShelter.mileage),
          elevation: rawShelter.elevation,
          latitude: parseFloat(rawShelter.latt || rawShelter.latitude || 0),
          longitude: parseFloat(rawShelter.long || rawShelter.longitude || 0),
          dailyWeather: rawShelter.daily_weather ? rawShelter.daily_weather.map((weather: any) => {
            const windData = parseWindData(weather.wind);
            return {
              dailyWeatherId: weather.daily_weather_id,
              date: weather.weather_date,
              high: weather.high,
              low: weather.low,
              description: weather.description,
              icon: 'default-icon',
              precipitation: 0,
              windSpeed: windData.speed,
              windDirection: windData.direction,
              hourlyWeather: weather.hourly_weather ? weather.hourly_weather.map((hourly: any) => {
                const hourlyWindData = parseWindData(hourly.wind);
                return {
                  hourlyWeatherId: hourly.hourly_weather_id,
                  time: hourly.time || hourly.weather_time || hourly.date,
                  temperature: hourly.temperature || parseInt(hourly.temp) || 0,
                  description: hourly.description || '',
                  icon: hourly.icon || 'cloud',
                  precipitation: hourly.precipitation || 0,
                  windSpeed: hourlyWindData.speed,
                  windDirection: hourlyWindData.direction
                };
              }) : []
            };
          }) : []
        };
        
        dispatch({
          type: SEARCH_SHELTER_BY_MILEAGE_SUCCESS,
          payload: transformedShelter
        });
      } else {
        throw new Error('No shelter found near this mileage');
      }
    } catch (error) {
      dispatch({
        type: SEARCH_SHELTER_BY_MILEAGE_FAILURE,
        payload: error instanceof Error ? error.message : 'An unknown error occurred'
      });
    }
  };
};

// Action to search for nearest shelters by coordinates
export const searchNearestShelters = (latitude: number, longitude: number) => {
  return async (dispatch: Dispatch) => {
    dispatch({ type: SEARCH_NEAREST_SHELTERS_REQUEST });

    try {
      const sheltersData = await API.findNearestShelters(latitude, longitude);
      
      if (sheltersData && sheltersData.length > 0) {
        // Transform the shelters data to match our app's data structure
        const transformedShelters = sheltersData.map((shelter: any) => ({
          shelterId: shelter.shelter_id,
          name: shelter.name,
          mileage: parseFloat(shelter.mileage),
          elevation: shelter.elevation,
          latitude: parseFloat(shelter.latt || shelter.latitude || 0),
          longitude: parseFloat(shelter.long || shelter.longitude || 0),
          dailyWeather: shelter.daily_weather ? shelter.daily_weather.map((weather: any) => {
            const windData = parseWindData(weather.wind);
            return {
              dailyWeatherId: weather.daily_weather_id,
              date: weather.weather_date,
              high: weather.high,
              low: weather.low,
              description: weather.description,
              icon: 'default-icon',
              precipitation: 0,
              windSpeed: windData.speed,
              windDirection: windData.direction,
              hourlyWeather: weather.hourly_weather ? weather.hourly_weather.map((hourly: any) => {
                const hourlyWindData = parseWindData(hourly.wind);
                return {
                  hourlyWeatherId: hourly.hourly_weather_id,
                  time: hourly.time || hourly.weather_time || hourly.date,
                  temperature: hourly.temperature || parseInt(hourly.temp) || 0,
                  description: hourly.description || '',
                  icon: hourly.icon || 'cloud',
                  precipitation: hourly.precipitation || 0,
                  windSpeed: hourlyWindData.speed,
                  windDirection: hourlyWindData.direction
                };
              }) : []
            };
          }) : []
        }));
        
        dispatch({
          type: SEARCH_NEAREST_SHELTERS_SUCCESS,
          payload: transformedShelters
        });
      } else {
        throw new Error('No shelters found near this location');
      }
    } catch (error) {
      dispatch({
        type: SEARCH_NEAREST_SHELTERS_FAILURE,
        payload: error instanceof Error ? error.message : 'An unknown error occurred'
      });
    }
  };
};

// Action to set the selected shelter
export const setSelectedShelter = (shelter: Shelter) => {
  return {
    type: SET_SELECTED_SHELTER,
    payload: shelter
  };
};

// Action to clear the selected shelter
export const clearSelectedShelter = () => {
  return {
    type: CLEAR_SELECTED_SHELTER
  };
};

// Action to fetch the previous shelter based on mileage
export const fetchPreviousShelter = (currentMileage: number) => {
  return async (dispatch: Dispatch, getState: any) => {
    dispatch({ type: FETCH_SHELTER_DETAIL_REQUEST });

    try {
      // First check if we have states with shelters data
      const state = getState();
      const allStates = state.states.states;
      let allShelters: Shelter[] = [];
      
      // Extract all shelters from states data if available
      if (allStates && allStates.length > 0) {
        allStates.forEach((stateData: any) => {
          if (stateData.shelters && stateData.shelters.length > 0) {
            allShelters = [...allShelters, ...stateData.shelters];
          }
        });
      }
      
      // If we don't have enough shelters from states, try the cache
      if (allShelters.length < 5) {
        const sheltersInCache = Object.values(state.shelters.sheltersCache) as Shelter[];
        if (sheltersInCache.length > 5) {
          allShelters = sheltersInCache;
        }
      }
      
      // If we have shelters, try to find the previous one
      if (allShelters.length > 0) {
        console.log('Looking for previous shelter in available data. Current mileage:', currentMileage);
        
        const previousShelter = findAdjacentShelterFromCache(allShelters, currentMileage, false);
        
        if (previousShelter && previousShelter.shelterId) {
          // If we have this shelter with full details, use it directly
          if (previousShelter.dailyWeather && previousShelter.dailyWeather.length > 0) {
            console.log('Using cached previous shelter data:', previousShelter.name);
            
            dispatch({
              type: FETCH_SHELTER_DETAIL_SUCCESS,
              payload: previousShelter
            });
            
            return previousShelter;
          }
          
          // If we have the ID but not full details, fetch those details
          if (previousShelter.shelterId) {
            console.log('Found previous shelter ID, fetching details:', previousShelter.shelterId);
            // Instead of dispatch, call the API directly
            const sheltersData = await API.getShelter(previousShelter.shelterId);
            
            if (sheltersData && sheltersData.length > 0) {
              // Use the same transformation logic as fetchShelterDetail
              const shelterData: any = sheltersData[0];
              const transformedShelter = {
                shelterId: shelterData.shelter_id,
                name: shelterData.name,
                mileage: parseFloat(shelterData.mileage),
                elevation: shelterData.elevation,
                latitude: parseFloat(shelterData.latt || shelterData.latitude || 0),
                longitude: parseFloat(shelterData.long || shelterData.longitude || 0),
                dailyWeather: shelterData.daily_weather ? shelterData.daily_weather.map((weather: any) => {
                  const windData = parseWindData(weather.wind);
                  return {
                    dailyWeatherId: weather.daily_weather_id,
                    date: weather.weather_date,
                    high: weather.high,
                    low: weather.low,
                    description: weather.description,
                    icon: 'default-icon',
                    precipitation: 0,
                    windSpeed: windData.speed,
                    windDirection: windData.direction,
                    hourlyWeather: weather.hourly_weather ? weather.hourly_weather.map((hourly: any) => {
                      const hourlyWindData = parseWindData(hourly.wind);
                      return {
                        hourlyWeatherId: hourly.hourly_weather_id,
                        time: hourly.time || hourly.weather_time || hourly.date,
                        temperature: hourly.temperature || parseInt(hourly.temp) || 0,
                        description: hourly.description || '',
                        icon: hourly.icon || 'cloud',
                        precipitation: hourly.precipitation || 0,
                        windSpeed: hourlyWindData.speed,
                        windDirection: hourlyWindData.direction
                      };
                    }) : []
                  };
                }) : []
              };
              
              dispatch({
                type: FETCH_SHELTER_DETAIL_SUCCESS,
                payload: transformedShelter
              });
              
              return transformedShelter;
            }
          }
        }
      }
      
      // If we don't have enough data or couldn't find a shelter, fall back to API request
      console.log('Fetching previous shelter from API');
      const previousShelter = await API.findPreviousShelter(currentMileage);
      
      if (previousShelter && previousShelter.shelterId) {
        // Instead of dispatching fetchShelterDetail, call the API directly
        const sheltersData = await API.getShelter(previousShelter.shelterId);
        
        if (sheltersData && sheltersData.length > 0) {
          // Use the same transformation logic as fetchShelterDetail
          const shelterData: any = sheltersData[0];
          const transformedShelter = {
            shelterId: shelterData.shelter_id,
            name: shelterData.name,
            mileage: parseFloat(shelterData.mileage),
            elevation: shelterData.elevation,
            latitude: parseFloat(shelterData.latt || shelterData.latitude || 0),
            longitude: parseFloat(shelterData.long || shelterData.longitude || 0),
            dailyWeather: shelterData.daily_weather ? shelterData.daily_weather.map((weather: any) => {
              const windData = parseWindData(weather.wind);
              return {
                dailyWeatherId: weather.daily_weather_id,
                date: weather.weather_date,
                high: weather.high,
                low: weather.low,
                description: weather.description,
                icon: 'default-icon',
                precipitation: 0,
                windSpeed: windData.speed,
                windDirection: windData.direction,
                hourlyWeather: weather.hourly_weather ? weather.hourly_weather.map((hourly: any) => {
                  const hourlyWindData = parseWindData(hourly.wind);
                  return {
                    hourlyWeatherId: hourly.hourly_weather_id,
                    time: hourly.time || hourly.weather_time || hourly.date,
                    temperature: hourly.temperature || parseInt(hourly.temp) || 0,
                    description: hourly.description || '',
                    icon: hourly.icon || 'cloud',
                    precipitation: hourly.precipitation || 0,
                    windSpeed: hourlyWindData.speed,
                    windDirection: hourlyWindData.direction
                  };
                }) : []
              };
            }) : []
          };
          
          dispatch({
            type: FETCH_SHELTER_DETAIL_SUCCESS,
            payload: transformedShelter
          });
          
          return transformedShelter;
        } else {
          throw new Error('Previous shelter details not found');
        }
      } else {
        throw new Error('No previous shelter found or invalid shelter ID');
      }
    } catch (error) {
      dispatch({
        type: FETCH_SHELTER_DETAIL_FAILURE,
        payload: error instanceof Error ? error.message : 'An unknown error occurred'
      });
      return null;
    }
  };
};

// Action to fetch the next shelter based on mileage
export const fetchNextShelter = (currentMileage: number) => {
  return async (dispatch: Dispatch, getState: any) => {
    dispatch({ type: FETCH_SHELTER_DETAIL_REQUEST });

    try {
      // First check if we have states with shelters data
      const state = getState();
      const allStates = state.states.states;
      let allShelters: Shelter[] = [];
      
      // Extract all shelters from states data if available
      if (allStates && allStates.length > 0) {
        allStates.forEach((stateData: any) => {
          if (stateData.shelters && stateData.shelters.length > 0) {
            allShelters = [...allShelters, ...stateData.shelters];
          }
        });
      }
      
      // If we don't have enough shelters from states, try the cache
      if (allShelters.length < 5) {
        const sheltersInCache = Object.values(state.shelters.sheltersCache) as Shelter[];
        if (sheltersInCache.length > 5) {
          allShelters = sheltersInCache;
        }
      }
      
      // If we have shelters, try to find the next one
      if (allShelters.length > 0) {
        console.log('Looking for next shelter in available data. Current mileage:', currentMileage);
        
        const nextShelter = findAdjacentShelterFromCache(allShelters, currentMileage, true);
        
        if (nextShelter && nextShelter.shelterId) {
          // If we have this shelter with full details, use it directly
          if (nextShelter.dailyWeather && nextShelter.dailyWeather.length > 0) {
            console.log('Using cached next shelter data:', nextShelter.name);
            
            dispatch({
              type: FETCH_SHELTER_DETAIL_SUCCESS,
              payload: nextShelter
            });
            
            return nextShelter;
          }
          
          // If we have the ID but not full details, fetch those details
          if (nextShelter.shelterId) {
            console.log('Found next shelter ID, fetching details:', nextShelter.shelterId);
            // Instead of dispatch, call the API directly
            const sheltersData = await API.getShelter(nextShelter.shelterId);
            
            if (sheltersData && sheltersData.length > 0) {
              // Use the same transformation logic as fetchShelterDetail
              const shelterData: any = sheltersData[0];
              const transformedShelter = {
                shelterId: shelterData.shelter_id,
                name: shelterData.name,
                mileage: parseFloat(shelterData.mileage),
                elevation: shelterData.elevation,
                latitude: parseFloat(shelterData.latt || shelterData.latitude || 0),
                longitude: parseFloat(shelterData.long || shelterData.longitude || 0),
                dailyWeather: shelterData.daily_weather ? shelterData.daily_weather.map((weather: any) => {
                  const windData = parseWindData(weather.wind);
                  return {
                    dailyWeatherId: weather.daily_weather_id,
                    date: weather.weather_date,
                    high: weather.high,
                    low: weather.low,
                    description: weather.description,
                    icon: 'default-icon',
                    precipitation: 0,
                    windSpeed: windData.speed,
                    windDirection: windData.direction,
                    hourlyWeather: weather.hourly_weather ? weather.hourly_weather.map((hourly: any) => {
                      const hourlyWindData = parseWindData(hourly.wind);
                      return {
                        hourlyWeatherId: hourly.hourly_weather_id,
                        time: hourly.time || hourly.weather_time || hourly.date,
                        temperature: hourly.temperature || parseInt(hourly.temp) || 0,
                        description: hourly.description || '',
                        icon: hourly.icon || 'cloud',
                        precipitation: hourly.precipitation || 0,
                        windSpeed: hourlyWindData.speed,
                        windDirection: hourlyWindData.direction
                      };
                    }) : []
                  };
                }) : []
              };
              
              dispatch({
                type: FETCH_SHELTER_DETAIL_SUCCESS,
                payload: transformedShelter
              });
              
              return transformedShelter;
            }
          }
        }
      }
      
      // If we don't have enough data or couldn't find a shelter, fall back to API request
      console.log('Fetching next shelter from API');
      const nextShelter = await API.findNextShelter(currentMileage);
      
      if (nextShelter && nextShelter.shelterId) {
        // Instead of dispatching fetchShelterDetail, call the API directly
        const sheltersData = await API.getShelter(nextShelter.shelterId);
        
        if (sheltersData && sheltersData.length > 0) {
          // Use the same transformation logic as fetchShelterDetail
          const shelterData: any = sheltersData[0];
          const transformedShelter = {
            shelterId: shelterData.shelter_id,
            name: shelterData.name,
            mileage: parseFloat(shelterData.mileage),
            elevation: shelterData.elevation,
            latitude: parseFloat(shelterData.latt || shelterData.latitude || 0),
            longitude: parseFloat(shelterData.long || shelterData.longitude || 0),
            dailyWeather: shelterData.daily_weather ? shelterData.daily_weather.map((weather: any) => {
              const windData = parseWindData(weather.wind);
              return {
                dailyWeatherId: weather.daily_weather_id,
                date: weather.weather_date,
                high: weather.high,
                low: weather.low,
                description: weather.description,
                icon: 'default-icon',
                precipitation: 0,
                windSpeed: windData.speed,
                windDirection: windData.direction,
                hourlyWeather: weather.hourly_weather ? weather.hourly_weather.map((hourly: any) => {
                  const hourlyWindData = parseWindData(hourly.wind);
                  return {
                    hourlyWeatherId: hourly.hourly_weather_id,
                    time: hourly.time || hourly.weather_time || hourly.date,
                    temperature: hourly.temperature || parseInt(hourly.temp) || 0,
                    description: hourly.description || '',
                    icon: hourly.icon || 'cloud',
                    precipitation: hourly.precipitation || 0,
                    windSpeed: hourlyWindData.speed,
                    windDirection: hourlyWindData.direction
                  };
                }) : []
              };
            }) : []
          };
          
          dispatch({
            type: FETCH_SHELTER_DETAIL_SUCCESS,
            payload: transformedShelter
          });
          
          return transformedShelter;
        } else {
          throw new Error('Next shelter details not found');
        }
      } else {
        throw new Error('No next shelter found or invalid shelter ID');
      }
    } catch (error) {
      dispatch({
        type: FETCH_SHELTER_DETAIL_FAILURE,
        payload: error instanceof Error ? error.message : 'An unknown error occurred'
      });
      return null;
    }
  };
}; 