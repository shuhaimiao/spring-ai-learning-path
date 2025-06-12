# ADR: Logging LLM Interactions for System Improvement

**Status:** Accepted

**Date:** 2025-06-12

## Context

In our hybrid agentic architecture, LLM agents (e.g., `SourcingEmailCrafterAgent` using Generator/Evaluator patterns) are invoked by an orchestrator. The quality of prompts, the effectiveness of few-shot examples, and the overall performance of these agents are critical to the system's success. Continuous improvement requires data-driven insights into these interactions.

Without systematic logging of LLM interactions, it becomes difficult to:
*   Objectively assess and improve prompt quality.
*   Monitor the performance and consistency of LLM responses.
*   Debug issues effectively when agents produce sub-optimal outputs.
*   Gather data for potential future fine-tuning of models.
*   Understand how different input contexts affect agent behavior.

## Decision

The **orchestrator will be responsible for logging detailed LLM interaction data to a persistent store** (e.g., a dedicated logging system, database, or analytics platform).

This data will include, but not be limited to:
1.  **Contextual Inputs:** A snapshot or reference to the primary inputs provided to the agent (e.g., `CandidateProfile`, `JobProfile`, `EmailContext`).
2.  **Prompts Used:** The exact prompts sent to the LLM(s) within the agent (e.g., for Generator and Evaluator roles).
3.  **Few-Shot Examples:** Any few-shot examples included in the prompts.
4.  **LLM Outputs:** The raw outputs from the LLM(s) (e.g., initial drafts, evaluation scores/feedback, refined drafts).
5.  **Iteration History:** For iterative patterns, the sequence of drafts and feedback across loops.
6.  **Agent Configuration:** Key parameters or settings of the agent at the time of invocation.
7.  **Performance Metrics:** Latency of LLM calls, number of iterations, token counts (if available).
8.  **Outcome Data (if available later):** Links to real-world outcomes (e.g., email open/reply rates) if this data can be correlated back to the specific interaction.

## Consequences

### Positive Consequences:

1.  **Data-Driven Prompt Engineering:** Provides a rich dataset for analyzing which prompt structures, instructions, and few-shot examples lead to the best outcomes, enabling continuous prompt optimization.
2.  **Performance Monitoring & Anomaly Detection:** Allows tracking of LLM performance (quality, latency, consistency) over time, helping to identify degradation or anomalies.
3.  **Effective Debugging:** When an agent produces an unexpected or poor output, the logged data provides a complete trace of the interaction, facilitating root cause analysis.
4.  **Foundation for Fine-Tuning:** Accumulates a valuable dataset that can be used for fine-tuning custom LLM models tailored to specific tasks and quality criteria in the future.
5.  **Understanding Edge Cases:** Helps identify input scenarios or contexts where agents struggle, allowing for targeted improvements in prompts or agent logic.
6.  **Facilitates A/B Testing:** Supports systematic A/B testing of different prompt versions or agent configurations by allowing for detailed comparison of their logged interactions and outcomes.
7.  **Improved System Observability:** Enhances overall understanding of how the agentic system is behaving.

### Negative Consequences (Minor):

1.  **Storage Costs:** Storing detailed logs will incur some storage costs, which need to be managed (e.g., with retention policies).
2.  **Implementation Effort:** Requires development effort in the orchestrator to implement the logging mechanism and define the data schema.
3.  **Data Privacy/Security:** If sensitive data is part of the prompts or outputs, appropriate measures for data privacy, anonymization (if needed), and security of the logs must be implemented.

## Alternatives Considered

1.  **No Detailed Logging / Agent-Specific Logging:**
    *   *Description:* Relying on basic application logs or having each agent implement its own ad-hoc logging.
    *   *Reason for Rejection:* Leads to inconsistent data, makes cross-agent analysis difficult, and often misses crucial details needed for comprehensive prompt engineering and performance analysis. Centralized logging by the orchestrator ensures consistency and completeness.

2.  **LLM Agent Manages Its Own Analytical Logging:**
    *   *Description:* The LLM agent itself is prompted or designed to write its interaction details to a memory/log.
    *   *Reason for Rejection:* Adds unnecessary complexity to the agent, blurs its primary responsibility, and makes it harder for the orchestrator to ensure comprehensive and standardized logging across all agents and interactions.

## Rationale

Systematic and centralized logging of LLM interactions by the orchestrator is a foundational practice for building robust, maintainable, and continuously improving agentic systems. The benefits in terms of prompt optimization, performance monitoring, debugging, and data collection for future enhancements far outweigh the implementation and storage considerations. This approach aligns with best practices for MLOps and operationalizing AI systems.
