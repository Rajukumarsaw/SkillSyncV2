package com.skillsync.api.controller;

import com.skillsync.api.dto.AuthRequest;
import com.skillsync.api.dto.AuthResponse;
import com.skillsync.api.dto.RefreshTokenRequest;
import com.skillsync.api.dto.RegisterRequest;
import com.skillsync.api.model.User;
import com.skillsync.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration request for username: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/register/creator")
    @Operation(
        summary = "Register a new creator (Admin only)",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registerCreator(@Valid @RequestBody RegisterRequest request) {
        log.info("Creator registration request for username: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request, User.Role.CREATOR));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Token refresh request");
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }
    
    @PostMapping("/role/{username}")
    @Operation(
        summary = "Change user role (Admin only)", 
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> changeUserRole(
            @PathVariable String username, 
            @RequestParam("role") String roleName) {
        log.info("Role change request for user: {} to role: {}", username, roleName);
        try {
            User.Role role = User.Role.valueOf(roleName.toUpperCase());
            return ResponseEntity.ok(authService.changeRole(username, role));
        } catch (IllegalArgumentException e) {
            log.error("Invalid role name: {}", roleName);
            throw new IllegalArgumentException("Invalid role name: " + roleName);
        }
    }
} 