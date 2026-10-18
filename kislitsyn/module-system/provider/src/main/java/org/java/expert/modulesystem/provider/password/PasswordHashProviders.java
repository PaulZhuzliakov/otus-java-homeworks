package org.java.expert.modulesystem.provider.password;

import org.java.expert.modulesystem.provider.internal.BCryptPasswordHashProvider;

public final class PasswordHashProviders {

    private PasswordHashProviders() {
    }

    public static PasswordHashProvider bcrypt() {
        return new BCryptPasswordHashProvider();
    }
}
