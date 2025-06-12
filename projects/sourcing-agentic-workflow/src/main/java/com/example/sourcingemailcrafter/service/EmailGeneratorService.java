package com.example.sourcingemailcrafter.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sourcingemailcrafter.model.EmailCrafterInput;

@Service
public class EmailGeneratorService {

    private final ChatClient chatClient;

    @Autowired
    public EmailGeneratorService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateInitialDraft(EmailCrafterInput input) {
        // Using simple string concatenation instead of templates to avoid syntax issues
        String promptText = "You are an expert recruitment email writer. Your task is to craft a compelling initial outreach email based on the provided information.\n\n" +
            "Candidate Profile:\n" +
            "- Key Skills: " + String.join(", ", input.candidateProfile().keySkills()) + "\n" +
            "- Relevant Experience: " + input.candidateProfile().relevantExperience() + "\n" +
            "- Stated Interests: " + input.candidateProfile().statedInterests() + "\n" +
            "- Online Profile: " + input.candidateProfile().onlineProfileLink() + "\n\n" +
            "Job Description:\n" +
            "- Key Responsibilities: " + input.jobDescription().keyResponsibilities() + "\n" +
            "- Unique Selling Points: " + input.jobDescription().uniqueSellingPoints() + "\n" +
            "- Company Culture Keywords: " + String.join(", ", input.jobDescription().companyCultureKeywords()) + "\n\n" +
            "Email Context:\n" +
            "- Email Type: " + input.emailContextPurpose().emailType() + "\n" +
            "- Desired Call to Action: " + input.emailContextPurpose().desiredCTA() + "\n\n" +
            "Structural Constraints:\n" +
            "- Needs HTML Placeholders: " + input.structuralConstraintsGuidelines().needsHtmlPlaceholders() + "\n" +
            "- Maximum Length: " + input.structuralConstraintsGuidelines().maxLength() + "\n" +
            "- Branding Guidelines: " + input.structuralConstraintsGuidelines().brandingGuidelines().highlightMatch() + "\n\n" +
            "Desired Tone and Style: " + input.desiredToneStyle() + "\n\n" +
            "Based on this information, draft a personalized email that connects the candidate's background with the job opportunity.\n" +
            "The email should be professional, engaging, and include a clear call to action.\n\n" +
            "IMPORTANT: Return ONLY the email text with no additional commentary, explanations, or markdown formatting.";

        return chatClient.prompt()
            .user(promptText)
            .call()
            .content();
    }

    public String refineDraft(String originalDraft, String feedback) {
        // Using simple string concatenation instead of templates to avoid syntax issues
        String promptText = "You are an expert recruitment email writer. Your task is to refine the following email draft based on the provided feedback.\n\n" +
            "Original Draft:\n" + originalDraft + "\n\n" +
            "Feedback:\n" + feedback + "\n\n" +
            "Please improve the email draft addressing all the feedback points. Maintain the professional tone and ensure the email remains personalized and engaging.\n\n" +
            "IMPORTANT: Return ONLY the revised email text with no additional commentary, explanations, or markdown formatting.";

        return chatClient.prompt()
            .user(promptText)
            .call()
            .content();
    }
} 