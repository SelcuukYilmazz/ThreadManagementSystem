package com.example.threadmanagement.application.controller.websocket;

import com.example.threadmanagement.domain.service.MessageQueueService;
import com.example.threadmanagement.model.dto.MessageQueuePagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageQueueWebSocketController {
    private final MessageQueueService messageQueueService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessageQueue")
    @SendTo("/topic/messageQueue")
    public Page<String> sendMessageQueue(@Payload MessageQueuePagingDto request) {
        Page<String>  result = messageQueueService.getQueuePage(request.getPage(), request.getSize());
        messagingTemplate.convertAndSend("/topic/messageQueue", result);
        return result;
    }
}
