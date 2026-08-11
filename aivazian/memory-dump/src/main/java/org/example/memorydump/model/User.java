package org.example.memorydump.model;

public record User(
        Long id,
        String login,
        String encodedPassword
) {
}
