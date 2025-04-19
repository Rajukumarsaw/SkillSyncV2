package com.skillsync.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot application class for SkillSync API
 */
@SpringBootApplication
@EnableJpaAuditing
public class SkillSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillSyncApplication.class, args);
    }
} 