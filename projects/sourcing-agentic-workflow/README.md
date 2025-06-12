# Sourcing Email Crafter Agent

This project implements an AI-powered agent that automates the creation of high-quality, personalized sourcing emails for recruiters. It uses the Evaluator-Optimizer pattern to iteratively refine email drafts until they meet a quality threshold.

## Technology Stack

- **Programming Language**: Java 17
- **Framework**: Spring Boot 3.3.0
- **Build Tool**: Apache Maven
- **AI Framework**: Spring AI 1.0.0
- **LLM Provider**: OpenAI (Model: `gpt-4-turbo`)

## Setup and Run Instructions

### Prerequisites

- Java 17 or later
- Apache Maven
- OpenAI API Key

### Configuration

The application requires an OpenAI API key to function.

1. Create a `.env` file in the `projects/sourcing-agentic-workflow` directory with the following content:

```
SPRING_AI_OPENAI_API_KEY=your_openai_api_key_here
```

### Build the Application

Open a terminal in the `projects/sourcing-agentic-workflow` directory and run the following Maven command to build the project:

```bash
mvn clean install
```

### Run the Application

Once the build is complete, you can run the application using the following command:

```bash
java -jar target/sourcing-email-crafter-0.0.1-SNAPSHOT.jar
```

## API Usage

### Craft Email Endpoint

**Endpoint**: `POST /api/v1/email/craft`

**Request Body**:

```json
{
  "candidateProfile": {
    "keySkills": ["Specific Skill 1", "Specific Skill 2", "Relevant Technology"],
    "relevantExperience": "Candidate's notable achievement or project",
    "statedInterests": "Candidate's stated career goals or interests",
    "onlineProfileLink": "linkedin.com/in/candidateprofile"
  },
  "jobDescription": {
    "keyResponsibilities": "Key duties of the role",
    "uniqueSellingPoints": "What makes the role/company attractive",
    "companyCultureKeywords": ["innovative", "fast-paced", "collaborative"]
  },
  "emailContextPurpose": {
    "emailType": "initial_outreach",
    "desiredCTA": "Schedule a brief introductory call"
  },
  "structuralConstraintsGuidelines": {
    "needsHtmlPlaceholders": false,
    "maxLength": "Main body approximately 3 paragraphs.",
    "brandingGuidelines": {
      "highlightMatch": "Highlight how the candidate's experience aligns with our need."
    }
  },
  "desiredToneStyle": "Professional yet engaging"
}
```

**Response**:

```json
{
  "finalEmailBody": "Subject: Exploring Opportunities at Our Company\n\nDear [Candidate Name],\n\n[Email content...]\n\nBest regards,\n[Your Name]",
  "evaluationScores": {
    "personalization": 9,
    "relevanceToRole": 9,
    "toneAlignment": 8,
    "clarityAndConciseness": 9,
    "ctaStrength": 8,
    "grammarAndProfessionalism": "Pass",
    "constraintAdherence": "Pass",
    "overallQualityScore": 8.6
  },
  "refinementLoops": 2
}
```

## Core Pattern: Evaluator-Optimizer

This implementation follows the Evaluator-Optimizer pattern described in the design document:

1. **Generator**: Creates an initial draft of the email based on the input parameters.
2. **Evaluator**: Reviews the draft against predefined criteria and provides feedback.
3. **Refinement Loop**: The Generator refines the email based on the Evaluator's feedback until a quality threshold is met or the maximum number of refinement loops is reached.

The process is fully automated and exposed as a REST API for easy integration into existing recruitment workflows and platforms. 