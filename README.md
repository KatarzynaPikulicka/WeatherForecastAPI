# 🌤️ Weather Forecast Service

This project provides a 7-day weather forecast and a weekly summary, leveraging data from the [Open-Meteo API](https://api.open-meteo.com/v1/forecast). It is built with Spring Boot and offers REST endpoints to retrieve forecast details for a specified latitude and longitude.

## ✨ Features

- **7-Day Forecast**: Retrieve daily maximum and minimum temperatures, weather codes, sunshine duration, and estimated solar energy generation.
- **Weekly Summary**: Obtain a summary that includes the minimum and maximum temperatures for the week, average atmospheric pressure, average sunshine hours, and a general weather assessment.

## 🔗 Endpoints

- **GET** `/weather/7-day-forecast`  
  **Query Parameters:**

  - `latitude` (double): Latitude of the location. Must be between -90 and 90.
  - `longitude` (double): Longitude of the location. Must be between -180 and 180.

  **Response:**  
  A JSON array with each element representing a day’s forecast, including:

  - `date`: The date in `DD/MM/YYYY` format.
  - `weatherCode`: Numeric code representing the weather condition.
  - `maxTemperature`: Maximum temperature for the day.
  - `minTemperature`: Minimum temperature for the day.
  - `generatedEnergy`: Estimated energy (in kWh) generated by a solar panel under given sunshine conditions.

- **GET** `/weather/weekly-summary`  
  **Query Parameters:**

  - `latitude` (double)
  - `longitude` (double)

  **Response:**  
  A JSON object containing:

  - `minTemperature`: The lowest temperature recorded over the week.
  - `maxTemperature`: The highest temperature recorded over the week.
  - `averagePressure`: The average atmospheric pressure (hPa).
  - `averageSunshineHours`: The average daily sunshine hours.
  - `weeklySummary`: A textual summary indicating whether the week includes precipitation or not.

## ⚙️ Configuration

Adjust the `application.properties` as needed:

```properties
open-meteo.base-url=https://api.open-meteo.com/v1/forecast
server.port=8080
```

## 🚀 How to Run

1. **Prerequisites**:

   - Java 17 or later
   - Maven

2. **Build and Run**:

   - Using Maven:
     ```bash
     mvn clean package
     mvn spring-boot:run
     ```

3. **Accessing the Service**:  
   Once started, the application will run on `http://localhost:8080`.  
   You can test the endpoints with queries like:
   ```
   http://localhost:8080/weather/7-day-forecast?latitude=50.0&longitude=20.0
   ```

## 🧪 Testing

Unit tests are provided to validate input parameters and behavior under invalid conditions. Execute the tests using your chosen build tool:

- **Maven**:
  ```bash
  mvn test
  ```

## 📜 License

This project is licensed under the [MIT License](LICENSE). You are free to use, modify, and distribute this code, subject to the terms of the license.


## 👤 Author

Made by Kasia Pikulicka
