package com.jobtracker.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "job_applications")
@CompoundIndex(def = "{'userId': 1, 'status': 1}")
@CompoundIndex(def = "{'userId': 1, 'appliedDate': 1}")
public class JobApplication {
    @Id
    private String id;
    private String userId;
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
    private LocalDateTime lastUpdated;
    private String notes;
    private Interview interview;
    private List<Activity> activities;
}