# ADR: Managing Maximum Refinement Loops

**Status:** Accepted

**Date:** 2025-06-12

## Context

Iterative LLM patterns, such as the Evaluator-Optimizer pattern, involve multiple calls to LLM(s) to progressively refine an output. Each call incurs costs (monetary, latency) and consumes resources. Without a control mechanism, these iterative processes could potentially run for an excessive number of loops, leading to high costs, poor performance, or even effectively infinite loops if quality criteria are never met or are misconfigured.

Therefore, a mechanism is required to limit the maximum number of refinement iterations.

## Decision

The **orchestrating component** of the agentic system will be responsible for managing and enforcing a maximum number of refinement loops for iterative LLM patterns like Evaluator-Optimizer.

This will be implemented by the orchestrator maintaining a loop counter and a predefined `max_refinement_loops` parameter. The loop will terminate if the quality criteria are met OR if the `current_loops` count reaches `max_refinement_loops`.

**Conceptual Logic in Orchestrator:**

```
max_refinement_loops = 5 // Example value
current_loops = 0
quality_met = False
current_content = initial_draft

while current_loops < max_refinement_loops and not quality_met:
    optimized_content = call_optimizer_agent(current_content, feedback_from_evaluator) // feedback is null on first loop
    evaluation_result = call_evaluator_agent(optimized_content)
    
    if evaluation_result.meets_quality_criteria:
        quality_met = True
        current_content = optimized_content
        break
    
    current_content = optimized_content
    feedback_from_evaluator = evaluation_result.feedback
    current_loops += 1

// Final content is current_content
```

## Consequences

### Positive Consequences:

1.  **Cost Control:** Prevents runaway LLM calls, thereby managing and capping potential costs associated with iterative refinement.
2.  **Performance Predictability:** Ensures that the iterative process terminates within a known number of steps, contributing to more predictable overall execution time.
3.  **Prevents Infinite Loops:** Acts as a crucial safeguard against scenarios where quality criteria might never be met, preventing the system from getting stuck.
4.  **Clear Separation of Concerns:** Reinforces the orchestrator's role in managing control flow, state, and operational safeguards, while LLM agents focus on their specialized tasks (generation, evaluation).
5.  **Robustness:** Makes the system more robust by providing a guaranteed exit condition for iterative processes.

### Negative Consequences (Minor):

1.  **Orchestrator Logic:** Adds a small amount of explicit loop management logic to the orchestrator. However, this is a natural responsibility for an orchestrating component.
2.  **Sub-Optimal Output if Max Loops Reached:** If the process terminates due to reaching `max_refinement_loops` before quality criteria are met, the output might be sub-optimal. This is an accepted trade-off for ensuring termination and cost control. The system should ideally log such occurrences for review and potential prompt/criteria tuning.

## Alternatives Considered

1.  **Instructing LLM via System Prompt to Manage Loop Count:**
    *   *Description:* Attempting to include instructions in the LLM's system prompt to keep track of loops and stop after a certain number.
    *   *Reason for Rejection:* This approach is unreliable and generally not feasible for the following reasons:
        *   **Statelessness of LLM Calls:** LLM API calls are typically stateless. The LLM does not inherently maintain memory or counters across separate, independent API requests that constitute an Optimizer-Evaluator sequence.
        *   **Complexity and Unreliability:** It would be very complex and unreliable to try and make the LLM manage this state through prompt engineering alone. The LLM's primary function is text processing based on the current prompt, not managing external loop counters across interactions.
        *   **Not a Designed Function:** This is not how LLMs are designed to be used for managing iterative control flow.

## Rationale

Controlling the flow and operational parameters of an iterative process, such as setting a maximum number of loops, is a classical responsibility of an orchestrating or controlling component in a software system. The orchestrator is stateful by design (or can be made so for the scope of a transaction) and is the appropriate place to implement such safeguards. Relying on the LLM for this would be an anti-pattern, misusing the LLM for a task it is not designed for and leading to an unreliable system.
