package com.example.threadmanagement.model.entity;

/**
 * Enum representing the types of threads in the thread management system.
 * Used to differentiate between threads that send messages and threads that receive messages.
 */
public enum ThreadType {
    /**
     * Sender thread which sends messages to queue
     */
    SENDER,

    /**
     * Receiver thread which consumes messages from queue
     */
    RECEIVER
}
