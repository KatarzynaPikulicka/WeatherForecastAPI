package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Value("${open-meteo.base-url}")
    private String openMeteoBaseUrl;

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/7-day-forecast")
    public ResponseEntity<?> getSevenDayForecast(@RequestParam double latitude, @RequestParam double longitude) {
        return weatherService.getSevenDayForecast(latitude, longitude, openMeteoBaseUrl);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/weekly-summary")
    public ResponseEntity<?> getSevenDaySummary(@RequestParam double latitude, @RequestParam double longitude) {
        return weatherService.getSevenDaySummary(latitude, longitude, openMeteoBaseUrl);
    }
}
