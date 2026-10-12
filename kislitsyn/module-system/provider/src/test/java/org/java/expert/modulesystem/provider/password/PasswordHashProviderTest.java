package org.java.expert.modulesystem.provider.password;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashProviderTest {

    @Test
    void createsBCryptHash() {
        var provider = PasswordHashProviders.bcrypt();

        var hash = provider.hash("secret");

        assertNotEquals("secret", hash);
        assertTrue(new BCryptPasswordEncoder().matches("secret", hash));
    }
}
