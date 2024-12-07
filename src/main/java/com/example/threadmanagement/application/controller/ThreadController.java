package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.model.dto.ThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.threadmanagement.domain.service.ThreadManagerService;
import com.example.threadmanagement.model.entity.ThreadEntity;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class ThreadController {
    private final ThreadManagerService threadManager;

    @PostMapping("/createSenderReceiverThreadsWithAmount")
    public boolean createSenderReceiverThreadsWithAmount(
            @RequestParam int senderAmount,
            @RequestParam int receiverAmount
    ) {
        return threadManager.createSenderReceiverThreadsWithAmount(senderAmount,receiverAmount);
    }

    @PostMapping("/createThread")
    public ResponseEntity<ThreadDto> createThread(
            @ParameterObject ThreadDto threadDto) {
        return ResponseEntity.ok(threadManager.createThread(threadDto).getBody());
    }

    @GetMapping("/getThreadById")
    public ResponseEntity<ThreadDto> getThreadById(@RequestParam UUID id) {
        return ResponseEntity.ok(threadManager.getThreadById(id));
    }

    @PutMapping("/{threadId}/updateThread")
    public ResponseEntity<String> updateThread(
            @ParameterObject ThreadDto threadDto) {
        return ResponseEntity.ok(threadManager.updateThread(threadDto).getBody());
    }

    @GetMapping("/getAllThreads")
    public ResponseEntity<List<ThreadDto>> getAllThreads() {
        return ResponseEntity.ok(threadManager.getAllThreadsInfo());
    }

    @DeleteMapping("/deleteThreadById")
    public ResponseEntity<String> deleteThreadById(@RequestParam UUID id){
        return ResponseEntity.ok(threadManager.deleteThreadById(id).getBody());
    }
}