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
public class ReceiverThreadService {
    private final BlockingQueue<String> sharedQueue;
    private final ExecutorService executorService;
    private final ThreadRepository threadRepository;
    private final Map<UUID, Future<?>> runningReceivers = new ConcurrentHashMap<>();

    public ThreadDto createReceiverThread(int priority) {
        UUID threadId = UUID.randomUUID();
        ThreadDto receiverThread = new ThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, priority);

        Future<?> receiverTask = executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Optional<ThreadDto> thisThread = threadRepository.isThreadAlive(receiverThread.getId());
                if (thisThread.isEmpty() || thisThread.get().getState() == ThreadState.STOPPED)
                {
                    Future<?> task = runningReceivers.get(threadId);
                    task.cancel(true);
                    runningReceivers.remove(threadId);
                }
                Thread.currentThread().setPriority(thisThread.get().getPriority());
                try {
                    String data = sharedQueue.take();
                    log.info("Receiver {} consumed: {}", threadId, data);
                    Thread.sleep(1000); // 1 second frequency
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Receiver {} interrupted", threadId);
                    break;
                }
            }
        });

        runningReceivers.put(threadId, receiverTask);
        threadRepository.createThread(receiverThread);
        return receiverThread;
    }

    public List<ThreadDto> getActiveReceiverThreads() {
        return threadRepository.getActiveReceiverThreads();
    }

    public List<ThreadDto> getPassiveReceiverThreads() {
        return threadRepository.getPassiveReceiverThreads();
    }

    public List<ThreadDto> getAllReceiverThreads() {
        return threadRepository.getAllReceiverThreads();
    }
}