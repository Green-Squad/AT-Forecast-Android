import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Switch,
  useColorScheme
} from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { Ionicons } from '@expo/vector-icons';

import { setUnitType, setThemeType } from '../redux/actions/settingsActions';
import { RootState, UnitType, ThemeType } from '../types';

// Settings screen component
const SettingsScreen = () => {
  const dispatch = useDispatch();
  const systemColorScheme = useColorScheme();
  
  // Get settings from Redux store
  const { unitType, themeType } = useSelector((state: RootState) => state.settings);
  
  // Determine if dark mode should be used
  const isDarkMode = themeType === ThemeType.DARK || 
    (themeType === ThemeType.SYSTEM && systemColorScheme === 'dark');
  
  // Toggle unit type between Fahrenheit and Celsius
  const handleUnitTypeToggle = () => {
    const newUnitType = unitType === UnitType.FAHRENHEIT ? UnitType.CELSIUS : UnitType.FAHRENHEIT;
    dispatch(setUnitType(newUnitType));
  };
  
  // Set theme type
  const handleThemeChange = (type: ThemeType) => {
    dispatch(setThemeType(type));
  };
  
  return (
    <View style={[styles.container, isDarkMode && styles.darkContainer]}>
      <View style={[styles.section, isDarkMode && styles.darkSection]}>
        <Text style={[styles.sectionTitle, isDarkMode && styles.darkText]}>Display</Text>
        
        <View style={styles.settingItem}>
          <View style={styles.settingLabelContainer}>
            <Ionicons name="moon" size={22} color="#3f51b5" />
            <Text style={[styles.settingLabel, isDarkMode && styles.darkText]}>Theme</Text>
          </View>
          <View style={styles.themeButtonsContainer}>
            <TouchableOpacity
              style={[
                styles.themeButton,
                themeType === ThemeType.LIGHT && styles.themeButtonActive,
                isDarkMode && styles.darkThemeButton
              ]}
              onPress={() => handleThemeChange(ThemeType.LIGHT)}
            >
              <Text style={[
                styles.themeButtonText,
                themeType === ThemeType.LIGHT && styles.themeButtonTextActive,
                isDarkMode && styles.darkThemeButtonText
              ]}>
                Light
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[
                styles.themeButton,
                themeType === ThemeType.DARK && styles.themeButtonActive,
                isDarkMode && styles.darkThemeButton
              ]}
              onPress={() => handleThemeChange(ThemeType.DARK)}
            >
              <Text style={[
                styles.themeButtonText,
                themeType === ThemeType.DARK && styles.themeButtonTextActive,
                isDarkMode && styles.darkThemeButtonText
              ]}>
                Dark
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[
                styles.themeButton,
                themeType === ThemeType.SYSTEM && styles.themeButtonActive,
                isDarkMode && styles.darkThemeButton
              ]}
              onPress={() => handleThemeChange(ThemeType.SYSTEM)}
            >
              <Text style={[
                styles.themeButtonText,
                themeType === ThemeType.SYSTEM && styles.themeButtonTextActive,
                isDarkMode && styles.darkThemeButtonText
              ]}>
                System
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
      
      <View style={[styles.section, isDarkMode && styles.darkSection]}>
        <Text style={[styles.sectionTitle, isDarkMode && styles.darkText]}>Units</Text>
        
        <View style={styles.settingItem}>
          <View style={styles.settingLabelContainer}>
            <Ionicons name="thermometer" size={22} color="#3f51b5" />
            <Text style={[styles.settingLabel, isDarkMode && styles.darkText]}>Temperature</Text>
          </View>
          <View style={styles.settingSwitchContainer}>
            <Text style={[styles.settingSwitchLabel, isDarkMode && styles.darkText]}>°F</Text>
            <Switch
              value={unitType === UnitType.CELSIUS}
              onValueChange={handleUnitTypeToggle}
              trackColor={{ false: '#767577', true: '#3f51b5' }}
              thumbColor="#f4f3f4"
            />
            <Text style={[styles.settingSwitchLabel, isDarkMode && styles.darkText]}>°C</Text>
          </View>
        </View>
      </View>
      
      <View style={[styles.section, isDarkMode && styles.darkSection]}>
        <Text style={[styles.sectionTitle, isDarkMode && styles.darkText]}>About</Text>
        
        <View style={styles.settingItem}>
          <View style={styles.settingLabelContainer}>
            <Ionicons name="information-circle" size={22} color="#3f51b5" />
            <Text style={[styles.settingLabel, isDarkMode && styles.darkText]}>Version</Text>
          </View>
          <Text style={[styles.settingValue, isDarkMode && styles.darkText]}>1.0.0</Text>
        </View>
        
        <View style={styles.settingItem}>
          <View style={styles.settingLabelContainer}>
            <Ionicons name="code" size={22} color="#3f51b5" />
            <Text style={[styles.settingLabel, isDarkMode && styles.darkText]}>Platform</Text>
          </View>
          <Text style={[styles.settingValue, isDarkMode && styles.darkText]}>React Native</Text>
        </View>
        
        <View style={styles.settingItem}>
          <View style={styles.settingLabelContainer}>
            <Ionicons name="heart" size={22} color="#3f51b5" />
            <Text style={[styles.settingLabel, isDarkMode && styles.darkText]}>Credits</Text>
          </View>
          <Text style={[styles.settingValue, isDarkMode && styles.darkText]}>Based on AT Forecast</Text>
        </View>
      </View>
    </View>
  );
};

// Styles
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5'
  },
  darkContainer: {
    backgroundColor: '#121212'
  },
  section: {
    backgroundColor: '#fff',
    marginVertical: 10,
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderRadius: 4,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4
  },
  darkSection: {
    backgroundColor: '#1e1e1e',
    shadowColor: '#fff',
    borderBottomColor: '#333'
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#333'
  },
  darkText: {
    color: '#fff'
  },
  settingItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#eee'
  },
  settingLabelContainer: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  settingLabel: {
    fontSize: 16,
    marginLeft: 10,
    color: '#333'
  },
  settingValue: {
    fontSize: 16,
    color: '#666'
  },
  settingSwitchContainer: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  settingSwitchLabel: {
    marginHorizontal: 8,
    fontSize: 16,
    color: '#666'
  },
  themeButtonsContainer: {
    flexDirection: 'row'
  },
  themeButton: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    marginLeft: 5,
    borderRadius: 4,
    borderWidth: 1,
    borderColor: '#ddd'
  },
  darkThemeButton: {
    borderColor: '#444'
  },
  themeButtonActive: {
    backgroundColor: '#3f51b5',
    borderColor: '#3f51b5'
  },
  themeButtonText: {
    fontSize: 14,
    color: '#666'
  },
  darkThemeButtonText: {
    color: '#ccc'
  },
  themeButtonTextActive: {
    color: '#fff'
  }
});

export default SettingsScreen; 