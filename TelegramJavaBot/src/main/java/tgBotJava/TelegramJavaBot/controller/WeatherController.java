package tgBotJava.TelegramJavaBot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tgBotJava.TelegramJavaBot.entity.WeatherData;
import tgBotJava.TelegramJavaBot.service.WeatherService;

@RestController
public class WeatherController {

    @Value("${weather.api.key}")
    private String apiKey;

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public WeatherData getWeather(@RequestParam String city) {
        return weatherService.getWeatherData(city);
    }
}
