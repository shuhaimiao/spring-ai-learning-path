package com.example.adoptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryStore;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.jdbc.store.JdbcChatMemoryStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.simple.JdbcClient;
import java.util.List;
import org.springframework.stereotype.Component; // Retained as it's used by @Controller
// Imports for MCP
import org.springframework.ai.mcp.McpClient;
import org.springframework.ai.mcp.McpSyncClient;
import org.springframework.ai.mcp.http.HttpClientSseClientTransport;
import org.springframework.ai.mcp.callbacks.SyncMcpToolCallbackProvider;
// Removed: Tool, Instant, ChronoUnit as they were for DogAdoptionScheduler

import javax.sql.DataSource;

@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

    @Bean
    public PromptChatMemoryAdvisor promptChatMemoryAdvisor(DataSource dataSource) {
        ChatMemoryStore chatMemoryStore = new JdbcChatMemoryStore(dataSource);
        var messageWindowChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryStore(chatMemoryStore)
                .build();
        return PromptChatMemoryAdvisor.builder(messageWindowChatMemory).build();
    }

    @Bean
    public McpSyncClient mcpSyncClient() {
        var mcp = McpClient
                .sync(HttpClientSseClientTransport.builder("http://localhost:8081").build())
                .build();
        mcp.initialize(); // Initialize the client
        return mcp;
    }
}

record Dog(@Id int id, String name, String owner, String description) {
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record DogAdoptionSuggestion(int id, String name, String description) {}

@Controller
@ResponseBody
class AdoptionsController {

    private final ChatClient ai;

    // This is the new constructor for AdoptionsController
    AdoptionsController(
            org.springframework.jdbc.core.simple.JdbcClient db,
            DogRepository repository,
            org.springframework.ai.vectorstore.VectorStore vectorStore,
            PromptChatMemoryAdvisor promptChatMemoryAdvisor,
            McpSyncClient mcpSyncClient, // <-- PARAMETER CHANGED
            ChatClient.Builder aiBuilder
    ) {

        // Existing logic to load data into VectorStore (MUST BE PRESERVED)
        var count = db
                .sql("select count(*) from vector_store")
                .query(Integer.class)
                .single();
        if (count == 0) {
            repository.findAll().forEach(dog -> {
                var dogDocument = new org.springframework.ai.document.Document(
                    String.format("id: %s, name: %s, description: %s", dog.id(), dog.name(), dog.description())
                );
                vectorStore.add(java.util.List.of(dogDocument));
            });
        }

        // Existing system prompt (MUST BE PRESERVED)
        var system = """
                You are an AI powered assistant to help people adopt a dog from
the adoption agency named Pooch Palace with locations in Rio de Janeiro, Mexico
City, Seoul, Tokyo, Singapore, New York City, Amsterdam, Paris, Mumbai, New Delh
i, Barcelona, London, and San Francisco. Information about the dogs available wi
ll be presented below. If there is no information, then return a polite response
 suggesting we don't have any dogs available.
                """;

        // ChatClient.Builder chain updated for MCP Tool Callback
        this.ai = aiBuilder
                .defaultSystem(system)
                .defaultAdvisors(
                        promptChatMemoryAdvisor,
                        new org.springframework.ai.vectorstore.advisor.QuestionAnswerAdvisor(vectorStore)
                )
                // OLD: .defaultTools(scheduler) 
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient)) // <-- LINE CHANGED
                .build();
    }

    @GetMapping("/{user}/assistant")
    String inquire(@PathVariable String user, @RequestParam String question) {
        return ai
                .prompt()
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user)) // ChatMemory import should exist
                .call()
                .content();
    }
}

// The DogAdoptionScheduler class definition has been removed
