package com.jobtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // Add this for AdzunaJob as well
public class AdzunaJob {
    private String id;
    private String title;
    private String description;

    @JsonProperty("created")
    private String createdDate;

    private Location location;

    @JsonProperty("salary_min")
    private Double salaryMin;

    @JsonProperty("salary_max")
    private Double salaryMax;

    @JsonProperty("company")
    private Company company;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)  // Add for nested classes too
    public static class Location {
        @JsonProperty("display_name")
        private String displayName;

        @JsonProperty("area")
        private List<String> area;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        @JsonProperty("display_name")
        private String displayName;
    }
}