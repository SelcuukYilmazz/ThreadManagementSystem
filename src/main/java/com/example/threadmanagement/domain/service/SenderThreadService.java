package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.SenderThreadRepository;
import com.example.threadmanagement.domain.service.interfaces.ISenderThreadService;
import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenderThreadService  implements ISenderThreadService {
    private final BlockingQueue<String> sharedQueue;
    private final ExecutorService executorService;
    private final SenderThreadRepository senderThreadRepository;
    private final Map<UUID, Future<?>> runningSenders = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageQueueService messageQueueService;

    /**
     * Creates multiple sender threads based on the specified amount and starts their lifecycle.
     * @param amount number of sender threads to be created
     * @return list of created sender thread DTOs
     * @throws IllegalArgumentException if amount is null
     */
    public List<SenderThreadDto> createSenderThreadsWithAmount(Integer amount) {
        List<SenderThreadDto> senderThreadDtoList = new ArrayList<SenderThreadDto>();

        if(amount == null)
        {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < amount; i++) {
            UUID threadId = UUID.randomUUID();
            senderThreadDtoList.add(new SenderThreadDto(threadId, ThreadType.SENDER, ThreadState.RUNNING, Thread.NORM_PRIORITY));
        }

        senderThreadRepository.createSenderThreadsWithList(senderThreadDtoList);

        for(int i = 0; i < senderThreadDtoList.size(); i++)
        {
            UUID threadId = senderThreadDtoList.get(i).getId();
            runSenderThreadLifeCycle(threadId);
        }

        // This will trigger handleThreadRefresh
        messagingTemplate.convertAndSend("/topic/senderThreads", getAllSenderThreads());

        return senderThreadDtoList;
    }

    /**
     * Updates an existing sender thread with new information and manages its lifecycle based on state changes.
     * @param senderThreadDto DTO containing updated sender thread information
     * @return updated sender thread DTO
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public SenderThreadDto updateSenderThread(SenderThreadDto senderThreadDto)
    {
        Optional<SenderThreadDto> currentSenderThread = senderThreadRepository.getSenderThreadById(senderThreadDto.getId());
        if(currentSenderThread.isEmpty())
        {
            throw new IllegalArgumentException();
        }

        SenderThreadDto result = senderThreadRepository.updateSenderThread(senderThreadDto);

        if(senderThreadDto.getState() == ThreadState.RUNNING && currentSenderThread.get().getState() != ThreadState.RUNNING)
        {
            runSenderThreadLifeCycle(senderThreadDto.getId());
        }

        messagingTemplate.convertAndSend("/topic/senderThreads", getAllSenderThreads());

        return result;
    }

    /**
     * Updates the state of a specific sender thread and manages its lifecycle accordingly.
     * @param id ID of the sender thread to update
     * @param threadState new thread state to be set
     * @return ID of the updated sender thread
     * @throws IllegalArgumentException if the thread with specified ID doesn't exist
     */
    public UUID updateSenderThreadState(UUID id, ThreadState threadState)
    {
        Optional<SenderThreadDto> senderThreadDto = senderThreadRepository.getSenderThreadById(id);
        if(senderThreadDto.isEmpty())
        {
            throw new IllegalArgumentException();
        }

        UUID result = senderThreadRepository.updateSenderThreadState(id, threadState);

        if(senderThreadDto.get().getState() != ThreadState.RUNNING && threadState == ThreadState.RUNNING)
        {
            runSenderThreadLifeCycle(id);
        }

        messagingTemplate.convertAndSend("/topic/senderThreads", getAllSenderThreads());

        return result;
    }

    /**
     * Updates the priority of a specific sender thread.
     * @param id ID of the sender thread to update
     * @param priority new priority value to be set
     * @return ID of the updated sender thread
     */
    public UUID updateSenderThreadPriority(UUID id, Integer priority)
    {
        UUID updatedSenderThreadId = senderThreadRepository.updateSenderThreadPriority(id, priority);
        messagingTemplate.convertAndSend("/topic/senderThreads", getAllSenderThreads());
        return updatedSenderThreadId;
    }

    /**
     * Retrieves all active sender threads (threads in RUNNING state).
     * @return list of active sender thread DTOs
     */
    public List<SenderThreadDto> getActiveSenderThreads()
    {
        return senderThreadRepository.getActiveSenderThreads();
    }

    /**
     * Retrieves all passive sender threads (threads in STOPPED state).
     * @return list of passive sender thread DTOs
     */
    public List<SenderThreadDto> getPassiveSenderThreads() {
        return senderThreadRepository.getPassiveSenderThreads();
    }

    /**
     * Retrieves all sender threads regardless of their state.
     * @return list of all sender thread DTOs
     */
    public List<SenderThreadDto> getAllSenderThreads() {
        return senderThreadRepository.getAllSenderThreads();
    }

    /**
     * Deletes a specific sender thread by its ID.
     * @param id ID of the sender thread to delete
     * @return ID of the deleted sender thread
     */
    public UUID deleteSenderThreadById(UUID id)
    {
        UUID deletedSenderThreadId = senderThreadRepository.deleteSenderThreadById(id);
        messagingTemplate.convertAndSend("/topic/senderThreads", getAllSenderThreads());

        return deletedSenderThreadId;
    }

    /**
     * Deletes all sender threads from the system.
     * @return true if all sender threads were successfully deleted
     */
    public Boolean deleteAllSenderThreads()
    {
        Boolean bulkDeletionResult = senderThreadRepository.deleteAllSenderThreads();
        messagingTemplate.convertAndSend("/topic/senderThreads", getAllSenderThreads());

        return bulkDeletionResult;
    }

    /**
     * Starts the lifecycle of all active sender threads in the system.
     * @return true if the lifecycle start operation was successful
     */
    public Boolean startSenderThreadsLifeCycle()
    {
        List<SenderThreadDto> senderThreadsList = senderThreadRepository.getActiveSenderThreads();
        for(int i = 0; i < senderThreadsList.size(); i++)
        {
            runSenderThreadLifeCycle(senderThreadsList.get(i).getId());
        }
        messagingTemplate.convertAndSend("/topic/senderThreads", getAllSenderThreads());

        return true;
    }

    /**
     * Initiates and manages the lifecycle of a specific sender thread.
     * Creates a task that continuously sends data to the shared queue until interrupted.
     * Monitors thread state and priority changes, and handles thread termination.
     * @param senderThreadId ID of the sender thread to run
     */
    private void runSenderThreadLifeCycle(UUID senderThreadId) {
        Future<?> senderTask = executorService.submit(() -> {
            long lastProcessTime = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProcessTime >= 1000) {
                        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                        String data = "Data from sender " + senderThreadId + " at " + timestamp;
                        sharedQueue.put(data);
                        messageQueueService.broadcastQueueUpdate();
                        log.info("Sender {} added: {}", senderThreadId, data);
                        lastProcessTime = currentTime;
                    }

                    Optional<SenderThreadDto> thisThread = senderThreadRepository.getSenderThreadById(senderThreadId);
                    if (thisThread.isEmpty() || thisThread.get().getState() == ThreadState.STOPPED) {
                        Future<?> task = runningSenders.get(senderThreadId);
                        task.cancel(true);
                        runningSenders.remove(senderThreadId);
                        break;
                    } else {
                        Thread.currentThread().setPriority(thisThread.get().getPriority());
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Sender {} interrupted", senderThreadId);
                    break;
                }
            }
        });

        runningSenders.put(senderThreadId, senderTask);
    }

}