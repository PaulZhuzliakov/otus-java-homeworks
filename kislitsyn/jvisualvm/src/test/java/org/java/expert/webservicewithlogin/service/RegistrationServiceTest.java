package org.java.expert.webservicewithlogin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.java.expert.webservicewithlogin.model.User;
import org.java.expert.webservicewithlogin.problem.RedundantQuerySimulator;
import org.java.expert.webservicewithlogin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class RegistrationServiceTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RedundantQuerySimulator redundantQuerySimulator =
            new RedundantQuerySimulator(userRepository, false, 0);
    private final RegistrationService registrationService =
            new RegistrationService(passwordEncoder, userRepository, redundantQuerySimulator);

    @Test
    void registersUserAndHashesPassword() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            var user = invocation.getArgument(0, User.class);
            user.setId(1L);
            return user;
        });

        var user = registrationService.register("alice", "secret");

        assertEquals(1L, user.getId());
        assertEquals("alice", user.getLogin());
        assertNotEquals("secret", user.getPasswordHash());
        assertTrue(passwordEncoder.matches("secret", user.getPasswordHash()));
    }

    @Test
    void rejectsDuplicateLogin() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate login"));

        assertThrows(
                DuplicateLoginException.class,
                () -> registrationService.register("alice", "another-secret")
        );
    }
}
