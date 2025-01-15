package com.jobtracker.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Interview {
    private String type; // e.g., "Technical", "HR", "Behavioral"
    private LocalDateTime scheduledDate;
    private String location; // Can be physical address or virtual meeting link
    private String notes;
    private InterviewStatus status;
}

