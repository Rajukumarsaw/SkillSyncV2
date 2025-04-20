package com.skillsync.api.controller;

import com.skillsync.api.model.User;
import com.skillsync.api.service.AuthService;
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

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User operations")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    @Operation(summary = "Get current user details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUser() {
        log.info("Request to get current user");
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
} 