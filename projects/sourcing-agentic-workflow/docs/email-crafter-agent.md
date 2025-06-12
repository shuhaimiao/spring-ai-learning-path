# Email Crafter Agent: Prototype Design (Evaluator-Optimizer Pattern)

This document outlines the prototype design for an `email-crafter-agent`. This agent will leverage the **Evaluator-Optimizer pattern** to generate high-quality, personalized outreach emails.

## 1. Core Pattern: Evaluator-Optimizer

This pattern involves two main LLM-driven components:

*   **Generator LLM:** Responsible for creating an initial draft of the email.
*   **Evaluator LLM:** Responsible for reviewing the draft against predefined criteria and providing feedback for refinement.

The process is iterative, with the Generator LLM refining the email based on the Evaluator LLM's feedback until a desired quality threshold is met.

## 2. Agent Inputs

The `email-crafter-agent` will require the following inputs to function effectively:

*   **Candidate Profile:**
    *   Key skills, relevant experience (e.g., from resume/CV).
    *   Stated interests or career goals (if available).
    *   Link to online profile (e.g., LinkedIn), if applicable.
*   **Job Description:**
    *   Key responsibilities and requirements.
    *   Unique selling points of the role or company.
    *   Company culture keywords (if applicable).
*   **Email Context & Purpose:**
    *   Type of email (e.g., initial outreach, follow-up, event invitation).
    *   Specific call to action (CTA) desired.
*   **Structural Constraints & Guidelines:**
    *   Indication if the content needs to fit into predefined HTML template placeholders.
    *   Maximum or target word/paragraph count for specific sections.
    *   Branding guidelines (e.g., keywords to include/avoid).
*   **Desired Tone & Style:**
    *   Examples: Formal, semi-formal, enthusiastic, direct, empathetic.

## 3. Workflow Steps

The agent will follow these steps:

### Step 1: Initial Draft Generation (Generator LLM)

*   **Action:** The Generator LLM receives all the inputs.
*   **Prompt (Example):**
    ```
    You are an expert recruitment email writer tasked with crafting a compelling initial outreach email.
    Given the following:
    - Candidate Profile: [Details of candidate's skills, experience, etc.]
    - Job Description: [Key aspects of the job, company selling points]
    - Email Purpose: Initial outreach to gauge interest in the role.
    - Desired Tone: Professional yet engaging.
    - Structural Constraint: The main body should be approximately 3 paragraphs and fit within our standard outreach template. Highlight how the candidate's experience with [Specific Skill/Project] aligns with our need for [Specific Job Requirement].

    Draft a personalized email.
    ```
*   **Output:** Initial email draft (text content).

### Step 2: Quality Evaluation (Evaluator LLM)

*   **Action:** The Evaluator LLM receives the initial email draft and a set of evaluation criteria.
*   **Evaluation Criteria (Examples):**
    *   **Personalization (1-10):** How well does it connect with the candidate's specific background?
    *   **Relevance to Role (1-10):** How clearly is the job's relevance articulated for this candidate?
    *   **Tone Alignment (1-10):** Does it match the desired tone?
    *   **Clarity & Conciseness (1-10):** Is the message clear and to the point?
    *   **Call to Action (CTA) Strength (1-10):** Is the CTA clear and compelling?
    *   **Grammar & Professionalism (Pass/Fail):** Free of errors?
    *   **Constraint Adherence (Pass/Fail):** Does it meet structural/length guidelines?
*   **Prompt (Example):**
    ```
    You are an expert email quality analyst. Evaluate the following email draft based on these criteria:
    [List criteria here, e.g., Personalization, Relevance, Tone, Clarity, CTA Strength, Grammar, Constraint Adherence].

    For each criterion, provide a score (if applicable) and specific, actionable feedback for improvement.
    If grammar issues exist, list them.
    If constraints are violated, specify how.

    Email Draft:
    [Insert initial email draft here]
    ```
*   **Output:** Detailed feedback, scores for each criterion, and an overall quality assessment.

### Step 3: Iterative Refinement Loop

*   **Decision:** Based on the Evaluator LLM's output:
    *   If the overall quality score meets a predefined threshold (e.g., >8/10) and all critical criteria (e.g., Grammar, Constraint Adherence) pass, the email is considered final.
    *   Otherwise, proceed to refine.
*   **Refinement Action (Generator LLM):**
    *   The Generator LLM receives the original draft AND the specific feedback from the Evaluator LLM.
    *   **Prompt (Example):**
        ```
        Refine the following email draft based on the provided feedback.
        Focus particularly on [Specific area, e.g., "enhancing the personalization by more directly referencing the candidate's work on Project X"] and [Specific area, e.g., "making the call to action more direct"].

        Original Draft:
        [Insert draft]

        Feedback:
        [Insert Evaluator's feedback]
        ```
    *   **Output:** Revised email draft.
*   **Loop:** The revised draft goes back to Step 2 (Evaluation). This loop continues for a set maximum number of iterations (e.g., 2-3 rounds) or until the quality threshold is met.

### Step 4: Final Output

*   **Action:** Once the email meets the quality standards.
*   **Output:** The final, refined email content (text). This content is then ready to be inserted into the appropriate HTML template by the orchestrator.

## 4. Considerations & Future Enhancements

*   **Prompt Engineering & Performance Monitoring:** The quality of prompts for both Generator and Evaluator LLMs is critical. Effective prompt engineering will be supported by detailed interaction logs captured by the orchestrator (see `adr-logging-llm-interactions.md`), which are invaluable for ongoing prompt optimization, performance tracking, debugging, and potentially for future model fine-tuning.
*   **Few-Shot Examples:** Providing good/bad examples in prompts (managed by the orchestrator, see `adr-few-shot-example-provisioning.md`) can significantly improve performance.
*   **Cost Management:** Iterative calls to LLMs will incur costs. The number of refinement loops should be optimized and will be managed by the orchestrator (see `adr-max-refinement-loops.md`).
*   **Template Integration:** (Current) The orchestrator will map the agent's final text output to placeholders in HTML templates using a separate template engine (see `adr-html-template-merging.md`). (Future) Direct LLM integration for merging content into HTML templates could be explored, with careful consideration of style preservation and HTML validity.
*   **Learning from User Feedback:** (Future) Incorporate feedback on which emails perform best (e.g., open rates, response rates) to refine the evaluation criteria or prompts over time.
*   **A/B Testing:** The system could generate multiple variations for A/B testing different approaches.

This Evaluator-Optimizer pattern provides a robust framework for creating a sophisticated `email-crafter-agent` capable of producing high-quality, personalized communications.
