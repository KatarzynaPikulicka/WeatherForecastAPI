
import org.example.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherServiceTest {

    private WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        weatherService = new WeatherService();
    }

    @Test
    public void testValidLatitude() {
        assertTrue(weatherService.isValidLatitude(4.07));
        assertTrue(weatherService.isValidLatitude(-4.07));
        assertTrue(weatherService.isValidLatitude(-90.0));
        assertTrue(weatherService.isValidLatitude(90.0));

        assertFalse(weatherService.isValidLatitude(127.0));
        assertFalse(weatherService.isValidLatitude(-127.0));
    }

    @Test
    public void testValidLongitude() {
        assertTrue(weatherService.isValidLongitude(4.07));
        assertTrue(weatherService.isValidLongitude(-4.07));
        assertTrue(weatherService.isValidLongitude(-180.0));
        assertTrue(weatherService.isValidLongitude(180.0));

        assertFalse(weatherService.isValidLongitude(200.0));
        assertFalse(weatherService.isValidLongitude(-200.0));
    }
    @Test
    public void testRoundMethod() {
        assertEquals(25.50, weatherService.round(25.4999));
        assertEquals(25.51, weatherService.round(25.505));
        assertEquals(0.00, weatherService.round(0.004));
        assertEquals(100.00, weatherService.round(100.0));
    }



}


