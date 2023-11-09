const axios = require('axios');

const sendRequestsConcurrently = async () => {
  const requests = [];
  const url = 'http://localhost:9000/pool'; // Change this to your desired URL

  // Create an array of 1000 promises (GET requests)
  for (let i = 0; i < 1; i++) {
    console.log("Push +", i)
    requests.push(axios.get(url));
  }

  // Start all requests concurrently and don't wait for responses
  Promise.all(requests)
    .then(() => console.log('All requests sent concurrently'))
    .catch(error => console.error('Error sending requests:', error));
};

// Call the function to send requests
sendRequestsConcurrently();