package com.example.ai.rag.controller;

import com.example.ai.rag.service.ChatService;
import com.example.ai.rag.service.DocumentIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rag")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private static final String UPLOAD_DIR_NAME = "temp_uploads";
    private final Path UPLOAD_DIR;

    private final ChatService chatService;
    private final DocumentIngestionService documentIngestionService;

    @Autowired
    public ChatController(ChatService chatService, DocumentIngestionService documentIngestionService) {
        this.chatService = chatService;
        this.documentIngestionService = documentIngestionService;

        // Resolve the upload directory path relative to the application's root
        // This attempts to create it in the current working directory, which might be /app/
        this.UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR_NAME).toAbsolutePath();
        log.info("Temporary upload directory resolved to: {}", UPLOAD_DIR.toString());

        try {
            Files.createDirectories(UPLOAD_DIR);
            log.info("Upload directory created (or already exists): {}", UPLOAD_DIR);
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", UPLOAD_DIR, e);
            // Depending on the application's needs, this could be a fatal error.
            // For now, we log it and proceed; ingestion might fail if the dir is truly unusable.
        }
    }

    @PostMapping("/query")
    public ResponseEntity<String> askQuestion(@RequestBody String query) {
        log.info("Received query request: {}", query);
        if (!StringUtils.hasText(query)) {
            log.warn("Query is null or empty.");
            return ResponseEntity.badRequest().body("Query cannot be null or empty.");
        }
        try {
            String response = chatService.getRAGResponse(query);
            log.info("Returning RAG response for query: {}", query);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing query: " + query, e);
            return ResponseEntity.status(500).body("Error processing your query: " + e.getMessage());
        }
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocument(@RequestParam("file") MultipartFile file,
                                                 @RequestParam(required = false) Map<String, String> metadataParams) {
        log.info("Received request to ingest document: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("File for ingestion is empty: {}", file.getOriginalFilename());
            return ResponseEntity.badRequest().body("File cannot be empty.");
        }

        Path tempFilePath = null;
        try {
            // Ensure UPLOAD_DIR exists, try creating again just in case it was removed or first attempt failed silently
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown_file");
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            tempFilePath = UPLOAD_DIR.resolve(uniqueFileName);

            log.info("Copying uploaded file to temporary path: {}", tempFilePath);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            Map<String, Object> metadata = new HashMap<>();
            if (metadataParams != null) {
                metadataParams.forEach(metadata::put);
            }
            log.info("Prepared metadata for ingestion: {}", metadata);

            documentIngestionService.ingestDocument(tempFilePath.toString(), metadata);
            log.info("Document ingestion successful for: {}", file.getOriginalFilename());

            return ResponseEntity.ok("Document ingested successfully: " + file.getOriginalFilename());

        } catch (IOException e) {
            log.error("IOException during file handling for {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Error during file handling: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error during document ingestion for {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Error during document ingestion: " + e.getMessage());
        } finally {
            if (tempFilePath != null) {
                try {
                    log.info("Deleting temporary file: {}", tempFilePath);
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    log.error("Could not delete temporary file: {}", tempFilePath, e);
                }
            }
        }
    }
}
