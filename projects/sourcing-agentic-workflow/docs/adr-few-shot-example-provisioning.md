# ADR: Few-Shot Example Provisioning in Hybrid Agentic Architecture

**Status:** Accepted

**Date:** 2025-06-12

## Context

Few-shot examples (i.e., providing a small number of good/bad examples of task execution and output) are a well-established technique to significantly improve the performance, reliability, and output quality of Large Language Models (LLMs). In our hybrid agentic architecture, which combines a classical orchestrator with specialized LLM agents, we need to determine the most effective and architecturally sound method for providing these few-shot examples to the LLM agents.

## Decision

The **orchestrator will be responsible for selecting and providing relevant few-shot examples directly within the prompt** sent to the specialized LLM agent.

These examples will be curated and injected by the orchestrator as part of the overall context preparation for the agent. The agent will receive a prompt that includes these examples, guiding its behavior and output style for the specific task at hand.

## Consequences

### Positive Consequences:

1.  **Centralized Context Provisioning:** Aligns with the orchestrator's primary role of gathering, preparing, and providing all necessary context (e.g., `CandidateProfile`, `JobProfile`, task-specific instructions) to the LLM agent. Few-shot examples are a form of contextual guidance.
2.  **Maintains Agent Simplicity and Specialization:** LLM agents remain focused on their core task (e.g., email crafting, candidate prioritization) using the provided context. They do not need to incorporate additional logic or tools for example retrieval (e.g., RAG, vector store lookups for this specific purpose).
3.  **Dynamic Relevance and Control:** The orchestrator can dynamically select the most pertinent few-shot examples based on the specifics of the current task instance (e.g., different examples for different job roles, different tones, or different stages of a workflow). This allows for highly tailored guidance.
4.  **Optimized Prompt Engineering:** Few-shot examples are an integral part of prompt engineering. Managing them within the orchestrator allows for easier iteration, A/B testing, and fine-tuning of the overall prompt structure for each agent.
5.  **Controlled Prompt Construction:** The orchestrator can manage the inclusion of examples to balance guidance with prompt length and associated token costs, ensuring only the most impactful examples are used.

### Negative Consequences (Minor):

1.  **Increased Orchestrator Responsibility:** The orchestrator takes on the additional task of managing and selecting appropriate few-shot examples. This may involve maintaining a repository or logic for example selection.
2.  **Prompt Length:** Including few-shot examples directly in the prompt increases its length and token count. This needs to be managed by the orchestrator to stay within model limits and optimize costs, typically by using a small number of concise examples.

## Alternatives Considered

1.  **Agent-Managed Retrieval of Few-Shot Examples (e.g., via RAG or Memory Lookup):**
    *   *Description:* The LLM agent itself would be equipped with tools or capabilities to query a vector database or other memory store to retrieve relevant few-shot examples at runtime.
    *   *Reason for Rejection:*
        *   **Increased Agent Complexity:** Adds significant complexity to the agent's design, requiring it to manage data retrieval, query formulation, and handling of missing examples.
        *   **Reduced Control and Precision:** It's harder for the agent to dynamically select the *most* relevant examples for a specific, nuanced task instance compared to the orchestrator, which has a broader view of the overall process.
        *   **Potential for Inconsistency:** The quality and relevance of RAG-retrieved examples might vary, potentially leading to less consistent agent performance for this specific guidance purpose.
        *   **Blurs Specialization:** Deviates from the principle of specialized agents that receive complete context to perform a focused task.

## Rationale

In a hybrid agentic architecture, the orchestrator is best positioned to manage the full context provided to specialized LLM agents. Few-shot examples are a powerful form of contextual guidance that directly influences the agent's behavior for a specific task. By making the orchestrator responsible for their provision, we ensure that agents remain lean and focused, while the examples themselves can be dynamically and precisely tailored to the task at hand. This approach offers better control, maintainability, and consistency in leveraging few-shot learning compared to delegating example retrieval to the agent itself.
