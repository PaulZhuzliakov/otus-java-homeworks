package org.example.memorydump.exception;

public class UserCreateException extends RuntimeException {
    public UserCreateException(String message) {
        super(message);
    }

    public UserCreateException(InterruptedException e) {
        super(e);
    }
}
