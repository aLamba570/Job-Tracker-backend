package com.jobtracker.controller;

import com.jobtracker.dto.AuthenticationRequest;
import com.jobtracker.dto.AuthenticationResponse;
import com.jobtracker.dto.RegisterRequest;
import com.jobtracker.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        System.out.println("=== Register endpoint hit ===");
        System.out.println("Request path: /api/auth/register");
        System.out.println("Request body: " + request);
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
