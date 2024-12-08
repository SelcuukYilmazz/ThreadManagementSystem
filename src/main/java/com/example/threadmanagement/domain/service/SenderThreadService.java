package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.SenderThreadRepository;
import com.example.threadmanagement.model.dto.SenderThreadDto;
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
public class SenderThreadService {
    private final BlockingQueue<String> sharedQueue;
    private final ExecutorService executorService;
    private final SenderThreadRepository senderThreadRepository;
    private final Map<UUID, Future<?>> runningSenders = new ConcurrentHashMap<>();

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
        return senderThreadDtoList;
    }

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

        return result;
    }

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

        return result;
    }

    public UUID updateSenderThreadPriority(UUID id, Integer priority)
    {
        return senderThreadRepository.updateSenderThreadPriority(id, priority);
    }

    public List<SenderThreadDto> getActiveSenderThreads()
    {
        return senderThreadRepository.getActiveSenderThreads();
    }

    public List<SenderThreadDto> getPassiveSenderThreads() {
        return senderThreadRepository.getPassiveSenderThreads();
    }

    public List<SenderThreadDto> getAllSenderThreads() {
        return senderThreadRepository.getAllSenderThreads();
    }

    public UUID deleteSenderThreadById(UUID id)
    {
        return senderThreadRepository.deleteSenderThreadById(id);
    }

    public Boolean deleteAllSenderThreads()
    {
        return senderThreadRepository.deleteAllSenderThreads();
    }

    public Boolean startSenderThreadsLifeCycle()
    {
        List<SenderThreadDto> senderThreadsList = senderThreadRepository.getActiveSenderThreads();
        for(int i = 0; i < senderThreadsList.size(); i++)
        {
            runSenderThreadLifeCycle(senderThreadsList.get(i).getId());
        }
        return true;
    }

    private void runSenderThreadLifeCycle(UUID senderThreadId) {
        Future<?> senderTask = executorService.submit(() -> {
            long lastProcessTime = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProcessTime >= 1000) {  // Check if 1 second has passed
                        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                        String data = "Data from sender " + senderThreadId + " at " + timestamp;
                        sharedQueue.put(data);
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