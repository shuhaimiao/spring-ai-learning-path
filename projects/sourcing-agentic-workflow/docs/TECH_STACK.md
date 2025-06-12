# Tech Stack: `my-weather-server` AI Agent

This document outlines the technology stack and core patterns for our AI agent projects, based on the `my-weather-server` reference implementation.

## 1. Core Technologies

*   **Programming Language**: **Java 17**
*   **Framework**: **Spring Boot 3.3.0**
*   **Build Tool**: **Apache Maven**
*   **AI Framework**: **Spring AI 1.0.0**
*   **LLM Provider**: **OpenAI** (Model: `gpt-4-turbo`)

## 2. Key Spring AI `1.0.0` Patterns

The latest version of Spring AI introduces a more streamlined and powerful API. This project relies on the following key features:

*   **`ChatClient` Interface**: This is the central, fluent API for all interactions with the Large Language Model (LLM). It simplifies sending prompts, providing tools, and receiving responses.

    *   *Implementation*: See `MyWeatherServerApplication.java` where `ChatClient.Builder` is used to construct the client.

*   **Annotation-Driven Tool Definitions**: This is the modern approach to "function calling". Instead of manually creating `FunctionCallback` beans, we can now directly expose methods as tools to the AI.

    *   **`@Tool` Annotation**: Any method in a Spring bean annotated with `@Tool` is automatically made available to the AI. Spring AI inspects the method signature (name, parameters) and the Javadoc/annotation description to provide the necessary metadata to the LLM.
    *   *Implementation*: The `getWeatherForecastByLocation` and `getAlerts` methods in `WeatherService.java` are exposed as tools using this annotation.

*   **Tool Registration at Call-Time**: Tools are provided to the LLM on a per-request basis using the `ChatClient`'s fluent API.

    *   **`.tools()` method**: The `WeatherService` bean, containing `@Tool`-annotated methods, is passed to the `chatClient.prompt().tools(weatherService)` method. This gives the LLM the context it needs to decide if it should use one of the provided tools to answer the user's prompt.

*   **Advisors for Cross-Cutting Concerns**: The `ChatClient` supports "Advisors," which are interceptors that can act on the request to and response from the LLM. This is useful for logging, metrics, security, or other custom logic.

    *   *Implementation*: The project uses both the built-in `SimpleLoggerAdvisor` and a custom `LoggerAdvisor.java` to demonstrate how to log the full request/response payload.

## 3. Other Important Components

*   **HTTP Communication**:
    *   **Spring `RestClient`**: The application uses the modern, synchronous `RestClient` (see `WeatherService.java`) to communicate with the external National Weather Service API. This is the recommended client for new applications over the older `RestTemplate`.

*   **Configuration**:
    *   **`application.properties`**: Standard Spring Boot file for configuring the application, including the AI model (`spring.ai.openai.chat.options.model`) and other framework settings.
    *   **`dotenv-java`**: This utility is used to load secrets (like the OpenAI API key) from a local `.env` file during development. This avoids hard-coding secrets in `application.properties` and keeps them out of version control. The setup is in the `main` method of `MyWeatherServerApplication.java`.

## 4. Application Structure

*   The project has two main interfaces:
    *   **`CommandLineRunner`** application, which provides a simple interactive shell. This is a great pattern for quickly testing and demonstrating the core AI functionality without needing a full web UI.
    *   **REST API** implemented using Spring Web MVC. The `WeatherController.java` exposes several endpoints:
        *   `POST /weather/forecast` - Direct access to weather forecast data
        *   `POST /weather/alerts` - Direct access to weather alerts
        *   `POST /weather/chat/forecast` - AI-powered natural language interface to weather data

## 5. Maven Dependencies (`pom.xml`)

The `pom.xml` file defines all the project dependencies and build configuration. Here is the full content for reference:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.0</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>my-weather-server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>my-weather-server</name>
	<description>Demo project for Spring Boot</description>
	<url />
	<licenses>
		<license />
	</licenses>
	<developers>
		<developer />
	</developers>
	<scm>
		<connection />
		<developerConnection />
		<tag />
		<url />
	</scm>
	<properties>
		<java.version>17</java.version>
		<spring-ai.version>1.0.0</spring-ai.version>
	</properties>
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

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

	<repositories>
		<repository>
			<id>spring-milestones</id>
			<name>Spring Milestones</name>
			<url>https://repo.spring.io/milestone</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>
	
</project>
``` 