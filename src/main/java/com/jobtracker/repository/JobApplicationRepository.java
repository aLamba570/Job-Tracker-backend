package com.jobtracker.repository;

import com.jobtracker.model.JobApplication;
import com.jobtracker.model.ApplicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends MongoRepository<JobApplication, String> {
    List<JobApplication> findByUserId(String userId);
    List<JobApplication> findByUserIdAndStatus(String userId, ApplicationStatus status);
    List<JobApplication> findByUserIdAndAppliedDateBetween(String userId, LocalDateTime start, LocalDateTime end);
    long countByUserIdAndStatus(String userId, ApplicationStatus status);
    Optional<JobApplication> findByIdAndUserId(String id, String userId);

    boolean existsByUserIdAndJobId(String userId, String jobId);
}