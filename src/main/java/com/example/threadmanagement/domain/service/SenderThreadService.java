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

    public void updateThreadState(UUID threadId, ThreadState newState) {
        Future<?> task = runningSenders.get(threadId);
        if (task != null) {
            if (newState == ThreadState.STOPPED) {
                task.cancel(true);
                runningSenders.remove(threadId);
            }
            ThreadDto threadDto = threadRepository.getThreadById(threadId);
            threadDto.setState(newState);
            threadRepository.updateThread(threadDto);
        }
    }

    public void updateThreadPriority(UUID threadId, int priority) {
        ThreadDto threadDto = threadRepository.getThreadById(threadId);
        threadDto.setPriority(priority);
        threadRepository.updateThread(threadDto);
    }

    public List<ThreadDto> getActiveSenderThreads() {
        return threadRepository.getAllThreads().getBody().stream()
                .filter(thread -> thread.getType() == ThreadType.SENDER)
                .collect(Collectors.toList());
    }
}