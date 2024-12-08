package com.example.threadmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Configuration // Indicates that this class provides Spring configuration for beans.
public class ThreadConfig {

    /**
     * Configures an ExecutorService with a cached thread pool.
     * - The cached thread pool dynamically creates threads as needed and reuses idle threads.
     * - All threads created are daemon threads, meaning they won't prevent the JVM from shutting down.
     *
     * @return An ExecutorService instance.
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r); // Create a new thread for each task.
            thread.setDaemon(true); // Mark the thread as a daemon to allow JVM shutdown.
            return thread; // Return the configured thread.
        });
    }

    /**
     * Provides a thread-safe queue for sharing string messages.
     * - The queue can hold an unlimited number of elements until memory runs out.
     * - Suitable for producer-consumer scenarios where threads communicate via this queue.
     *
     * @return A LinkedBlockingQueue for shared strings.
     */
    @Bean
    public BlockingQueue<String> sharedQueue() {
        return new LinkedBlockingQueue<>(); // Create a queue that blocks when full or empty.
    }

    /**
     * Configures a thread-safe map to store and manage threads by their unique IDs.
     * - The UUID is used as a key to ensure unique identification.
     * - ConcurrentHashMap allows safe access by multiple threads.
     *
     * @return A ConcurrentHashMap for managing threads.
     */
    @Bean
    public Map<UUID, Thread> threadMap() {
        return new ConcurrentHashMap<>(); // Create a map to manage threads with UUID keys.
    }

    /**
     * Configures a thread-safe map to associate thread-related objects with their unique IDs.
     * - The UUID is used as a key for uniquely identifying each thread's associated object.
     * - Useful for mapping additional metadata or context objects to threads.
     *
     * @return A ConcurrentHashMap for mapping thread-related objects.
     */
    @Bean
    public Map<UUID, Object> threadObjectMap() {
        return new ConcurrentHashMap<>(); // Create a map for thread-related objects.
    }

    /**
     * Provides another thread-safe queue specifically for message management.
     * - Similar to `sharedQueue`, but allows flexibility to segregate specific message types or flows.
     * - Useful for decoupling different message processing pipelines.
     *
     * @return A LinkedBlockingQueue for managing messages.
     */
    @Bean
    public BlockingQueue<String> messageQueue() {
        return new LinkedBlockingQueue<>(); // Create a queue for managing messages.
    }
}
