package com.example.ai.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.splitter.RecursiveCharacterTextSplitter;
import dev.langchain4j.data.segment.TextSegment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.ai.vectorstore.VectorStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTest {

    @Mock
    private VectorStore vectorStore;

    // We don't mock the splitter directly, but the static method that creates it.
    // Or we can mock the splitter if it were injected.
    // For DocumentSplitters.recursive, it's a static factory.

    @InjectMocks
    private DocumentIngestionService documentIngestionService;

    private MockedStatic<FileSystemDocumentLoader> mockedFileLoader;
    private MockedStatic<DocumentSplitters> mockedDocumentSplitters;
    private MockedStatic<LoggerFactory> mockedLoggerFactory; // For capturing logs if needed

    @Mock
    private Logger mockLogger;


    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Mock LoggerFactory to inject our mock Logger
        // This is a bit more involved if we want to verify log messages.
        // For simplicity, direct log verification isn't shown here but can be done via ArgumentCaptor on a logging appender
        // or by refactoring service to have Logger injected.
        // Here, we will test behavior based on method calls and exceptions.

        // Setup for static mocks
        mockedFileLoader = mockStatic(FileSystemDocumentLoader.class);
        mockedDocumentSplitters = mockStatic(DocumentSplitters.class);


        tempFile = Files.createTempFile("test-doc", ".pdf");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockedFileLoader.close();
        mockedDocumentSplitters.close();
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }

    @Test
    void ingestDocument_successfulIngestion() {
        // Arrange
        String filePath = tempFile.toString();
        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("customKey", "customValue");

        Document loadedDocument = new Document("Sample PDF content here.", new HashMap<>());
        loadedDocument.metadata().put("originalKey", "originalValue");

        RecursiveCharacterTextSplitter mockSplitter = mock(RecursiveCharacterTextSplitter.class);
        TextSegment segment1 = TextSegment.from("Sample PDF content here.", new dev.langchain4j.data.document.Metadata(new HashMap<>()));

        mockedFileLoader.when(() -> FileSystemDocumentLoader.loadDocument(any(Path.class), any(DocumentParser.class)))
                .thenReturn(loadedDocument);
        mockedDocumentSplitters.when(() -> DocumentSplitters.recursive(anyInt(), anyInt()))
                .thenReturn(mockSplitter);
        when(mockSplitter.split(any(Document.class))).thenReturn(List.of(segment1));

        // Act
        documentIngestionService.ingestDocument(filePath, customMetadata);

        // Assert
        ArgumentCaptor<List<org.springframework.ai.document.Document>> documentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(documentsCaptor.capture());

        List<org.springframework.ai.document.Document> capturedDocs = documentsCaptor.getValue();
        assertThat(capturedDocs).hasSize(1);
        org.springframework.ai.document.Document capturedDoc = capturedDocs.get(0);

        assertThat(capturedDoc.getContent()).isEqualTo("Sample PDF content here.");
        assertThat(capturedDoc.getMetadata()).containsEntry("source_file", tempFile.getFileName().toString());
        assertThat(capturedDoc.getMetadata()).containsEntry("customKey", "customValue");
        // originalKey from the loadedDocument's metadata should also be preserved if the service logic does that.
        // The current service logic creates a new Document for cleaning, then new TextSegments,
        // then new Spring AI documents. Let's check what metadata is actually preserved.
        // The service's Document cleanedDocument = new Document(cleanedText, document.metadata()); preserves it.
        // Then TextSegment.from(text, metadata) should preserve it.
        // And new org.springframework.ai.document.Document(segment.text(), segment.metadata().asMap())
        assertThat(capturedDoc.getMetadata()).containsEntry("originalKey", "originalValue");


        // Verify that loadDocument was called with the correct path
        mockedFileLoader.verify(() -> FileSystemDocumentLoader.loadDocument(eq(tempFile), any(ApachePdfBoxDocumentParser.class)));
        // Verify splitter was used
        verify(mockSplitter).split(any(Document.class));
    }

    @Test
    void ingestDocument_fileNotFound() {
        // Arrange
        String nonExistentFilePath = "path/to/non/existent/file.pdf";
        Map<String, Object> customMetadata = new HashMap<>();

        // Simulate FileSystemDocumentLoader throwing an exception (e.g. RuntimeException wrapping IOException)
        mockedFileLoader.when(() -> FileSystemDocumentLoader.loadDocument(any(Path.class), any(DocumentParser.class)))
                .thenThrow(new RuntimeException("Simulated IOException: File not found"));

        // Act
        documentIngestionService.ingestDocument(nonExistentFilePath, customMetadata);

        // Assert
        verify(vectorStore, never()).add(anyList());
        // Add verification for logging if a mock logger was properly injected and used by the service's static log field.
        // This typically requires more advanced setup like PowerMockito for static loggers or refactoring service to inject Logger.
    }

    @Test
    void ingestDocument_emptyDocumentContent() {
        // Arrange
        String filePath = tempFile.toString();
        Map<String, Object> customMetadata = new HashMap<>();
        // Document with null text, metadata is preserved
        Document loadedDocument = new Document(null, dev.langchain4j.data.document.Metadata.from("source_file", "test.pdf"));


        RecursiveCharacterTextSplitter mockSplitter = mock(RecursiveCharacterTextSplitter.class);

        mockedFileLoader.when(() -> FileSystemDocumentLoader.loadDocument(any(Path.class), any(DocumentParser.class)))
                .thenReturn(loadedDocument);
        mockedDocumentSplitters.when(() -> DocumentSplitters.recursive(anyInt(), anyInt()))
                .thenReturn(mockSplitter);

        // When cleanText is called with null, it returns "", so splitter gets an empty string.
        // The splitter then might return an empty list of segments or a segment with empty text.
        // Let's assume it returns an empty list of TextSegments if the input text to split is empty.
        when(mockSplitter.split(any(Document.class))).thenReturn(Collections.emptyList());


        // Act
        documentIngestionService.ingestDocument(filePath, customMetadata);

        // Assert
        // Depending on behavior: could be never(), or add with empty list.
        // Current VectorStore behavior might throw error on empty list, or do nothing.
        // Assuming if no segments, .add is not called or called with empty list.
        // The service code collects segments into springAiDocuments. If segments is empty, springAiDocuments is empty.
        // vectorStore.add(emptyList) is valid.
        ArgumentCaptor<List<org.springframework.ai.document.Document>> documentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(documentsCaptor.capture());
        assertThat(documentsCaptor.getValue()).isEmpty();
    }

     @Test
    void ingestDocument_documentWithOnlyWhitespaceContent() {
        // Arrange
        String filePath = tempFile.toString();
        Map<String, Object> customMetadata = new HashMap<>();
        Document loadedDocument = new Document("   \n\t   ", dev.langchain4j.data.document.Metadata.from("source_file", "test.pdf"));

        RecursiveCharacterTextSplitter mockSplitter = mock(RecursiveCharacterTextSplitter.class);

        mockedFileLoader.when(() -> FileSystemDocumentLoader.loadDocument(any(Path.class), any(DocumentParser.class)))
            .thenReturn(loadedDocument);
        mockedDocumentSplitters.when(() -> DocumentSplitters.recursive(anyInt(), anyInt()))
            .thenReturn(mockSplitter);

        // The cleanText method turns "   \n\t   " into "".
        // So, similar to the emptyDocumentContent test, the splitter receives an empty document.
        when(mockSplitter.split(any(Document.class))).thenReturn(Collections.emptyList());

        // Act
        documentIngestionService.ingestDocument(filePath, customMetadata);

        // Assert
        ArgumentCaptor<List<org.springframework.ai.document.Document>> documentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(documentsCaptor.capture());
        assertThat(documentsCaptor.getValue()).isEmpty();
    }
}
