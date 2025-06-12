package com.example.my_weather_server;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MyWeatherServerApplication {

	private static final Logger logger = LoggerFactory.getLogger(MyWeatherServerApplication.class);

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			.directory("./projects/my-weather-server")
			.ignoreIfMissing()
			.load();
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

		SpringApplication.run(MyWeatherServerApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner demo(ChatClient.Builder chatClientBuilder, WeatherService weatherService) {
		return args -> {
			ChatClient chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
			Scanner scanner = new Scanner(System.in);

			System.out.println("Chat with the AI. Type 'exit' to quit.");

			while (true) {
				System.out.print("User: ");
				String userInput = scanner.nextLine();

				if (userInput.trim().equalsIgnoreCase("exit")) {
					scanner.close();
					System.exit(0);
				}

				//logger.info("User prompt: {}", userInput);
				Object response = chatClient.prompt()
					.advisors(new LoggerAdvisor())
					.user(userInput)
					.tools(weatherService)
					.call()
					.content();
				
				logger.info("AI response: {}", response);
			}
		};
	}

}
