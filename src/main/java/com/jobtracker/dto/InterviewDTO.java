package com.jobtracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InterviewDTO {
    private String id;
    private String company;
    private String position;
    private LocalDateTime date;
    private String type;
    private String status;
    private String location;
}