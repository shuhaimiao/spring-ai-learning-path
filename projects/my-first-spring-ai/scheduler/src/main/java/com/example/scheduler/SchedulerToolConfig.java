package com.example.scheduler;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import com.example.scheduler.api.model.ScheduleRequest;
import com.example.scheduler.api.model.ScheduleResponse;

@Configuration
public class SchedulerToolConfig {

    private final SchedulerService schedulerService;

    public SchedulerToolConfig(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Bean
    @Description("Schedules a dog adoption appointment. Requires dogId (integer) and dogName (string). Returns a confirmation including appointmentId, dogId, dogName, the scheduled appointmentTime, and a message.")
    public Function<ScheduleRequest, ScheduleResponse> scheduleAppointment() {
        return this.schedulerService::scheduleAppointment;
    }
} 