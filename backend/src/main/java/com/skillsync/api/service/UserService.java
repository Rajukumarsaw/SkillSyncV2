package com.skillsync.api.service;

import com.skillsync.api.model.User;
import com.skillsync.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    
    public User findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public boolean existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User save(User user) {
        log.debug("Saving user: {}", user.getUsername());
        return userRepository.save(user);
    }
    
    public Page<User> findAll(Pageable pageable) {
        log.debug("Finding all users with pageable: {}", pageable);
        return userRepository.findAll(pageable);
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting user by id: {}", id);
        userRepository.deleteById(id);
    }
} 