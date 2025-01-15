
package com.jobtracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jobtracker.config.AdzunaConfig;
import com.jobtracker.dto.JobDTO;
import com.jobtracker.dto.JobSearchRequest;
import com.jobtracker.dto.JobSearchResponse;
import com.jobtracker.dto.AdzunaJob;
import com.jobtracker.dto.AdzunaJobResponse;
import com.jobtracker.exception.AdzunaApiException;
import com.jobtracker.model.SavedJob;
import com.jobtracker.repository.SavedJobRepository;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSearchService {
    private final AdzunaConfig adzunaConfig;
    private final RestTemplate restTemplate;
    private final SavedJobRepository savedJobRepository;
    private final Bucket rateLimitBucket;
    private final ObjectMapper objectMapper;  // Add this

    @Cacheable(value = "jobSearch", key = "#request.toString()")
    public JobSearchResponse searchJobs(JobSearchRequest request, String userId) {
        try {
            String url = adzunaConfig.buildSearchUrl(
                    request.getKeyword(),
                    request.getLocation(),
                    request.getPage()
            );

            log.debug("Making request to Adzuna API: {}", url);

            ResponseEntity<String> rawResponse = restTemplate.getForEntity(url, String.class);
            log.debug("Raw API Response: {}", rawResponse.getBody());

            AdzunaJobResponse response = objectMapper.readValue(
                    rawResponse.getBody(),
                    AdzunaJobResponse.class
            );

            return mapToJobSearchResponse(response, userId);
        } catch (Exception e) {
            log.error("Error fetching jobs from Adzuna", e);
            throw new AdzunaApiException("Failed to fetch jobs from Adzuna: " + e.getMessage(), e);
        }
    }

    private JobSearchResponse mapToJobSearchResponse(AdzunaJobResponse adzunaResponse, String userId) {
        if (adzunaResponse == null || adzunaResponse.getResults() == null) {
            throw new AdzunaApiException("Invalid response from Adzuna API");
        }

        List<String> savedJobIds = savedJobRepository.findByUserId(userId)
                .stream()
                .map(savedJob -> savedJob.getJobId())
                .collect(Collectors.toList());

        List<JobDTO> jobs = adzunaResponse.getResults()
                .stream()
                .map(adzunaJob -> mapToJobDTO(adzunaJob, savedJobIds))
                .collect(Collectors.toList());

        JobSearchResponse response = new JobSearchResponse();
        response.setJobs(jobs);
        response.setTotalResults(adzunaResponse.getCount());
        response.setMeanSalary(adzunaResponse.getMeanSalary());
        // Calculate total pages based on results per page
        response.setTotalPages((int) Math.ceil(adzunaResponse.getCount() /
                (double) adzunaConfig.getResultsPerPage()));

        return response;
    }

    private JobDTO mapToJobDTO(AdzunaJob adzunaJob, List<String> savedJobIds) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(adzunaJob.getId());
        jobDTO.setTitle(adzunaJob.getTitle());
        jobDTO.setCompany(adzunaJob.getCompany().getDisplayName());
        jobDTO.setLocation(adzunaJob.getLocation().getDisplayName());
        jobDTO.setDescription(adzunaJob.getDescription());
        jobDTO.setSalaryMin(adzunaJob.getSalaryMin());
        jobDTO.setSalaryMax(adzunaJob.getSalaryMax());
        jobDTO.setApplicationUrl(adzunaJob.getRedirectUrl());
        jobDTO.setSaved(savedJobIds.contains(adzunaJob.getId()));

        // Parse and set the posted date
        if (adzunaJob.getCreatedDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            jobDTO.setPostedDate(LocalDateTime.parse(adzunaJob.getCreatedDate(), formatter));
        }

        return jobDTO;
    }
}