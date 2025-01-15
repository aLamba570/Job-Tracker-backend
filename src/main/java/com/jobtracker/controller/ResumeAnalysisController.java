package com.jobtracker.controller;

import com.jobtracker.dto.ResumeAnalysisRequest;
import com.jobtracker.dto.ResumeAnalysisResponse;
import com.jobtracker.service.ResumeAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume-analysis")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResumeAnalysisController {
    private final ResumeAnalysisService resumeAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(@RequestBody ResumeAnalysisRequest request) {
        ResumeAnalysisResponse analysis = resumeAnalysisService.analyzeResume(
                request.getResumeText(),
                request.getJobDescription()
        );
        return ResponseEntity.ok(analysis);
    }
}