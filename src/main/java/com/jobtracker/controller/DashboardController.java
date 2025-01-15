package com.jobtracker.controller;

import com.jobtracker.dto.DashboardStatsDTO;
import com.jobtracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(Authentication authentication) {
        String userId = authentication.getName();
        DashboardStatsDTO stats = dashboardService.getDashboardStats(userId);
        return ResponseEntity.ok(stats);
    }
}