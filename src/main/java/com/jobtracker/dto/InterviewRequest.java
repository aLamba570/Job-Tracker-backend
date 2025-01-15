package com.jobtracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InterviewRequest {
    private String type;
    private LocalDateTime scheduledDate;
    private String location;
    private String notes;
}