package com.example.ai.rag.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.CallPromptResponseSpec callPromptResponseSpec;

    @Mock
    private ChatClient.ResultResponseSpec resultResponseSpec;


    @Mock
    private ChatResponse chatResponse;

    @Mock
    private Generation generation;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        // This line is crucial to make the @InjectMocks work for chatService,
        // as chatClient is initialized in the constructor of ChatService using the builder.
        when(chatClientBuilder.build()).thenReturn(chatClient);
        // Re-initialize chatService or ensure constructor is called after mock setup if not using @InjectMocks for constructor injection.
        // @InjectMocks handles this, but if issues arise, manual instantiation might be needed.
        chatService = new ChatService(chatClientBuilder, vectorStore);
    }

    @Test
    void getRAGResponse_successfulResponseGeneration() {
        // Arrange
        String userQuery = "What is Spring AI?";
        Document doc1 = new Document("Spring AI is awesome.", Map.of("source_file", "doc1.pdf"));
        Document doc2 = new Document("It simplifies AI development.", Map.of("source_file", "doc2.pdf"));
        List<Document> similarDocs = List.of(doc1, doc2);

        String expectedLlmResponse = "Spring AI is a framework for AI applications.";

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(similarDocs);

        // Mocking the fluent API of ChatClient
        when(chatClient.prompt(any(Prompt.class))).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.call()).thenReturn(resultResponseSpec);
        when(resultResponseSpec.chatResponse()).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(generation);
        when(generation.getOutput()).thenReturn(new ChatModel.Generation.Output(expectedLlmResponse, Map.of()));


        // Act
        String actualResponse = chatService.getRAGResponse(userQuery);

        // Assert
        assertThat(actualResponse).isEqualTo(expectedLlmResponse);

        ArgumentCaptor<SearchRequest> searchRequestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(searchRequestCaptor.capture());
        assertThat(searchRequestCaptor.getValue().getQuery()).isEqualTo(userQuery);
        assertThat(searchRequestCaptor.getValue().getTopK()).isEqualTo(5); // As per ChatService implementation

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(chatClient).prompt(promptCaptor.capture());

        Prompt capturedPrompt = promptCaptor.getValue();
        // System message + user message
        assertThat(capturedPrompt.getInstructions()).hasSize(2);
        String userMessageContent = capturedPrompt.getInstructions().get(1).getContent();

        assertThat(userMessageContent).contains("Context:");
        assertThat(userMessageContent).contains("[Source: doc1.pdf]\nSpring AI is awesome.");
        assertThat(userMessageContent).contains("[Source: doc2.pdf]\nIt simplifies AI development.");
        assertThat(userMessageContent).contains("Question:\n" + userQuery);
    }

    @Test
    void getRAGResponse_noDocumentsFound() {
        // Arrange
        String userQuery = "Tell me something obscure.";
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(Collections.emptyList());

        // Act
        String actualResponse = chatService.getRAGResponse(userQuery);

        // Assert
        assertThat(actualResponse).isEqualTo("I could not find any information relevant to your query. Please try rephrasing your question or ensure documents have been ingested.");
        verify(chatClient, never()).prompt(any(Prompt.class));
    }

    @Test
    void getRAGResponse_llmCallThrowsException() {
        // Arrange
        String userQuery = "This will fail.";
        Document doc1 = new Document("Some content.", Map.of("source_file", "doc1.pdf"));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc1));

        when(chatClient.prompt(any(Prompt.class))).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.call()).thenThrow(new RuntimeException("Simulated LLM API error"));

        // Act & Assert
        // Assuming ChatService does not catch and re-wrap this specific exception,
        // but lets it propagate (or Spring's MVC layer handles it).
        // If it were caught and returned a specific message, the assertion would be different.
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            chatService.getRAGResponse(userQuery);
        }, "Simulated LLM API error"); // Check the message if the service re-throws with a specific message

        // Verify that an attempt was made to call the LLM
        verify(chatClient).prompt(any(Prompt.class));
    }
}
