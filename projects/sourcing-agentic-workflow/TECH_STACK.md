# Tech Stack: `sourcing-email-crafter` AI Agent

This document outlines the technology stack and core patterns for the `sourcing-email-crafter` AI agent, following the reference implementation patterns from the `my-weather-server` project.

## 1. Core Technologies

*   **Programming Language**: **Java 17**
*   **Framework**: **Spring Boot 3.3.0**
*   **Build Tool**: **Apache Maven**
*   **AI Framework**: **Spring AI 1.0.0**
*   **LLM Provider**: **OpenAI** (Model: `gpt-4-turbo`)

## 2. Key Spring AI `1.0.0` Patterns

The latest version of Spring AI introduces a streamlined and powerful API. This project leverages the following key features:

*   **`ChatClient` Interface**: This is the central, fluent API for all interactions with the Large Language Model (LLM). It simplifies sending prompts and receiving responses.

    *   *Implementation*: Used in both `EmailGeneratorService.java` and `EmailEvaluatorService.java` to interact with the LLM.

*   **`PromptTemplate`**: Spring AI provides a powerful templating system for creating prompts with variable substitution.

    *   *Implementation*: Both services use `PromptTemplate` to create dynamic prompts based on the input parameters.

*   **Structured Output Parsing**: Spring AI can parse structured outputs (like JSON) from LLM responses into Java objects.

    *   *Implementation*: `EmailEvaluatorService.java` uses an `ObjectMapper` to parse the evaluation results into Java objects.

## 3. Core Pattern: Evaluator-Optimizer

This project implements the Evaluator-Optimizer pattern, which involves two main LLM-driven components:

*   **Generator LLM** (`EmailGeneratorService.java`): Responsible for creating an initial draft of the email and refining it based on feedback.

*   **Evaluator LLM** (`EmailEvaluatorService.java`): Responsible for reviewing the draft against predefined criteria and providing feedback for refinement.

The process is iterative, with the Generator LLM refining the email based on the Evaluator LLM's feedback until a desired quality threshold is met or the maximum number of refinement loops is reached.

*   *Implementation*: `EmailCrafterService.java` orchestrates this iterative process, managing the interaction between the Generator and Evaluator.

## 4. Other Important Components

*   **Configuration**:
    *   **`application.properties`**: Standard Spring Boot file for configuring the application, including the AI model (`spring.ai.openai.chat.options.model`) and other framework settings.
    *   **`dotenv-java`**: This utility is used to load secrets (like the OpenAI API key) from a local `.env` file during development. This avoids hard-coding secrets in `application.properties` and keeps them out of version control. The setup is in the `main` method of `SourcingEmailCrafterApplication.java`.

*   **Model Classes**:
    *   Java records are used to represent the structured input and output data, making the code more concise and readable.
    *   *Implementation*: See the `model` package, which includes classes like `EmailCrafterInput.java` and `EmailCrafterOutput.java`.

## 5. Application Structure

*   The project is a **REST API** application, which provides a stateless, API-first design for easy integration into existing recruitment workflows and platforms.
    *   **`EmailCrafterController.java`** exposes a single endpoint:
        *   `POST /api/v1/email/craft` - Takes a JSON input with candidate and job details and returns a personalized email with quality scores.

## 6. Maven Dependencies (`pom.xml`)

The `pom.xml` file defines all the project dependencies and build configuration, following the pattern established in the reference implementation:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
        <version>${spring-ai.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
        <version>3.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
``` 