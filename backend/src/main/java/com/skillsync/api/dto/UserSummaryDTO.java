package com.skillsync.api.dto;

import com.skillsync.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    
    private Long id;
    private String username;
    private String profilePictureUrl;
    private String role;
    
    public static UserSummaryDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole().name())
                .build();
    }
} 