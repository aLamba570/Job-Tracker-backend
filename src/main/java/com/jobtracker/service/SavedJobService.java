package com.jobtracker.service;

import com.jobtracker.dto.JobDTO;
import com.jobtracker.model.SavedJob;
import com.jobtracker.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {
    private final SavedJobRepository savedJobRepository;

    public SavedJob saveJob(String userId, JobDTO jobDTO) {
        if (savedJobRepository.existsByUserIdAndJobId(userId, jobDTO.getId())) {
            throw new IllegalStateException("Job already saved");
        }

        SavedJob savedJob = new SavedJob();
        savedJob.setUserId(userId);
        savedJob.setJobId(jobDTO.getId());
        savedJob.setTitle(jobDTO.getTitle());
        savedJob.setCompany(jobDTO.getCompany());
        savedJob.setLocation(jobDTO.getLocation());
        savedJob.setSalaryMin(jobDTO.getSalaryMin());
        savedJob.setSalaryMax(jobDTO.getSalaryMax());
        savedJob.setApplicationUrl(jobDTO.getApplicationUrl());
        savedJob.setSavedAt(LocalDateTime.now());

        return savedJobRepository.save(savedJob);
    }

    public void unsaveJob(String userId, String jobId) {
        savedJobRepository.deleteByUserIdAndJobId(userId, jobId);
    }

    public List<JobDTO> getSavedJobs(String userId) {
        return savedJobRepository.findByUserId(userId)
                .stream()
                .map(this::mapToJobDTO)
                .collect(Collectors.toList());
    }

    private JobDTO mapToJobDTO(SavedJob savedJob) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(savedJob.getJobId());
        jobDTO.setTitle(savedJob.getTitle());
        jobDTO.setCompany(savedJob.getCompany());
        jobDTO.setLocation(savedJob.getLocation());
        jobDTO.setSalaryMin(savedJob.getSalaryMin());
        jobDTO.setSalaryMax(savedJob.getSalaryMax());
        jobDTO.setApplicationUrl(savedJob.getApplicationUrl());
        jobDTO.setSaved(true);
        return jobDTO;
    }
}