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
  CLEAR_SELECTED_SHELTER,
  LOAD_CACHE_SUCCESS,
  SAVE_CACHE_REQUEST
} from '../actions/types';
import { SheltersState, Shelter } from '../../types';

// Initial state
const initialState: SheltersState = {
  loading: false,
  error: null,
  shelters: [],
  sheltersCache: {},
  selectedShelter: null,
  cacheLoaded: false
};

// Shelters reducer
const sheltersReducer = (state = initialState, action: any): SheltersState => {
  switch (action.type) {
    case FETCH_SHELTERS_REQUEST:
    case FETCH_SHELTER_DETAIL_REQUEST:
    case SEARCH_SHELTER_BY_MILEAGE_REQUEST:
    case SEARCH_NEAREST_SHELTERS_REQUEST:
      return {
        ...state,
        loading: true,
        error: null
      };
    case FETCH_SHELTERS_SUCCESS:
    case SEARCH_NEAREST_SHELTERS_SUCCESS: {
      const newCache = { ...state.sheltersCache };
      action.payload.forEach((shelter: Shelter) => {
        if (shelter.shelterId) {
          newCache[shelter.shelterId] = shelter;
        }
      });

      return {
        ...state,
        loading: false,
        error: null,
        shelters: action.payload,
        sheltersCache: newCache
      };
    }
    case FETCH_SHELTER_DETAIL_SUCCESS:
    case SEARCH_SHELTER_BY_MILEAGE_SUCCESS: {
      const updatedCache = { ...state.sheltersCache };
      if (action.payload && action.payload.shelterId) {
        updatedCache[action.payload.shelterId] = action.payload;
      }

      return {
        ...state,
        loading: false,
        error: null,
        selectedShelter: action.payload,
        sheltersCache: updatedCache
      };
    }
    case FETCH_SHELTERS_FAILURE:
    case FETCH_SHELTER_DETAIL_FAILURE:
    case SEARCH_SHELTER_BY_MILEAGE_FAILURE:
    case SEARCH_NEAREST_SHELTERS_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload
      };
    case SET_SELECTED_SHELTER: {
      const updatedCache = { ...state.sheltersCache };
      if (action.payload && action.payload.shelterId) {
        updatedCache[action.payload.shelterId] = action.payload;
      }
      
      return {
        ...state,
        selectedShelter: action.payload,
        sheltersCache: updatedCache
      };
    }
    case CLEAR_SELECTED_SHELTER:
      return {
        ...state,
        selectedShelter: null
      };
    case LOAD_CACHE_SUCCESS:
      return {
        ...state,
        sheltersCache: action.payload || {},
        cacheLoaded: true
      };
    case SAVE_CACHE_REQUEST:
      return state;
    default:
      return state;
  }
};

export default sheltersReducer; 