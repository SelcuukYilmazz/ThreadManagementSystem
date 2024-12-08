package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.ThreadRepository;
import com.example.threadmanagement.model.dto.ThreadDto;
import com.example.threadmanagement.model.entity.ThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThreadManagerService {
    private final ExecutorService executorService;
    private final ThreadRepository threadRepository;
    private final SenderThreadService senderThreadService;
    private final  ReceiverThreadService receiverThreadService;

    public ResponseEntity<ThreadDto> createThread(ThreadDto threadDto)
    {
        threadDto.setId(UUID.randomUUID());
        return threadRepository.createThread(threadDto);
    }

    public boolean createSenderReceiverThreadsWithAmount(int senderAmount, int receiverAmount)
    {
        List<ThreadDto> threadDtoList = new ArrayList<ThreadDto>();

        for (int i = 0; i < senderAmount; i++) {
            ThreadDto senderThread = senderThreadService.createSenderThread(i + 1);
            threadDtoList.add(senderThread);
        }

        for (int i = 0; i < receiverAmount; i++) {
            ThreadDto receiverThread = receiverThreadService.createReceiverThread(senderAmount + i + 1);
            threadDtoList.add(receiverThread);
        }

        return threadRepository.createSenderReceiverThreadsWithAmount(threadDtoList);
    }

    public ThreadDto getThreadById(UUID id)
    {
        return threadRepository.getThreadById(id);
    }

    public ResponseEntity<String> updateThread(ThreadDto threadDto)
    {
        ThreadDto currentThread = threadRepository.getThreadById(threadDto.getId());

        if(threadDto.getType() == ThreadType.SENDER)
        {
            ThreadDto senderThread = senderThreadService.createSenderThread(0);
        }
        else if(threadDto.getType() == ThreadType.RECEIVER)
        {
            ThreadDto senderThread = receiverThreadService.createReceiverThread(0);
        }
        return threadRepository.updateThread(threadDto);
    }

    public ResponseEntity<String> updateThreadPriority(UUID id, Integer priority)
    {
        return threadRepository.updateThreadPriority(id, priority);
    }

    public ResponseEntity<String> updateThreadState(UUID id, ThreadState threadState)
    {
        ThreadDto threadDto = threadRepository.getThreadById(id);

        threadRepository.updateThreadState(id, threadState);

        if(threadDto.getType() == ThreadType.SENDER)
        {
            ThreadDto senderThread = senderThreadService.createSenderThread(0);
        }
        else if(threadDto.getType() == ThreadType.RECEIVER)
        {
            ThreadDto senderThread = receiverThreadService.createReceiverThread(0);
        }
        return ResponseEntity.ok("Thread State Update Successful");
    }

    public List<ThreadDto> getAllThreadsInfo()
    {
        return threadRepository.getAllThreads().getBody();
    }

    public ResponseEntity<String> deleteThreadById(UUID id)
    {
        return threadRepository.deleteThreadById(id);
    }

    public ResponseEntity<String> deleteAllThreads()
    {
        return threadRepository.deleteAllThreads();
    }

}