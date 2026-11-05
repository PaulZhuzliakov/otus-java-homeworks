package org.java.expert.webservicewithlogin.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Login is already registered")
public class DuplicateLoginException extends RuntimeException {

    public DuplicateLoginException(String login) {
        super("Login is already registered: " + login);
    }
}
