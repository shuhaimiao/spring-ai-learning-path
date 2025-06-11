package com.example.scheduler;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

// SLF4J imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.scheduler.api.controller.SchedulerApi;
import com.example.scheduler.api.model.ScheduleRequest;
import com.example.scheduler.api.model.ScheduleResponse;

@RestController
public class SchedulerApiController implements SchedulerApi {

    private static final Logger log = LoggerFactory.getLogger(SchedulerApiController.class);

    @Override
    public ResponseEntity<ScheduleResponse> scheduleAppointment(ScheduleRequest scheduleRequest) {
        Integer dogIdInt = scheduleRequest.getDogId();
        String dogName = scheduleRequest.getDogName();

        log.info("=== SCHEDULER API - HTTP REQUEST ===");
        log.info("Endpoint: /schedule (POST)");
        log.info("Request Body: dogId={}, dogName={}", dogIdInt, dogName);
        log.info("Timestamp: {}", Instant.now());

        Instant appointmentInstant = Instant.now().plus(3, ChronoUnit.DAYS);
        OffsetDateTime appointmentOffsetDateTime = OffsetDateTime.ofInstant(appointmentInstant, ZoneOffset.UTC);

        ScheduleResponse response = new ScheduleResponse();
        response.setSuccess(true);
        response.setAppointmentTime(appointmentOffsetDateTime);
        response.setAppointmentId(UUID.randomUUID());
        if (dogIdInt != null) {
            response.setDogId(dogIdInt);
        }
        response.setDogName(dogName);
        response.setMessage("Appointment scheduled for " + dogName + " on " + appointmentOffsetDateTime.toString());

        log.info("=== SCHEDULER API - HTTP RESPONSE ===");
        log.info("Response Body: {}", response);
        log.info("====================================");

        return ResponseEntity.ok(response);
    }

    // Removed custom health() method as Actuator will be used.
} 