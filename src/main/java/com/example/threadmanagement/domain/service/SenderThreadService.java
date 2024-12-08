package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.ThreadRepository;
import com.example.threadmanagement.model.dto.ThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenderThreadService {
    private final BlockingQueue<String> sharedQueue;
    private final ExecutorService executorService;
    private final ThreadRepository threadRepository;
    private final Map<UUID, Future<?>> runningSenders = new ConcurrentHashMap<>();

    public ThreadDto createSenderThread(int priority) {
        UUID threadId = UUID.randomUUID();
        ThreadDto senderThread = new ThreadDto(threadId, ThreadType.SENDER, ThreadState.RUNNING, priority);

        Future<?> senderTask = executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Optional<ThreadDto> thisThread = threadRepository.isThreadAlive(senderThread.getId());
                if (thisThread.isEmpty() || thisThread.get().getState() == ThreadState.STOPPED)
                {
                    Future<?> task = runningSenders.get(threadId);
                    task.cancel(true);
                    runningSenders.remove(threadId);
                }
                Thread.currentThread().setPriority(thisThread.get().getPriority());
                try {
                    String data = "Data from sender " + threadId + " at " + System.currentTimeMillis();
                    sharedQueue.put(data);
                    log.info("Sender {} added: {}", threadId, data);
                    Thread.sleep(1000); // 1 second frequency
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Sender {} interrupted", threadId);
                    break;
                }
            }
        });

        runningSenders.put(threadId, senderTask);
        threadRepository.createThread(senderThread);
        return senderThread;
    }

    public List<ThreadDto> getActiveSenderThreads()
    {
        return threadRepository.getActiveSenderThreads();
    }

    public List<ThreadDto> getPassiveSenderThreads() {
        return threadRepository.getPassiveSenderThreads();
    }

    public List<ThreadDto> getAllSenderThreads() {
        return threadRepository.getAllSenderThreads();
    }
}