import React, { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { loadCache } from '../redux/actions/cacheActions';

/**
 * Component that initializes app data on startup
 * Currently loads cached shelter data from AsyncStorage
 */
const AppInitializer: React.FC = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    // Type assertion to avoid TypeScript error with thunk actions
    dispatch(loadCache() as any);
    console.log('Initialized app - loading cache from storage');
  }, [dispatch]);

  // This component doesn't render anything
  return null;
};

export default AppInitializer; 