package com.jobtracker.dto;

import com.jobtracker.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDTO {
    private String id;

    private String jobId;

    private String title;

    private String company;

    private String location;
    private String description;
    private Double salaryMin;
    private Double salaryMax;

    private String applicationUrl;

    private ApplicationStatus status;
    private LocalDateTime appliedDate;
    private String notes;

    // Response specific fields
    private boolean isBookmarked;
    private LocalDateTime lastUpdated;
}
