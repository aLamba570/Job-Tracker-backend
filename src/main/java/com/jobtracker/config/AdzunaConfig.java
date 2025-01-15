package com.jobtracker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@Configuration
@ConfigurationProperties(prefix = "adzuna.api")

public class AdzunaConfig {
    private String appId;
    private String apiKey;
    private String baseUrl;
    private String country;
    private Integer resultsPerPage;
    private Integer maxDaysOld;

    @Bean
    public RestTemplate adzunaRestTemplate() {
        return new RestTemplate();
    }

    public String buildSearchUrl(String what, String where, int page) {
        return UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment(country, "search", String.valueOf(page))
                .queryParam("app_id", appId)
                .queryParam("app_key", apiKey)
                .queryParam("results_per_page", resultsPerPage)
                .queryParam("max_days_old", maxDaysOld)
                .queryParam("what", what)
                .queryParamIfPresent("where", where != null ? java.util.Optional.of(where) : java.util.Optional.empty())
                .build()
                .toUriString();
    }
}