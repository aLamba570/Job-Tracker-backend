package com.jobtracker.dto;

import lombok.Data;
import lombok.Builder;
import java.util.Map;
import java.util.List;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalApplications;
    private long activeApplications;
    private long savedJobs;
    private long upcomingInterviews;
    private Map<String, Long> applicationsByStatus;
    private Map<String, Long> applicationsByMonth;
    private double responseRate;
    private List<InterviewDTO> upcomingInterviewsList;
    private List<ActivityDTO> recentActivities;
}
