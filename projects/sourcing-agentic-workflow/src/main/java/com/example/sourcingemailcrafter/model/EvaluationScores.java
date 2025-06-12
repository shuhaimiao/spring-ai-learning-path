package com.example.sourcingemailcrafter.model;

public record EvaluationScores(
    int personalization,
    int relevanceToRole,
    int toneAlignment,
    int clarityAndConciseness,
    int ctaStrength,
    String grammarAndProfessionalism,
    String constraintAdherence,
    double overallQualityScore
) {} 