import org.example.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceGetSevenDaySummaryTest {

    @Test
    void testGetSevenDaySummary_NullBaseUrl() {
        // Arrange
        WeatherService weatherService = new WeatherService();
        double latitude = 45.0;
        double longitude = 50.0;
        String baseUrl = null; // Invalid URL

        // Act
        ResponseEntity<?> response = weatherService.getSevenDaySummary(latitude, longitude, baseUrl);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid base URL.", response.getBody());
    }
}