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
    public List<SenderThreadDto> createSenderThreadsWithAmount(
            @RequestParam int senderAmount
    )
    {
        return senderThreadService.createSenderThreadsWithAmount(senderAmount);
    }

    @GetMapping("/startSenderThreadsLifeCycle")
    public ResponseEntity<Boolean> startSenderThreadsLifeCycle() {
        return senderThreadService.startSenderThreadsLifeCycle();
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
    public ResponseEntity<String> updateSenderThread(
            @ParameterObject SenderThreadDto senderThreadDto) {
        return ResponseEntity.ok(senderThreadService.updateSenderThread(senderThreadDto).getBody());
    }

    @PutMapping("/{threadId}/updateSenderThreadPriority")
    public ResponseEntity<String> updateSenderThreadPriority(
            @RequestParam UUID id,
            @RequestParam Integer priority) {
        return ResponseEntity.ok(senderThreadService.updateSenderThreadPriority(id, priority).getBody());
    }

    @PutMapping("/{threadId}/updateSenderThreadState")
    public ResponseEntity<String> updateSenderThreadState(
            @RequestParam UUID id,
            @RequestParam ThreadState threadState) {
        return ResponseEntity.ok(senderThreadService.updateSenderThreadState(id, threadState).getBody());
    }

    @DeleteMapping("/deleteSenderThreadById")
    public ResponseEntity<String> deleteSenderThreadById(@RequestParam UUID id){
        return ResponseEntity.ok(senderThreadService.deleteSenderThreadById(id).getBody());
    }

    @DeleteMapping("/deleteAllSenderThreads")
    public ResponseEntity<String> deleteAllSenderThreads(){
        return ResponseEntity.ok(senderThreadService.deleteAllSenderThreads().getBody());
    }
}