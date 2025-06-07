import { Dispatch } from 'redux';
import {
  LOAD_CACHE_REQUEST,
  LOAD_CACHE_SUCCESS,
  LOAD_CACHE_FAILURE,
  SAVE_CACHE_REQUEST,
  SAVE_CACHE_SUCCESS,
  SAVE_CACHE_FAILURE,
  CLEAR_CACHE_REQUEST,
  CLEAR_CACHE_SUCCESS,
  CLEAR_CACHE_FAILURE
} from './types';
import { Shelter } from '../../types';
import { 
  loadShelterCache,
  saveShelterCache,
  clearShelterCache
} from '../../utils/storageUtils';

/**
 * Action to load shelter cache from AsyncStorage
 */
export const loadCache = () => {
  return async (dispatch: Dispatch) => {
    dispatch({ type: LOAD_CACHE_REQUEST });

    try {
      const cachedData = await loadShelterCache();
      
      dispatch({
        type: LOAD_CACHE_SUCCESS,
        payload: cachedData
      });
      
      return cachedData;
    } catch (error) {
      dispatch({
        type: LOAD_CACHE_FAILURE,
        payload: error instanceof Error ? error.message : 'Failed to load cache'
      });
      
      return null;
    }
  };
};

/**
 * Action to save shelter cache to AsyncStorage
 * @param sheltersCache The shelter cache to save
 */
export const saveCache = (sheltersCache: { [key: number]: Shelter }) => {
  return async (dispatch: Dispatch) => {
    dispatch({ type: SAVE_CACHE_REQUEST });

    try {
      const success = await saveShelterCache(sheltersCache);
      
      if (success) {
        dispatch({ type: SAVE_CACHE_SUCCESS });
      } else {
        throw new Error('Failed to save cache');
      }
      
      return success;
    } catch (error) {
      dispatch({
        type: SAVE_CACHE_FAILURE,
        payload: error instanceof Error ? error.message : 'Failed to save cache'
      });
      
      return false;
    }
  };
};

/**
 * Action to clear shelter cache from AsyncStorage
 */
export const clearCache = () => {
  return async (dispatch: Dispatch) => {
    dispatch({ type: CLEAR_CACHE_REQUEST });

    try {
      const success = await clearShelterCache();
      
      if (success) {
        dispatch({ type: CLEAR_CACHE_SUCCESS });
      } else {
        throw new Error('Failed to clear cache');
      }
      
      return success;
    } catch (error) {
      dispatch({
        type: CLEAR_CACHE_FAILURE,
        payload: error instanceof Error ? error.message : 'Failed to clear cache'
      });
      
      return false;
    }
  };
}; 