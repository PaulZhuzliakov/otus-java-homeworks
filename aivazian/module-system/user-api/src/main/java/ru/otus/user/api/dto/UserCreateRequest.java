package ru.otus.user.api.dto;

public record UserCreateRequest(
        String name,
        String lastname
) {
}
