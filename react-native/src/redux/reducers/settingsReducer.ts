import { SET_UNIT_TYPE, SET_THEME_TYPE } from '../actions/types';
import { SettingsState, UnitType, ThemeType } from '../../types';

// Initial state
const initialState: SettingsState = {
  unitType: UnitType.FAHRENHEIT, // Default to Fahrenheit
  themeType: ThemeType.SYSTEM // Default to system theme
};

// Settings reducer
const settingsReducer = (state = initialState, action: any): SettingsState => {
  switch (action.type) {
    case SET_UNIT_TYPE:
      return {
        ...state,
        unitType: action.payload
      };
    case SET_THEME_TYPE:
      return {
        ...state,
        themeType: action.payload
      };
    default:
      return state;
  }
};

export default settingsReducer; 