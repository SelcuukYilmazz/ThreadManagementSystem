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
@RequestMapping("/senderThreads") // Base URL for all sender thread-related endpoints
@RequiredArgsConstructor // Automatically generates a constructor for final fields
public class SenderThreadController {

    private final SenderThreadService senderThreadService; // Service layer for managing sender threads

    /**
     * Creates multiple sender threads based on the provided amount.
     * @param senderAmount The number of sender threads to create.
     * @return A list of the created sender threads.
     */
    @PostMapping("/createSenderThreadsWithAmount")
    public ResponseEntity<List<SenderThreadDto>> createSenderThreadsWithAmount(
            @RequestParam int senderAmount // The number of threads to create, passed as a query parameter
    ) {
        return ResponseEntity.ok(senderThreadService.createSenderThreadsWithAmount(senderAmount));
    }

    /**
     * Starts the lifecycle for all active sender threads.
     * @return A boolean indicating whether the lifecycle was successfully started.
     */
    @GetMapping("/startSenderThreadsLifeCycle")
    public ResponseEntity<Boolean> startSenderThreadsLifeCycle() {
        return ResponseEntity.ok(senderThreadService.startSenderThreadsLifeCycle());
    }

    /**
     * Retrieves a list of all active sender threads.
     * @return A list of active sender threads.
     */
    @GetMapping("/getActiveSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getActiveSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getActiveSenderThreads());
    }

    /**
     * Retrieves a list of all sender threads (both active and passive).
     * @return A list of all sender threads.
     */
    @GetMapping("/getAllSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getAllSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getAllSenderThreads());
    }

    /**
     * Retrieves a list of all passive sender threads.
     * @return A list of passive sender threads.
     */
    @GetMapping("/getPassiveSenderThreads")
    public ResponseEntity<List<SenderThreadDto>> getPassiveSenderThreads() {
        return ResponseEntity.ok(senderThreadService.getPassiveSenderThreads());
    }

    /**
     * Updates the details of a specific sender thread.
     * @param senderThreadDto The sender thread details to update.
     * @return The updated sender thread information.
     */
    @PutMapping("/{threadId}/updateSenderThread")
    public ResponseEntity<SenderThreadDto> updateSenderThread(
            @ParameterObject SenderThreadDto senderThreadDto // Thread details to update
    ) {
        return ResponseEntity.ok(senderThreadService.updateSenderThread(senderThreadDto));
    }

    /**
     * Updates the priority of a specific sender thread.
     * @param id The ID of the sender thread to update.
     * @param priority The new priority level.
     * @return The ID of the updated sender thread.
     */
    @PutMapping("/{threadId}/updateSenderThreadPriority")
    public ResponseEntity<UUID> updateSenderThreadPriority(
            @RequestParam UUID id, // ID of the thread to update
            @RequestParam Integer priority // New priority level
    ) {
        return ResponseEntity.ok(senderThreadService.updateSenderThreadPriority(id, priority));
    }

    /**
     * Updates the state of a specific sender thread.
     * @param id The ID of the sender thread to update.
     * @param threadState The new state to set (e.g., RUNNING, STOPPED).
     * @return The ID of the updated sender thread.
     */
    @PutMapping("/{threadId}/updateSenderThreadState")
    public ResponseEntity<UUID> updateSenderThreadState(
            @RequestParam UUID id, // ID of the thread to update
            @RequestParam ThreadState threadState // New state for the thread
    ) {
        return ResponseEntity.ok(senderThreadService.updateSenderThreadState(id, threadState));
    }

    /**
     * Deletes a specific sender thread by its ID.
     * @param id The ID of the sender thread to delete.
     * @return The ID of the deleted thread.
     */
    @DeleteMapping("/deleteSenderThreadById")
    public ResponseEntity<UUID> deleteSenderThreadById(@RequestParam UUID id) {
        return ResponseEntity.ok(senderThreadService.deleteSenderThreadById(id));
    }

    /**
     * Deletes all sender threads in the system.
     * @return A boolean indicating whether all threads were successfully deleted.
     */
    @DeleteMapping("/deleteAllSenderThreads")
    public ResponseEntity<Boolean> deleteAllSenderThreads() {
        return ResponseEntity.ok(senderThreadService.deleteAllSenderThreads());
    }
}
