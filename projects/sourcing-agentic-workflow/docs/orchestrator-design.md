# Orchestrator Design

**Date:** 2025-06-12

## 1. Introduction

This document outlines the design considerations and responsibilities of the **Orchestrator** component within our hybrid agentic workflow. The Orchestrator plays a crucial role in managing the execution flow, providing context to specialized LLM agents, handling interactions with external systems, and implementing cross-cutting concerns like logging and operational safeguards.

It acts as the central coordinator that invokes agents (like the `SourcingEmailCrafterAgent` or `CandidatePrioritizationAgent`), supplies them with necessary data, and processes their outputs.

## 2. Key Responsibilities

The Orchestrator is responsible for the following key areas, as detailed in their respective Architectural Decision Records (ADRs) and further elaborated here:

*   **Agent Invocation and Context Provisioning:**
    *   Preparing and formatting all input data for LLM agents according to a standardized structure (typically a single JSON object alongside instructional prompts).
        *   *Reference:* `adr-agent-input-format.md`
    *   Providing few-shot examples directly within agent prompts to guide LLM behavior and output style.
        *   *Reference:* `adr-few-shot-example-provisioning.md`

*   **Control Flow Management:**
    *   Managing iterative processes, such as the Evaluator-Optimizer pattern, including enforcing a maximum number of refinement loops to control cost and ensure termination.
        *   *Reference:* `adr-max-refinement-loops.md`

*   **Output Handling and Integration:**
    *   Receiving processed data (e.g., refined text content) from agents.
    *   Merging agent-generated text content into predefined HTML templates using a dedicated templating engine, ensuring style and structural integrity.
        *   *Reference:* `adr-html-template-merging.md`

*   **System Improvement and Observability:**
    *   Logging detailed LLM interaction data (inputs, prompts, outputs, evaluations, performance metrics) to a persistent store for analytics, debugging, prompt optimization, and future model fine-tuning.
        *   *Reference:* `adr-logging-llm-interactions.md`

*   **Error Handling and Resilience:**
    *   (To be detailed further) Managing errors from agent calls, API interactions, or other parts of the workflow.

*   **Interaction with External Systems:**
    *   (To be detailed further) Fetching data from backend APIs (e.g., candidate profiles from an MCP server or TCAI) to provide context to agents.
    *   (To be detailed further) Pushing agent outputs to other systems (e.g., sending an email via an email service).

## 3. Interaction Patterns

(This section will be expanded to describe common sequences, e.g., how the orchestrator handles a request to draft an email using the `SourcingEmailCrafterAgent`.)

## 4. Technology Choices

(This section will be expanded to discuss potential technologies for the orchestrator, logging, templating, etc.)

## 5. Open Questions & Future Considerations

*   Detailed error handling strategies.
*   Scalability and performance characteristics.
*   Security considerations for data handled by the orchestrator.
