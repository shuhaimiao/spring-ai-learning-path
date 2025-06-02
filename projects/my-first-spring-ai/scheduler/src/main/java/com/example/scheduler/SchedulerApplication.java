package com.example.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}

@RestController 
class DogAdoptionSchedulerController {

    @PostMapping("/schedule")
    public Map<String, Object> schedule(@RequestBody Map<String, Object> request) {
        // Extract parameters from the request
        Object dogIdObj = request.get("dogId");
        Object dogNameObj = request.get("dogName");
        
        int dogId = dogIdObj instanceof Number ? ((Number) dogIdObj).intValue() : 
                   dogIdObj instanceof String ? Integer.parseInt((String) dogIdObj) : 0;
        String dogName = dogNameObj != null ? dogNameObj.toString() : "Unknown Dog";
        
        System.out.println("SCHEDULER MCP SERVICE: Scheduling adoption for dog " + dogName + " (ID: " + dogId + ")");
        
        String appointmentTime = Instant
                .now()
                .plus(3, ChronoUnit.DAYS)
                .toString();
                
        return Map.of(
            "success", true,
            "appointmentTime", appointmentTime,
            "dogId", dogId,
            "dogName", dogName,
            "message", "Appointment scheduled for " + dogName + " on " + appointmentTime
        );
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "Dog Adoption Scheduler");
    }
}
