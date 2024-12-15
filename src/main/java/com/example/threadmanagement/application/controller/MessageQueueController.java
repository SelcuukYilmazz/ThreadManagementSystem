package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.MessageQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messageQueue")
@RequiredArgsConstructor
public class MessageQueueController {
    private final MessageQueueService messageQueueService;

    @GetMapping("/getMessageQueue")
    public ResponseEntity<Page<String>> getQueue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "14") int size
    ) {
        return ResponseEntity.ok(messageQueueService.getQueuePage(page, size));
    }

    @MessageMapping("/subscribe")
    public void subscribeToQueue(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "14") int size,
                                 String sessionId) {
        messageQueueService.addSubscriber(sessionId, page, size);
    }

    @MessageMapping("/unsubscribe")
    public void unsubscribeFromQueue(String sessionId) {
        messageQueueService.removeSubscriber(sessionId);
    }

    @MessageMapping("/updatePage")
    public void updateSubscriberPage(String sessionId, int page) {
        messageQueueService.updateSubscriberPage(sessionId, page);
    }
}