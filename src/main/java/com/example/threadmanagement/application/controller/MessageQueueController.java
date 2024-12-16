package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.MessageQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messageQueue")
@RequiredArgsConstructor
public class MessageQueueController {
    private final MessageQueueService messageQueueService;
    private final SimpMessagingTemplate messagingTemplate;

}