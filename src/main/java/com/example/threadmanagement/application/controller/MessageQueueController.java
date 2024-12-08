package com.example.threadmanagement.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/messageQueue")
@RequiredArgsConstructor
public class MessageQueueController {
    private final BlockingQueue<String> sharedQueue;

    @GetMapping("/getQueue")
    public ResponseEntity<BlockingQueue<String>> getQueue(){
        return ResponseEntity.ok(sharedQueue);
    }
}