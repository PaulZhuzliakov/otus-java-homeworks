package org.java.expert.webservicewithlogin.service;

import org.java.expert.webservicewithlogin.repository.UserRepository;
import org.java.expert.webservicewithlogin.model.User;
import org.java.expert.webservicewithlogin.problem.RedundantQuerySimulator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RedundantQuerySimulator redundantQuerySimulator;

    public RegistrationService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RedundantQuerySimulator redundantQuerySimulator
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.redundantQuerySimulator = redundantQuerySimulator;
    }

    public User register(String login, String password) {
        redundantQuerySimulator.executeRedundantDatabaseQueries(login);

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
