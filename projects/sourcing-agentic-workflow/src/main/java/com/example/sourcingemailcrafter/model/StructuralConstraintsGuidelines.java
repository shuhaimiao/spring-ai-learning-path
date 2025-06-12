package com.example.sourcingemailcrafter.model;

public record StructuralConstraintsGuidelines(
    boolean needsHtmlPlaceholders,
    String maxLength,
    BrandingGuidelines brandingGuidelines
) {} 