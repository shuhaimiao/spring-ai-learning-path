package com.example.ai.rag.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String getRAGResponse(String userQuery) {
        log.info("Received user query: {}", userQuery);

        // Retrieve relevant documents
        log.info("Retrieving relevant documents from VectorStore for query: '{}'", userQuery);
        SearchRequest searchRequest = SearchRequest.query(userQuery).withTopK(5).withSimilarityThreshold(0.5); // Example: topK=5, threshold=0.5
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        log.info("Retrieved {} relevant documents from VectorStore.", similarDocuments.size());

        if (similarDocuments.isEmpty()) {
            log.warn("No relevant documents found for query: {}", userQuery);
            return "I could not find any information relevant to your query. Please try rephrasing your question or ensure documents have been ingested.";
        }

        // Construct context for LLM
        String context = similarDocuments.stream()
                .map(doc -> {
                    String content = doc.getContent();
                    String sourceFile = doc.getMetadata().get("source_file") != null ? doc.getMetadata().get("source_file").toString() : "Unknown source";
                    return String.format("[Source: %s]\n%s", sourceFile, content);
                })
                .collect(Collectors.joining("\n\n---\n\n"));

        if (log.isDebugEnabled()) { // Be mindful of context length in logs
            log.debug("Constructed context for LLM: {}", context);
        } else {
            log.info("Constructed context for LLM from {} documents.", similarDocuments.size());
        }


        // Create prompt for LLM
        String systemMessageText = """
                You are a helpful AI assistant. Your task is to answer user questions based *only* on the provided context.
                If the information to answer the question is not in the context, state that you cannot answer based on the provided information.
                Do not make up answers or provide information not explicitly found in the context.
                When you use information from a source, cite the source file name mentioned in the context (e.g., [Source: example.pdf]).
                """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemMessageText);
        var systemMessage = systemPromptTemplate.createMessage();

        String userPromptText = """
                Context:
                {context}

                Question:
                {question}

                Based *only* on the context provided above, please answer the question.
                """;
        PromptTemplate userPromptTemplate = new PromptTemplate(userPromptText);
        var userMessage = userPromptTemplate.createMessage(Map.of("context", context, "question", userQuery));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        log.info("Sending prompt to LLM.");
        if (log.isDebugEnabled()) {
            log.debug("Prompt details: System: '{}', User: '{}'", systemMessage.getContent(), userMessage.getContent());
        }

        // Call LLM to generate response
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        String llmResponse = chatResponse.getResult().getOutput().getContent();
        log.info("Received response from LLM.");
        if (log.isDebugEnabled()){
            log.debug("LLM Response: {}", llmResponse);
        }

        return llmResponse;
    }
}
