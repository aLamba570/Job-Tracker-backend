package com.jobtracker.controller;

import com.jobtracker.dto.JobApplicationDTO;
import com.jobtracker.model.JobApplication;
import com.jobtracker.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    @PostMapping("/track")
    public ResponseEntity<JobApplication> trackApplication(
            @RequestBody JobApplicationDTO applicationDTO,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        JobApplication trackedApplication = jobApplicationService.trackApplication(userId, applicationDTO);
        return ResponseEntity.ok(trackedApplication);
    }
}
