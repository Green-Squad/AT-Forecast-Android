import { Middleware } from 'redux';
import { 
  SAVE_CACHE_REQUEST, 
  SAVE_CACHE_SUCCESS, 
  SAVE_CACHE_FAILURE 
} from '../actions/types';
import { saveShelterCache } from '../../utils/storageUtils';

/**
 * Middleware to handle persisting cache to AsyncStorage
 */
const cacheMiddleware: Middleware = ({ dispatch }) => next => async action => {
  // Let the action go through first
  const result = next(action);
  
  // Then handle cache persistence
  if (action.type === SAVE_CACHE_REQUEST && action.payload) {
    try {
      console.log('Saving cache to AsyncStorage...');
      const success = await saveShelterCache(action.payload);
      
      if (success) {
        dispatch({ type: SAVE_CACHE_SUCCESS });
        console.log('Cache saved successfully');
      } else {
        throw new Error('Failed to save cache');
      }
    } catch (error) {
      console.error('Error saving cache:', error);
      dispatch({ 
        type: SAVE_CACHE_FAILURE, 
        payload: error instanceof Error ? error.message : 'Unknown error saving cache' 
      });
    }
  }
  
  return result;
};

export default cacheMiddleware; 