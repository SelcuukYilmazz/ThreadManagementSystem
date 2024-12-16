package com.example.threadmanagement.application.controller.websocket;

import com.example.threadmanagement.domain.service.interfaces.ISenderThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SenderThreadWebSocketController {
    private final ISenderThreadService iSenderThreadService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendSenderThreads")
    @SendTo("/topic/senderThreads")
    public void sendSenderThreads() {
        messagingTemplate.convertAndSend("/topic/senderThreads",
                iSenderThreadService.getAllSenderThreads());
    }
}