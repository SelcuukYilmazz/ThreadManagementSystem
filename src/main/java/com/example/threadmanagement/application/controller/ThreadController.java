package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.ReceiverThreadService;
import com.example.threadmanagement.domain.service.SenderThreadService;
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
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class ThreadController {
    private final ThreadManagerService threadManager;
    private final BlockingQueue<String> sharedQueue;
    private final SenderThreadService senderThreadService;
    private final ReceiverThreadService receiverThreadService;

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


    @GetMapping("/getActiveSenderThreads")
    public ResponseEntity<List<ThreadDto>> getActiveSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getActiveSenderThreads());
    }

    @GetMapping("/getAllSenderThreads")
    public ResponseEntity<List<ThreadDto>> getAllSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getAllSenderThreads());
    }

    @GetMapping("/getPassiveSenderThreads")
    public ResponseEntity<List<ThreadDto>> getPassiveSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getPassiveSenderThreads());
    }

    @GetMapping("/getActiveReceiverThreads")
    public ResponseEntity<List<ThreadDto>> getActiveReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getActiveReceiverThreads());
    }

    @GetMapping("/getAllReceiverThreads")
    public ResponseEntity<List<ThreadDto>> getAllReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getAllReceiverThreads());
    }

    @GetMapping("/getPassiveReceiverThreads")
    public ResponseEntity<List<ThreadDto>> getPassiveReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getPassiveReceiverThreads());
    }

    @PutMapping("/{threadId}/updateThread")
    public ResponseEntity<String> updateThread(
            @ParameterObject ThreadDto threadDto) {
        return ResponseEntity.ok(threadManager.updateThread(threadDto).getBody());
    }

    @PutMapping("/{threadId}/updateThreadPriority")
    public ResponseEntity<String> updateThreadPriority(
            @RequestParam UUID id,
            @RequestParam Integer priority) {
        return ResponseEntity.ok(threadManager.updateThreadPriority(id, priority).getBody());
    }

    @PutMapping("/{threadId}/updateThreadState")
    public ResponseEntity<String> updateThreadPriority(
            @RequestParam UUID id,
            @RequestParam ThreadState threadState) {
        return ResponseEntity.ok(threadManager.updateThreadState(id, threadState).getBody());
    }

    @GetMapping("/getAllThreads")
    public ResponseEntity<List<ThreadDto>> getAllThreads() {
        return ResponseEntity.ok(threadManager.getAllThreadsInfo());
    }

    @DeleteMapping("/deleteThreadById")
    public ResponseEntity<String> deleteThreadById(@RequestParam UUID id){
        return ResponseEntity.ok(threadManager.deleteThreadById(id).getBody());
    }

    @DeleteMapping("/deleteAllThreads")
    public ResponseEntity<String> deleteAllThreads(){
        return ResponseEntity.ok(threadManager.deleteAllThreads().getBody());
    }

    @GetMapping("/getQueue")
    public ResponseEntity<BlockingQueue<String>> getQueue(){
        return ResponseEntity.ok(sharedQueue);
    }
}