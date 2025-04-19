package com.skillsync.api.controller;

import com.skillsync.api.model.User;
import com.skillsync.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Admin operations")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "Get all users (paginated)")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        log.info("Admin request to get all users");
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Admin request to get user by ID: {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/users/{id}/status")
    @Operation(summary = "Enable or disable a user")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> status) {
        
        boolean enabled = status.getOrDefault("enabled", true);
        log.info("Admin request to update user status: {} to enabled={}", id, enabled);
        
        User user = userService.findById(id);
        user.setEnabled(enabled);
        return ResponseEntity.ok(userService.save(user));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Admin request to delete user: {}", id);
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 