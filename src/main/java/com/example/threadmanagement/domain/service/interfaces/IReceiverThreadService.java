package com.example.threadmanagement.domain.service.interfaces;

import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import java.util.List;
import java.util.UUID;

public interface IReceiverThreadService
{
    /**
     * Creates multiple receiver threads based on the specified amount and starts their lifecycle.
     * @param amount number of receiver threads to be created
     * @return list of created receiver thread DTOs
     * @throws IllegalArgumentException if amount is null
     */
    public List<ReceiverThreadDto> createReceiverThreadsWithAmount(Integer amount);

    /**
     * Updates an existing receiver thread with new information and manages its lifecycle based on state changes.
     * @param receiverThreadDto DTO containing updated receiver thread information
     * @return updated receiver thread DTO
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public ReceiverThreadDto updateReceiverThread(ReceiverThreadDto receiverThreadDto);

    /**
     * Updates the state of a specific receiver thread and manages its lifecycle accordingly.
     * @param id ID of the receiver thread to update
     * @param threadState new thread state to be set
     * @return ID of the updated receiver thread
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public UUID updateReceiverThreadState(UUID id, ThreadState threadState);

    /**
     * Updates the priority of a specific receiver thread.
     * @param id ID of the receiver thread to update
     * @param priority new priority value to be set
     * @return ID of the updated receiver thread
     */
    public UUID updateReceiverThreadPriority(UUID id, Integer priority);

    /**
     * Retrieves all active receiver threads (threads in RUNNING state).
     * @return list of active receiver thread DTOs
     */
    public List<ReceiverThreadDto> getActiveReceiverThreads();

    /**
     * Retrieves all passive receiver threads (threads in STOPPED state).
     * @return list of passive receiver thread DTOs
     */
    public List<ReceiverThreadDto> getPassiveReceiverThreads();

    /**
     * Retrieves all receiver threads regardless of their state.
     * @return list of all receiver thread DTOs
     */
    public List<ReceiverThreadDto> getAllReceiverThreads();

    /**
     * Deletes a specific receiver thread by its ID.
     * @param id ID of the receiver thread to delete
     * @return ID of the deleted receiver thread
     */
    public UUID deleteReceiverThreadById(UUID id);

    /**
     * Deletes all receiver threads from the system.
     * @return true if all receiver threads were successfully deleted
     */
    public Boolean deleteAllReceiverThreads();

    /**
     * Starts the lifecycle of all active receiver threads in the system.
     * @return true if the lifecycle start operation was successful
     */
    public Boolean startReceiverThreadsLifeCycle();

}
