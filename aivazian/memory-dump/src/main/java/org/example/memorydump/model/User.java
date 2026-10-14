package org.example.memorydump.model;

import lombok.Builder;

@Builder
public record User(
        Long id,
        String login,
        String encodedPassword,
        String firstName,
        String lastName,
        String email
) {
}
