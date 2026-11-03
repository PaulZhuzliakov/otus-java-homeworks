package org.java.expert.webservicewithlogin.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfiguration {

    @Bean
    @Primary
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    PasswordEncoder md5PasswordEncoder() {
        return new MessageDigestPasswordEncoder("MD5");
    }

    @Bean
    PasswordEncoder sha256PasswordEncoder() {
        return new MessageDigestPasswordEncoder("SHA-256");
    }

    @Bean
    PasswordEncoder sha512PasswordEncoder() {
        return new MessageDigestPasswordEncoder("SHA-512");
    }
}
