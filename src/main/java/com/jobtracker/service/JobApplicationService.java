package com.jobtracker.service;

import com.jobtracker.dto.JobApplicationDTO;
import com.jobtracker.model.JobApplication;
import com.jobtracker.model.ApplicationStatus;
import com.jobtracker.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;

    public JobApplication trackApplication(String userId, JobApplicationDTO dto) {
        JobApplication application = new JobApplication();
        application.setUserId(userId);
        application.setJobId(dto.getJobId());
        application.setTitle(dto.getTitle());
        application.setCompany(dto.getCompany());
        application.setLocation(dto.getLocation());
        application.setDescription(dto.getDescription());
        application.setSalaryMin(dto.getSalaryMin());
        application.setSalaryMax(dto.getSalaryMax());
        application.setApplicationUrl(dto.getApplicationUrl());
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDateTime.now());
        application.setLastUpdated(LocalDateTime.now());
        application.setNotes(dto.getNotes());

        return jobApplicationRepository.save(application);
    }
}