package hu.fodor.genesys_interview.dto;

import hu.fodor.genesys_interview.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime lastLogin
) {
    public static UserResponse toDto(User u){
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getLastLogin()
        );
    }
}