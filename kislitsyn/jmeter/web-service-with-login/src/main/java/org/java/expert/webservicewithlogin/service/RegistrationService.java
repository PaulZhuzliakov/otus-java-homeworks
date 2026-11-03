package org.java.expert.webservicewithlogin.service;

import org.java.expert.webservicewithlogin.repository.UserRepository;
import org.java.expert.webservicewithlogin.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public RegistrationService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User register(String login, String password) {
        var user = new User();
        user.setLogin(login);
        user.setPasswordHash(passwordEncoder.encode(password));

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateLoginException(login);
        }

        return user;
    }
}
