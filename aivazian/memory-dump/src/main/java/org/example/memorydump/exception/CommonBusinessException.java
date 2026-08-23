package org.example.memorydump.exception;

public class CommonBusinessException extends RuntimeException{
    public CommonBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
