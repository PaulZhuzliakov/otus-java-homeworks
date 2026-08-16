package ru.otus.user.api.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String name,
        String lastname
) {
}
