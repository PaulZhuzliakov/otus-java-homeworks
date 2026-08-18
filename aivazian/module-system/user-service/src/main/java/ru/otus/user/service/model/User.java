package ru.otus.user.service.model;

import lombok.Builder;

@Builder
public record User(
        Long id,
        String name,
        String lastname
) {
}
