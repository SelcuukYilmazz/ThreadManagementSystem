package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.interfaces.IReceiverThreadService;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/receiverThreads")
@RequiredArgsConstructor
public class ReceiverThreadController {
    private final IReceiverThreadService iReceiverThreadService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Creates multiple receiver threads based on the specified amount.
     * @param receiverAmount number of receiver threads to create
     * @return list of created receiver thread DTOs
     * @throws IllegalArgumentException if receiverAmount is null
     */
    @PostMapping("/createReceiverThreadsWithAmount")
    public ResponseEntity<List<ReceiverThreadDto>> createReceiverThreadsWithAmount(
            @RequestParam int receiverAmount
    ) {
        return ResponseEntity.ok(iReceiverThreadService.createReceiverThreadsWithAmount(receiverAmount));
    }

    /**
     * Initiates the lifecycle for all active receiver threads in the system.
     * @return ResponseEntity containing true if lifecycle start was successful
     */
    @GetMapping("/startReceiverThreadsLifeCycle")
    public ResponseEntity<Boolean> startReceiverThreadsLifeCycle() {
        return ResponseEntity.ok(iReceiverThreadService.startReceiverThreadsLifeCycle());
    }

    /**
     * Retrieves all receiver threads currently in RUNNING state.
     * @return ResponseEntity containing list of active receiver threads
     */
    @GetMapping("/getActiveReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getActiveReceiverThreads() {
        return ResponseEntity.ok(iReceiverThreadService.getActiveReceiverThreads());
    }

    /**
     * Retrieves all receiver threads regardless of their state.
     * @return ResponseEntity containing list of all receiver threads
     */
    @GetMapping("/getAllReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getAllReceiverThreads() {
        return ResponseEntity.ok(iReceiverThreadService.getAllReceiverThreads());
    }

    /**
     * Retrieves all receiver threads currently in STOPPED state.
     * @return ResponseEntity containing list of passive receiver threads
     */
    @GetMapping("/getPassiveReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getPassiveReceiverThreads() {
        return ResponseEntity.ok(iReceiverThreadService.getPassiveReceiverThreads());
    }

    /**
     * Updates the configuration of a specific receiver thread.
     * @param threadDto updated thread configuration containing new state, priority, or type
     * @return ResponseEntity containing updated receiver thread DTO
     * @throws IllegalArgumentException if thread with specified ID doesn't exist
     */
    @PutMapping("/{threadId}/updateReceiverThread")
    public ResponseEntity<ReceiverThreadDto> updateReceiverThread(
            @ParameterObject ReceiverThreadDto threadDto) {
        return ResponseEntity.ok(iReceiverThreadService.updateReceiverThread(threadDto));
    }

    /**
     * Updates the priority of a specific receiver thread.
     * @param id ID of the receiver thread to update
     * @param priority new priority value to set
     * @return ResponseEntity containing ID of updated thread
     * @throws IllegalArgumentException if thread not found or priority is null
     */
    @PutMapping("/{threadId}/updateReceiverThreadPriority")
    public ResponseEntity<UUID> updateReceiverThreadPriority(
            @RequestParam UUID id,
            @RequestParam Integer priority
    ) {
        return ResponseEntity.ok(iReceiverThreadService.updateReceiverThreadPriority(id, priority));
    }

    /**
     * Updates the state of a specific receiver thread.
     * @param id ID of the receiver thread to update
     * @param threadState new thread state to set
     * @return ResponseEntity containing ID of updated thread
     * @throws IllegalArgumentException if thread not found or state is null
     */
    @PutMapping("/{threadId}/updateReceiverThreadState")
    public ResponseEntity<UUID> updateReceiverThreadState(
            @RequestParam UUID id,
            @RequestParam ThreadState threadState
    ) {
        return ResponseEntity.ok(iReceiverThreadService.updateReceiverThreadState(id, threadState));
    }

    /**
     * Deletes a receiver thread by its ID.
     * @param id ID of the receiver thread to delete
     * @return ResponseEntity containing ID of deleted thread
     * @throws IllegalArgumentException if thread with specified ID doesn't exist
     */
    @DeleteMapping("/deleteReceiverThreadById")
    public ResponseEntity<UUID> deleteReceiverThreadById(@RequestParam UUID id) {
        return ResponseEntity.ok(iReceiverThreadService.deleteReceiverThreadById(id));
    }

    /**
     * Deletes all receiver threads from the system.
     * @return ResponseEntity containing true if all threads were successfully deleted
     */
    @DeleteMapping("/deleteAllReceiverThreads")
    public ResponseEntity<Boolean> deleteAllReceiverThreads() {
        return ResponseEntity.ok(iReceiverThreadService.deleteAllReceiverThreads());
    }

    @MessageMapping("/sendReceiverThreads")
    @SendTo("/topic/messages")
    public void sendReceiverThreads() {
        messagingTemplate.convertAndSend("/topic/receiverThreads", iReceiverThreadService.getAllReceiverThreads());
    }
}
