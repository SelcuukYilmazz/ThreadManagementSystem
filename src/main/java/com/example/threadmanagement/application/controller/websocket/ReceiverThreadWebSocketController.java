package com.example.threadmanagement.application.controller.websocket;

import com.example.threadmanagement.domain.service.interfaces.IReceiverThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ReceiverThreadWebSocketController {
    private final IReceiverThreadService iReceiverThreadService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendReceiverThreads")
    @SendTo("/topic/receiverThreads")
    public void sendReceiverThreads() {
        messagingTemplate.convertAndSend("/topic/receiverThreads", iReceiverThreadService.getAllReceiverThreads());
    }
}
