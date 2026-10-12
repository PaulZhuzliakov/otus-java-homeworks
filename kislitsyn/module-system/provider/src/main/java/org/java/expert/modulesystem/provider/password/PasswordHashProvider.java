package org.java.expert.modulesystem.provider.password;

public interface PasswordHashProvider {

    String hash(String password);
}
