package com.jobtracker.controller;


import com.jobtracker.dto.ApplicationRequest;
import com.jobtracker.dto.InterviewRequest;
import com.jobtracker.model.JobApplication;
import com.jobtracker.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllApplications(Authentication authentication) {
        String userId = authentication.getName();
        List<JobApplication> applications = applicationService.getAllApplications(userId);
        return ResponseEntity.ok(applications);
    }

    @PostMapping
    public ResponseEntity<JobApplication> createApplication(
            @RequestBody ApplicationRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(applicationService.createApplication(request, userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplication> updateStatus(
            @PathVariable String id,
            @RequestBody String status,
            Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(applicationService.updateStatus(id, status, userId));
    }

    @PatchMapping("/{id}/notes")
    public ResponseEntity<JobApplication> updateNotes(
            @PathVariable String id,
            @RequestBody String notes,
            Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(applicationService.updateNotes(id, notes, userId));
    }

    @PostMapping("/{id}/interview")
    public ResponseEntity<JobApplication> scheduleInterview(
            @PathVariable String id,
            @RequestBody InterviewRequest interview,
            Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(applicationService.scheduleInterview(id, interview, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable String id,
            Authentication authentication) {
        String userId = authentication.getName();
        applicationService.deleteApplication(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{jobId}")
    public ResponseEntity<Map<String, Boolean>> checkApplicationExists(
            @PathVariable String jobId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        boolean exists = applicationService.hasApplied(userId, jobId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
