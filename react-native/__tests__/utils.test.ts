import { 
  toCelsius, 
  toFahrenheit, 
  formatTemperature, 
  formatDate, 
  formatTime,
  formatElevation,
  formatMileage
} from '../src/utils/utils';
import { UnitType } from '../src/types';

describe('Utils', () => {
  describe('toCelsius', () => {
    it('should convert Fahrenheit to Celsius correctly', () => {
      expect(toCelsius(32)).toBe(0);
      expect(toCelsius(212)).toBe(100);
      expect(toCelsius(68)).toBe(20);
    });
  });

  describe('toFahrenheit', () => {
    it('should convert Celsius to Fahrenheit correctly', () => {
      expect(toFahrenheit(0)).toBe(32);
      expect(toFahrenheit(100)).toBe(212);
      expect(toFahrenheit(20)).toBe(68);
    });
  });

  describe('formatTemperature', () => {
    it('should format temperature with Fahrenheit symbol when unit is Fahrenheit', () => {
      expect(formatTemperature(72, UnitType.FAHRENHEIT)).toBe('72°F');
    });

    it('should convert and format temperature with Celsius symbol when unit is Celsius', () => {
      expect(formatTemperature(68, UnitType.CELSIUS)).toBe('20°C');
    });
  });

  describe('formatDate', () => {
    it('should format date string correctly', () => {
      const date = new Date('2023-07-15');
      const formattedDate = formatDate('2023-07-15');
      expect(formattedDate).toMatch(/\w{3}, \w{3} \d{1,2}/); // e.g., "Sat, Jul 15"
    });
  });

  describe('formatTime', () => {
    it('should format time string correctly', () => {
      expect(formatTime('13:30')).toBe('1:30 PM');
      expect(formatTime('09:05')).toBe('9:05 AM');
      expect(formatTime('00:00')).toBe('12:00 AM');
    });
  });

  describe('formatElevation', () => {
    it('should format elevation with feet when unit is Fahrenheit', () => {
      expect(formatElevation(1000, UnitType.FAHRENHEIT)).toBe('1000 ft');
    });

    it('should convert and format elevation with meters when unit is Celsius', () => {
      expect(formatElevation(1000, UnitType.CELSIUS)).toBe('305 m');
    });
  });

  describe('formatMileage', () => {
    it('should format mileage with one decimal place', () => {
      expect(formatMileage(123.45)).toBe('123.5 mi');
      expect(formatMileage(123)).toBe('123.0 mi');
    });
  });
}); 