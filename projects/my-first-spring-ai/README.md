# My First Spring AI Project

This project demonstrates a Spring AI application with two modules: `adoptions` and `scheduler`.
It is structured as a multi-module Maven project.

## Overview

The `my-first-spring-ai` project consists of two main Spring Boot applications:

1.  **`adoptions`**: This application is the primary service, providing an AI-powered assistant for a dog adoption agency named "Pooch Palace". It utilizes Spring AI, Spring Data, and a VectorStore to offer information about available dogs and assist with the adoption process. It exposes a REST endpoint at `/{user}/assistant` for users to interact with the AI assistant. This application also integrates with an MCP (Model Control Protocol) service, which is the `scheduler` application, for handling specific tool calls or scheduled operations.

2.  **`scheduler`**: This application functions as an MCP service. It is designed to handle requests from the `adoptions` application, likely for performing scheduled tasks or executing tool-specific functions that are offloaded from the main `adoptions` service.

## Prerequisites

- Java 17 or later
- Maven

## How to Run

This is a multi-module Maven project. You can build and run the applications from the root directory (`projects/my-first-spring-ai`).

1.  **Build the entire project:**
    Navigate to the root directory (`projects/my-first-spring-ai`) and run:
    ```bash
    mvn clean install
    ```

2.  **Run the `scheduler` application:**
    After building, you can run the `scheduler` application. Open a new terminal, navigate to the `scheduler` module directory, and use the Spring Boot Maven plugin:
    ```bash
    cd scheduler
    mvn spring-boot:run
    ```
    Alternatively, you can run the packaged JAR (after `mvn clean install` from the root):
    ```bash
    java -jar target/scheduler-0.0.1-SNAPSHOT.jar
    ```
    This application will typically start on port 8081.

3.  **Run the `adoptions` application:**
    Similarly, open another new terminal, navigate to the `adoptions` module directory, and use the Spring Boot Maven plugin:
    ```bash
    # Make sure you are in the root project directory first, then navigate to adoptions
    # cd ../adoptions  (if you were in scheduler directory)
    # If you are in the root directory (projects/my-first-spring-ai):
    cd adoptions
    mvn spring-boot:run
    ```
    Alternatively, you can run the packaged JAR (after `mvn clean install` from the root):
    ```bash
    java -jar target/adoptions-0.0.1-SNAPSHOT.jar
    ```
    This application will typically start on port 8080.

    **Order of Execution:** Ensure the `scheduler` application is running before starting the `adoptions` application, as `adoptions` depends on `scheduler` for MCP communication.

## Interacting with the Application

Once both applications are running (scheduler first, then adoptions), you can interact with the `adoptions` assistant by sending GET requests to:

`http://localhost:8080/{user}/assistant?question=Your_question_here`

Replace `{user}` with a user identifier (e.g., `testuser`) and `Your_question_here` with your query about dog adoptions.

For example:
`http://localhost:8080/testuser/assistant?question=Do%20you%20have%20any%20labradors%3F`



# Reference 

https://spring.io/blog/2025/05/20/your-first-spring-ai-1