package com.example.sourcingemailcrafter.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sourcingemailcrafter.model.EmailCrafterInput;
import com.example.sourcingemailcrafter.model.EvaluationScores;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailEvaluatorService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public EmailEvaluatorService(ChatClient chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    public EvaluationResult evaluateEmail(String emailDraft, EmailCrafterInput input) {
        // Using simple string concatenation instead of templates to avoid syntax issues
        String promptText = "You are an expert email quality analyst. Evaluate the following recruitment email draft based on these criteria:\n\n" +
            "1. Personalization (1-10): How well does it connect with the candidate's specific background?\n" +
            "2. Relevance to Role (1-10): How clearly is the job's relevance articulated for this candidate?\n" +
            "3. Tone Alignment (1-10): Does it match the desired tone of \"" + input.desiredToneStyle() + "\"?\n" +
            "4. Clarity & Conciseness (1-10): Is the message clear and to the point?\n" +
            "5. Call to Action (CTA) Strength (1-10): Is the CTA clear and compelling?\n" +
            "6. Grammar & Professionalism (Pass/Fail): Free of errors?\n" +
            "7. Constraint Adherence (Pass/Fail): Does it meet the structural constraints of \"" + input.structuralConstraintsGuidelines().maxLength() + "\"?\n\n" +
            "For each criterion, provide a score (if applicable) and specific, actionable feedback for improvement.\n" +
            "If grammar issues exist, list them.\n" +
            "If constraints are violated, specify how.\n\n" +
            "Email Draft:\n" + emailDraft + "\n\n" +
            "Your response must be in JSON format with the following structure:\n" +
            "{\n" +
            "    \"scores\": {\n" +
            "        \"personalization\": <score>,\n" +
            "        \"relevanceToRole\": <score>,\n" +
            "        \"toneAlignment\": <score>,\n" +
            "        \"clarityAndConciseness\": <score>,\n" +
            "        \"ctaStrength\": <score>,\n" +
            "        \"grammarAndProfessionalism\": \"<Pass or Fail>\",\n" +
            "        \"constraintAdherence\": \"<Pass or Fail>\",\n" +
            "        \"overallQualityScore\": <average of numeric scores>\n" +
            "    },\n" +
            "    \"feedback\": \"<detailed feedback with specific suggestions for improvement>\"\n" +
            "}\n\n" +
            "IMPORTANT: Return ONLY the JSON object with no additional text, markdown formatting, or code blocks.";

        String evaluationJson = chatClient.prompt()
            .user(promptText)
            .call()
            .content();
        
        // Extract JSON from potential markdown code blocks
        evaluationJson = extractJsonFromMarkdown(evaluationJson);
        
        try {
            return objectMapper.readValue(evaluationJson, EvaluationResult.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse evaluation result: " + evaluationJson, e);
        }
    }
    
    /**
     * Extracts JSON from a string that might be wrapped in markdown code blocks
     */
    private String extractJsonFromMarkdown(String input) {
        // Check if the input starts with a markdown code block
        Pattern pattern = Pattern.compile("```(?:json)?\\s*\\n?(.*?)\\n?```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.find()) {
            // Return the content inside the code block
            return matcher.group(1).trim();
        }
        
        // If no code block is found, return the original input
        return input.trim();
    }

    public static class EvaluationResult {
        private EvaluationScores scores;
        private String feedback;

        public EvaluationScores getScores() {
            return scores;
        }

        public void setScores(EvaluationScores scores) {
            this.scores = scores;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }
} 