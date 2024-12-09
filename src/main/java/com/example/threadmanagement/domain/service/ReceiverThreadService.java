package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.ReceiverThreadRepository;
import com.example.threadmanagement.domain.service.interfaces.IReceiverThreadService;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiverThreadService implements IReceiverThreadService {
    private final BlockingQueue<String> sharedQueue;
    private final ExecutorService executorService;
    private final ReceiverThreadRepository receiverThreadRepository;
    private final Map<UUID, Future<?>> runningReceivers = new ConcurrentHashMap<>();

    /**
     * Creates multiple receiver threads based on the specified amount and starts their lifecycle.
     * @param amount number of receiver threads to be created
     * @return list of created receiver thread DTOs
     * @throws IllegalArgumentException if amount is null
     */
    public List<ReceiverThreadDto> createReceiverThreadsWithAmount(Integer amount) {
        List<ReceiverThreadDto> receiverThreadDtoList = new ArrayList<ReceiverThreadDto>();

        if(amount == null)
        {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < amount; i++) {
            UUID threadId = UUID.randomUUID();
            receiverThreadDtoList.add(new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY));
        }

        receiverThreadRepository.createReceiverThreadsWithList(receiverThreadDtoList);

        for(int i = 0; i < receiverThreadDtoList.size(); i++)
        {
            UUID threadId = receiverThreadDtoList.get(i).getId();
            runReceiverThreadLifeCycle(threadId);
        }
        return receiverThreadDtoList;
    }

    /**
     * Updates an existing receiver thread with new information and manages its lifecycle based on state changes.
     * @param receiverThreadDto DTO containing updated receiver thread information
     * @return updated receiver thread DTO
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public ReceiverThreadDto updateReceiverThread(ReceiverThreadDto receiverThreadDto)
    {
        Optional<ReceiverThreadDto> currentReceiverThread = receiverThreadRepository.getReceiverThreadById(receiverThreadDto.getId());

        if(currentReceiverThread.isEmpty())
        {
            throw new IllegalArgumentException();
        }
        if(receiverThreadDto.getState() == ThreadState.RUNNING && currentReceiverThread.get().getState() != ThreadState.RUNNING)
        {
            runReceiverThreadLifeCycle(receiverThreadDto.getId());
        }

        return receiverThreadRepository.updateReceiverThread(receiverThreadDto);
    }

    /**
     * Updates the state of a specific receiver thread and manages its lifecycle accordingly.
     * @param id ID of the receiver thread to update
     * @param threadState new thread state to be set
     * @return ID of the updated receiver thread
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public UUID updateReceiverThreadState(UUID id, ThreadState threadState)
    {
        Optional<ReceiverThreadDto> receiverThreadDto = receiverThreadRepository.getReceiverThreadById(id);
        if(receiverThreadDto.isEmpty())
        {
            throw new IllegalArgumentException();
        }
        receiverThreadRepository.updateReceiverThreadState(id, threadState);
        if(receiverThreadDto.get().getState() != ThreadState.RUNNING && threadState == ThreadState.RUNNING)
        {
            runReceiverThreadLifeCycle(id);
        }

        return id;
    }

    /**
     * Updates the priority of a specific receiver thread.
     * @param id ID of the receiver thread to update
     * @param priority new priority value to be set
     * @return ID of the updated receiver thread
     */
    public UUID updateReceiverThreadPriority(UUID id, Integer priority)
    {
        return receiverThreadRepository.updateReceiverThreadPriority(id, priority);
    }

    /**
     * Retrieves all active receiver threads (threads in RUNNING state).
     * @return list of active receiver thread DTOs
     */
    public List<ReceiverThreadDto> getActiveReceiverThreads() {
        return receiverThreadRepository.getActiveReceiverThreads();
    }

    /**
     * Retrieves all passive receiver threads (threads in STOPPED state).
     * @return list of passive receiver thread DTOs
     */
    public List<ReceiverThreadDto> getPassiveReceiverThreads() {
        return receiverThreadRepository.getPassiveReceiverThreads();
    }

    /**
     * Retrieves all receiver threads regardless of their state.
     * @return list of all receiver thread DTOs
     */
    public List<ReceiverThreadDto> getAllReceiverThreads() {
        return receiverThreadRepository.getAllReceiverThreads();
    }

    /**
     * Deletes a specific receiver thread by its ID.
     * @param id ID of the receiver thread to delete
     * @return ID of the deleted receiver thread
     */
    public UUID deleteReceiverThreadById(UUID id)
    {
        return receiverThreadRepository.deleteReceiverThreadById(id);
    }

    /**
     * Deletes all receiver threads from the system.
     * @return true if all receiver threads were successfully deleted
     */
    public Boolean deleteAllReceiverThreads()
    {
        return receiverThreadRepository.deleteAllReceiverThreads();
    }

    /**
     * Starts the lifecycle of all active receiver threads in the system.
     * @return true if the lifecycle start operation was successful
     */
    public Boolean startReceiverThreadsLifeCycle()
    {
        List<ReceiverThreadDto> receiverThreadsList = receiverThreadRepository.getActiveReceiverThreads();
        for(int i = 0; i < receiverThreadsList.size(); i++)
        {
            runReceiverThreadLifeCycle(receiverThreadsList.get(i).getId());
        }
        return true;
    }

    /**
     * Initiates and manages the lifecycle of a specific receiver thread.
     * Creates a task that continuously polls data from the shared queue until interrupted.
     * Monitors thread state and priority changes, and handles thread termination.
     * The receiver checks for new data every second and logs consumed messages.
     * @param receiverThreadId ID of the receiver thread to run
     */
    private void runReceiverThreadLifeCycle(UUID receiverThreadId) {
        Future<?> receiverTask = executorService.submit(() -> {
            long lastProcessTime = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                Optional<ReceiverThreadDto> thisThread = receiverThreadRepository.getReceiverThreadById(receiverThreadId);
                if (thisThread.isEmpty() || thisThread.get().getState() == ThreadState.STOPPED) {
                    Future<?> task = runningReceivers.get(receiverThreadId);
                    task.cancel(true);
                    runningReceivers.remove(receiverThreadId);
                    break;
                }
                Thread.currentThread().setPriority(thisThread.get().getPriority());
                try {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProcessTime >= 1000) {
                        String data = sharedQueue.poll();
                        if (data != null) {
                            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                            log.info("Receiver {} consumed: {} at {}", receiverThreadId, data, timestamp);
                            lastProcessTime = currentTime;
                        }
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Receiver {} interrupted", receiverThreadId);
                    break;
                }
            }
        });
        runningReceivers.put(receiverThreadId, receiverTask);
    }
}