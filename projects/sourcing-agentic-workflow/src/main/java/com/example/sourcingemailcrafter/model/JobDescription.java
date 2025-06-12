package com.example.sourcingemailcrafter.model;

import java.util.List;

public record JobDescription(
    String keyResponsibilities,
    String uniqueSellingPoints,
    List<String> companyCultureKeywords
) {} 