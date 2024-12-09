package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.interfaces.ISenderThreadService;
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
    private final ISenderThreadService iSenderThreadService; // Service layer for managing sender threads

    /**
     * Creates multiple sender threads based on the specified amount.
     * @param senderAmount number of sender threads to create
     * @return ResponseEntity containing list of created sender thread DTOs
     * @throws IllegalArgumentException if senderAmount is null
     */
    @PostMapping("/createSenderThreadsWithAmount")
    public ResponseEntity<List<SenderThreadDto>> createSenderThreadsWithAmount(
            @RequestParam int senderAmount // The number of threads to create, passed as a query parameter
    ) {
        return ResponseEntity.ok(iSenderThreadService.createSenderThreadsWithAmount(senderAmount));
    }

    /**
     * Initiates the lifecycle for all active sender threads in the system.
     * @return ResponseEntity containing true if lifecycle start was successful
     */
    @GetMapping("/startSenderThreadsLifeCycle")
    public ResponseEntity<Boolean> startSenderThreadsLifeCycle() {
        return ResponseEntity.ok(iSenderThreadService.startSenderThreadsLifeCycle());
    }

    /**
     * Retrieves all sender threads currently in RUNNING state.
     * @return ResponseEntity containing list of active sender threads
     */
    @GetMapping("/getActiveSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getActiveSenderThreads() {
        return ResponseEntity.ok(iSenderThreadService.getActiveSenderThreads());
    }

    /**
     * Retrieves all sender threads regardless of their state.
     * @return ResponseEntity containing list of all sender threads
     */
    @GetMapping("/getAllSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getAllSenderThreads() {
        return ResponseEntity.ok(iSenderThreadService.getAllSenderThreads());
    }

    /**
     * Retrieves all sender threads currently in STOPPED state.
     * @return ResponseEntity containing list of passive sender threads
     */
    @GetMapping("/getPassiveSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getPassiveSenderThreads() {
        return ResponseEntity.ok(iSenderThreadService.getPassiveSenderThreads());
    }

    /**
     * Updates the configuration of a specific sender thread.
     * @param senderThreadDto updated thread configuration containing new state, priority, or type
     * @return ResponseEntity containing updated sender thread DTO
     * @throws IllegalArgumentException if thread with specified ID doesn't exist
     */
    @PutMapping("/{threadId}/updateSenderThread")
    public ResponseEntity<SenderThreadDto> updateSenderThread(
            @ParameterObject SenderThreadDto senderThreadDto // Thread details to update
    ) {
        return ResponseEntity.ok(iSenderThreadService.updateSenderThread(senderThreadDto));
    }

    /**
     * Updates the priority of a specific sender thread.
     * @param id ID of the sender thread to update
     * @param priority new priority value to set
     * @return ResponseEntity containing ID of updated thread
     * @throws IllegalArgumentException if thread not found or priority is null
     */
    @PutMapping("/{threadId}/updateSenderThreadPriority")
    public ResponseEntity<UUID> updateSenderThreadPriority(
            @RequestParam UUID id, // ID of the thread to update
            @RequestParam Integer priority // New priority level
    ) {
        return ResponseEntity.ok(iSenderThreadService.updateSenderThreadPriority(id, priority));
    }

    /**
     * Updates the state of a specific sender thread.
     * @param id ID of the sender thread to update
     * @param threadState new thread state to set
     * @return ResponseEntity containing ID of updated thread
     * @throws IllegalArgumentException if thread not found or state is null
     */
    @PutMapping("/{threadId}/updateSenderThreadState")
    public ResponseEntity<UUID> updateSenderThreadState(
            @RequestParam UUID id, // ID of the thread to update
            @RequestParam ThreadState threadState // New state for the thread
    ) {
        return ResponseEntity.ok(iSenderThreadService.updateSenderThreadState(id, threadState));
    }

    /**
     * Deletes a sender thread by its ID.
     * @param id ID of the sender thread to delete
     * @return ResponseEntity containing ID of deleted thread
     * @throws IllegalArgumentException if thread with specified ID doesn't exist
     */
    @DeleteMapping("/deleteSenderThreadById")
    public ResponseEntity<UUID> deleteSenderThreadById(@RequestParam UUID id) {
        return ResponseEntity.ok(iSenderThreadService.deleteSenderThreadById(id));
    }

    /**
     * Deletes all sender threads from the system.
     * @return ResponseEntity containing true if all threads were successfully deleted
     */
    @DeleteMapping("/deleteAllSenderThreads")
    public ResponseEntity<Boolean> deleteAllSenderThreads() {
        return ResponseEntity.ok(iSenderThreadService.deleteAllSenderThreads());
    }
}
