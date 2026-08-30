package org.example.memorydump.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank
        String login,
        @NotBlank
        @Size(min = 6)
        String password,
        String algorithm
) {
}
