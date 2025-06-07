// State model
export interface State {
  stateId: number;
  name: string;
  averageHigh: number;
  averageLow: number;
  shelters?: Shelter[];
  updatedAt?: string;
}

// Shelter model
export interface Shelter {
  shelterId: number;
  name: string;
  mileage: number;
  elevation: number;
  dailyWeather?: DailyWeather[];
  latitude: number;
  longitude: number;
  stateId?: number;
}

// DailyWeather model
export interface DailyWeather {
  dailyWeatherId?: number;
  shelterId?: number;
  date: string;
  high: number;
  low: number;
  description: string;
  icon: string;
  precipitation: number;
  windSpeed: number;
  windDirection: string;
  hourlyWeather?: HourlyWeather[];
}

// HourlyWeather model
export interface HourlyWeather {
  hourlyWeatherId?: number;
  dailyWeatherId?: number;
  time: string;
  temperature: number;
  description: string;
  icon: string;
  precipitation: number;
  windSpeed: number;
  windDirection: string;
}

// Units type
export enum UnitType {
  FAHRENHEIT = 0,
  CELSIUS = 1
}

// Theme type
export enum ThemeType {
  LIGHT = 'light',
  DARK = 'dark',
  SYSTEM = 'system'
}

// App Settings
export interface AppSettings {
  unitType: UnitType;
  themeType: ThemeType;
}

// API Response types
export interface StateListResponse {
  states: State[];
}

export interface ShelterResponse {
  shelter: Shelter;
}

export interface ShelterListResponse {
  shelters: Shelter[];
}

// Redux state types
export interface RootState {
  states: StatesState;
  shelters: SheltersState;
  settings: SettingsState;
}

export interface StatesState {
  loading: boolean;
  error: string | null;
  states: State[];
}

export interface SheltersState {
  loading: boolean;
  error: string | null;
  shelters: Shelter[];
  sheltersCache: { [key: number]: Shelter };
  selectedShelter: Shelter | null;
  cacheLoaded: boolean;
}

export interface SettingsState {
  unitType: UnitType;
  themeType: ThemeType;
}

// Navigation types
export type RootStackParamList = {
  Home: undefined;
  StateList: undefined;
  ShelterList: { stateId: number; stateName: string };
  ShelterDetail: { shelterId: number };
  Settings: undefined;
}; 