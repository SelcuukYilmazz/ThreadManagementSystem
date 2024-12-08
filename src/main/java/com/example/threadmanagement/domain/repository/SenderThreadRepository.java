package com.example.threadmanagement.domain.repository;


import com.example.threadmanagement.domain.repository.interfaces.ISenderThreadRepository;
import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.SenderThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.mapper.interfaces.ISenderThreadMapper;
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
public class SenderThreadRepository{
    private final ISenderThreadRepository iSenderThreadRepository;
    private final ISenderThreadMapper iThreadMapper;

    public ResponseEntity<SenderThreadDto> createSenderThread(SenderThreadDto senderThreadDto){
        try {
            SenderThreadEntity senderThreadEntity = iThreadMapper.toEntity(senderThreadDto);
            iSenderThreadRepository.save(senderThreadEntity);
            return ResponseEntity.ok(senderThreadDto);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(senderThreadDto);
        }
    }

    public boolean createSenderThreadsWithList(List<SenderThreadDto> senderThreadDtoList)
    {
        try {
            List<SenderThreadEntity> senderThreadEntityList = iThreadMapper.toEntityList(senderThreadDtoList);
            iSenderThreadRepository.saveAll(senderThreadEntityList);
            return true;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public Optional<SenderThreadDto> getSenderThreadById(UUID id) {
        return iSenderThreadRepository.findById(id)
                .map(iThreadMapper::toDto);
    }

    public List<SenderThreadDto> getAllSenderThreads() {
        try{
            List<SenderThreadEntity> entities = iSenderThreadRepository.findAll();
            return new ArrayList<>(iThreadMapper.toDtoList(entities));
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<SenderThreadDto> getActiveSenderThreads() {
        try{
            List<SenderThreadEntity> senderThreadEntities = iSenderThreadRepository.findAll();
            return iThreadMapper.toDtoList(senderThreadEntities).stream()
                    .filter(thread -> thread.getState() == ThreadState.RUNNING )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<SenderThreadDto> getPassiveSenderThreads() {
        try{
            List<SenderThreadEntity> entities = iSenderThreadRepository.findAll();
            return iThreadMapper.toDtoList(entities).stream()
                    .filter(thread -> thread.getState() == ThreadState.STOPPED )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResponseEntity<String> deleteSenderThreadById(UUID id) {
        try {
            iSenderThreadRepository.deleteById(id);
            return ResponseEntity.ok("Sender Thread Deleted Successfully : " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Sender Thread Not Found : " + id);
        }
    }

    public ResponseEntity<String> deleteAllSenderThreads() {
        try {
            iSenderThreadRepository.deleteAll();
            return ResponseEntity.ok("Sender Threads Deleted Successfully");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Unknown Error");
        }
    }

    public ResponseEntity<String> updateSenderThread(SenderThreadDto senderThreadDto)
    {
        try
        {
            SenderThreadEntity senderThreadEntity = iSenderThreadRepository.findById(senderThreadDto.getId()).orElseThrow(()
                    -> new EntityNotFoundException("Thread Not Found"));

            if(senderThreadDto.getState() != null && senderThreadEntity.getState() != senderThreadDto.getState())
            {
                senderThreadEntity.setState(senderThreadDto.getState());
            }
            if(senderThreadDto.getPriority() != null && senderThreadEntity.getPriority() != senderThreadDto.getPriority())
            {
                senderThreadEntity.setPriority(senderThreadDto.getPriority());
            }
            if(senderThreadDto.getType() != null && senderThreadEntity.getType() != senderThreadDto.getType())
            {
                senderThreadEntity.setType(senderThreadDto.getType());
            }
            iSenderThreadRepository.save(senderThreadEntity);
            return ResponseEntity.ok("Thread Updated Successfully = " + senderThreadDto.getId());
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

    public ResponseEntity<String> updateSenderThreadPriority(UUID id, Integer priority)
    {
        try
        {
            if(priority == null)
            {
                throw new IllegalArgumentException();
            }
            SenderThreadEntity threadEntity = iSenderThreadRepository.findById(id).orElseThrow(()
                    -> new EntityNotFoundException("Thread Not Found"));
            if(threadEntity.getPriority() != priority)
            {
                threadEntity.setPriority(priority);
            }
            iSenderThreadRepository.save(threadEntity);
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

    public ResponseEntity<String> updateSenderThreadState(UUID id, ThreadState threadState)
    {
        try
        {
            if(threadState == null)
            {
                throw new IllegalArgumentException();
            }
            SenderThreadEntity senderThreadEntity = iSenderThreadRepository.findById(id).orElseThrow(()
                    -> new EntityNotFoundException("Thread Not Found"));
            if(senderThreadEntity.getState() != threadState)
            {
                senderThreadEntity.setState(threadState);
            }
            iSenderThreadRepository.save(senderThreadEntity);
            return ResponseEntity.ok("Sender Thread Updated Successfully = " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Sender Thread State Can't Be Null");
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Sender Thread Not Found");
        }
    }
}
