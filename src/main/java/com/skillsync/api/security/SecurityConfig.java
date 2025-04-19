package com.skillsync.api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true
)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;

    private static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/auth/**",
        "/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/health",
        "/actuator/**"
    };

    private static final String[] ADMIN_ENDPOINTS = {
        "/api/v1/admin/**"
    };

    private static final String[] MODERATOR_ENDPOINTS = {
        "/api/v1/moderator/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain");
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                
                // Admin-only endpoints
                .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                
                // Moderator endpoints (accessible by ADMIN and MODERATOR)
                .requestMatchers(MODERATOR_ENDPOINTS).hasAnyRole("ADMIN", "MODERATOR")
                
                // Videos - GET endpoints are public
                .requestMatchers(HttpMethod.GET, "/api/v1/videos/**").permitAll()
                
                // Videos - POST/PUT/DELETE endpoints require CREATOR or higher role
                .requestMatchers(HttpMethod.POST, "/api/v1/videos/**").hasAnyRole("ADMIN", "MODERATOR", "CREATOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/videos/**").hasAnyRole("ADMIN", "MODERATOR", "CREATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/videos/**").hasAnyRole("ADMIN", "MODERATOR", "CREATOR")
                
                // User profiles - GET is public, but POST/PUT/DELETE requires authentication
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/profile").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users/*/profile").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/*/profile").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*/profile").authenticated()
                
                // All other authenticated endpoints
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("Security filter chain configured successfully");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
} 