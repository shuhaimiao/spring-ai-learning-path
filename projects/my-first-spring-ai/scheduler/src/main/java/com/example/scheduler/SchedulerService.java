package com.example.scheduler;

import com.example.scheduler.api.model.ScheduleRequest;
import com.example.scheduler.api.model.ScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    public ScheduleResponse scheduleAppointment(ScheduleRequest scheduleRequest) {
        Integer dogIdInt = scheduleRequest.getDogId();
        String dogName = scheduleRequest.getDogName();

        log.info("=== SCHEDULER SERVICE ===");
        log.info("Scheduling appointment for dog: id={}, name={}", dogIdInt, dogName);

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

        log.info("Appointment scheduled: {}", response);
        log.info("=========================");
        return response;
    }
} 