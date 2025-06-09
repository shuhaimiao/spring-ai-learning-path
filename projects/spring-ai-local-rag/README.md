# Local RAG with Spring AI, Ollama, and PostgreSQL

This project implements a Retrieval Augmented Generation (RAG) system using Spring AI. It leverages local Large Language Models (LLMs) served by Ollama for reasoning and embedding generation, and a Dockerized PostgreSQL instance with the pgvector extension for vector storage.

## Prerequisites

Before you begin, ensure you have the following installed:
- [Java Development Kit (JDK) 21](https://adoptium.net/) or later
- [Apache Maven 3.8.x](https://maven.apache.org/download.cgi) or later
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (for running PostgreSQL and Ollama)
- [Ollama](https://ollama.com/) (ensure the Ollama application is running or the `ollama` command is in your PATH if you plan to run it outside Docker)

## Setup Instructions

Follow these steps to set up and run the application:

### Step 1: Set up Ollama and Qwen 3 Models

You can run Ollama as a desktop application or within Docker (as configured in the `compose.yml` of this project).

**1. Install and Start Ollama:**
   - If not using the Dockerized version, download and install Ollama from the [official website](https://ollama.com/). Ensure the Ollama application is running.
   - If using the Dockerized version (recommended for this project), Ollama will be started in a later step using `docker-compose`.

**2. Pull Required LLM Models:**
   Open your terminal and run the following commands to download the Qwen 3 models. These models will be used for generating embeddings and for the chat functionality.

   ```bash
   ollama pull qwen3:8b
   ollama pull dengcao/qwen3-embedding-8b:q4_K_M
   ```

**3. Verify Model Installation:**
   List the models available in Ollama to ensure they were downloaded correctly:
   ```bash
   ollama list
   ```
   You should see `qwen3:8b` and `dengcao/qwen3-embedding-8b:q4_K_M` in the output.

### Step 2: Set up Dockerized PostgreSQL with pgvector

This project uses a Dockerized PostgreSQL instance with the `pgvector` extension for efficient similarity searches.

**1. Start PostgreSQL Service:**
   Navigate to the `projects/spring-ai-local-rag` directory in your terminal (where the `compose.yml` file is located) and run:
   ```bash
   docker compose up -d postgres
   ```
   This command will download the `pgvector/pgvector:pg16` image if you don't have it and start the PostgreSQL service in detached mode. The database will be named `vectordb` with user `testuser` and password `testpwd`, as configured in `compose.yml` and `application.properties`.

**2. Verify PostgreSQL (Optional):**
   You can check if the PostgreSQL container is running using `docker ps`. To connect to the database and verify the `vector` extension, you can use a SQL client or run:
   ```bash
   docker compose exec postgres psql -U testuser -d vectordb -c "CREATE EXTENSION IF NOT EXISTS vector;"
   ```
   (The application is configured with `spring.ai.vectorstore.pgvector.initialize-schema=true`, which should also attempt to create the extension and table if they don't exist upon startup.)

### Step 3: Configure the Spring Boot Application

The Spring Boot application is pre-configured for this local setup. Key properties in `src/main/resources/application.properties` include:
- Ollama URLs: `spring.ai.ollama.base-url=http://localhost:11434`
- Ollama models:
    - Chat: `spring.ai.ollama.chat.options.model=qwen3:8b`
    - Embedding: `spring.ai.ollama.embedding.options.model=dengcao/qwen3-embedding-8b:q4_K_M`
- Database credentials: `spring.datasource.username=testuser`, `spring.datasource.password=testpwd` (matching `compose.yml`)
- Vector store dimensions: `spring.ai.vectorstore.pgvector.dimensions=4096` (matching the chosen embedding model's capabilities)

No changes are typically needed to `application.properties` for the default local setup.

### Step 4: Build and Run the Application

**1. Start Ollama Service (if not already running):**
   If you haven't started the Ollama desktop application and want to use the Dockerized version, navigate to the `projects/spring-ai-local-rag` directory and run:
   ```bash
   docker compose up -d ollama
   ```
   Ensure both `postgres` and `ollama` containers are running before starting the Java application. You can start both with:
   ```bash
   docker compose up -d ollama postgres
   ```

**2. Build the Application:**
   Navigate to the `projects/spring-ai-local-rag` directory in your terminal and build the application using Maven:
   ```bash
   mvn clean package
   ```
   This will generate a JAR file in the `target/` directory (e.g., `local-rag-service-0.0.1-SNAPSHOT.jar`).

**3. Run the Application:**
   Once built, run the application:
   ```bash
   java -jar target/local-rag-service-0.0.1-SNAPSHOT.jar
   ```
   (Replace `local-rag-service-0.0.1-SNAPSHOT.jar` with the actual JAR file name if it differs.)
   The application will start on port `8080` by default.

## Testing the Application

Once the application is running, you can test its endpoints using `curl` or any API client like Postman.

### Ingesting a Document

To ingest a PDF document into the RAG system:
```bash
# Replace /path/to/your/document.pdf with the actual path to a PDF file on your system.
curl -X POST   -F "file=@/path/to/your/document.pdf"   -F "source_id=doc123"   -F "category=technical"   http://localhost:8080/api/rag/ingest
```
**Expected Response:**
```
Document ingested successfully: your_document.pdf
```
The `-F` options allow you to pass metadata along with the file. This metadata will be stored with the document chunks.

### Querying the RAG System

To ask a question based on the ingested documents:
```bash
# Replace "Your question about the ingested document" with an actual question.
curl -X POST   -H "Content-Type: text/plain"   -d "Your question about the ingested document"   http://localhost:8080/api/rag/query
```
**Expected Response:**
The system will return a plain text response generated by the LLM, based on the context retrieved from your ingested documents. For example:
```
Based on the provided context from document.pdf, the answer to your question is X.
```

## Project Structure Overview

- `projects/spring-ai-local-rag/`
    - `src/main/java/`: Java source code for the Spring Boot application.
        - `com/example/ai/rag/`
            - `LocalRagApplication.java`: Main application class.
            - `controller/ChatController.java`: REST API endpoints.
            - `service/ChatService.java`: RAG logic (retrieval, prompt construction, LLM call).
            - `service/DocumentIngestionService.java`: Document loading, chunking, embedding, and storage.
    - `src/main/resources/`: Application resources.
        - `application.properties`: Configuration for Spring Boot, Spring AI, Ollama, database, etc.
    - `src/test/java/`: Unit tests.
    - `compose.yml`: Docker Compose file for PostgreSQL and Ollama services.
    - `pom.xml`: Maven project configuration.
    - `README.md`: This file.

## Troubleshooting (Optional)

- **Ollama Connection Issues:** Ensure Ollama is running and accessible at `http://localhost:11434`. Check `docker ps` if using Docker.
- **PostgreSQL Connection Issues:** Verify the PostgreSQL container is running and credentials in `application.properties` match `compose.yml`.
- **Model Not Found:** Double-check that you have pulled the correct models using `ollama pull ...` and that they are listed by `ollama list`.
- **Embedding Dimension Mismatch:** If you change the embedding model, ensure `spring.ai.vectorstore.pgvector.dimensions` in `application.properties` matches the new model's output dimension.
```
