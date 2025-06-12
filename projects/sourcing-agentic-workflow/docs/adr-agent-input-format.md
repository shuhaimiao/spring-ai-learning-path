# ADR: Agent Input Formatting

**Status:** Accepted

**Date:** 2025-06-12

## Context

In our hybrid agentic architecture, a classical orchestrator is responsible for retrieving all necessary contextual information from backend APIs and then invoking specialized LLM agents to perform specific tasks (e.g., `SourcingEmailCrafterAgent`, `CandidatePrioritizationAgent`).

The key challenge is to define a standardized, robust, and maintainable method for passing this potentially complex and multi-faceted contextual information from the orchestrator to the LLM agents. This input format needs to ensure that agents can easily understand and utilize the provided data to perform their tasks effectively.

## Decision

We will use a **single, well-structured JSON object** as the primary mechanism for passing all structured data from the orchestrator to an LLM agent. This JSON object will encapsulate all distinct pieces of information (e.g., `CandidateProfile`, `JobProfile`, `EmailContext`, `DesiredToneStyle`) as clearly named fields within it.

This JSON data payload will be accompanied by **plain text instructions** within the overall prompt sent to the agent. These instructions will define the agent's role, its specific task, and guide it on how to interpret and use the provided JSON data.

**Example Snippet (Conceptual):**

```
You are the SourcingEmailCrafterAgent...

Use the following JSON data:
```json
{
  "candidateProfile": { ... },
  "jobProfile": { ... },
  "emailContext": { ... },
  "desiredToneStyle": { ... }
}
```
Further instructions on how to use this data...
```

## Consequences

### Positive Consequences:

1.  **Clarity and Structure:** A single JSON object with clearly defined fields provides a highly organized and unambiguous structure for the input data. This makes it easier for the LLM to differentiate and correctly interpret each piece of information.
2.  **Robust Parsing:** LLMs are generally proficient at parsing well-formed JSON. A single, valid JSON input minimizes the risk of parsing errors or misinterpretations that could arise from more complex or fragmented input formats.
3.  **Ease of Integration and Maintenance:**
    *   **Orchestrator:** Simplifies the orchestrator's role to constructing one comprehensive JSON payload.
    *   **Agent:** Provides a consistent and predictable input schema for the agent.
    *   **Debugging:** Easier to validate, log, and debug a single, structured JSON input.
4.  **Improved Prompt Engineering:** Separating structured data (JSON) from instructional text (plain text prompt) allows for cleaner and more effective prompt design. The LLM can focus on the task described in the instructions, using the JSON as its data source.
5.  **Scalability:** As new data elements are required, they can be added as new fields to the JSON object without fundamentally altering the input mechanism, as long as the overall structure remains a single JSON payload.
6.  **API and MCP Server Compatibility:** Using a single JSON object aligns with standard API design practices (e.g., for RESTful POST/PUT requests) and facilitates exposing the agent's capabilities as a well-defined API endpoint or as part of a Model Context Protocol (MCP) server. It simplifies schema definition (e.g., OpenAPI, JSON Schema), client integration, and API versioning.

### Negative Consequences (Minor):

1.  **Initial Schema Definition:** There's an upfront effort required to define the comprehensive JSON schema for each agent's input. However, this is a standard part of good API and system design.
2.  **Payload Size:** For very large amounts of contextual data, the JSON payload could become large. This is a general concern with passing extensive context, regardless of format, and can be managed through appropriate data filtering by the orchestrator.

## Alternatives Considered

1.  **Multiple JSON Blocks:**
    *   *Description:* Passing several distinct JSON objects, potentially interspersed with plain text instructions.
    *   *Reason for Rejection:* Can make the prompt harder for the LLM to parse reliably and increases the risk of the LLM confusing which data belongs to which context. It also makes the orchestrator's job of assembling the prompt more complex.

2.  **Mixture of Plain Text and Unstructured/Semi-structured Data:**
    *   *Description:* Embedding core data elements (like lists of skills or responsibilities) directly as plain text within the instructional part of the prompt, rather than within a structured JSON object.
    *   *Reason for Rejection:* Forces the LLM to perform more complex parsing and inference to separate data from instructions. This is less reliable and more prone to errors than providing data in a clearly structured format like JSON. It also makes it harder to update or manage the data elements systematically.

By adopting a single JSON object for structured data, we aim for a balance of robustness, clarity, and maintainability in our agentic system's communication layer.
