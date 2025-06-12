# Candidate Prioritization Agent: Prototype Design

This document outlines the prototype design for a `CandidatePrioritizationAgent`. This agent is responsible for refining a list of candidates provided by an initial matching service (e.g., TCAI) by performing a deeper, LLM-powered screening against a specific job description to select a smaller set of top-tier candidates.

## 1. Agent Name & Purpose

*   **Name:** `CandidatePrioritizationAgent`
*   **Purpose:** To evaluate a list of pre-screened candidates (e.g., top 20 from TCAI) against a detailed job description and select a specified number of top candidates (e.g., top 10) for progression to the sourcing pipeline. This involves a more nuanced, qualitative assessment than what a primary algorithmic match might provide.

## 2. Core Approach/Pattern

*   **Augmented LLM for Evaluation and Ranking:** The agent will process each candidate individually. For each candidate, an LLM call will be made to evaluate their profile against the job description. The LLM will be prompted to provide a suitability score and a concise rationale for its assessment. After evaluating all candidates, the agent will rank them based on their scores (and potentially the rationales) to select the top N.

## 3. Agent Inputs

The `CandidatePrioritizationAgent` will require:

*   **Candidate List (from TCAI):**
    *   A list of candidate profiles (e.g., top 20).
    *   Each profile should include: Key skills, relevant experience (e.g., summarized from resume/CV), education, and potentially links to online profiles (e.g., LinkedIn).
*   **Job Description (Detailed):**
    *   Comprehensive details of the role: key responsibilities, mandatory skills/experience, preferred qualifications, company culture insights, and specific challenges or objectives of the role.
*   **Selection Parameters:**
    *   Number of top candidates to select (e.g., 10).
    *   (Optional) Specific criteria or weightings to emphasize during evaluation (e.g., "prioritize candidates with direct experience in X technology").

## 4. Workflow Steps

The agent will execute the following steps:

### Step 1: Iterate Through Candidates
*   The agent receives the list of (e.g., 20) candidate profiles from TCAI.

### Step 2: Individual Candidate Evaluation (LLM Call per Candidate)
*   **For each candidate in the list:**
    *   **Action:** An LLM call is made.
    *   **Prompt (Example):**
        ```
        You are an expert technical recruiter. Evaluate the following candidate's profile for suitability for the provided job description. 

        Job Description:
        --- --- --- --- ---
        [Insert Full Job Description Here]
        --- --- --- --- ---

        Candidate Profile:
        --- --- --- --- ---
        Name: [Candidate Name]
        Skills: [Candidate Skills]
        Experience Summary: [Candidate Experience Summary]
        Education: [Candidate Education]
        (Optional) LinkedIn: [Link]
        --- --- --- --- ---

        Based on the job description, provide:
        1. A suitability score from 1 (poor fit) to 10 (excellent fit).
        2. A concise rationale (2-3 sentences) explaining your score, highlighting key strengths or weaknesses regarding the job requirements. Focus on how well the candidate's experience aligns with the primary responsibilities and mandatory skills.
        (Optional) If specific criteria were provided (e.g., "prioritize experience in X technology"), explicitly address how the candidate meets or doesn't meet this.
        ```
    *   **Output (for each candidate):**
        *   Suitability Score (e.g., 8/10).
        *   Rationale (text).

### Step 3: Collect and Aggregate Evaluations
*   **Action:** The agent collects the score and rationale for all processed candidates.
*   **Data Structure Example:** A list of objects, each containing `candidate_id`, `original_profile_data`, `llm_score`, `llm_rationale`.

### Step 4: Rank Candidates and Select Top N
*   **Action:**
    *   Sort the evaluated candidates in descending order based on their `llm_score`.
    *   If scores are tied, the rationales can be used for tie-breaking (this might involve a secondary LLM call for pairwise comparison if complex, or a simpler heuristic, or just maintaining order of arrival).
    *   Select the top N candidates as per the input `selection_parameters` (e.g., top 10).

## 5. Agent Output

*   **Primary Output:** A ranked list of the selected top N candidates.
    *   Each item in the list should include: `candidate_id`, `original_profile_data`, `llm_score`, `llm_rationale`.
*   **(Optional Secondary Output):** A summary report including all evaluated candidates with their scores and rationales, potentially for human review or audit purposes.

## 6. Key Considerations & Future Enhancements

*   **Prompt Engineering:** The quality and specificity of the evaluation prompt are crucial for consistent and relevant scoring. Iterative refinement will be necessary.
*   **Scoring Consistency & Calibration:** Ensuring the LLM applies scoring criteria consistently across different candidates can be challenging. Calibration exercises or providing few-shot examples in the prompt might be needed.
*   **Bias Mitigation:** Be mindful of potential biases the LLM might introduce. Evaluation criteria should be objective, and outputs should be monitored.
*   **Cost Management:** An LLM call per candidate can be costly for large initial lists. Consider this for the number of candidates passed from TCAI (e.g., 20 might be reasonable, 200 might be too expensive for this stage).
*   **Rationale Quality:** The rationales should be genuinely insightful and useful for any subsequent human review.
*   **Error Handling:** Manage cases where candidate data is incomplete or the LLM fails to return a valid score/rationale.
*   **Human-in-the-Loop:** For critical roles, the LLM's top N selection and rationales should ideally be reviewed by a human recruiter before candidates are added to the pipeline.
*   **Learning & Feedback:** (Future) If recruiters can provide feedback on the quality of the LLM's selections (e.g., "this candidate, though scored high, was not a good fit because..."), this feedback could be used to refine prompts or evaluation criteria over time.

This `CandidatePrioritizationAgent` aims to add a layer of intelligent, qualitative assessment to the automated sourcing workflow, improving the quality of candidates progressing to the pipeline.
