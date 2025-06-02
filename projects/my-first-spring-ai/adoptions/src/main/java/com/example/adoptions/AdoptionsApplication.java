package com.example.adoptions;

import javax.sql.DataSource;

import org.springframework.ai.chat.client.ChatClient;
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

@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
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

    AdoptionsController(
            org.springframework.jdbc.core.simple.JdbcClient db,
            DogRepository repository,
            org.springframework.ai.vectorstore.VectorStore vectorStore,
            ChatClient.Builder aiBuilder
    ) {

        // Load data into VectorStore
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

        // System prompt
        var system = """
                You are an AI powered assistant to help people adopt a dog from
the adoption agency named Pooch Palace with locations in Rio de Janeiro, Mexico
City, Seoul, Tokyo, Singapore, New York City, Amsterdam, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco. Information about the dogs available will be presented below. If there is no information, then return a polite response suggesting we don't have any dogs available.
                """;

        // Build ChatClient
        this.ai = aiBuilder
                .defaultSystem(system)
                .build();
    }

    @GetMapping("/{user}/assistant")
    String inquire(@PathVariable String user, @RequestParam String question) {
        return ai
                .prompt()
                .user(question)
                .call()
                .content();
    }
}
