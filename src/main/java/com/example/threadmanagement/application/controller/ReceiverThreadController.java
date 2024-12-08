package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.ReceiverThreadService;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/receiverThreads")
@RequiredArgsConstructor
public class ReceiverThreadController {
    private final ReceiverThreadService receiverThreadService;

    @PostMapping("/createReceiverThreadsWithAmount")
    public List<ReceiverThreadDto> createReceiverThreadsWithAmount(
            @RequestParam int receiverAmount
    )
    {
        return receiverThreadService.createReceiverThreadsWithAmount(receiverAmount);
    }

    @GetMapping("/startReceiverThreadsLifeCycle")
    public ResponseEntity<Boolean> startSenderThreadsLifeCycle() {
        return ResponseEntity.ok(receiverThreadService.startReceiverThreadsLifeCycle());
    }

    @GetMapping("/getActiveReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getActiveReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getActiveReceiverThreads());
    }

    @GetMapping("/getAllReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getAllReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getAllReceiverThreads());
    }

    @GetMapping("/getPassiveReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getPassiveReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getPassiveReceiverThreads());
    }

    @PutMapping("/{threadId}/updateReceiverThread")
    public ResponseEntity<ReceiverThreadDto> updateReceiverThread(
            @ParameterObject ReceiverThreadDto threadDto) {
        return ResponseEntity.ok(receiverThreadService.updateReceiverThread(threadDto));
    }

    @PutMapping("/{threadId}/updateReceiverThreadPriority")
    public ResponseEntity<UUID> updateReceiverThreadPriority(
            @RequestParam UUID id,
            @RequestParam Integer priority) {
        return ResponseEntity.ok(receiverThreadService.updateReceiverThreadPriority(id, priority));
    }

    @PutMapping("/{threadId}/updateReceiverThreadState")
    public ResponseEntity<UUID> updateReceiverThreadState(
            @RequestParam UUID id,
            @RequestParam ThreadState threadState) {
        return ResponseEntity.ok(receiverThreadService.updateReceiverThreadState(id, threadState));
    }

    @DeleteMapping("/deleteReceiverThreadById")
    public ResponseEntity<UUID> deleteReceiverThreadById(@RequestParam UUID id){
        return ResponseEntity.ok(receiverThreadService.deleteReceiverThreadById(id));
    }

    @DeleteMapping("/deleteAllReceiverThreads")
    public ResponseEntity<Boolean> deleteAllReceiverThreads(){
        return ResponseEntity.ok(receiverThreadService.deleteAllReceiverThreads());
    }
}