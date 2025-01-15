package com.jobtracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobDTO {
    private String id;
    private String title;
    private String company;
    private String location;
    private String description;
    private Double salaryMin;
    private Double salaryMax;
    private LocalDateTime postedDate;
    private String applicationUrl;
    private boolean isSaved;
}