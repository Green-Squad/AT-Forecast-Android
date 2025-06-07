// This script tests the AT Forecast API directly from Node.js
const axios = require('axios');
const fs = require('fs');

// Use hardcoded API key for testing
const API_KEY = "jLEy94zVGv"; // Hardcoded for direct testing

console.log('API Key (hardcoded):', API_KEY);

// Test the API
async function testApi() {
  try {
    console.log('Testing API...');
    
    // Test states endpoint
    const statesUrl = `https://www.atforecast.app/index.json?api_key=${encodeURIComponent(API_KEY)}&include_shelters=false`;
    console.log('Requesting URL:', statesUrl);
    
    const statesResponse = await axios.get(statesUrl);
    console.log('States API response status:', statesResponse.status);
    console.log('Received states:', statesResponse.data.length);
    
    // Write the first state to a file for examination
    if (statesResponse.data.length > 0) {
      console.log('First state:', statesResponse.data[0]);
      fs.writeFileSync('api-response.json', JSON.stringify(statesResponse.data, null, 2));
      console.log('Wrote response to api-response.json');
    }
    
    // Test a shelter endpoint if states were retrieved successfully
    if (statesResponse.data.length > 0 && statesResponse.data[0].state_id) {
      const firstStateId = statesResponse.data[0].state_id;
      
      // Get a shelter ID from the first state
      const sheltersUrl = `https://www.atforecast.app/shelters/${firstStateId}.json?api_key=${encodeURIComponent(API_KEY)}`;
      console.log('\nRequesting shelter URL:', sheltersUrl);
      
      const sheltersResponse = await axios.get(sheltersUrl);
      console.log('Shelters API response status:', sheltersResponse.status);
      console.log('Received shelters:', sheltersResponse.data.length);
      
      if (sheltersResponse.data.length > 0) {
        console.log('First shelter:', sheltersResponse.data[0]);
      }
    }
    
    console.log('\nAPI test completed successfully!');
  } catch (error) {
    console.error('API Test Error:');
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      console.error('Status:', error.response.status);
      console.error('Headers:', error.response.headers);
      console.error('Data:', error.response.data);
    } else if (error.request) {
      // The request was made but no response was received
      console.error('No response received. Request:', error.request);
    } else {
      // Something happened in setting up the request that triggered an Error
      console.error('Error message:', error.message);
    }
  }
}

// Run the test
testApi(); 