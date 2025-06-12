package com.example.my_weather_server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.client.ChatClient;

@RestController
public class WeatherController {

    private final WeatherService weatherService;
    private final ChatClient chatClient;

    public WeatherController(WeatherService weatherService, ChatClient.Builder chatClientBuilder) {
        this.weatherService = weatherService;
        this.chatClient = chatClientBuilder.build();
    }

    public record ChatRequest(String query) {}
    public record ChatResponse(Object response) {}

    @PostMapping("/weather/forecast")
    public WeatherService.Forecast getForecast(@RequestBody WeatherService.Location location) {
        return weatherService.getWeatherForecastByLocation(location);
    }

    @PostMapping("/weather/alerts")
    public WeatherService.Alert getAlerts(@RequestBody WeatherService.AlertRequest alertRequest) {
        return weatherService.getAlerts(alertRequest);
    }

    @PostMapping("/weather/chat/forecast")
    public ChatResponse chatWithAi(@RequestBody ChatRequest request) {
        Object response = chatClient.prompt()
            .user(request.query())
            .tools(weatherService)
            .call()
            .content();
        return new ChatResponse(response);
    }
} 