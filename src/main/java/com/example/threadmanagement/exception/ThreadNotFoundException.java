package com.example.threadmanagement.exception;

import java.util.UUID;

public class ThreadNotFoundException extends ThreadManagementException {

    /**
     * Constructs a new thread not found exception with a message containing the thread ID.
     * @param threadId ID of the thread that could not be found
     */
    public ThreadNotFoundException(UUID threadId) {
        super("Thread not found with ID: " + threadId);
    }
}