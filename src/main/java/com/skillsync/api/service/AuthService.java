package com.skillsync.api.service;

import com.skillsync.api.dto.AuthRequest;
import com.skillsync.api.dto.AuthResponse;
import com.skillsync.api.dto.RefreshTokenRequest;
import com.skillsync.api.dto.RegisterRequest;
import com.skillsync.api.exception.TokenRefreshException;
import com.skillsync.api.exception.UserAlreadyExistsException;
import com.skillsync.api.model.User;
import com.skillsync.api.security.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Value("${jwt.refreshToken.expiration}")
    private Long refreshExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        return register(request, User.Role.USER);
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request, User.Role role) {
        log.info("Registering user with username: {} and role: {}", request.getUsername(), role);
        
        if (userService.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        User savedUser = userService.save(user);
        log.info("User registered successfully: {} with role: {}", savedUser.getUsername(), savedUser.getRole());

        String accessToken = jwtTokenUtil.generateToken(savedUser);
        String refreshToken = jwtTokenUtil.generateRefreshToken(savedUser);

        return AuthResponse.fromUserAndTokens(savedUser, accessToken, refreshToken, jwtExpiration);
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Authenticating user: {}", request.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        
        log.info("User authenticated successfully: {}", user.getUsername());
        
        String accessToken = jwtTokenUtil.generateToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        return AuthResponse.fromUserAndTokens(user, accessToken, refreshToken, jwtExpiration);
    }
    
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");
        
        String refreshToken = request.getRefreshToken();
        
        try {
            String username = jwtTokenUtil.extractUsername(refreshToken);
            
            if (username == null) {
                throw new TokenRefreshException("Invalid refresh token");
            }
            
            User user = userService.findByUsername(username);
            
            if (!jwtTokenUtil.validateToken(refreshToken, user)) {
                throw new TokenRefreshException("Invalid refresh token");
            }
            
            String newAccessToken = jwtTokenUtil.generateToken(user);
            
            log.info("Token refreshed successfully for user: {}", username);
            
            return AuthResponse.fromUserAndTokens(user, newAccessToken, refreshToken, jwtExpiration);
            
        } catch (ExpiredJwtException ex) {
            throw new TokenRefreshException("Refresh token has expired. Please login again.");
        } catch (UsernameNotFoundException ex) {
            throw new TokenRefreshException("User not found with the token provided");
        }
    }
    
    @Transactional
    public AuthResponse changeRole(String username, User.Role newRole) {
        User user = userService.findByUsername(username);
        user.setRole(newRole);
        User updatedUser = userService.save(user);
        
        String accessToken = jwtTokenUtil.generateToken(updatedUser);
        String refreshToken = jwtTokenUtil.generateRefreshToken(updatedUser);
        
        return AuthResponse.fromUserAndTokens(updatedUser, accessToken, refreshToken, jwtExpiration);
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        
        return (User) authentication.getPrincipal();
    }
} 