import { Dispatch } from 'redux';
import { 
  FETCH_STATES_REQUEST, 
  FETCH_STATES_SUCCESS, 
  FETCH_STATES_FAILURE 
} from './types';
import API from '../../api/api';
import { State, Shelter } from '../../types';

// Helper function to transform API response data to match our State interface
const transformStateData = (states: any[]): State[] => {
  return states.map(state => ({
    stateId: parseInt(state.state_id),
    name: state.name,
    averageHigh: state.average_high,
    averageLow: state.average_low,
    shelters: state.shelters ? state.shelters.map((shelter: any) => ({
      shelterId: parseInt(shelter.shelter_id),
      name: shelter.name,
      mileage: parseFloat(shelter.mileage),
      elevation: shelter.elevation,
      latitude: parseFloat(shelter.latt || shelter.latitude || 0),
      longitude: parseFloat(shelter.long || shelter.longitude || 0),
      dailyWeather: shelter.daily_weather || []
    })) : undefined
  }));
};

// Extract all shelters from states for caching
const extractSheltersFromStates = (states: State[]): { [key: number]: Shelter } => {
  const sheltersMap: { [key: number]: Shelter } = {};
  
  states.forEach(state => {
    if (state.shelters && state.shelters.length > 0) {
      state.shelters.forEach(shelter => {
        if (shelter.shelterId) {
          sheltersMap[shelter.shelterId] = shelter;
        }
      });
    }
  });
  
  return sheltersMap;
};

// Action to fetch all states
export const fetchStates = (includeShelters: boolean = false) => {
  return async (dispatch: Dispatch, getState: any) => {
    dispatch({ type: FETCH_STATES_REQUEST });

    try {
      console.log('Fetching states with includeShelters =', includeShelters);
      const statesData = await API.getStates(includeShelters);
      // Transform the data to match our State interface
      const transformedStates = transformStateData(statesData);
      
      dispatch({
        type: FETCH_STATES_SUCCESS,
        payload: transformedStates
      });
      
      // If shelters were included, update the shelter cache
      if (includeShelters) {
        const sheltersMap = extractSheltersFromStates(transformedStates);
        console.log(`Extracted ${Object.keys(sheltersMap).length} shelters from states for cache`);
        
        // Merge with existing cache
        const state = getState();
        const existingCache = state.shelters.sheltersCache || {};
        const updatedCache = { ...existingCache, ...sheltersMap };
        
        // Update the cache
        dispatch({
          type: 'SAVE_CACHE_REQUEST',
          payload: updatedCache
        });
      }
    } catch (error) {
      dispatch({
        type: FETCH_STATES_FAILURE,
        payload: error instanceof Error ? error.message : 'An unknown error occurred'
      });
    }
  };
}; 