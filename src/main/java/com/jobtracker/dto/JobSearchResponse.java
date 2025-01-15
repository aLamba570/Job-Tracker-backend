package com.jobtracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class JobSearchResponse {
    private List<JobDTO> jobs;
    private int totalResults;
    private int currentPage;
    private int totalPages;
    private Double meanSalary;
}

