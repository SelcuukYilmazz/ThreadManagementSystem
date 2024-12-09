package com.example.threadmanagement.exception;

public class ThreadManagementException extends RuntimeException {

    /**
     * Constructs a new thread management exception with a specific message.
     * @param message detailed message describing the error condition
     */
    public ThreadManagementException(String message) {
        super(message);
    }

    /**
     * Constructs a new thread management exception with a specific message and cause.
     * @param message detailed message describing the error condition
     * @param cause the underlying cause (which is saved for later retrieval by the Throwable.getCause() method)
     */
    public ThreadManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}