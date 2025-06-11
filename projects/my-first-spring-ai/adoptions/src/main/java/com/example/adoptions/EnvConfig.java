package com.example.adoptions;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loads environment variables from .env file before Spring context initialization
 */
public class EnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // Try multiple locations for .env file
            Dotenv dotenv = null;
            
            // First try current directory
            try {
                dotenv = Dotenv.configure()
                        .directory(".")
                        .ignoreIfMissing()
                        .load();
            } catch (Exception e) {
                // If not found, try parent directory (for when running from adoptions/ folder)
                dotenv = Dotenv.configure()
                        .directory("..")
                        .ignoreIfMissing()
                        .load();
            }

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            
            // Convert dotenv entries to a Map and add as property source
            Map<String, Object> envMap = dotenv.entries().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> e.getValue()
                    ));
            
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenv", envMap)
            );
            
            System.out.println("✅ Loaded .env file with " + envMap.size() + " variables");
            
        } catch (DotenvException e) {
            System.out.println("ℹ️  No .env file found, using system environment variables only");
        }
    }
} 