package org.java.expert.modulesystem.service;

import org.java.expert.modulesystem.core.model.User;
import org.java.expert.modulesystem.core.repository.UserRepository;
import org.java.expert.modulesystem.provider.password.PasswordHashProvider;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegistrationServiceTest {

    private final PasswordHashProvider passwordHashProvider = mock(PasswordHashProvider.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RegistrationService registrationService =
            new RegistrationService(passwordHashProvider, userRepository);

    @Test
    void registersUserWithPasswordHash() {
        when(passwordHashProvider.hash("secret")).thenReturn("password-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            var user = invocation.getArgument(0, User.class);
            user.setId(1L);
            return user;
        });

        var user = registrationService.register("alice", "secret");

        assertEquals(1L, user.getId());
        assertEquals("alice", user.getLogin());
        assertEquals("password-hash", user.getPasswordHash());
    }

    @Test
    void rejectsDuplicateLogin() {
        when(passwordHashProvider.hash("another-secret")).thenReturn("another-password-hash");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate login"));

        assertThrows(
                DuplicateLoginException.class,
                () -> registrationService.register("alice", "another-secret")
        );
    }
}
