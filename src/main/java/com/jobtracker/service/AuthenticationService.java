package com.jobtracker.service;

import com.jobtracker.dto.AuthenticationRequest;
import com.jobtracker.dto.AuthenticationResponse;
import com.jobtracker.dto.RegisterRequest;
import com.jobtracker.model.Role;
import com.jobtracker.model.User;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        System.out.println("=== Authentication Debug ===");
        System.out.println("Attempting authentication for email: " + request.getEmail());

        var userOptional = userRepository.findByEmail(request.getEmail());
        System.out.println("User found in database: " + userOptional.isPresent());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("User details from DB:");
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password hash: " + user.getPassword());
            System.out.println("Roles: " + user.getAuthorities());
        }

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            System.out.println("Created authentication token");

            var authentication = authenticationManager.authenticate(authToken);
            System.out.println("Authentication successful: " + authentication.isAuthenticated());

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow();
            var jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            System.out.println("Authentication failed with error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}