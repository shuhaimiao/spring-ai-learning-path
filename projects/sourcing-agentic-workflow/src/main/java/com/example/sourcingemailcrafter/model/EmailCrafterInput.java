package com.example.sourcingemailcrafter.model;

public record EmailCrafterInput(
    CandidateProfile candidateProfile,
    JobDescription jobDescription,
    EmailContextPurpose emailContextPurpose,
    StructuralConstraintsGuidelines structuralConstraintsGuidelines,
    String desiredToneStyle
) {} 