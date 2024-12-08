package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.SenderThreadService;
import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/senderThreads")
@RequiredArgsConstructor
public class SenderThreadController {
    private final SenderThreadService senderThreadService;

    @PostMapping("/createSenderThreadsWithAmount")
    public ResponseEntity<List<SenderThreadDto>> createSenderThreadsWithAmount(
            @RequestParam int senderAmount
    )
    {
        return ResponseEntity.ok(senderThreadService.createSenderThreadsWithAmount(senderAmount));
    }

    @GetMapping("/startSenderThreadsLifeCycle")
    public ResponseEntity<Boolean> startSenderThreadsLifeCycle() {
        return ResponseEntity.ok(senderThreadService.startSenderThreadsLifeCycle());
    }

    @GetMapping("/getActiveSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getActiveSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getActiveSenderThreads());
    }

    @GetMapping("/getAllSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getAllSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getAllSenderThreads());
    }

    @GetMapping("/getPassiveSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getPassiveSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getPassiveSenderThreads());
    }

    @PutMapping("/{threadId}/updateSenderThread")
    public ResponseEntity<SenderThreadDto> updateSenderThread(
            @ParameterObject SenderThreadDto senderThreadDto) {
        return ResponseEntity.ok(senderThreadService.updateSenderThread(senderThreadDto));
    }

    @PutMapping("/{threadId}/updateSenderThreadPriority")
    public ResponseEntity<UUID> updateSenderThreadPriority(
            @RequestParam UUID id,
            @RequestParam Integer priority) {
        return ResponseEntity.ok(senderThreadService.updateSenderThreadPriority(id, priority));
    }

    @PutMapping("/{threadId}/updateSenderThreadState")
    public ResponseEntity<UUID> updateSenderThreadState(
            @RequestParam UUID id,
            @RequestParam ThreadState threadState) {
        return ResponseEntity.ok(senderThreadService.updateSenderThreadState(id, threadState));
    }

    @DeleteMapping("/deleteSenderThreadById")
    public ResponseEntity<UUID> deleteSenderThreadById(@RequestParam UUID id){
        return ResponseEntity.ok(senderThreadService.deleteSenderThreadById(id));
    }

    @DeleteMapping("/deleteAllSenderThreads")
    public ResponseEntity<Boolean> deleteAllSenderThreads(){
        return ResponseEntity.ok(senderThreadService.deleteAllSenderThreads());
    }
}