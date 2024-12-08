package com.example.threadmanagement.domain.repository;

import com.example.threadmanagement.domain.repository.interfaces.IReceiverThreadRepository;
import com.example.threadmanagement.exception.ThreadManagementException;
import com.example.threadmanagement.exception.ThreadNotFoundException;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ReceiverThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.mapper.interfaces.IReceiverThreadMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReceiverThreadRepository {
    private final IReceiverThreadRepository iReceiverThreadRepository;
    private final IReceiverThreadMapper iReceiverThreadMapper;

    /**
     * Creates multiple receiver threads based on the provided amount.
     * @param receiverThreadDtoList list of receiver threads to be created in database
     * @return if function is success then returns true else throws exception
     */
    public Boolean createReceiverThreadsWithList(List<ReceiverThreadDto> receiverThreadDtoList)
    {
        try {
            List<ReceiverThreadEntity> receiverThreadEntityList = iReceiverThreadMapper.toEntityList(receiverThreadDtoList);
            iReceiverThreadRepository.saveAll(receiverThreadEntityList);
            return true;
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Gets receiver thread from database with id value
     * @param id id of receiver thread
     * @return returns receiver thread if found else it returns empty() for Optional if Id null returns
     * IllegalArgumentException and else it throws ThreadManagementException
     */
    public Optional<ReceiverThreadDto> getReceiverThreadById(UUID id) {
        try{
            return iReceiverThreadRepository.findById(id)
                    .map(iReceiverThreadMapper::toDto);
        }
        catch (IllegalArgumentException e)
        {
            throw new ThreadManagementException("Id Can't Be Null",e.getCause());
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Gets all receiver threads from database
     * @return returns list of receiver threads that it found from database else it throws ThreadManagementException
     */
    public List<ReceiverThreadDto>getAllReceiverThreads() {
        try{
            List<ReceiverThreadEntity> entities = iReceiverThreadRepository.findAll();
            return iReceiverThreadMapper.toDtoList(entities);
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Gets all active receiver threads which thread states are running
     * @return returns list of active receiver threads and in exception throws ThreadManagementException
     */
    public List<ReceiverThreadDto> getActiveReceiverThreads() {
        try{
            List<ReceiverThreadEntity> receiverThreadEntities = iReceiverThreadRepository.findAll();
            return iReceiverThreadMapper.toDtoList(receiverThreadEntities).stream()
                    .filter(receiverThreadDto -> receiverThreadDto.getState() == ThreadState.RUNNING )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Gets all passive receiver threads which thread states are stopped
     * @return returns list of passive receiver threads and in exception throws ThreadManagementException
     */
    public List<ReceiverThreadDto> getPassiveReceiverThreads() {
        try{
            List<ReceiverThreadEntity> entities = iReceiverThreadRepository.findAll();
            return iReceiverThreadMapper.toDtoList(entities).stream()
                    .filter(receiverThreadDto -> receiverThreadDto.getState() == ThreadState.STOPPED )
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Deletes receiver thread with its id
     * @param id id of receiver thread
     * @return returns id of deleted receiver thread and if thread not found throws ThreadNotFoundException
     * For other exceptions throws ThreadManagementException
     */
    public UUID deleteReceiverThreadById(UUID id) {
        try {
            iReceiverThreadRepository.deleteById(id);
            return id;
        }
        catch (IllegalArgumentException e)
        {
            throw new ThreadNotFoundException(id);
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Deletes all receiver threads from database
     * @return true if deletion of all receiver threads are true
     * Throws ThreadManagementException in exception
     */
    public Boolean deleteAllReceiverThreads() {
        try {
            iReceiverThreadRepository.deleteAll();
            return true;
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Updates receiver thread from database with receiverthreaddto
     * @param receiverThreadDto DTO value of receiverThread entity
     * @return returns receiverThreadDto which is update dto in successful state if thread not found in database
     * Throws ThreadNotFoundException for other exceptions throws ThreadManagementException
     */
    public ReceiverThreadDto updateReceiverThread(ReceiverThreadDto receiverThreadDto)
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
            return receiverThreadDto;
        }
        catch (EntityNotFoundException e)
        {
            throw new ThreadNotFoundException(receiverThreadDto.getId());
        }
        catch (Exception e)
        {
            throw new ThreadManagementException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Updates receiver thread priority number with id and priority value
     * @param id id of receiver thread
     * @param priority priority value of user selection
     * @return returns id of updated receiver thread
     */
    public UUID updateReceiverThreadPriority(UUID id, Integer priority)
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

    /**
     * Updates receiver thread state value with id value and threadState value
     * @param id id of receiver thread
     * @param threadState thread state value for update
     * @return returns updated receiver thread id value
     */
    public UUID updateReceiverThreadState(UUID id, ThreadState threadState)
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
