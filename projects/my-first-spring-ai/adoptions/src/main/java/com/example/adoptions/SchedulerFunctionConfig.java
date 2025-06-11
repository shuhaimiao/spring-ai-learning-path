package com.example.adoptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Function;

@Configuration
public class SchedulerFunctionConfig {
    
    @Value("${scheduler.url:http://localhost:8081}")
    private String schedulerUrl;
    
    @Bean
    @Description("Schedule an appointment to meet a dog for adoption")
    public Function<ScheduleRequest, ScheduleResponse> scheduleAppointment() {
        return request -> {
            try {
                System.out.println("=== ADOPTIONS SERVICE - CALLING SCHEDULER ===");
                System.out.println("Function: scheduleAppointment");
                System.out.println("Request: " + request);
                System.out.println("Scheduler URL: " + schedulerUrl);
                
                RestTemplate restTemplate = new RestTemplate();
                
                Map<String, Object> requestBody = Map.of(
                    "dogId", request.dogId(),
                    "dogName", request.dogName()
                );
                
                System.out.println("HTTP Request Body: " + requestBody);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.postForObject(
                    schedulerUrl + "/schedule",
                    requestBody,
                    Map.class
                );
                
                System.out.println("HTTP Response: " + response);
                System.out.println("=== ADOPTIONS SERVICE - SCHEDULER RESPONSE ===");
                
                if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                    return new ScheduleResponse(
                        true,
                        response.get("appointmentTime") != null ? response.get("appointmentTime").toString() : "TBD",
                        "Appointment scheduled successfully for " + request.dogName()
                    );
                } else {
                    return new ScheduleResponse(
                        false,
                        "TBD",
                        "Failed to schedule appointment for " + request.dogName()
                    );
                }
            } catch (Exception e) {
                System.err.println("ERROR calling scheduler service: " + e.getMessage());
                e.printStackTrace();
                return new ScheduleResponse(
                    false,
                    "TBD",
                    "Technical error scheduling appointment for " + request.dogName() + ": " + e.getMessage()
                );
            }
        };
    }
}

// Records are defined in AdoptionsApplication.java 