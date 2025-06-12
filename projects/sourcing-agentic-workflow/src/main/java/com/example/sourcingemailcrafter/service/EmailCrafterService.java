package com.example.sourcingemailcrafter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.sourcingemailcrafter.model.EmailCrafterInput;
import com.example.sourcingemailcrafter.model.EmailCrafterOutput;
import com.example.sourcingemailcrafter.model.EvaluationScores;
import com.example.sourcingemailcrafter.service.EmailEvaluatorService.EvaluationResult;

@Service
public class EmailCrafterService {

    private static final Logger logger = LoggerFactory.getLogger(EmailCrafterService.class);
    
    private final EmailGeneratorService generatorService;
    private final EmailEvaluatorService evaluatorService;
    
    @Value("${email.crafter.quality.threshold:8.0}")
    private double qualityThreshold;
    
    @Value("${email.crafter.max.refinement.loops:3}")
    private int maxRefinementLoops;

    @Autowired
    public EmailCrafterService(EmailGeneratorService generatorService, EmailEvaluatorService evaluatorService) {
        this.generatorService = generatorService;
        this.evaluatorService = evaluatorService;
    }

    public EmailCrafterOutput craftEmail(EmailCrafterInput input) {
        logger.info("Starting email crafting process");
        
        try {
            // Step 1: Generate initial draft
            String emailDraft = generatorService.generateInitialDraft(input);
            logger.info("Initial draft generated");
            
            int refinementLoops = 0;
            EvaluationResult evaluation = null;
            
            // Step 2-3: Evaluate and refine in a loop
            do {
                try {
                    // Evaluate current draft
                    evaluation = evaluatorService.evaluateEmail(emailDraft, input);
                    logger.info("Email evaluated, overall score: {}", evaluation.getScores().overallQualityScore());
                    
                    // Check if quality threshold is met
                    if (evaluation.getScores().overallQualityScore() >= qualityThreshold &&
                        "Pass".equalsIgnoreCase(evaluation.getScores().grammarAndProfessionalism()) &&
                        "Pass".equalsIgnoreCase(evaluation.getScores().constraintAdherence())) {
                        
                        logger.info("Quality threshold met, stopping refinement");
                        break;
                    }
                    
                    // Check if max refinement loops reached
                    if (refinementLoops >= maxRefinementLoops) {
                        logger.info("Max refinement loops reached, stopping refinement");
                        break;
                    }
                    
                    // Refine the draft
                    emailDraft = generatorService.refineDraft(emailDraft, evaluation.getFeedback());
                    refinementLoops++;
                    logger.info("Email refined, loop {}/{}", refinementLoops, maxRefinementLoops);
                } catch (Exception e) {
                    logger.error("Error during evaluation/refinement loop: {}", e.getMessage(), e);
                    // If we have at least one successful evaluation, return what we have
                    if (evaluation != null) {
                        break;
                    } else {
                        // Otherwise, create a default evaluation with error message
                        evaluation = createDefaultEvaluation("Error during evaluation: " + e.getMessage());
                        break;
                    }
                }
                
            } while (true);
            
            // Step 4: Return final output
            return new EmailCrafterOutput(
                emailDraft,
                evaluation.getScores(),
                refinementLoops
            );
        } catch (Exception e) {
            logger.error("Error in email crafting process: {}", e.getMessage(), e);
            // Return a fallback response with error information
            EvaluationResult defaultEvaluation = createDefaultEvaluation("Error in email crafting process: " + e.getMessage());
            return new EmailCrafterOutput(
                "Error generating email: " + e.getMessage(),
                defaultEvaluation.getScores(),
                0
            );
        }
    }
    
    /**
     * Creates a default evaluation result when an error occurs
     */
    private EvaluationResult createDefaultEvaluation(String errorMessage) {
        EvaluationResult result = new EvaluationResult();
        EvaluationScores scores = new EvaluationScores(
            5, // personalization
            5, // relevanceToRole
            5, // toneAlignment
            5, // clarityAndConciseness
            5, // ctaStrength
            "Fail", // grammarAndProfessionalism
            "Fail", // constraintAdherence
            5.0  // overallQualityScore
        );
        result.setScores(scores);
        result.setFeedback(errorMessage);
        return result;
    }
} 