package com.skillsync.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Tag(name = "Test", description = "Role-based test endpoints")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class RoleTestController {

    @GetMapping("/all")
    @Operation(summary = "Publicly accessible endpoint")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        log.info("Public endpoint accessed");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public endpoint is accessible by anyone");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @Operation(summary = "User role test endpoint")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> userEndpoint() {
        log.info("User endpoint accessed");
        Map<String, String> response = new HashMap<>();
        response.put("message", "User role endpoint is accessible by authenticated users with USER role or higher");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/creator")
    @Operation(summary = "Creator role test endpoint")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<Map<String, String>> creatorEndpoint() {
        log.info("Creator endpoint accessed");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Creator role endpoint is accessible by authenticated users with CREATOR role or higher");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/moderator")
    @Operation(summary = "Moderator role test endpoint")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Map<String, String>> moderatorEndpoint() {
        log.info("Moderator endpoint accessed");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Moderator role endpoint is accessible by authenticated users with MODERATOR role or higher");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @Operation(summary = "Admin role test endpoint")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminEndpoint() {
        log.info("Admin endpoint accessed");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin role endpoint is accessible only by ADMIN users");
        return ResponseEntity.ok(response);
    }
} 