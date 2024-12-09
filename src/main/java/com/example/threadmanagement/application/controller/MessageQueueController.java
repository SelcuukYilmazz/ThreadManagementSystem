package com.example.threadmanagement.application.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/messageQueue")
@RequiredArgsConstructor
public class MessageQueueController {

    /**
     * Shared blocking queue that holds messages managed across the application.
     * Thread-safe implementation for concurrent access by sender and receiver threads.
     */
    private final BlockingQueue<String> sharedQueue;

    /**
     * Retrieves messages from the shared queue in a paginated format.
     * Converts the queue contents to a paginated response for easier client consumption.
     *
     * @param page page number to retrieve (zero-based indexing)
     * @param size number of messages per page
     * @return ResponseEntity containing a Page of messages
     *         If the requested page is beyond available data, returns an empty Page
     *         The Page includes:
     *         - List of messages for the current page
     *         - Total number of messages
     *         - Current page information
     */
    @GetMapping("/getMessageQueue")
    public ResponseEntity<Page<String>> getQueue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "14") int size
    ) {
        List<String> queueAsList = new ArrayList<>(sharedQueue);

        int start = page * size;
        int end = Math.min(start + size, queueAsList.size());

        if (start > queueAsList.size()) {
            return ResponseEntity.ok(Page.empty());
        }

        List<String> pageContent = queueAsList.subList(start, end);

        return ResponseEntity.ok(new PageImpl<>(
                pageContent,
                PageRequest.of(page, size),
                queueAsList.size()
        ));
    }
}
