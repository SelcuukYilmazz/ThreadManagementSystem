package com.example.threadmanagement.domain.repository;


import com.example.threadmanagement.domain.repository.interfaces.ISenderThreadRepository;
import com.example.threadmanagement.exception.ThreadManagementException;
import com.example.threadmanagement.exception.ThreadNotFoundException;
import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.SenderThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.mapper.interfaces.ISenderThreadMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public Boolean createSenderThreadsWithList(List<SenderThreadDto> senderThreadDtoList)
    {
        try {
            List<SenderThreadEntity> senderThreadEntityList = iThreadMapper.toEntityList(senderThreadDtoList);
            iSenderThreadRepository.saveAll(senderThreadEntityList);
            return true;
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
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
            throw new ThreadManagementException(e.getMessage(),e.getCause());
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
            throw new ThreadManagementException(e.getMessage(),e.getCause());
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
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    public UUID deleteSenderThreadById(UUID id) {
        try {
            iSenderThreadRepository.deleteById(id);
            return id;
        }
        catch (Exception e)
        {
            throw new ThreadNotFoundException(id);
        }
    }

    public Boolean deleteAllSenderThreads() {
        try {
            iSenderThreadRepository.deleteAll();
            return true;
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    public SenderThreadDto updateSenderThread(SenderThreadDto senderThreadDto)
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
            return senderThreadDto;
        }
        catch (EntityNotFoundException e)
        {
            throw new ThreadNotFoundException(senderThreadDto.getId());
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    public UUID updateSenderThreadPriority(UUID id, Integer priority)
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
            return id;
        }
        catch (IllegalArgumentException e)
        {
            throw new ThreadManagementException("Priority Can't Be Null",e.getCause());
        }
        catch (EntityNotFoundException e)
        {
            throw new ThreadNotFoundException(id);
        }
    }

    public UUID updateSenderThreadState(UUID id, ThreadState threadState)
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
            return id;
        }
        catch (IllegalArgumentException e)
        {
            throw new ThreadManagementException("Thread State Can't Be Null",e.getCause());
        }
        catch (EntityNotFoundException e)
        {
            throw new ThreadNotFoundException(id);
        }
    }
}
