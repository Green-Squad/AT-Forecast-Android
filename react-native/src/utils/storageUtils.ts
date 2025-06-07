import AsyncStorage from '@react-native-async-storage/async-storage';
import { Shelter } from '../types';

// Storage keys
const SHELTER_CACHE_KEY = 'atforecast_shelter_cache';
const LAST_UPDATED_KEY = 'atforecast_cache_last_updated';
const CACHE_EXPIRY_HOURS = 24; // Cache expires after 24 hours

/**
 * Save shelter cache to AsyncStorage
 * @param sheltersCache Object containing shelters by ID
 */
export const saveShelterCache = async (sheltersCache: { [key: number]: Shelter }): Promise<boolean> => {
  try {
    // Store the cache
    await AsyncStorage.setItem(SHELTER_CACHE_KEY, JSON.stringify(sheltersCache));
    
    // Update the last updated timestamp
    await AsyncStorage.setItem(LAST_UPDATED_KEY, Date.now().toString());
    
    console.log('Shelter cache saved to storage');
    return true;
  } catch (error) {
    console.error('Error saving shelter cache to storage:', error);
    return false;
  }
};

/**
 * Load shelter cache from AsyncStorage
 * @returns The shelter cache or null if not found or expired
 */
export const loadShelterCache = async (): Promise<{ [key: number]: Shelter } | null> => {
  try {
    // Check when the cache was last updated
    const lastUpdated = await AsyncStorage.getItem(LAST_UPDATED_KEY);
    
    if (lastUpdated) {
      const lastUpdatedTime = parseInt(lastUpdated);
      const now = Date.now();
      const hoursSinceUpdate = (now - lastUpdatedTime) / (1000 * 60 * 60);
      
      // If cache is older than expiry time, consider it invalid
      if (hoursSinceUpdate > CACHE_EXPIRY_HOURS) {
        console.log('Shelter cache expired, will fetch fresh data');
        return null;
      }
    }
    
    // Get the cached data
    const cachedData = await AsyncStorage.getItem(SHELTER_CACHE_KEY);
    
    if (cachedData) {
      const sheltersCache = JSON.parse(cachedData);
      console.log('Loaded shelter cache from storage');
      return sheltersCache;
    }
    
    return null;
  } catch (error) {
    console.error('Error loading shelter cache from storage:', error);
    return null;
  }
};

/**
 * Clear the shelter cache from AsyncStorage
 */
export const clearShelterCache = async (): Promise<boolean> => {
  try {
    await AsyncStorage.removeItem(SHELTER_CACHE_KEY);
    await AsyncStorage.removeItem(LAST_UPDATED_KEY);
    console.log('Shelter cache cleared from storage');
    return true;
  } catch (error) {
    console.error('Error clearing shelter cache from storage:', error);
    return false;
  }
};

/**
 * Get the age of the cache in hours
 * @returns The age of the cache in hours or -1 if no cache exists
 */
export const getCacheAge = async (): Promise<number> => {
  try {
    const lastUpdated = await AsyncStorage.getItem(LAST_UPDATED_KEY);
    
    if (lastUpdated) {
      const lastUpdatedTime = parseInt(lastUpdated);
      const now = Date.now();
      const hoursSinceUpdate = (now - lastUpdatedTime) / (1000 * 60 * 60);
      return hoursSinceUpdate;
    }
    
    return -1;
  } catch (error) {
    console.error('Error getting cache age:', error);
    return -1;
  }
}; 