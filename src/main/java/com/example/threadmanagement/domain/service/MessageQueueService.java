package com.example.threadmanagement.domain.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MessageQueueService {
    private final BlockingQueue<String> sharedQueue;
    private final SimpMessagingTemplate messagingTemplate;

    public Page<String> getQueuePage(int page, int size) {
        List<String> queueAsList = new ArrayList<>(sharedQueue);
        int start = page * size;
        int end = Math.min(start + size, queueAsList.size());

        if (start > queueAsList.size()) {
            return Page.empty();
        }

        List<String> pageContent = queueAsList.subList(start, end);
        return new PageImpl<>(
                pageContent,
                PageRequest.of(page, size),
                queueAsList.size()
        );
    }
}