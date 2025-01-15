package com.jobtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // Add this annotation to ignore unknown fields
public class AdzunaJobResponse {
    @JsonProperty("results")
    private List<AdzunaJob> results;

    @JsonProperty("count")
    private int count;

    @JsonProperty("mean_salary")
    private Double meanSalary;
}