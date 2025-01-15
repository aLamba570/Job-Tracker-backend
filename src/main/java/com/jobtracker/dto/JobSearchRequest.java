package com.jobtracker.dto;

import lombok.Data;
@Data
public class JobSearchRequest {
    private String keyword;
    private String location;
    private Integer page = 1;
    private String category;
    private String sortBy;
    private Boolean remote;
}
