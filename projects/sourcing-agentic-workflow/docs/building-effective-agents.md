# Key Takeaways: Building Effective Agents (Anthropic)

This document summarizes the key takeaways from Anthropic's article on building effective agents.

**Core Philosophy:**

*   **Simplicity is Key:** The most successful agentic systems are built with simple, composable patterns rather than complex frameworks. Start with simple prompts and only add complexity (like multi-step agentic systems) if it demonstrably improves outcomes.
*   **Workflows vs. Agents:**
    *   **Workflows:** LLMs and tools are orchestrated through predefined code paths. Good for predictability and consistency in well-defined tasks.
    *   **Agents:** LLMs dynamically direct their own processes and tool usage. Better for flexibility and model-driven decision-making at scale, especially for open-ended problems where the path isn't predictable.
*   **Augmented LLM as a Building Block:** The foundation is an LLM enhanced with retrieval, tools, and memory.

**When to Use Agents (and When Not To):**

*   Consider agents when simpler solutions (optimized single LLM calls with retrieval/examples) fall short.
*   Agentic systems often trade latency and cost for better task performance; evaluate if this tradeoff is acceptable.
*   Use agents for open-ended problems where steps are unpredictable and trust in the LLM's decision-making is feasible.

**Frameworks:**

*   Frameworks (LangGraph, Amazon Bedrock AI Agent framework, Rivet, Vellum) can simplify initial setup (calling LLMs, parsing tools, chaining calls).
*   However, they can add abstraction layers, making debugging harder and potentially encouraging unnecessary complexity.
*   **Recommendation:** Start by using LLM APIs directly. If using a framework, understand its underlying code to avoid incorrect assumptions.

**Common Agentic System Patterns (Building Blocks & Workflows):**

1.  **Augmented LLM:** The basic unit – LLM + retrieval, tools, memory.
2.  **Prompt Chaining:** Decomposes a task into a sequence of LLM calls, with outputs feeding into subsequent steps. Good for breaking down tasks and improving accuracy by simplifying each step.
3.  **Routing:** Classifies input and directs it to specialized follow-up tasks/prompts/tools. Useful for handling diverse inputs effectively.
4.  **Parallelization:**
    *   **Sectioning:** Breaks a task into independent subtasks run in parallel.
    *   **Voting:** Runs the same task multiple times for diverse outputs or higher confidence.
5.  **Orchestrator-Workers:** A central LLM dynamically breaks down tasks, delegates to worker LLMs, and synthesizes results. Suited for complex tasks with unpredictable subtasks (e.g., coding).
6.  **Evaluator-Optimizer:** One LLM generates a response, another evaluates and provides feedback in a loop. Effective for iterative refinement when evaluation criteria are clear.

**Building Autonomous Agents:**

*   Agents plan and operate independently after receiving a task, potentially returning to humans for information or judgment.
*   Crucial for agents to get "ground truth" from the environment (tool results, code execution) at each step.
*   Implement stopping conditions (e.g., max iterations) to maintain control.
*   Higher costs and potential for compounding errors mean extensive testing in sandboxed environments and guardrails are essential.

**Prompt Engineering for Tools (Agent-Computer Interface - ACI):**

*   Treat tool definitions and specifications with the same care as overall prompts.
*   **Key Principles for Tool Design:**
    *   Give the model enough tokens to "think" before committing to a format.
    *   Keep the format close to naturally occurring text on the internet.
    *   Avoid formatting "overhead" (e.g., complex escaping, line counting).
    *   **Invest effort in ACI similar to Human-Computer Interfaces (HCI).**
    *   Make tool usage obvious from descriptions and parameters (like good docstrings).
    *   Test tool usage extensively (e.g., in a workbench) and iterate.
    *   "Poka-yoke" (mistake-proof) your tools by designing arguments to prevent errors (e.g., requiring absolute filepaths instead of relative ones).

**Three Core Principles for Implementing Agents:**

1.  **Maintain simplicity** in agent design.
2.  **Prioritize transparency** by explicitly showing the agent’s planning steps.
3.  **Carefully craft the agent-computer interface (ACI)** through thorough tool documentation and testing.

**Promising Applications:**

*   **Customer Support:** Conversational flow, tool integration for data/actions, measurable success.
*   **Coding Agents:** Verifiable solutions (tests), iterative improvement, well-defined problem space.
