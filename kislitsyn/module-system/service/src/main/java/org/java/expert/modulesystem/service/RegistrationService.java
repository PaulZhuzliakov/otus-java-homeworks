package org.java.expert.modulesystem.service;

import org.java.expert.modulesystem.core.model.User;
import org.java.expert.modulesystem.core.repository.UserRepository;
import org.java.expert.modulesystem.provider.password.PasswordHashProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final PasswordHashProvider passwordHashProvider;
    private final UserRepository userRepository;

    public RegistrationService(PasswordHashProvider passwordHashProvider, UserRepository userRepository) {
        this.passwordHashProvider = passwordHashProvider;
        this.userRepository = userRepository;
    }

    public User register(String login, String password) {
        var user = new User();
        user.setLogin(login);
        user.setPasswordHash(passwordHashProvider.hash(password));

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateLoginException(login);
        }

        return user;
    }
}
