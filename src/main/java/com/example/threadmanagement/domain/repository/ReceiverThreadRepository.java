package com.example.threadmanagement.domain.repository;


import com.example.threadmanagement.domain.repository.interfaces.IReceiverThreadRepository;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ReceiverThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import com.example.threadmanagement.model.mapper.interfaces.IReceiverThreadMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReceiverThreadRepository {
    private final IReceiverThreadRepository iReceiverThreadRepository;
    private final IReceiverThreadMapper iReceiverThreadMapper;

    public ResponseEntity<ReceiverThreadDto> createReceiverThread(ReceiverThreadDto receiverThreadDto){
        try {
            ReceiverThreadEntity receiverThreadEntity = iReceiverThreadMapper.toEntity(receiverThreadDto);
            iReceiverThreadRepository.save(receiverThreadEntity);
            return ResponseEntity.ok(receiverThreadDto);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(receiverThreadDto);
        }
    }

    public boolean createReceiverThreadsWithList(List<ReceiverThreadDto> receiverThreadDtoList)
    {
        try {
            List<ReceiverThreadEntity> receiverThreadEntityList = iReceiverThreadMapper.toEntityList(receiverThreadDtoList);
            iReceiverThreadRepository.saveAll(receiverThreadEntityList);
            return true;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public Optional<ReceiverThreadDto> getReceiverThreadById(UUID id) {
        return iReceiverThreadRepository.findById(id)
                .map(iReceiverThreadMapper::toDto);
    }

    public List<ReceiverThreadDto>getAllReceiverThreads() {
        try{
            List<ReceiverThreadEntity> entities = iReceiverThreadRepository.findAll();
            return iReceiverThreadMapper.toDtoList(entities);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<ReceiverThreadDto> getActiveReceiverThreads() {
        try{
            List<ReceiverThreadEntity> receiverThreadEntities = iReceiverThreadRepository.findAll();
            return iReceiverThreadMapper.toDtoList(receiverThreadEntities).stream()
                    .filter(receiverThreadDto -> receiverThreadDto.getState() == ThreadState.RUNNING )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<ReceiverThreadDto> getPassiveReceiverThreads() {
        try{
            List<ReceiverThreadEntity> entities = iReceiverThreadRepository.findAll();
            return iReceiverThreadMapper.toDtoList(entities).stream()
                    .filter(receiverThreadDto -> receiverThreadDto.getState() == ThreadState.STOPPED )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResponseEntity<String> deleteReceiverThreadById(UUID id) {
        try {
            iReceiverThreadRepository.deleteById(id);
            return ResponseEntity.ok("Receiver Thread Deleted Successfully : " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Receiver Thread Not Found : " + id);
        }
    }

    public ResponseEntity<String> deleteAllReceiverThreads() {
        try {
            iReceiverThreadRepository.deleteAll();
            return ResponseEntity.ok("Receiver Threads Deleted Successfully");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Unknown Error");
        }
    }

    public ResponseEntity<String> updateReceiverThread(ReceiverThreadDto receiverThreadDto)
    {
        try
        {
            ReceiverThreadEntity receiverThreadEntity = iReceiverThreadRepository.findById(receiverThreadDto.getId()).orElseThrow(()
                    -> new EntityNotFoundException("ReceiverThread Not Found"));

            if(receiverThreadDto.getState() != null && receiverThreadEntity.getState() != receiverThreadDto.getState())
            {
                receiverThreadEntity.setState(receiverThreadDto.getState());
            }
            if(receiverThreadDto.getPriority() != null && receiverThreadEntity.getPriority() != receiverThreadDto.getPriority())
            {
                receiverThreadEntity.setPriority(receiverThreadDto.getPriority());
            }
            if(receiverThreadDto.getType() != null && receiverThreadEntity.getType() != receiverThreadDto.getType())
            {
                receiverThreadEntity.setType(receiverThreadDto.getType());
            }
            iReceiverThreadRepository.save(receiverThreadEntity);
            return ResponseEntity.ok("Receiver Thread Updated Successfully = " + receiverThreadDto.getId());
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Receiver Thread Not Found");
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public ResponseEntity<String> updateReceiverThreadPriority(UUID id, Integer priority)
    {
        try
        {
            if(priority == null)
            {
                throw new IllegalArgumentException();
            }
            ReceiverThreadEntity receiverThreadEntity = iReceiverThreadRepository.findById(id).orElseThrow(()
                    -> new EntityNotFoundException("Receiver Thread Not Found"));
            if(receiverThreadEntity.getPriority() != priority)
            {
                receiverThreadEntity.setPriority(priority);
            }
            iReceiverThreadRepository.save(receiverThreadEntity);
            return ResponseEntity.ok("Receiver Thread Updated Successfully = " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Priority Can't Be Null");
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Receiver Thread Not Found");
        }
    }

    public ResponseEntity<String> updateReceiverThreadState(UUID id, ThreadState threadState)
    {
        try
        {
            if(threadState == null)
            {
                throw new IllegalArgumentException();
            }
            ReceiverThreadEntity receiverThreadEntity = iReceiverThreadRepository.findById(id).orElseThrow(()
                    -> new EntityNotFoundException("Receiver Thread Not Found"));
            if(receiverThreadEntity.getState() != threadState)
            {
                receiverThreadEntity.setState(threadState);
            }
            iReceiverThreadRepository.save(receiverThreadEntity);
            return ResponseEntity.ok("Receiver Thread Updated Successfully = " + id);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body("Receiver Thread State Can't Be Null");
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.badRequest().body("Receiver Thread Not Found");
        }
    }
}
