package com.jobtracker.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "saved_jobs")
public class SavedJob {
    @Id
    private String id;
    private String userId;
    private String jobId;
    private String title;
    private String company;
    private String location;
    private Double salaryMin;
    private Double salaryMax;
    private String applicationUrl;
    private LocalDateTime savedAt;
    private String notes;
}
