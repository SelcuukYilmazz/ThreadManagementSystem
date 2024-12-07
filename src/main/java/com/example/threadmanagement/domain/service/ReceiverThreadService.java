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

    public void updateThreadState(UUID threadId, ThreadState newState) {
        Future<?> task = runningReceivers.get(threadId);
        if (task != null) {
            if (newState == ThreadState.STOPPED) {
                task.cancel(true);
                runningReceivers.remove(threadId);
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

    public List<ThreadDto> getActiveReceiverThreads() {
        return threadRepository.getAllThreads().getBody().stream()
                .filter(thread -> thread.getType() == ThreadType.RECEIVER)
                .collect(Collectors.toList());
    }
}