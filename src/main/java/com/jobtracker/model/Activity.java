package com.jobtracker.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Activity {
    private String type;           // e.g., "STATUS_CHANGE", "NOTE_ADDED", "INTERVIEW_SCHEDULED"
    private String description;
    private LocalDateTime timestamp;
    private String oldValue;
    private String newValue;
}