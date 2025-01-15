package com.jobtracker.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResumeAnalysisResponse {
    // Overall Scores
    private double matchPercentage;      // Overall match percentage
    private double skillScore;           // Technical skill match score
    private double keywordScore;         // Keyword match score
    private double formatScore;          // Resume format score

    // Keywords and Skills
    private List<String> matchingKeywords;    // Keywords found in both resume and job description
    private List<String> missingKeywords;     // Required keywords missing from resume
    private List<String> recommendedKeywords;  // Additional relevant keywords to consider

    // Detailed Analysis
    private Map<String, Double> skillAnalysis;    // Detailed skill-by-skill analysis
    private List<String> strengths;              // Strong points in the resume
    private List<String> weaknesses;             // Areas needing improvement
    private List<String> suggestions;            // Specific improvement suggestions

    // Format Analysis
    private boolean hasProperFormatting;        // Whether resume follows proper formatting
    private List<String> formattingSuggestions; // Suggestions for formatting improvements
    private Map<String, Boolean> formatChecklist; // Checklist of formatting requirements

    // Section Analysis
    private Map<String, Double> sectionScores;   // Scores for each resume section
    private List<String> missingSections;        // Important sections that are missing
    private Map<String, List<String>> sectionSuggestions; // Suggestions for each section

    // ATS Compatibility
    private boolean isATSCompatible;            // Whether the resume is ATS-friendly
    private List<String> atsIssues;            // Issues that might affect ATS parsing
    private List<String> atsOptimizationTips;  // Tips for better ATS compatibility

    // Industry-Specific Analysis
    private String industryCategory;            // Detected industry category
    private List<String> industryKeywords;      // Industry-specific keywords found
    private List<String> industryRecommendations; // Industry-specific recommendations

    // Action Items
    private List<String> priorityImprovements;  // High-priority improvements needed
    private List<String> quickWins;             // Easy-to-implement improvements
    private List<Map<String, String>> actionableSteps; // Step-by-step improvement plan
}