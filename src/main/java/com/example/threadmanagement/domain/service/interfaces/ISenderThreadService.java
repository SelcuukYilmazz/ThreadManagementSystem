package com.example.threadmanagement.domain.service.interfaces;

import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import java.util.List;
import java.util.UUID;

public interface ISenderThreadService
{
    /**
     * Creates multiple sender threads based on the specified amount and starts their lifecycle.
     * @param amount number of sender threads to be created
     * @return list of created sender thread DTOs
     * @throws IllegalArgumentException if amount is null
     */
    public List<SenderThreadDto> createSenderThreadsWithAmount(Integer amount);

    /**
     * Updates an existing sender thread with new information and manages its lifecycle based on state changes.
     * @param senderThreadDto DTO containing updated sender thread information
     * @return updated sender thread DTO
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public SenderThreadDto updateSenderThread(SenderThreadDto senderThreadDto);

    /**
     * Updates the state of a specific sender thread and manages its lifecycle accordingly.
     * @param id ID of the sender thread to update
     * @param threadState new thread state to be set
     * @return ID of the updated sender thread
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public UUID updateSenderThreadState(UUID id, ThreadState threadState);

    /**
     * Updates the priority of a specific sender thread.
     * @param id ID of the sender thread to update
     * @param priority new priority value to be set
     * @return ID of the updated sender thread
     */
    public UUID updateSenderThreadPriority(UUID id, Integer priority);

    /**
     * Retrieves all active sender threads (threads in RUNNING state).
     * @return list of active sender thread DTOs
     */
    public List<SenderThreadDto> getActiveSenderThreads();

    /**
     * Retrieves all passive sender threads (threads in STOPPED state).
     * @return list of passive sender thread DTOs
     */
    public List<SenderThreadDto> getPassiveSenderThreads();

    /**
     * Retrieves all sender threads regardless of their state.
     * @return list of all sender thread DTOs
     */
    public List<SenderThreadDto> getAllSenderThreads();

    /**
     * Deletes a specific sender thread by its ID.
     * @param id ID of the sender thread to delete
     * @return ID of the deleted sender thread
     */
    public UUID deleteSenderThreadById(UUID id);

    /**
     * Deletes all sender threads from the system.
     * @return true if all sender threads were successfully deleted
     */
    public Boolean deleteAllSenderThreads();

    /**
     * Starts the lifecycle of all active sender threads in the system.
     * @return true if the lifecycle start operation was successful
     */
    public Boolean startSenderThreadsLifeCycle();

}
