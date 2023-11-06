const axios = require("axios");

async function fetchData() {
  const urls = [];
  for (let i = 0; i < 1000; i++) urls.push(`https://google.com.in?q=test${i}`);

  for (const url of urls) {
    try {
      const response = await axios.post(`http://localhost:8081?url=${url}`);
      const data = response.data;
      console.log(data);
    } catch (error) {
      console.error("Failed to fetch data:", error);
    }
  }
}

fetchData();
