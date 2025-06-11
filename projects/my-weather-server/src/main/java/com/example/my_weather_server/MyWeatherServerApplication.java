package com.example.my_weather_server;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MyWeatherServerApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
		
		SpringApplication.run(MyWeatherServerApplication.class, args);
	}

	@Bean
	public OpenAiApi openAiApi(@Value("${spring.ai.openai.api-key}") String apiKey) {
		return new OpenAiApi(apiKey);
	}

	@Bean
	public ChatModel chatModel(OpenAiApi openAiApi) {
		return new OpenAiChatModel(openAiApi);
	}

	@Bean
	@Description("Get weather forecast for a specific latitude/longitude")
	public FunctionCallback getWeatherForecastByLocation(WeatherService weatherService) {
		return FunctionCallbackWrapper
			.builder((WeatherService.Location location) -> weatherService.getWeatherForecastByLocation(location.latitude(), location.longitude()))
			.withInputType(WeatherService.Location.class)
			.withName("getWeatherForecastByLocation")
			.withDescription("Get weather forecast for a specific latitude/longitude")
			.build();
	}

	@Bean
	@Description("Get weather alerts for a US state. Input is Two-letter US state code (e.g. CA, NY)")
	public FunctionCallback getAlerts(WeatherService weatherService) {
		return FunctionCallbackWrapper
			.builder((WeatherService.AlertRequest alertRequest) -> weatherService.getAlerts(alertRequest.state()))
			.withInputType(WeatherService.AlertRequest.class)
			.withName("getAlerts")
			.withDescription("Get weather alerts for a US state. Input is Two-letter US state code (e.g. CA, NY)")
			.build();
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner demo(ChatModel chatModel, List<FunctionCallback> functionCallbacks) {
		return args -> {
			Scanner scanner = new Scanner(System.in);
			Set<String> functionNames = functionCallbacks.stream().map(FunctionCallback::getName).collect(Collectors.toSet());

			System.out.println("Chat with the AI. Type 'exit' to quit.");

			while (true) {
				System.out.print("User: ");
				String userInput = scanner.nextLine();

				if (userInput.trim().equalsIgnoreCase("exit")) {
					scanner.close();
					System.exit(0);
				}

				OpenAiChatOptions options = OpenAiChatOptions.builder()
					.withFunctionCallbacks(functionCallbacks)
					.withFunctions(functionNames)
					.build();
				
				ChatResponse response = chatModel.call(new Prompt(userInput, options));

				System.out.println("AI: " + response.getResult().getOutput().getContent());
			}
		};
	}

}
