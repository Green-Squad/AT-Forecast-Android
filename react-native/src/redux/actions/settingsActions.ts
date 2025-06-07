import { SET_UNIT_TYPE, SET_THEME_TYPE } from './types';
import { UnitType, ThemeType } from '../../types';

// Action to set the unit type (Fahrenheit or Celsius)
export const setUnitType = (unitType: UnitType) => {
  return {
    type: SET_UNIT_TYPE,
    payload: unitType
  };
};

// Action to set the theme type (light, dark, or system)
export const setThemeType = (themeType: ThemeType) => {
  return {
    type: SET_THEME_TYPE,
    payload: themeType
  };
}; 