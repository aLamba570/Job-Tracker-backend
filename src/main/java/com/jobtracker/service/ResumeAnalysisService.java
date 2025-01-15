package com.jobtracker.service;
import com.jobtracker.dto.ResumeAnalysisRequest;
import com.jobtracker.dto.ResumeAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {
    private final RestTemplate restTemplate;

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.url.parser}")
    private String parserApiUrl;

    @Value("${huggingface.api.url.keyword}")
    private String keywordApiUrl;

    @Value("${huggingface.api.url.skill}")
    private String skillApiUrl;

    public ResumeAnalysisResponse analyzeResume(String resumeText, String jobDescription) {
        // Use all three models for comprehensive analysis
        Map<String, Double> parserResults = analyzeWithParser(resumeText, jobDescription);
        List<String> keywords = extractKeywords(resumeText, jobDescription);
        double skillMatch = calculateSkillMatch(resumeText, jobDescription);

        // Combine results from all models
        return combineResults(parserResults, keywords, skillMatch, resumeText, jobDescription);
    }

    private Map<String, Double> analyzeWithParser(String resumeText, String jobDescription) {
        HttpHeaders headers = createHeaders();
        Map<String, String> body = Map.of(
                "inputs", resumeText,
                "parameters", jobDescription
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    parserApiUrl,
                    request,
                    Map.class
            );
            // Process parser response
            return processParserResponse(response.getBody());
        } catch (Exception e) {
            log.error("Error calling parser API", e);
            return new HashMap<>();
        }
    }

    private List<String> extractKeywords(String resumeText, String jobDescription) {
        HttpHeaders headers = createHeaders();
        Map<String, String> body = Map.of(
                "inputs", Map.of(
                        "source_text", resumeText,
                        "target_text", jobDescription
                ).toString()
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<List> response = restTemplate.postForEntity(
                    keywordApiUrl,
                    request,
                    List.class
            );
            return processKeywordResponse(response.getBody());
        } catch (Exception e) {
            log.error("Error calling keyword API", e);
            return new ArrayList<>();
        }
    }

    private double calculateSkillMatch(String resumeText, String jobDescription) {
        HttpHeaders headers = createHeaders();
        Map<String, List<String>> body = Map.of(
                "inputs", List.of(resumeText, jobDescription)
        );

        HttpEntity<Map<String, List<String>>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Double> response = restTemplate.postForEntity(
                    skillApiUrl,
                    request,
                    Double.class
            );
            return response.getBody() != null ? response.getBody() : 0.0;
        } catch (Exception e) {
            log.error("Error calling skill API", e);
            return 0.0;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiToken);
        return headers;
    }

    private ResumeAnalysisResponse combineResults(
            Map<String, Double> parserResults,
            List<String> keywords,
            double skillMatch,
            String resumeText,
            String jobDescription) {

        // Calculate overall match percentage using weighted average
        double overallMatch = (
                parserResults.getOrDefault("match", 0.0) * 0.4 +
                        skillMatch * 0.4 +
                        (keywords.size() / 20.0) * 0.2  // Normalize keyword count
        );

        // Generate suggestions based on all analyses
        List<String> suggestions = generateEnhancedSuggestions(
                parserResults,
                keywords,
                skillMatch,
                resumeText,
                jobDescription
        );

        return ResumeAnalysisResponse.builder()
                .matchPercentage(overallMatch * 100)
                .matchingKeywords(keywords)
                .missingKeywords(findMissingKeywords(jobDescription, keywords))
                .suggestions(suggestions)
                .skillScore(skillMatch * 100)
                .keywordScore(parserResults.getOrDefault("match", 0.0) * 100)
                .build();
    }

    private List<String> generateEnhancedSuggestions(
            Map<String, Double> parserResults,
            List<String> keywords,
            double skillMatch,
            String resumeText,
            String jobDescription) {

        List<String> suggestions = new ArrayList<>();

        // Add specific suggestions based on each model's results
        if (skillMatch < 0.6) {
            suggestions.add("Your skills alignment with the job requirements needs improvement");
        }

        if (parserResults.getOrDefault("match", 0.0) < 0.6) {
            suggestions.add("Consider restructuring your resume to better match the job description");
        }

        if (keywords.size() < 10) {
            suggestions.add("Include more relevant keywords from the job description");
        }

        // Add detailed improvement suggestions
        suggestions.addAll(generateDetailedSuggestions(resumeText, jobDescription));

        return suggestions;
    }

    private List<String> generateDetailedSuggestions(String resumeText, String jobDescription) {
        List<String> suggestions = new ArrayList<>();

        // Add specific formatting suggestions
        if (!containsBulletPoints(resumeText)) {
            suggestions.add("Use bullet points to highlight key achievements and responsibilities");
        }

        if (!containsActionVerbs(resumeText)) {
            suggestions.add("Start bullet points with strong action verbs");
        }

        if (!containsQuantifiableResults(resumeText)) {
            suggestions.add("Include quantifiable results and metrics where possible");
        }

        return suggestions;
    }

    private boolean containsBulletPoints(String text) {
        return text.contains("•") || text.contains("-") || text.contains("*");
    }

    private boolean containsActionVerbs(String text) {
        List<String> actionVerbs = Arrays.asList("led", "managed", "developed", "created", "implemented");
        return actionVerbs.stream().anyMatch(text.toLowerCase()::contains);
    }

    private boolean containsQuantifiableResults(String text) {
        return text.matches(".*\\d+%.*") || text.matches(".*\\$\\d+.*") || text.matches(".*increased.*by.*");
    }

    private Map<String, Double> processParserResponse(Map<String, Object> response) {
        Map<String, Double> results = new HashMap<>();
        try {
            if (response != null && response.containsKey("scores")) {
                List<Double> scores = (List<Double>) response.get("scores");
                results.put("match", scores.get(0));
            }
        } catch (Exception e) {
            log.error("Error processing parser response", e);
            results.put("match", 0.0);
        }
        return results;
    }

    private List<String> processKeywordResponse(List<Object> response) {
        List<String> keywords = new ArrayList<>();
        try {
            if (response != null) {
                for (Object item : response) {
                    if (item instanceof Map) {
                        Map<String, Object> keywordMap = (Map<String, Object>) item;
                        if (keywordMap.containsKey("word")) {
                            keywords.add((String) keywordMap.get("word"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing keyword response", e);
        }
        return keywords;
    }

    private List<String> findMissingKeywords(String jobDescription, List<String> existingKeywords) {
        // Extract required keywords from job description
        Set<String> requiredKeywords = extractRequiredKeywords(jobDescription);

        // Convert existing keywords to lowercase for case-insensitive comparison
        Set<String> existingLower = existingKeywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Find missing keywords
        return requiredKeywords.stream()
                .filter(keyword -> !existingLower.contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private Set<String> extractRequiredKeywords(String jobDescription) {
        Set<String> keywords = new HashSet<>();

        // Common requirement indicators in job descriptions
        List<String> indicators = Arrays.asList(
                "required", "requirements", "must have", "essential",
                "qualifications", "skills needed", "proficient in",
                "experience with", "knowledge of"
        );

        // Split job description into sentences
        String[] sentences = jobDescription.split("[.!?]+");

        for (String sentence : sentences) {
            String lowerSentence = sentence.toLowerCase();

            // Check if sentence contains requirement indicators
            if (indicators.stream().anyMatch(lowerSentence::contains)) {
                // Extract technical terms and skills
                keywords.addAll(extractTechnicalTerms(sentence));
            }
        }

        // Add common technical skills if found in the description
        addCommonTechnicalSkills(jobDescription, keywords);

        return keywords;
    }

    private Set<String> extractTechnicalTerms(String text) {
        Set<String> terms = new HashSet<>();

        // Pattern for technical terms (e.g., Java, Python, AWS, etc.)
        Pattern pattern = Pattern.compile("\\b[A-Za-z+#]+(?:\\s*[A-Za-z+#]+)*\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String term = matcher.group().trim();
            // Filter out common words and keep only likely technical terms
            if (isLikelyTechnicalTerm(term)) {
                terms.add(term.toLowerCase());
            }
        }

        return terms;
    }

    private boolean isLikelyTechnicalTerm(String term) {
        // List of common technical terms and programming languages
        Set<String> technicalTerms = new HashSet<>(Arrays.asList(
                "java", "python", "javascript", "react", "angular", "vue", "node",
                "spring", "hibernate", "sql", "nosql", "aws", "azure", "docker",
                "kubernetes", "ci/cd", "agile", "scrum", "rest", "api", "microservices",
                "git", "devops", "cloud", "database", "frontend", "backend", "fullstack",
                "testing", "security", "linux", "windows", "mac", "ios", "android"
        ));

        // Check if the term is in our list of technical terms
        return technicalTerms.contains(term.toLowerCase()) ||
                // Or if it starts with a capital letter (likely a proper noun/technology name)
                Character.isUpperCase(term.charAt(0)) ||
                // Or contains version numbers or technical symbols
                term.matches(".*[0-9]+.*") ||
                term.contains("+") ||
                term.contains("#");
    }

    private void addCommonTechnicalSkills(String jobDescription, Set<String> keywords) {
        // Map of common variations of technical terms
        Map<String, List<String>> skillVariations = new HashMap<>();
        skillVariations.put("java", Arrays.asList("core java", "java ee", "java se"));
        skillVariations.put("javascript", Arrays.asList("js", "es6", "ecmascript"));
        skillVariations.put("python", Arrays.asList("python3", "django", "flask"));
        // Add more variations as needed

        String lowerDescription = jobDescription.toLowerCase();
        skillVariations.forEach((skill, variations) -> {
            if (variations.stream().anyMatch(lowerDescription::contains)) {
                keywords.add(skill);
            }
        });
    }
}