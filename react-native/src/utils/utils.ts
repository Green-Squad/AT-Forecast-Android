import { UnitType } from '../types';

/**
 * Converts Fahrenheit to Celsius
 * @param fahrenheit Temperature in Fahrenheit
 * @returns Temperature in Celsius (rounded to nearest integer)
 */
export const toCelsius = (fahrenheit: number): number => {
  return Math.round((fahrenheit - 32) * 5 / 9);
};

/**
 * Converts Celsius to Fahrenheit
 * @param celsius Temperature in Celsius
 * @returns Temperature in Fahrenheit (rounded to nearest integer)
 */
export const toFahrenheit = (celsius: number): number => {
  return Math.round((celsius * 9 / 5) + 32);
};

/**
 * Gets temperature in the specified unit
 * @param temperature Temperature in Fahrenheit
 * @param unitType Unit type to convert to
 * @returns Temperature in the specified unit
 */
export const getTemperatureInUnit = (temperature: number, unitType: UnitType): number => {
  return unitType === UnitType.CELSIUS ? toCelsius(temperature) : temperature;
};

/**
 * Formats temperature for display with the appropriate unit symbol
 * @param temperature Temperature in Fahrenheit
 * @param unitType Unit type to display
 * @returns Formatted temperature string with unit symbol
 */
export const formatTemperature = (temperature: number | undefined | null, unitType: UnitType): string => {
  if (temperature === undefined || temperature === null) {
    return 'N/A';
  }
  const convertedTemp = getTemperatureInUnit(temperature, unitType);
  const unitSymbol = unitType === UnitType.CELSIUS ? '°C' : '°F';
  return `${convertedTemp}${unitSymbol}`;
};

/**
 * Formats a date string (YYYY-MM-DD) to a more readable format
 * @param dateString Date string in YYYY-MM-DD format
 * @returns Formatted date string (e.g., "Mon, Jan 1")
 */
export const formatDate = (dateString: string | undefined | null): string => {
  if (!dateString) return 'N/A';
  
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return dateString;
    }
    
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric'
    });
  } catch (error) {
    console.log('Error formatting date:', error);
    return dateString || 'N/A';
  }
};

/**
 * Formats a time string (HH:MM) to a more readable format
 * @param timeString Time string in 24-hour format (HH:MM) or ISO format (YYYY-MM-DDTHH:MM:SS)
 * @returns Formatted time string in 12-hour format with AM/PM
 */
export const formatTime = (timeString: string | undefined | null): string => {
  if (!timeString) return 'N/A';
  
  try {
    let hours = 0;
    let minutes = 0;
    
    // Handle ISO format (YYYY-MM-DDTHH:MM:SS)
    if (timeString.includes('T')) {
      const timePart = timeString.split('T')[1];
      const timeSplit = timePart.substring(0, 5).split(':');
      hours = parseInt(timeSplit[0]);
      minutes = parseInt(timeSplit[1]);
    } 
    // Handle HH:MM format
    else if (timeString.includes(':')) {
      const [hoursStr, minutesStr] = timeString.split(':');
      hours = parseInt(hoursStr);
      minutes = parseInt(minutesStr);
    } 
    // If it's not in a recognized format, return as is
    else {
      return timeString;
    }
    
    if (isNaN(hours) || isNaN(minutes)) {
      return timeString;
    }
    
    const period = hours >= 12 ? 'PM' : 'AM';
    const formattedHours = hours % 12 || 12;
    return `${formattedHours}:${minutes.toString().padStart(2, '0')} ${period}`;
  } catch (error) {
    console.log('Error formatting time:', error);
    return timeString || 'N/A';
  }
};

/**
 * Formats elevation for display with the appropriate unit
 * @param elevation Elevation in feet
 * @param unitType Unit type to display
 * @returns Formatted elevation string with unit
 */
export const formatElevation = (elevation: number, unitType: UnitType): string => {
  if (unitType === UnitType.CELSIUS) {
    // Convert to meters
    const meters = Math.round(elevation * 0.3048);
    return `${meters} m`;
  }
  return `${elevation} ft`;
};

/**
 * Formats mileage for display
 * @param mileage Mileage as a number
 * @returns Formatted mileage string with unit
 */
export const formatMileage = (mileage: number): string => {
  return `${mileage.toFixed(1)} mi`;
}; 