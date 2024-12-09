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

    /**
     * Creates multiple sender threads in the database.
     * @param senderThreadDtoList list of sender threads to be created in database
     * @return true if threads were successfully created
     * @throws ThreadManagementException if any error occurs during creation
     */
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

    /**
     * Retrieves a sender thread by its ID.
     * @param id ID of the sender thread to retrieve
     * @return Optional containing the sender thread if found, empty otherwise
     * @throws ThreadManagementException if ID is null or other errors occur
     */
    public Optional<SenderThreadDto> getSenderThreadById(UUID id) {
        try {
            return iSenderThreadRepository.findById(id)
                    .map(iThreadMapper::toDto);
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
     * Retrieves all sender threads from the database.
     * @return list of all sender threads
     * @throws ThreadManagementException if any error occurs during retrieval
     */
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

    /**
     * Retrieves all active sender threads (state = RUNNING).
     * @return list of active sender threads
     * @throws ThreadManagementException if any error occurs during retrieval
     */
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

    /**
     * Retrieves all passive sender threads (state = STOPPED).
     * @return list of passive sender threads
     * @throws ThreadManagementException if any error occurs during retrieval
     */
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

    /**
     * Deletes a sender thread by its ID.
     * @param id ID of the sender thread to delete
     * @return ID of the deleted thread
     * @throws ThreadNotFoundException if thread with given ID is not found
     * @throws ThreadManagementException for other errors during deletion
     */
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

    /**
     * Deletes all sender threads from the database.
     * @return true if all threads were successfully deleted
     * @throws ThreadManagementException if any error occurs during deletion
     */
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

    /**
     * Updates a sender thread's information in the database.
     * @param senderThreadDto updated thread information
     * @return updated sender thread DTO
     * @throws ThreadNotFoundException if thread is not found in database
     * @throws ThreadManagementException for other errors during update
     */
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

    /**
     * Updates the priority of a specific sender thread.
     * @param id ID of the sender thread
     * @param priority new priority value
     * @return ID of the updated thread
     * @throws ThreadManagementException if priority is null
     * @throws ThreadNotFoundException if thread is not found
     */
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

    /**
     * Updates the state of a specific sender thread.
     * @param id ID of the sender thread
     * @param threadState new thread state
     * @return ID of the updated thread
     * @throws ThreadManagementException if thread state is null
     * @throws ThreadNotFoundException if thread is not found
     */
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
