package com.jobtracker.dto;

import lombok.Data;

@Data
public class ApplicationRequest {
    private String jobId;          // Added jobId field
    private String title;
    private String company;
    private String location;
    private String description;
    private Double salaryMin;
    private Double salaryMax;
    private String applicationUrl;
    private String notes;          // Added notes field
    private String status;
}
