package com.example.ai.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final VectorStore vectorStore;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestDocument(String filePath, Map<String, Object> customMetadata) {
        log.info("Starting ingestion for document: {}", filePath);
        Path path = Paths.get(filePath);

        try {
            // Load PDF document using Langchain4j's FileSystemDocumentLoader and ApachePdfBoxDocumentParser
            log.info("Loading document from path: {}", path);
            DocumentParser documentParser = new ApachePdfBoxDocumentParser();
            Document document = FileSystemDocumentLoader.loadDocument(path, documentParser);

            // Add custom metadata and source file name
            document.metadata().putAll(customMetadata);
            document.metadata().put("source_file", path.getFileName().toString());
            log.info("Added metadata to document. Current metadata: {}", document.metadata());

            // Perform text cleaning (basic example: remove superfluous whitespace)
            String cleanedText = cleanText(document.text());
            Document cleanedDocument = new Document(cleanedText, document.metadata());
            log.info("Document text cleaned. Original length: {}, Cleaned length: {}", document.text().length(), cleanedText.length());

            // Chunk the document content using RecursiveCharacterTextSplitter
            // Configure chunk size (e.g., 500 characters), overlap (e.g., 50 characters)
            var textSplitter = DocumentSplitters.recursive(500, 50);
            List<TextSegment> segments = textSplitter.split(cleanedDocument);
            log.info("Document split into {} segments", segments.size());

            // Convert TextSegments to Spring AI Document objects for VectorStore
            List<org.springframework.ai.document.Document> springAiDocuments = segments.stream()
                    .map(segment -> new org.springframework.ai.document.Document(segment.text(), segment.metadata().asMap()))
                    .collect(Collectors.toList());

            // Add the list of Document chunks to the VectorStore
            log.info("Adding {} document chunks to VectorStore", springAiDocuments.size());
            vectorStore.add(springAiDocuments);
            log.info("Successfully added document chunks to VectorStore.");

        } catch (Exception e) {
            log.error("Error during document ingestion for file: " + filePath, e);
            // Depending on requirements, you might want to throw a custom exception
            // or handle it in a specific way (e.g., move file to an error directory)
        }
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        // Remove superfluous whitespace (leading/trailing, multiple spaces)
        String cleaned = text.trim().replaceAll("\\s+", " ");
        // Example: Remove some common special characters that might not be useful for embedding.
        // This is a very basic example and might need to be adjusted based on the document content.
        // cleaned = cleaned.replaceAll("[^a-zA-Z0-9\\s.,;:'\"()?!-]", ""); // Keep basic punctuation
        return cleaned;
    }
}
