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
    private final BlockingQueue<String> sharedQueue;

    @GetMapping("/getMessageQueue")
    public ResponseEntity<Page<String>> getQueue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "14") int size) {

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