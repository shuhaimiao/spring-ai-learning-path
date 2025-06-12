package com.example.sourcingemailcrafter.model;

import java.util.List;

public record CandidateProfile(
    List<String> keySkills,
    String relevantExperience,
    String statedInterests,
    String onlineProfileLink
) {} 