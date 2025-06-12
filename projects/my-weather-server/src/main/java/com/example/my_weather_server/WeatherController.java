package com.example.my_weather_server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/weather/forecast")
    public WeatherService.Forecast getForecast(@RequestBody WeatherService.Location location) {
        return weatherService.getWeatherForecastByLocation(location);
    }

    @PostMapping("/weather/alerts")
    public WeatherService.Alert getAlerts(@RequestBody WeatherService.AlertRequest alertRequest) {
        return weatherService.getAlerts(alertRequest);
    }
} 