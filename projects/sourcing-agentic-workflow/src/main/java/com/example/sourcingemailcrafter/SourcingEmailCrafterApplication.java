package com.example.sourcingemailcrafter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SourcingEmailCrafterApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			.directory("./")  // Look in the workspace root directory
			.ignoreIfMissing()
			.load();
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
		
		SpringApplication.run(SourcingEmailCrafterApplication.class, args);
	}

} 