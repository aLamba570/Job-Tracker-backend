
package com.jobtracker.controller;

import com.jobtracker.dto.JobSearchRequest;
import com.jobtracker.dto.JobSearchResponse;
import com.jobtracker.service.JobSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobSearchController {
    private final JobSearchService jobSearchService;

    @GetMapping("/search")
    public ResponseEntity<JobSearchResponse> searchJobs(
            @ModelAttribute JobSearchRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName(); // Gets the user's email/username
        JobSearchResponse response = jobSearchService.searchJobs(request, userId);
        return ResponseEntity.ok(response);
    }
}