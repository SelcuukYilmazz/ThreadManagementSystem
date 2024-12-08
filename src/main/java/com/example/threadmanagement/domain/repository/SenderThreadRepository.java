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
     * Creates multiple sender threads based on the provided amount.
     * @param senderThreadDtoList list of sender threads to be created in database
     * @return if function is success then returns true else throws exception
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
     * Gets sender thread from database with id value
     * @param id id of sender thread
     * @return returns sender thread if found else it returns empty() for Optional if Id null returns
     * IllegalArgumentException and else it throws ThreadManagementException
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
     * Gets all sender threads from database
     * @return returns list of sender threads that it found from database else it throws ThreadManagementException
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
     * Gets all active sender threads which thread states are running
     * @return returns list of active sender threads and in exception throws ThreadManagementException
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
     * Gets all passive sender threads which thread states are stopped
     * @return returns list of passive sender threads and in exception throws ThreadManagementException
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
     * Deletes sender thread with its id
     * @param id id of sender thread
     * @return returns id of deleted sender thread and if thread not found throws ThreadNotFoundException
     * For other exceptions throws ThreadManagementException
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
     * Deletes all sender threads from database
     * @return true if deletion of all sender threads are true
     * Throws ThreadManagementException in exception
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
     * Updates sender thread from database with senderthreaddto
     * @param senderThreadDto DTO value of senderThread entity
     * @return returns senderThreadDto which is update dto in successful state if thread not found in database
     * Throws ThreadNotFoundException for other exceptions throws ThreadManagementException
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
     * Updates sender thread priority number with id and priority value
     * @param id id of sender thread
     * @param priority priority value of user selection
     * @return returns id of updated sender thread
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
     * Updates sender thread state value with id value and threadState value
     * @param id id of sender thread
     * @param threadState thread state value for update
     * @return returns updated sender thread id value
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
