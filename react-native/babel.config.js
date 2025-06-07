module.exports = {
  presets: ['babel-preset-expo'],
  plugins: [
    [
      'module-resolver',
      {
        root: ['./src'],
        extensions: ['.ios.js', '.android.js', '.js', '.ts', '.tsx', '.json'],
        alias: {
          '@components': './src/components',
          '@screens': './src/screens',
          '@navigation': './src/navigation',
          '@redux': './src/redux',
          '@api': './src/api',
          '@utils': './src/utils',
          '@types': './src/types',
          '@assets': './src/assets'
        }
      }
    ],
    'react-native-reanimated/plugin'
  ]
}; 