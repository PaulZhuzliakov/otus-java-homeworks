package org.java.expert.modulesystem.provider.internal;

import org.java.expert.modulesystem.provider.password.PasswordHashProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class BCryptPasswordHashProvider implements PasswordHashProvider {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String password) {
        return passwordEncoder.encode(password);
    }
}
