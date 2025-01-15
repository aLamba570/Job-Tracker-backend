package com.jobtracker.controller;

import com.jobtracker.dto.JobDTO;
import com.jobtracker.model.SavedJob;
import com.jobtracker.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs/saved")
@RequiredArgsConstructor
public class SavedJobController {
    private final SavedJobService savedJobService;

    @PostMapping
    public ResponseEntity<SavedJob> saveJob(
            @RequestBody JobDTO jobDTO,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        SavedJob savedJob = savedJobService.saveJob(userId, jobDTO);
        return ResponseEntity.ok(savedJob);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> unsaveJob(
            @PathVariable String jobId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        savedJobService.unsaveJob(userId, jobId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> getSavedJobs(Authentication authentication) {
        String userId = authentication.getName();
        List<JobDTO> savedJobs = savedJobService.getSavedJobs(userId);
        return ResponseEntity.ok(savedJobs);
    }
}
