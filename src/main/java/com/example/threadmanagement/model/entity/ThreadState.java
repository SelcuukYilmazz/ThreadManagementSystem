package com.example.threadmanagement.model.entity;

/**
 * Enum representing possible states of a thread in the thread management system.
 * Used by both sender and receiver threads to track their operational status.
 */
public enum ThreadState {
    /**
     * Indicates that the thread is currently active and processing messages.
     */
    RUNNING,

    /**
     * Indicates that the thread is currently inactive and not processing messages.
     */
    STOPPED
}