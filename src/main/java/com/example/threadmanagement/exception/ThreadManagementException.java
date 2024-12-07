package com.example.threadmanagement.exception;

public class ThreadManagementException extends RuntimeException {
    public ThreadManagementException(String message) {
        super(message);
    }

    public ThreadManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}