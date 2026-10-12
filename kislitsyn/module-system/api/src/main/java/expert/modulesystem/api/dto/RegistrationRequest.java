package org.java.expert.modulesystem.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank @Size(max = 100) String login,
        @NotBlank @Size(max = 100) String password
) {
}