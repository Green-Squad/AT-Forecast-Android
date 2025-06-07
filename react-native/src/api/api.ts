import axios from 'axios';
import { State, Shelter } from '../types';

// API base URL, matching the native app
const BASE_URL = 'https://www.atforecast.app/';

// Hardcoded API key as fallback
// In production, this should use secure environment variables
const API_KEY = "jLEy94zVGv";

// Debug log the API key (remove in production)
console.log('Using API Key:', API_KEY);

// Create axios instance
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor for debugging
apiClient.interceptors.request.use(request => {
  console.log('Making request to:', request.url);
  console.log('With params:', request.params);
  return request;
});

// Add response interceptor for debugging
apiClient.interceptors.response.use(
  response => {
    console.log('Response received:', response.status);
    return response;
  },
  error => {
    console.error('API Error:', error.response ? error.response.status : error.message);
    console.error('Error details:', error.response ? error.response.data : 'No response data');
    return Promise.reject(error);
  }
);

// API endpoints
export const API = {
  // Get all states with optional shelter data
  getStates: async (includeShelters: boolean = false): Promise<State[]> => {
    try {
      // Try a different way of passing the API key
      const url = `/index.json?api_key=${encodeURIComponent(API_KEY)}&include_shelters=${includeShelters}`;
      console.log('Requesting URL:', BASE_URL + url);
      console.log('Fetching states with includeShelters =', includeShelters);
      
      const response = await apiClient.get(url);
      return response.data;
    } catch (error) {
      console.error('Error fetching states:', error);
      throw error;
    }
  },

  // Get a specific shelter by ID with optional distance parameter
  getShelter: async (id: number, distMiles?: number): Promise<Shelter[]> => {
    try {
      const url = `/shelters/${id}.json?api_key=${encodeURIComponent(API_KEY)}${distMiles ? `&dist_miles=${distMiles}` : ''}`;
      const response = await apiClient.get(url);
      return response.data;
    } catch (error) {
      console.error(`Error fetching shelter with ID ${id}:`, error);
      throw error;
    }
  },

  // Find nearest shelters by coordinates
  findNearestShelters: async (latitude: number, longitude: number): Promise<Shelter[]> => {
    try {
      // This endpoint would need to be implemented on the server side
      // For now, we'll retrieve all shelters and filter on the client side
      const allStates = await API.getStates(true);
      const allShelters: Shelter[] = [];
      
      allStates.forEach(state => {
        if (state.shelters && state.shelters.length > 0) {
          allShelters.push(...state.shelters);
        }
      });
      
      // Find nearest shelter using Haversine formula
      const nearest = findNearestShelterByCoords(allShelters, latitude, longitude);
      
      return nearest;
    } catch (error) {
      console.error('Error finding nearest shelters:', error);
      throw error;
    }
  },

  // Find shelter by mileage
  findShelterByMileage: async (mileage: number): Promise<Shelter | null> => {
    try {
      console.log('Finding shelter near mileage:', mileage);
      
      // We'll always include shelters when fetching states for mileage searches
      const allStates = await API.getStates(true);
      const allShelters: Shelter[] = [];
      
      allStates.forEach(state => {
        if (state.shelters && state.shelters.length > 0) {
          allShelters.push(...state.shelters);
        }
      });
      
      // Sort shelters by how close their mileage is to the target
      const sortedShelters = [...allShelters].sort((a, b) => {
        return Math.abs(a.mileage - mileage) - Math.abs(b.mileage - mileage);
      });
      
      return sortedShelters.length > 0 ? sortedShelters[0] : null;
    } catch (error) {
      console.error('Error finding shelter by mileage:', error);
      throw error;
    }
  },

  // Find the previous shelter based on mileage
  findPreviousShelter: async (currentMileage: number): Promise<Shelter | null> => {
    try {
      console.log('Finding previous shelter from mileage:', currentMileage);
      
      // We'll always include shelters when fetching states for navigation
      const allStates = await API.getStates(true);
      const allShelters: Shelter[] = [];
      
      allStates.forEach(state => {
        if (state.shelters && state.shelters.length > 0) {
          // Handle raw API data which might have shelter_id instead of shelterId
          state.shelters.forEach((shelter: any) => {
            allShelters.push({
              shelterId: shelter.shelterId || shelter.shelter_id,
              name: shelter.name,
              mileage: parseFloat(shelter.mileage),
              elevation: shelter.elevation,
              latitude: parseFloat(shelter.latitude || shelter.latt || 0),
              longitude: parseFloat(shelter.longitude || shelter.long || 0)
            });
          });
        }
      });
      
      // Filter shelters with lower mileage
      const previousShelters = allShelters.filter(shelter => 
        shelter.mileage < currentMileage && shelter.shelterId
      );
      
      if (previousShelters.length === 0) {
        return null;
      }
      
      // Find the shelter with the highest mileage among those with lower mileage
      const previousShelter = previousShelters.reduce((prev, current) => 
        (prev.mileage > current.mileage) ? prev : current
      );
      
      return previousShelter;
    } catch (error) {
      console.error('Error finding previous shelter:', error);
      throw error;
    }
  },

  // Find the next shelter based on mileage
  findNextShelter: async (currentMileage: number): Promise<Shelter | null> => {
    try {
      console.log('Finding next shelter from mileage:', currentMileage);
      
      // We'll always include shelters when fetching states for navigation
      const allStates = await API.getStates(true);
      const allShelters: Shelter[] = [];
      
      allStates.forEach(state => {
        if (state.shelters && state.shelters.length > 0) {
          // Handle raw API data which might have shelter_id instead of shelterId
          state.shelters.forEach((shelter: any) => {
            allShelters.push({
              shelterId: shelter.shelterId || shelter.shelter_id,
              name: shelter.name,
              mileage: parseFloat(shelter.mileage),
              elevation: shelter.elevation,
              latitude: parseFloat(shelter.latitude || shelter.latt || 0),
              longitude: parseFloat(shelter.longitude || shelter.long || 0)
            });
          });
        }
      });
      
      // Filter shelters with higher mileage
      const nextShelters = allShelters.filter(shelter => 
        shelter.mileage > currentMileage && shelter.shelterId
      );
      
      if (nextShelters.length === 0) {
        return null;
      }
      
      // Find the shelter with the lowest mileage among those with higher mileage
      const nextShelter = nextShelters.reduce((prev, current) => 
        (prev.mileage < current.mileage) ? prev : current
      );
      
      return nextShelter;
    } catch (error) {
      console.error('Error finding next shelter:', error);
      throw error;
    }
  }
};

// Helper function to find nearest shelter by coordinates
// Uses Haversine formula to calculate distance between two coordinates
function findNearestShelterByCoords(shelters: Shelter[], latitude: number, longitude: number): Shelter[] {
  // Calculate distances
  const sheltersWithDistance = shelters.map(shelter => {
    const distance = haversine(
      [latitude, longitude],
      [shelter.latitude, shelter.longitude]
    );
    return { ...shelter, distance };
  });

  // Sort by distance
  const sortedShelters = [...sheltersWithDistance].sort((a, b) => {
    return (a.distance as number) - (b.distance as number);
  });

  // Find the nearest shelter
  const nearest = sortedShelters[0];
  const nearestMileage = nearest.mileage;

  // Get previous shelters (up to 2)
  const previousShelters = shelters
    .filter(shelter => shelter.mileage < nearestMileage)
    .sort((a, b) => b.mileage - a.mileage)
    .slice(0, 2);

  // Get next shelters (up to 2)
  const nextShelters = shelters
    .filter(shelter => shelter.mileage > nearestMileage)
    .sort((a, b) => a.mileage - b.mileage)
    .slice(0, 2);

  // Combine the lists
  return [...previousShelters, nearest, ...nextShelters];
}

// Haversine formula to calculate distance between two coordinates
function haversine(start: number[], end: number[]): number {
  const R = 6372.8; // Earth radius in kilometers
  const lat1 = start[0];
  const lat2 = end[0];
  const lon1 = start[1];
  const lon2 = end[1];

  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a = Math.pow(Math.sin(dLat / 2), 2) +
    Math.pow(Math.sin(dLon / 2), 2) * Math.cos(toRad(lat1)) * Math.cos(toRad(lat2));
  const c = 2 * Math.asin(Math.sqrt(a));
  
  return R * c;
}

// Convert degrees to radians
function toRad(degrees: number): number {
  return degrees * Math.PI / 180;
}

export default API; 