package com.jobtracker.service;

import com.jobtracker.dto.ApplicationRequest;
import com.jobtracker.dto.InterviewRequest;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.model.*;
import com.jobtracker.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final JobApplicationRepository applicationRepository;

    public List<JobApplication> getAllApplications(String userId) {
        return applicationRepository.findByUserId(userId);
    }

    public JobApplication createApplication(ApplicationRequest request, String userId) {
        JobApplication application = new JobApplication();
        application.setUserId(userId);
        application.setJobId(request.getJobId());
        application.setTitle(request.getTitle());
        application.setCompany(request.getCompany());
        application.setLocation(request.getLocation());
        application.setDescription(request.getDescription());
        application.setSalaryMin(request.getSalaryMin());
        application.setSalaryMax(request.getSalaryMax());
        application.setApplicationUrl(request.getApplicationUrl());
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDateTime.now());
        application.setLastUpdated(LocalDateTime.now());
        application.setNotes(request.getNotes());

        // Initialize activities list with first activity
        Activity activity = new Activity();
        activity.setType("APPLICATION_CREATED");
        activity.setDescription("Application submitted");
        activity.setTimestamp(LocalDateTime.now());
        application.setActivities(Collections.singletonList(activity));

        return applicationRepository.save(application);
    }

    public JobApplication updateStatus(String id, String status, String userId) {
        JobApplication application = getApplicationByIdAndUserId(id, userId);
        application.setStatus(ApplicationStatus.valueOf(status));
        application.setLastUpdated(LocalDateTime.now());
        return applicationRepository.save(application);
    }
    public boolean hasApplied(String userId, String jobId) {
        return applicationRepository.existsByUserIdAndJobId(userId, jobId);
    }

    public JobApplication updateNotes(String id, String notes, String userId) {
        JobApplication application = getApplicationByIdAndUserId(id, userId);
        application.setNotes(notes);
        application.setLastUpdated(LocalDateTime.now());
        return applicationRepository.save(application);
    }

    public JobApplication scheduleInterview(String id, InterviewRequest interviewRequest, String userId) {
        JobApplication application = getApplicationByIdAndUserId(id, userId);

        Interview interview = new Interview();
        interview.setType(interviewRequest.getType());
        interview.setScheduledDate(interviewRequest.getScheduledDate());
        interview.setLocation(interviewRequest.getLocation());
        interview.setNotes(interviewRequest.getNotes());
        interview.setStatus(InterviewStatus.SCHEDULED);

        application.setInterview(interview);
        application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        application.setLastUpdated(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public void deleteApplication(String id, String userId) {
        JobApplication application = getApplicationByIdAndUserId(id, userId);
        applicationRepository.delete(application);
    }

    private JobApplication getApplicationByIdAndUserId(String id, String userId) {
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Application not found");
                });
    }
}