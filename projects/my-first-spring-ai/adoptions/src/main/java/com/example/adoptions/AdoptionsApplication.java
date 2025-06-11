package com.example.adoptions;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }
    


}

record Dog(@Id int id, String name, String owner, String description, 
           String breed, Integer ageYears, String size, String energyLevel,
           Boolean goodWithKids, Boolean goodWithCats, Boolean goodWithDogs,
           java.math.BigDecimal adoptionFee, String location,
           java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record DogAdoptionSuggestion(int id, String name, String description) {}

// MCP function calling records
record ScheduleRequest(int dogId, String dogName) {}
record ScheduleResponse(boolean success, String appointmentTime, String message) {}

@Controller
@ResponseBody
class AdoptionsController {

    private final ChatClient ai;
    private final DogRepository repository;
    private final org.springframework.jdbc.core.simple.JdbcClient db;
    private final org.springframework.ai.vectorstore.VectorStore vectorStore;
    
    // Inject scheduler URL from application.properties
    @org.springframework.beans.factory.annotation.Value("${scheduler.url:http://localhost:8081}")
    private String schedulerUrl;

    AdoptionsController(
            org.springframework.jdbc.core.simple.JdbcClient db,
            DogRepository repository,
            org.springframework.ai.vectorstore.VectorStore vectorStore,
            ChatClient.Builder aiBuilder
    ) {
        this.repository = repository;
        this.db = db;
        this.vectorStore = vectorStore;

        // Load data into VectorStore
        var count = db
                .sql("select count(*) from vector_store")
                .query(Integer.class)
                .single();
        if (count == 0) {
            repository.findAll().forEach(dog -> {
                var dogInfo = String.format(
                    "id: %s, name: %s, breed: %s, age: %s years, size: %s, energy: %s, " +
                    "good with kids: %s, good with cats: %s, good with dogs: %s, " +
                    "adoption fee: $%s, location: %s, description: %s",
                    dog.id(), dog.name(), dog.breed(), dog.ageYears(), dog.size(), dog.energyLevel(),
                    dog.goodWithKids() ? "Yes" : "No",
                    dog.goodWithCats() ? "Yes" : "No", 
                    dog.goodWithDogs() ? "Yes" : "No",
                    dog.adoptionFee(), dog.location(), dog.description()
                );
                var dogDocument = new org.springframework.ai.document.Document(dogInfo);
                vectorStore.add(java.util.List.of(dogDocument));
            });
        }

        // System prompt for adoption assistance
        var system = """
                You are an AI powered assistant to help people adopt a dog from
the adoption agency named Pooch Palace with locations in Rio de Janeiro, Mexico
City, Seoul, Tokyo, Singapore, New York City, Amsterdam, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco. 

Information about the dogs available will be presented below. If there is no information, then return a polite response suggesting we don't have any dogs available.

When a user expresses interest in scheduling an appointment for a specific dog, you MUST automatically schedule the appointment for them using the scheduleAppointment function. Call this function with the dog's ID and name to book the appointment. DO NOT just tell them to contact the scheduler - always use the scheduleAppointment function when requested.

Be helpful, friendly, and provide detailed information about each dog's characteristics, needs, and suitability for different families.
                """;

        // Build ChatClient with system prompt. 
        this.ai = aiBuilder
                .defaultSystem(system)
                .build();
    }

    @GetMapping("/{user}/assistant")
    String inquire(@PathVariable String user, @RequestParam String question) {
        System.out.printf("🤖 AI ASSISTANT REQUEST: user='%s', question='%s'%n", user, question);
        
        // Get all dogs from database
        var allDogs = repository.findAll();
        System.out.println("🐕 Found " + allDogs.size() + " dogs in database");
        
        // Build context from all dogs
        var context = new StringBuilder();
        context.append("Here are all the dogs available for adoption at Pooch Palace:\n\n");
        
        for (var dog : allDogs) {
            context.append(String.format(
                "• Dog ID: %d - %s (%s) - %s, %d years old, %s size, %s energy level. " +
                "Good with kids: %s, cats: %s, dogs: %s. " +
                "Adoption fee: $%.2f. Location: %s. %s\n",
                dog.id(), dog.name(), dog.breed(), dog.description(), dog.ageYears(), dog.size(), dog.energyLevel(),
                dog.goodWithKids() ? "Yes" : "No",
                dog.goodWithCats() ? "Yes" : "No", 
                dog.goodWithDogs() ? "Yes" : "No",
                dog.adoptionFee(), dog.location(), dog.description()
            ));
        }
        
        // Create enhanced prompt with context
        var promptWithContext = String.format(
            "%s\n\nUser question: %s", 
            context.toString(), question
        );
        
        System.out.println("📤 Sending prompt to OpenAI (length: " + promptWithContext.length() + " chars)");
        System.out.println("🔍 Question analysis: " + (question.toLowerCase().contains("schedule") || 
                                                        question.toLowerCase().contains("appointment") ? 
                                                        "Contains scheduling keywords - may trigger MCP function" : 
                                                        "No scheduling keywords detected"));
        
        long startTime = System.currentTimeMillis();
        
        // Reverted to simple call, assuming String response for now
        String response = ai
                .prompt()
                .user(promptWithContext)
                .call()
                .content(); // Assuming content() returns String when no functions are explicitly handled
        long endTime = System.currentTimeMillis();
        
        System.out.println("📥 OpenAI response received in " + (endTime - startTime) + "ms");
        System.out.println("📝 Response length: " + response.length() + " chars");
        
        return response;
    }
    
    @GetMapping("/dogs")
    java.util.List<Dog> listAllDogs() {
        return repository.findAll();
    }
    
    @GetMapping("/dogs/count")
    String getDogCount() {
        try {
            var dogCount = repository.count();
            var vectorCount = db
                    .sql("select count(*) from vector_store")
                    .query(Integer.class)
                    .single();
            return String.format("Dogs in database: %d, Documents in vector store: %d", dogCount, vectorCount);
        } catch (Exception e) {
            return "Error counting: " + e.getMessage();
        }
    }
    
    @GetMapping("/admin/flyway-info")
    String flywayInfo(org.flywaydb.core.Flyway flyway) {
        var info = flyway.info();
        var sb = new StringBuilder();
        sb.append("Flyway Migration Status:\n");
        sb.append("Current Version: ").append(info.current() != null ? info.current().getVersion() : "None").append("\n");
        sb.append("Pending Migrations: ").append(info.pending().length).append("\n");
        sb.append("\nAll Migrations:\n");
        for (var migration : info.all()) {
            sb.append("- ").append(migration.getVersion())
              .append(" (").append(migration.getState()).append("): ")
              .append(migration.getDescription()).append("\n");
        }
        return sb.toString();
    }
    
    @GetMapping("/admin/mcp-config")
    String mcpConfig() {
        // Use injected schedulerUrl field instead of System.getProperty
        var sb = new StringBuilder();
        sb.append("MCP Configuration Status:\n");
        sb.append("=========================\n");
        sb.append("Scheduler URL: ").append(schedulerUrl).append("\n");
        sb.append("Connection Type: ").append(schedulerUrl.contains("devtunnels") ? "Dev Tunnel (Public)" : 
                                              schedulerUrl.contains("ngrok") ? "NGROK (Public)" : "Localhost (Local)").append("\n");
        sb.append("MCP Function: scheduleAppointment - AVAILABLE VIA REST\n");
        sb.append("\nFunction Details:\n");
        sb.append("- Purpose: Schedule dog adoption appointments\n");
        sb.append("- Input: dogId (int), dogName (string)\n");
        sb.append("- Output: success, appointmentTime, message\n");
        sb.append("\nUsage:\n");
        sb.append("POST /schedule-appointment with dogId and dogName\n");
        sb.append("Example: curl -X POST /schedule-appointment -d '{\"dogId\":1,\"dogName\":\"Buddy\"}'\n");
        return sb.toString();
    }
    
    @org.springframework.web.bind.annotation.PostMapping("/schedule-appointment")
    ScheduleResponse scheduleAppointment(@org.springframework.web.bind.annotation.RequestBody ScheduleRequest request) {
        try {
            var restTemplate = new org.springframework.web.client.RestTemplate();
            var requestBody = java.util.Map.of(
                "dogId", request.dogId(),
                "dogName", request.dogName()
            );
            
            @SuppressWarnings("unchecked")
            var response = restTemplate.postForObject(
                schedulerUrl + "/schedule", 
                requestBody, 
                java.util.Map.class
            );
            
            return new ScheduleResponse(
                (Boolean) response.get("success"),
                (String) response.get("appointmentTime"),
                (String) response.get("message")
            );
        } catch (Exception e) {
            return new ScheduleResponse(false, null, "Failed to schedule appointment: " + e.getMessage());
        }
    }
}
