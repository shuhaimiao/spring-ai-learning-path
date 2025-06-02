package com.example.memorymcp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "memories")
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    // pgvector typically uses 'vector' type. The dimension should match the embedding model.
    // For now, let's assume a dimension of 1536 (common for OpenAI models, adjust if using a different one with Ollama).
    // Spring AI's PgVectorStore will handle the conversion from List<Double> to the 'vector' type.
    @Column(columnDefinition = "vector(1536)")
    private List<Double> embedding;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "JSONB")
    private String metadata; // Store as JSON string

    // Constructors
    public Memory() {
    }

    public Memory(String content, List<Double> embedding, LocalDateTime timestamp, String metadata) {
        this.content = content;
        this.embedding = embedding;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "Memory{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", metadata='" + metadata + '\'' +
                '}'; // Embedding excluded for brevity
    }
}
