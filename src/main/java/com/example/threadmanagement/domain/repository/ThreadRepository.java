package com.example.threadmanagement.domain.repository;


import com.example.threadmanagement.domain.repository.interfaces.IThreadRepository;
import com.example.threadmanagement.model.dto.ThreadDto;
import com.example.threadmanagement.model.entity.ThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import com.example.threadmanagement.model.mapper.interfaces.IThreadMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ThreadRepository{
    private final IThreadRepository iThreadRepository;
    private final IThreadMapper iThreadMapper;

    public ResponseEntity<ThreadDto> createThread(ThreadDto threadDto){
        try {
            ThreadEntity threadEntity = iThreadMapper.toEntity(threadDto);
            iThreadRepository.save(threadEntity);
            return ResponseEntity.ok(threadDto);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(threadDto);
        }
    }

    public boolean createSenderReceiverThreadsWithAmount(List<ThreadDto> threadDtoList)
    {
        try {
            List<ThreadEntity> threadEntityList = iThreadMapper.toEntityList(threadDtoList);
            iThreadRepository.saveAll(threadEntityList);
            return true;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ThreadDto getThreadById(UUID id) {
        return iThreadRepository.findById(id)
                .map(iThreadMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Thread not found."));
    }

    public Optional<ThreadDto> isThreadAlive(UUID id) {
        return iThreadRepository.findById(id)
                .map(iThreadMapper::toDto);
    }

    public ResponseEntity<List<ThreadDto>> getAllThreads() {
        try{
            List<ThreadEntity> entities = iThreadRepository.findAll();
            return ResponseEntity.ok(iThreadMapper.toDtoList(entities));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ArrayList<ThreadDto>());
        }
    }

    public List<ThreadDto> getAllSenderThreads() {
        try{
            List<ThreadEntity> entities = iThreadRepository.findAll();
            return iThreadMapper.toDtoList(entities).stream()
                    .filter(thread -> thread.getType() == ThreadType.SENDER)
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<ThreadDto> getActiveSenderThreads() {
        try{
            List<ThreadEntity> entities = iThreadRepository.findAll();
            return iThreadMapper.toDtoList(entities).stream()
                    .filter(thread -> thread.getType() == ThreadType.SENDER)
                    .filter(thread -> thread.getState() == ThreadState.RUNNING )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<ThreadDto> getPassiveSenderThreads() {
        try{
            List<ThreadEntity> entities = iThreadRepository.findAll();
            return iThreadMapper.toDtoList(entities).stream()
                    .filter(thread -> thread.getType() == ThreadType.SENDER)
                    .filter(thread -> thread.getState() == ThreadState.STOPPED )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<ThreadDto> getAllReceiverThreads() {
        try{
            List<ThreadEntity> entities = iThreadRepository.findAll();
            return iThreadMapper.toDtoList(entities).stream()
                    .filter(thread -> thread.getType() == ThreadType.RECEIVER)
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<ThreadDto> getActiveReceiverThreads() {
        try{
            List<ThreadEntity> entities = iThreadRepository.findAll();
            return iThreadMapper.toDtoList(entities).stream()
                    .filter(thread -> thread.getType() == ThreadType.RECEIVER)
                    .filter(thread -> thread.getState() == ThreadState.RUNNING )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<ThreadDto> getPassiveReceiverThreads() {
        try{
            List<ThreadEntity> entities = iThreadRepository.findAll();
            return iThreadMapper.toDtoList(entities).stream()
                    .filter(thread -> thread.getType() == ThreadType.RECEIVER)
                    .filter(thread -> thread.getState() == ThreadState.STOPPED )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResponseEntity<String> deleteThreadById(UUID id) {
        try {
            iThreadRepository.deleteById(id);
            return ResponseEntity.ok("Thread Deleted Successfully : " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Thread Not Found : " + id);
        }
    }

    public ResponseEntity<String> deleteAllThreads() {
        try {
            iThreadRepository.deleteAll();
            return ResponseEntity.ok("Threads Deleted Successfully");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Unknown Error");
        }
    }

    public ResponseEntity<String> updateThread(ThreadDto threadDto)
    {
        try
        {
            ThreadEntity threadEntity = iThreadRepository.findById(threadDto.getId()).orElseThrow(()
                    -> new EntityNotFoundException("Thread Not Found"));

            if(threadDto.getState() != null && threadEntity.getState() != threadDto.getState())
            {
                threadEntity.setState(threadDto.getState());
            }
            if(threadDto.getPriority() != null && threadEntity.getPriority() != threadDto.getPriority())
            {
                threadEntity.setPriority(threadDto.getPriority());
            }
            if(threadDto.getType() != null && threadEntity.getType() != threadDto.getType())
            {
                threadEntity.setType(threadDto.getType());
            }
            iThreadRepository.save(threadEntity);
            return ResponseEntity.ok("Thread Updated Successfully = " + threadDto.getId());
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Thread Not Found");
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResponseEntity<String> updateThreadPriority(UUID id, Integer priority)
    {
        try
        {
            if(priority == null)
            {
                throw new IllegalArgumentException();
            }
            ThreadEntity threadEntity = iThreadRepository.findById(id).orElseThrow(()
                    -> new EntityNotFoundException("Thread Not Found"));
            if(threadEntity.getPriority() != priority)
            {
                threadEntity.setPriority(priority);
            }
            iThreadRepository.save(threadEntity);
            return ResponseEntity.ok("Thread Updated Successfully = " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Priority Can't Be Null");
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Entity Not Found");
        }
    }

    public ResponseEntity<String> updateThreadState(UUID id, ThreadState threadState)
    {
        try
        {
            if(threadState == null)
            {
                throw new IllegalArgumentException();
            }
            ThreadEntity threadEntity = iThreadRepository.findById(id).orElseThrow(()
                    -> new EntityNotFoundException("Thread Not Found"));
            if(threadEntity.getState() != threadState)
            {
                threadEntity.setState(threadState);
            }
            iThreadRepository.save(threadEntity);
            return ResponseEntity.ok("Thread Updated Successfully = " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Thread State Can't Be Null");
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Entity Not Found");
        }
    }
}
