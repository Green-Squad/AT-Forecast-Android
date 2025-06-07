import { createStore, combineReducers, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import statesReducer from './reducers/statesReducer';
import sheltersReducer from './reducers/sheltersReducer';
import settingsReducer from './reducers/settingsReducer';
import cacheMiddleware from './middleware/cacheMiddleware';
import { RootState } from '../types';

// Combine all reducers
const rootReducer = combineReducers<RootState>({
  states: statesReducer,
  shelters: sheltersReducer,
  settings: settingsReducer
});

// Create store with thunk middleware
const store = createStore(
  rootReducer,
  applyMiddleware(thunk, cacheMiddleware)
);

export default store; 