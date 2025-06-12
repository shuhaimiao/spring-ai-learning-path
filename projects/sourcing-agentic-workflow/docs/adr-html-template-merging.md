# ADR: HTML Template Merging Responsibility

**Status:** Accepted

**Date:** 2025-06-12

## Context

In our agentic workflow, an LLM agent (e.g., `EmailCrafterAgent`) is responsible for generating refined text content, such as the body of an email. This generated text content often needs to be integrated into a predefined HTML template to produce the final output (e.g., a fully formatted HTML email).

The question is whether the LLM agent itself should be responsible for merging its generated text content into the HTML template, or if this task should be handled by the orchestrator component of the system.

## Decision

The **orchestrator will be responsible for merging the LLM-generated text content into the appropriate HTML template.**

The LLM agent's responsibility will be to output the refined text content. The orchestrator will then take this text and use a standard templating engine or similar deterministic mechanism to inject it into the predefined HTML template.

**Workflow:**
1. LLM Agent generates and outputs refined plain text content.
2. Orchestrator receives the plain text content from the LLM agent.
3. Orchestrator uses a templating engine (e.g., Jinja2, Handlebars) and the predefined HTML template to insert the text content into the correct placeholders.
4. Orchestrator outputs the final, fully formatted HTML.

## Consequences

### Positive Consequences:

1.  **Preservation of HTML Structure and Styling:** Using a dedicated templating engine ensures that the HTML template's structure, styling (CSS, inline styles), and attributes are preserved exactly as designed. This significantly reduces the risk of the LLM inadvertently altering the visual presentation.
2.  **Deterministic Output:** Templating engines provide deterministic output. Given the same template and the same input text, the resulting HTML will always be identical. This is crucial for consistency and reliability.
3.  **Leverages Component Strengths:** This approach aligns with the core strengths of each component:
    *   **LLM Agent:** Excels at natural language understanding, content generation, and refinement.
    *   **Orchestrator:** Excels at deterministic integration tasks, data transformation, and managing workflows.
4.  **Simplified LLM Task:** The LLM agent can focus solely on generating high-quality text content without the added complexity and potential pitfalls of HTML manipulation.
5.  **Robustness and Reliability:** The process is more robust and less prone to errors compared to relying on an LLM for precise HTML manipulation, which can be non-deterministic and harder to constrain perfectly.
6.  **Maintainability:** HTML templates are managed separately and can be updated by front-end developers or designers without requiring changes to the LLM agent's prompts or logic.

### Negative Consequences (Minor):

1.  **Orchestrator Complexity:** The orchestrator needs to incorporate logic and potentially a library for HTML templating. However, this is a standard and well-understood task in software development, and many mature libraries exist.

## Alternatives Considered

1.  **LLM Agent Handles HTML Merging:**
    *   *Description:* The LLM agent would be provided with both the text content (or generate it) and the HTML template, and then be instructed to merge them, preserving styles.
    *   *Reason for Rejection:* While technically feasible with careful prompting, this approach carries significant risks:
        *   **Style Alteration:** High risk of the LLM unintentionally modifying HTML structure, tags, attributes, or styles, even with explicit instructions not to do so.
        *   **Non-Deterministic Output:** LLMs can produce slightly different outputs even for the same input, which is undesirable for precise template merging.
        *   **HTML Validity:** Potential for the LLM to generate invalid HTML.
        *   **Increased LLM Complexity:** Adds an unnecessary burden and point of failure to the LLM's task.

## Rationale

The LLM's primary strength lies in understanding and generating nuanced, context-aware *content*. Deterministic, structural tasks like merging content into predefined HTML templates are better suited for traditional software components like an orchestrator equipped with a templating engine. This separation of concerns leads to a more robust, maintainable, and reliable system, ensuring the integrity of the final HTML output.
