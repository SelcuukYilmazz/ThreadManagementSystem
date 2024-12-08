package com.example.threadmanagement.exception;

import java.util.UUID;

public class ThreadNotFoundException extends ThreadManagementException {
    public ThreadNotFoundException(UUID threadId) {
        super("Thread not found with ID: " + threadId);
    }
}