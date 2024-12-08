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
@RequestMapping("/messageQueue") // All endpoints in this controller will start with "/messageQueue"
@RequiredArgsConstructor // Automatically generates a constructor for all final fields
public class MessageQueueController {

    // A shared, thread-safe queue that holds messages managed across the application
    private final BlockingQueue<String> sharedQueue;

    /**
     * This endpoint retrieves messages from the shared queue in a paginated format.
     * Clients can specify the page number and size; if not, it defaults to page 0 with 14 items.
     */
    @GetMapping("/getMessageQueue") // Maps GET requests to this method with the URL "/getMessageQueue"
    public ResponseEntity<Page<String>> getQueue(
            @RequestParam(defaultValue = "0") int page, // The page number (starts from 0)
            @RequestParam(defaultValue = "14") int size // The number of messages per page
    ) {
        // Convert the queue to a list so we can easily slice it into pages
        List<String> queueAsList = new ArrayList<>(sharedQueue);

        // Calculate the start and end indices for the requested page
        int start = page * size; // Where this page starts in the full list
        int end = Math.min(start + size, queueAsList.size()); // Make sure we don't go beyond the list size

        // If the start index is out of bounds, return an empty page
        if (start > queueAsList.size()) {
            return ResponseEntity.ok(Page.empty());
        }

        // Get the messages for the requested page (sublist)
        List<String> pageContent = queueAsList.subList(start, end);

        // Wrap the sublist in a paginated response and return it
        return ResponseEntity.ok(new PageImpl<>(
                pageContent, // The current page's messages
                PageRequest.of(page, size), // The page and size information
                queueAsList.size() // The total number of messages in the queue
        ));
    }
}
