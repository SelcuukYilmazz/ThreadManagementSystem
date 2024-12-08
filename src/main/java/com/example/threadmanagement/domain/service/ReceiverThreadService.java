package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.ReceiverThreadRepository;
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
public class ReceiverThreadService {
    private final BlockingQueue<String> sharedQueue;
    private final ExecutorService executorService;
    private final ReceiverThreadRepository receiverThreadRepository;
    private final Map<UUID, Future<?>> runningReceivers = new ConcurrentHashMap<>();

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

    public UUID updateReceiverThreadPriority(UUID id, Integer priority)
    {
        return receiverThreadRepository.updateReceiverThreadPriority(id, priority);
    }

    public List<ReceiverThreadDto> getActiveReceiverThreads() {
        return receiverThreadRepository.getActiveReceiverThreads();
    }

    public List<ReceiverThreadDto> getPassiveReceiverThreads() {
        return receiverThreadRepository.getPassiveReceiverThreads();
    }

    public List<ReceiverThreadDto> getAllReceiverThreads() {
        return receiverThreadRepository.getAllReceiverThreads();
    }

    public UUID deleteReceiverThreadById(UUID id)
    {
        return receiverThreadRepository.deleteReceiverThreadById(id);
    }

    public Boolean deleteAllReceiverThreads()
    {
        return receiverThreadRepository.deleteAllReceiverThreads();
    }

    public Boolean startReceiverThreadsLifeCycle()
    {
        List<ReceiverThreadDto> receiverThreadsList = receiverThreadRepository.getActiveReceiverThreads();
        for(int i = 0; i < receiverThreadsList.size(); i++)
        {
            runReceiverThreadLifeCycle(receiverThreadsList.get(i).getId());
        }
        return true;
    }

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