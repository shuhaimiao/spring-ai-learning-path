package com.example.scheduler;

import java.util.function.Function;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import com.example.scheduler.api.model.ScheduleRequest;
import com.example.scheduler.api.model.ScheduleResponse;

@Configuration
public class SchedulerToolConfig {

    private final SchedulerApiController schedulerApiController;

    // Spring will inject the existing SchedulerApiController bean
    public SchedulerToolConfig(SchedulerApiController schedulerApiController) {
        this.schedulerApiController = schedulerApiController;
    }

    // Helper function that contains the core logic for scheduling
    private Function<ScheduleRequest, ScheduleResponse> scheduleAppointmentLogicFunction() {
        return request -> {
            // Call the existing controller method
            ResponseEntity<ScheduleResponse> responseEntity = schedulerApiController.scheduleAppointment(request);
            // The tool should return the core data, not the ResponseEntity wrapper.
            if (responseEntity != null) {
                return responseEntity.getBody();
            }
            // Consider logging an error or throwing a specific exception if responseEntity or body is null
            return null; 
        };
    }

    @Bean
    public ToolCallbackProvider mcpScheduleAppointmentToolProvider() {
        String toolName = "scheduleAppointment";
        // This description is what the AI model (or MCP client) will see.
        String description = "Schedules a dog adoption appointment. Requires dogId (integer) and dogName (string). Returns a confirmation including appointmentId, dogId, dogName, the scheduled appointmentTime, and a message.";
        
        // Create a FunctionCallback. Spring AI uses this to handle POJO input/output types.
        // The FunctionCallback itself is a ToolCallback.
        FunctionCallback scheduleAppointmentCallback = new FunctionCallback(
            toolName, 
            description, 
            scheduleAppointmentLogicFunction() // Provide the actual function implementation
        );

        // The ToolCallbackProvider makes this tool (via the FunctionCallback) available.
        // The MCP server auto-configuration should pick up ToolCallbackProvider beans.
        return ToolCallbackProvider.of(scheduleAppointmentCallback);
    }
} 