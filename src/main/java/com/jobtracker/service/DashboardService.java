package com.jobtracker.service;

import com.jobtracker.dto.DashboardStatsDTO;
import com.jobtracker.dto.ActivityDTO;
import com.jobtracker.dto.InterviewDTO;
import com.jobtracker.model.*;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;

    public DashboardStatsDTO getDashboardStats(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minus(1, ChronoUnit.MONTHS);

        // Get all applications
        List<JobApplication> allApplications = jobApplicationRepository.findByUserId(userId);

        // Get upcoming interviews
        List<JobApplication> applicationsWithInterviews = allApplications.stream()
                .filter(app -> app.getInterview() != null &&
                        app.getInterview().getScheduledDate() != null &&
                        app.getInterview().getScheduledDate().isAfter(now))
                .collect(Collectors.toList());

        // Calculate statistics
        Map<String, Long> statusStats = Arrays.stream(ApplicationStatus.values())
                .collect(Collectors.toMap(
                        ApplicationStatus::name,
                        status -> jobApplicationRepository.countByUserIdAndStatus(userId, status)
                ));

        // Calculate response rate (applications with interviews or offers / total applications)
        long responsiveApplications = allApplications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.INTERVIEW_SCHEDULED ||
                        app.getStatus() == ApplicationStatus.INTERVIEW_COMPLETED ||
                        app.getStatus() == ApplicationStatus.OFFER_RECEIVED)
                .count();
        double responseRate = allApplications.isEmpty() ? 0 :
                (double) responsiveApplications / allApplications.size() * 100;

        // Get recent activities
        List<ActivityDTO> recentActivities = getRecentActivities(allApplications);

        // Map upcoming interviews
        List<InterviewDTO> upcomingInterviews = mapUpcomingInterviews(applicationsWithInterviews);

        return DashboardStatsDTO.builder()
                .totalApplications(allApplications.size())
                .activeApplications(allApplications.stream()
                        .filter(app -> app.getStatus() != ApplicationStatus.REJECTED &&
                                app.getStatus() != ApplicationStatus.WITHDRAWN)
                        .count())
                .savedJobs(savedJobRepository.countByUserId(userId))
                .upcomingInterviews(applicationsWithInterviews.size())
                .applicationsByStatus(statusStats)
                .responseRate(responseRate)
                .upcomingInterviewsList(upcomingInterviews)
                .recentActivities(recentActivities)
                .build();
    }

    private List<ActivityDTO> getRecentActivities(List<JobApplication> applications) {
        return applications.stream()
                .sorted(Comparator.comparing(JobApplication::getLastUpdated).reversed())
                .limit(10)
                .map(this::mapToActivityDTO)
                .collect(Collectors.toList());
    }

    private ActivityDTO mapToActivityDTO(JobApplication application) {
        ActivityDTO activity = new ActivityDTO();
        activity.setId(application.getId());
        activity.setCompany(application.getCompany());
        activity.setPosition(application.getTitle());
        activity.setDate(application.getLastUpdated());
        activity.setStatus(application.getStatus().name());

        if (application.getInterview() != null) {
            activity.setType("INTERVIEW");
            activity.setDescription("Interview " + application.getInterview().getStatus().name().toLowerCase());
        } else {
            activity.setType("APPLICATION");
            activity.setDescription("Application " + application.getStatus().name().toLowerCase());
        }

        return activity;
    }

    private List<InterviewDTO> mapUpcomingInterviews(List<JobApplication> applications) {
        return applications.stream()
                .filter(app -> app.getInterview() != null)
                .map(app -> {
                    InterviewDTO interview = new InterviewDTO();
                    interview.setId(app.getId());
                    interview.setCompany(app.getCompany());
                    interview.setPosition(app.getTitle());
                    interview.setDate(app.getInterview().getScheduledDate());
                    interview.setType(app.getInterview().getType());
                    interview.setStatus(app.getInterview().getStatus().name());
                    interview.setLocation(app.getInterview().getLocation());
                    return interview;
                })
                .sorted(Comparator.comparing(InterviewDTO::getDate))
                .collect(Collectors.toList());
    }
}