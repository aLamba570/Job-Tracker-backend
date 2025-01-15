package com.jobtracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private String id;
    private String type;
    private String company;
    private String position;
    private LocalDateTime date;
    private String status;
    private String description;
}
