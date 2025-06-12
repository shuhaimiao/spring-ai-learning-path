# ADR: LLM Usage in Evaluator-Optimizer Pattern

**Status:** Accepted

**Date:** 2025-06-12

## Context

The Evaluator-Optimizer pattern is a design approach for LLM-powered agents where one logical component (the Optimizer) generates or refines content, and another logical component (the Evaluator) assesses this content against specific criteria. This iterative process aims to improve the quality of the generated output.

A key architectural consideration is whether to use two physically separate LLM instances for the Evaluator and Optimizer roles or to use a single LLM instance that adopts different roles based on prompting.

## Decision

For implementing the Evaluator-Optimizer pattern, we will **use a single, shared LLM instance**. The distinct responsibilities of the "Evaluator" and "Optimizer" will be achieved by providing **different system prompts, role definitions, and specific instructions** to the LLM for each logical role.

*   **Optimizer Role:** The LLM will be prompted to generate or refine content based on initial inputs or feedback from the Evaluator. Its system prompt will define its role as a creative, constructive, or refining entity.
*   **Evaluator Role:** The LLM will be prompted to critically assess the output from the Optimizer (or an initial draft) against predefined criteria. It will be instructed to provide scores, identify flaws, and suggest concrete areas for improvement. Its system prompt will define its role as an analytical, critical entity.

## Consequences

### Positive Consequences:

1.  **Cost-Effectiveness:** Utilizing a single LLM instance is generally more economical than deploying, managing, and paying for two separate model instances, especially when using commercial LLM APIs.
2.  **Reduced Complexity:** Simplifies the overall system architecture and infrastructure requirements. There's no need to manage separate endpoints or configurations for two distinct models.
3.  **Leverages Model Capability:** Modern, powerful LLMs are highly capable of context-switching and adopting different personas or task focuses based on the provided prompt. A single, capable model can effectively perform both generation and evaluation tasks when guided appropriately.
4.  **Simplified State Management:** While the orchestrator still needs to manage the flow and state (e.g., current draft, evaluation feedback), it interacts with a single LLM endpoint, streamlining communication.

### Negative Consequences (Minor/Manageable):

1.  **Prompt Engineering Effort:** Success heavily relies on the clarity, specificity, and distinctness of the prompts for the Evaluator and Optimizer roles. Crafting these prompts effectively requires careful design and testing.
2.  **Potential for Bleed-over (if prompts are not distinct):** If prompts are not sufficiently differentiated, there's a slight theoretical risk of the LLM not fully switching context between roles, though well-designed prompts mitigate this.

## Alternatives Considered

1.  **Two Separate LLM Instances:**
    *   *Description:* Deploying or using two distinct LLM models (or instances of the same model) dedicated separately to the Evaluator and Optimizer roles.
    *   *Reason for Rejection:* Increased cost, higher infrastructure complexity, and often unnecessary if a single, sufficiently capable LLM can handle both roles through effective prompting. This might only be considered if highly specialized models were available and their performance difference justified the overhead.

## Rationale

The core of the Evaluator-Optimizer pattern is the *logical* separation of tasks. This logical separation can be robustly achieved through carefully crafted, role-specific prompting for a single LLM. The benefits of cost-effectiveness and reduced complexity make this the preferred approach for most implementations, assuming a capable underlying LLM.
