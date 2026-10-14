package org.example.memorydump.dto;

import lombok.Builder;
import org.example.memorydump.model.User;

@Builder
public record UserResponse(Long id,
                           String login,
                           String firstName,
                           String lastName,
                           String email) {

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.id())
                .login(user.login())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .email(user.email())
                .build();
    }
}
