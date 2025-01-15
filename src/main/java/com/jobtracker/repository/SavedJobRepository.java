package com.jobtracker.repository;

import com.jobtracker.model.SavedJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends MongoRepository<SavedJob, String> {
    List<SavedJob> findByUserId(String userId);
    Optional<SavedJob> findByUserIdAndJobId(String userId, String jobId);
    boolean existsByUserIdAndJobId(String userId, String jobId);
    void deleteByUserIdAndJobId(String userId, String jobId);
    long countByUserId(String userId);
}