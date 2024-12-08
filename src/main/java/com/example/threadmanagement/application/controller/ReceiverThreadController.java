package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.ReceiverThreadService;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/receiverThreads") // Base URL for all endpoints in this controller
@RequiredArgsConstructor // Automatically generates a constructor for all final fields
public class ReceiverThreadController {

    private final ReceiverThreadService receiverThreadService; // Service layer to handle receiver thread logic

    /**
     * Creates multiple receiver threads based on the specified amount.
     * @param receiverAmount The number of receiver threads to create.
     * @return A list of the created receiver thread details.
     */
    @PostMapping("/createReceiverThreadsWithAmount")
    public List<ReceiverThreadDto> createReceiverThreadsWithAmount(
            @RequestParam int receiverAmount // Number of threads to create, provided as a query parameter
    ) {
        return receiverThreadService.createReceiverThreadsWithAmount(receiverAmount);
    }

    /**
     * Starts the lifecycle for all active receiver threads.
     * @return A boolean indicating whether the lifecycle was successfully started.
     */
    @GetMapping("/startReceiverThreadsLifeCycle")
    public ResponseEntity<Boolean> startSenderThreadsLifeCycle() {
        return ResponseEntity.ok(receiverThreadService.startReceiverThreadsLifeCycle());
    }

    /**
     * Retrieves a list of all active receiver threads.
     * @return A list of active receiver threads.
     */
    @GetMapping("/getActiveReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getActiveReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getActiveReceiverThreads());
    }

    /**
     * Retrieves a list of all receiver threads (both active and passive).
     * @return A list of all receiver threads.
     */
    @GetMapping("/getAllReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getAllReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getAllReceiverThreads());
    }

    /**
     * Retrieves a list of all passive receiver threads.
     * @return A list of passive receiver threads.
     */
    @GetMapping("/getPassiveReceiverThreads")
    public ResponseEntity<List<ReceiverThreadDto>> getPassiveReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.getPassiveReceiverThreads());
    }

    /**
     * Updates the details of a specific receiver thread.
     * @param threadDto The thread details to update.
     * @return The updated receiver thread.
     */
    @PutMapping("/{threadId}/updateReceiverThread")
    public ResponseEntity<ReceiverThreadDto> updateReceiverThread(
            @ParameterObject ReceiverThreadDto threadDto) {
        return ResponseEntity.ok(receiverThreadService.updateReceiverThread(threadDto));
    }

    /**
     * Updates the priority of a specific receiver thread.
     * @param id The ID of the receiver thread.
     * @param priority The new priority to set.
     * @return The ID of the updated thread.
     */
    @PutMapping("/{threadId}/updateReceiverThreadPriority")
    public ResponseEntity<UUID> updateReceiverThreadPriority(
            @RequestParam UUID id, // ID of the thread to update
            @RequestParam Integer priority // New priority level
    ) {
        return ResponseEntity.ok(receiverThreadService.updateReceiverThreadPriority(id, priority));
    }

    /**
     * Updates the state of a specific receiver thread.
     * @param id The ID of the receiver thread.
     * @param threadState The new state to set (e.g., RUNNING, STOPPED).
     * @return The ID of the updated thread.
     */
    @PutMapping("/{threadId}/updateReceiverThreadState")
    public ResponseEntity<UUID> updateReceiverThreadState(
            @RequestParam UUID id, // ID of the thread to update
            @RequestParam ThreadState threadState // New state for the thread
    ) {
        return ResponseEntity.ok(receiverThreadService.updateReceiverThreadState(id, threadState));
    }

    /**
     * Deletes a specific receiver thread by its ID.
     * @param id The ID of the receiver thread to delete.
     * @return The ID of the deleted thread.
     */
    @DeleteMapping("/deleteReceiverThreadById")
    public ResponseEntity<UUID> deleteReceiverThreadById(@RequestParam UUID id) {
        return ResponseEntity.ok(receiverThreadService.deleteReceiverThreadById(id));
    }

    /**
     * Deletes all receiver threads in the system.
     * @return A boolean indicating whether all threads were successfully deleted.
     */
    @DeleteMapping("/deleteAllReceiverThreads")
    public ResponseEntity<Boolean> deleteAllReceiverThreads() {
        return ResponseEntity.ok(receiverThreadService.deleteAllReceiverThreads());
    }
}
