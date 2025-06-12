# Sourcing Email Crafter Agent: Prototype Design (Evaluator-Optimizer Pattern)

## 0. Overview

### 0.1. Problem Statement
Recruiters spend significant time manually drafting personalized outreach emails to potential candidates. Scaling this effort while maintaining high quality and personalization is a major challenge. This often leads to generic messaging, resulting in low candidate engagement rates and missed opportunities.

### 0.2. Goal
To develop an AI-powered agent, `SourcingEmailCrafterAgent`, that automates the creation of high-quality, personalized sourcing emails. The primary aim is to improve recruiter efficiency, increase candidate engagement, and enhance the overall effectiveness of the talent acquisition process.

### 0.3. Basic Features
*   Generation of personalized email drafts based on comprehensive candidate profiles and detailed job descriptions.
*   Iterative refinement of email content using an Evaluator-Optimizer pattern to ensure quality and adherence to guidelines.
*   Quality scoring of generated emails against predefined criteria (e.g., personalization, relevance, clarity, call-to-action strength).
*   Structured JSON input for receiving all necessary data (candidate details, job specifics, context, constraints).
*   Structured JSON output delivering the final email body, evaluation scores, and refinement metadata.

### 0.4. API-First Design
The `SourcingEmailCrafterAgent` will be developed as a secure, stateless API. This architectural choice offers several advantages:
*   **Interoperability:** Enables easy integration into existing recruitment workflows, orchestrators, and platforms (e.g., Talent Cloud AI, MCP server).
*   **Scalability:** Allows the agent to handle varying loads by leveraging standard API scaling techniques.
*   **Reusability:** The agent can be consumed by multiple internal or external services.
*   **Infrastructure Leverage:** Allows utilization of existing enterprise infrastructure for critical non-functional requirements such as:
    *   **Authentication and Authorization:** Ensuring only permitted users/systems can access the agent.
    *   **Rate Limiting and Throttling:** Protecting the service from abuse and ensuring fair usage.
    *   **Monitoring and Logging:** Centralized tracking of API calls, performance, and errors.
    *   **Security:** Applying standard API security best practices.

Inputs to the API will be a single JSON object, and outputs will also be a JSON object, ensuring clear and consistent data contracts.

## 1. Core Pattern: Evaluator-Optimizer

This pattern involves two main LLM-driven components:

*   **Generator LLM:** Responsible for creating an initial draft of the email.
*   **Evaluator LLM:** Responsible for reviewing the draft against predefined criteria and providing feedback for refinement.

The process is iterative, with the Generator LLM refining the email based on the Evaluator LLM's feedback until a desired quality threshold is met.

## 2. Agent Inputs

The `SourcingEmailCrafterAgent` will require the following inputs to function effectively, provided as fields within a single JSON object by the orchestrator (as per `adr-agent-input-format.md`):

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

*   **Action:** The Generator LLM receives all the inputs as a single JSON object.
*   **Prompt (Example):**
    ```
    You are an expert recruitment email writer. Your task is to craft a compelling initial outreach email based on the provided JSON input.

    The JSON input contains the following fields:
    - `candidateProfile`: Detailed information about the candidate (e.g., skills, experience, interests).
    - `jobDescription`: Details about the job role and company (e.g., responsibilities, selling points).
    - `emailContextPurpose`: The purpose and context for the email (e.g., type of email, desired call to action).
    - `structuralConstraintsGuidelines`: Any constraints on email structure, length, or specific content to include/highlight.
    - `desiredToneStyle`: The desired tone for the email.

    Here is the JSON input:
    ```json
    {
      "candidateProfile": {
        "keySkills": ["Specific Skill 1", "Specific Skill 2", "Relevant Technology"],
        "relevantExperience": "Candidate's notable achievement or project, e.g., 'Led a team that increased user engagement by 20%'",
        "statedInterests": "Candidate's stated career goals or interests, if known",
        "onlineProfileLink": "linkedin.com/in/candidateprofile"
      },
      "jobDescription": {
        "keyResponsibilities": "Key duties of the role, e.g., 'Develop and implement new software solutions'",
        "uniqueSellingPoints": "What makes the role/company attractive, e.g., 'Opportunity to work on cutting-edge AI'",
        "companyCultureKeywords": ["e.g., innovative", "fast-paced", "collaborative"]
      },
      "emailContextPurpose": {
        "emailType": "initial_outreach",
        "desiredCTA": "e.g., Schedule a brief introductory call"
      },
      "structuralConstraintsGuidelines": {
        "needsHtmlPlaceholders": false,
        "maxLength": "Main body approximately 3 paragraphs.",
        "brandingGuidelines": {
          "highlightMatch": "Highlight how the candidate's experience with [Specific Skill/Project from Profile] aligns with our need for [Specific Job Requirement from JD]."
        }
      },
      "desiredToneStyle": "Professional yet engaging"
    }
    ```

    Based on the JSON input above, draft a personalized email.
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

*   **Action:** Once the email meets the quality standards or the maximum refinement loops are reached.
*   **Output:** A single JSON object containing the final, refined email content and the detailed evaluation scores from the last evaluation cycle. This allows the orchestrator to have full visibility into the generated content and its assessed quality.

    **Example JSON Output:**
    ```json
    {
      "finalEmailBody": "Subject: Exploring Synergies: AI Research at Innovatech Inc.\n\nDear Alex Chen,\n\nI came across your impressive background as a Senior Software Engineer, particularly your work at Innovatech Inc. leading the development of a new recommendation engine. Your expertise in Python, Machine Learning, and NLP is highly relevant to an exciting opportunity we have at [YourCompanyName].\n\nWe are currently seeking an AI Research Scientist for our Advanced Research Wing. This role involves designing novel machine learning models and collaborating directly with product teams on cutting-edge generative AI projects. Given your achievements in improving user engagement through AI, I believe your skills would be a great asset to our team and allow you to make a direct impact on a new, high-visibility product line.\n\nWould you be open to a brief exploratory call next week to discuss how this role might align with your career aspirations?\n\nBest regards,\n\n[Your Name/Recruiter Name]",
      "evaluationScores": {
        "personalization": 9,
        "relevanceToRole": 9,
        "toneAlignment": 8,
        "clarityAndConciseness": 9,
        "ctaStrength": 8,
        "grammarAndProfessionalism": "Pass",
        "constraintAdherence": "Pass",
        "overallQualityScore": 8.5 // Example: could be an average or a specific metric
      },
      "refinementLoops": 2 // Example: number of loops taken
    }
    ```
    This structured output is then ready for the orchestrator to process, for example, by inserting the `finalEmailBody` into an HTML template.

## 5. Technology Stack (To Be Determined)

This section will be populated during the prototyping and implementation phases. Potential components include:

*   **Large Language Model (LLM):** (e.g., GPT-4, Claude 3, Gemini, or a fine-tuned model)
*   **Programming Language & Framework (for API):** (e.g., Python with FastAPI/Flask, Node.js with Express)
*   **Prompt Management System:** (If necessary for complex prompt versioning and management)
*   **Logging & Monitoring Tools:** (Leveraging existing platform capabilities or dedicated tools like Prometheus, Grafana, ELK stack)
*   **Deployment Environment:** (e.g., Kubernetes, Serverless functions)

## 6. Considerations & Future Enhancements

*   **Prompt Engineering & Performance Monitoring:** The quality of prompts for both Generator and Evaluator LLMs is critical. Effective prompt engineering will be supported by detailed interaction logs captured by the orchestrator (see `adr-logging-llm-interactions.md`), which are invaluable for ongoing prompt optimization, performance tracking, debugging, and potentially for future model fine-tuning.
*   **Few-Shot Examples:** Providing good/bad examples in prompts (managed by the orchestrator, see `adr-few-shot-example-provisioning.md`) can significantly improve performance.
*   **Cost Management:** Iterative calls to LLMs will incur costs. The number of refinement loops should be optimized and will be managed by the orchestrator (see `adr-max-refinement-loops.md`).
*   **Template Integration:** (Current) The orchestrator will map the agent's final text output to placeholders in HTML templates using a separate template engine (see `adr-html-template-merging.md`). (Future) Direct LLM integration for merging content into HTML templates could be explored, with careful consideration of style preservation and HTML validity.
*   **Learning from User Feedback:** (Future) Incorporate feedback on which emails perform best (e.g., open rates, response rates) to refine the evaluation criteria or prompts over time.
*   **A/B Testing:** The system could generate multiple variations for A/B testing different approaches.

This Evaluator-Optimizer pattern provides a robust framework for creating a sophisticated `email-crafter-agent` capable of producing high-quality, personalized communications.
