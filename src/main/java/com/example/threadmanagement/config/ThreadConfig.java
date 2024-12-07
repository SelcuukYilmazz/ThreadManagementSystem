package com.example.threadmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Configuration
public class ThreadConfig {

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public Map<UUID, Thread> threadMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<UUID, Object> threadObjectMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public BlockingQueue<String> messageQueue() {
        return new LinkedBlockingQueue<>();
    }
}