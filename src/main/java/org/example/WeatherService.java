package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherService {

    private static final double SOLAR_PANEL_POWER = 2.5;
    private static final double PANEL_EFFICIENCY = 0.2;

    public ResponseEntity<?> getSevenDayForecast(double latitude, double longitude, String openMeteoBaseUrl) {
        if (!isValidLatitude(latitude) || !isValidLongitude(longitude)) {
            return ResponseEntity.badRequest().body("Invalid latitude or longitude.");
        }

        if (openMeteoBaseUrl == null || openMeteoBaseUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid base URL.");
        }

        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(openMeteoBaseUrl)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("daily", "temperature_2m_max,temperature_2m_min,weathercode,sunshine_duration")
                .queryParam("timezone", "auto")
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || !response.containsKey("daily")) {
                return ResponseEntity.badRequest().body("Unable to fetch weather data.");
            }

            Map<String, Object> dailyData = (Map<String, Object>) response.get("daily");
            List<String> dates = (List<String>) dailyData.get("time");
            List<Integer> weatherCodes = (List<Integer>) dailyData.get("weathercode");
            List<Double> maxTemperatures = (List<Double>) dailyData.get("temperature_2m_max");
            List<Double> minTemperatures = (List<Double>) dailyData.get("temperature_2m_min");
            List<Double> sunshineDurations = (List<Double>) dailyData.get("sunshine_duration");

            List<Map<String, Object>> forecastData = processForecastData(dates, weatherCodes, maxTemperatures, minTemperatures, sunshineDurations);

            return ResponseEntity.ok(forecastData);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while fetching data from API: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getSevenDaySummary(double latitude, double longitude, String openMeteoBaseUrl) {
        if (!isValidLatitude(latitude) || !isValidLongitude(longitude)) {
            return ResponseEntity.badRequest().body("Invalid latitude or longitude.");
        }

        if (openMeteoBaseUrl == null || openMeteoBaseUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid base URL.");
        }

        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(openMeteoBaseUrl)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("daily", "temperature_2m_max,temperature_2m_min,weathercode,sunshine_duration")
                .queryParam("hourly", "surface_pressure")
                .queryParam("timezone", "auto")
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return ResponseEntity.badRequest().body("Unable to fetch weather data");
            }

            Map<String, Object> dailyData = (Map<String, Object>) response.get("daily");
            Map<String, Object> hourlyData = (Map<String, Object>) response.get("hourly");

            if (dailyData == null || hourlyData == null) {
                return ResponseEntity.badRequest().body("Missing either daily or hourly data. Check your parameters.");
            }
            List<Integer> weatherCodes = (List<Integer>) dailyData.get("weathercode");
            List<Double> maxTemperatures = (List<Double>) dailyData.get("temperature_2m_max");
            List<Double> minTemperatures = (List<Double>) dailyData.get("temperature_2m_min");
            List<Double> sunshineDurations = (List<Double>) dailyData.get("sunshine_duration");

            List<Double> surfacePressures = (List<Double>) hourlyData.get("surface_pressure");

            if (maxTemperatures == null || minTemperatures == null || sunshineDurations == null || weatherCodes == null || surfacePressures == null) {
                return ResponseEntity.badRequest().body("Some required data is missing. Are you sure the API supports these parameters?");
            }

            double avgPressure = round(surfacePressures.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN));
            double avgSunshineHours = round(sunshineDurations.stream().mapToDouble(d -> d / 60.0).average().orElse(Double.NaN));
            double overallMaxTemp = round(maxTemperatures.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN));
            double overallMinTemp = round(minTemperatures.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN));
            long precipDays = weatherCodes.stream().filter(code -> code >= 51).count();
            String summary = precipDays >= 4 ? "z opadami" : "bez opadów";

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("minTemperature", overallMinTemp);
            result.put("maxTemperature", overallMaxTemp);
            result.put("averagePressure", avgPressure);
            result.put("averageSunshineHours", avgSunshineHours);
            result.put("weeklySummary", summary);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(503).body("Error while fetching data from API. the server is currently unable to handle the incoming requests." + e.getMessage());
        }
    }

    private List<Map<String, Object>> processForecastData(List<String> dates,
                                                          List<Integer> weatherCodes,
                                                          List<Double> maxTemperatures,
                                                          List<Double> minTemperatures,
                                                          List<Double> sunshineDurations) {
        List<Map<String, Object>> forecast = new ArrayList<>();


        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < dates.size(); i++) {
            Map<String, Object> dayData = new LinkedHashMap<>();


            String formattedDate = LocalDate.parse(dates.get(i), inputFormatter).format(outputFormatter);

            dayData.put("date", formattedDate);
            dayData.put("weatherCode", weatherCodes.get(i));
            dayData.put("maxTemperature", round(maxTemperatures.get(i)));
            dayData.put("minTemperature", round(minTemperatures.get(i)));

            double sunshineHours = sunshineDurations.get(i) / 60.0; // convert minutes to hours
            double generatedEnergy = round(SOLAR_PANEL_POWER * sunshineHours * PANEL_EFFICIENCY);
            dayData.put("generatedEnergy", generatedEnergy);

            forecast.add(dayData);
        }

        return forecast;
    }

    public double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public boolean isValidLatitude(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    public boolean isValidLongitude(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }
}


