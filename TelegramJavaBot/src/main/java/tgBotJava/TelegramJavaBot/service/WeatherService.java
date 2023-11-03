package tgBotJava.TelegramJavaBot.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tgBotJava.TelegramJavaBot.entity.WeatherData;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    /**
     *
     * @param restTemplate template for restapi
     * @param apiKey key for using openweatherapi
     */
    @Autowired
    public WeatherService(RestTemplate restTemplate, @Value("${weather.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     * Method for request weather data in current city
     * @param cityName
     * @return json with weather data
     */
    public WeatherData getWeatherData(String cityName) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

        ResponseEntity<WeatherData> response = restTemplate.getForEntity(apiUrl, WeatherData.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to retrieve weather data for city: " + cityName);
        }
    }
}
