import { 
  FETCH_STATES_REQUEST, 
  FETCH_STATES_SUCCESS, 
  FETCH_STATES_FAILURE 
} from '../actions/types';
import { StatesState } from '../../types';

// Initial state
const initialState: StatesState = {
  loading: false,
  error: null,
  states: []
};

// States reducer
const statesReducer = (state = initialState, action: any): StatesState => {
  switch (action.type) {
    case FETCH_STATES_REQUEST:
      return {
        ...state,
        loading: true,
        error: null
      };
    case FETCH_STATES_SUCCESS:
      return {
        ...state,
        loading: false,
        error: null,
        states: action.payload
      };
    case FETCH_STATES_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
        states: []
      };
    default:
      return state;
  }
};

export default statesReducer; 