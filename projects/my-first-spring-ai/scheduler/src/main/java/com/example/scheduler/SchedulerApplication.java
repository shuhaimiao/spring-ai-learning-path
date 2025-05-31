package com.example.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.Tool; 
import org.springframework.ai.mcp.MethodToolCallbackProvider; 

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Bean
    MethodToolCallbackProvider methodToolCallbackProvider(DogAdoptionScheduler scheduler) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(scheduler) 
                .build();
    }
}

@Component 
class DogAdoptionScheduler {

    @Tool(description = "schedule an appointment to pickup or adopt a " +
            "dog from a Pooch Palace location")
    public String schedule(int dogId, String dogName) {
        System.out.println("SCHEDULER MCP SERVICE: Scheduling adoption for dog " + dogName + " (ID: " + dogId + ")");
        return Instant
                .now()
                .plus(3, ChronoUnit.DAYS)
                .toString();
    }
}
