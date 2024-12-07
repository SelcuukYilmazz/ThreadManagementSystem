package com.example.threadmanagement.exception;

public class ThreadNotFoundException extends ThreadManagementException {
    public ThreadNotFoundException(String threadId) {
        super("Thread not found with ID: " + threadId);
    }
}