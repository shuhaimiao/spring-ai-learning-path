package com.example.scheduler;

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
    private final SchedulerService schedulerService;

    public SchedulerApiController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    public ResponseEntity<ScheduleResponse> scheduleAppointment(ScheduleRequest scheduleRequest) {
        log.info("=== SCHEDULER API - HTTP REQUEST RECEIVED ===");
        log.info("Endpoint: /schedule (POST)");
        log.info("Request Body: {}", scheduleRequest);
        
        ScheduleResponse response = this.schedulerService.scheduleAppointment(scheduleRequest);

        log.info("=== SCHEDULER API - HTTP RESPONSE SENT ===");
        return ResponseEntity.ok(response);
    }

    // Removed custom health() method as Actuator will be used.
} 