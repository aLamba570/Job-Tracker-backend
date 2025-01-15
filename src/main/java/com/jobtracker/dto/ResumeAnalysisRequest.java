package com.jobtracker.dto;

import lombok.Data;

@Data
public class ResumeAnalysisRequest {
    private String resumeText;
    private String jobDescription;
}