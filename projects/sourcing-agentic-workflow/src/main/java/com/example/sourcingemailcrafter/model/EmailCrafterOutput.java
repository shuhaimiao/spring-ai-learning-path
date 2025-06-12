package com.example.sourcingemailcrafter.model;

public record EmailCrafterOutput(
    String finalEmailBody,
    EvaluationScores evaluationScores,
    int refinementLoops
) {} 