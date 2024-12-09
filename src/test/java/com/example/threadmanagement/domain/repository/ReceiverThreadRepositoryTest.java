package com.example.threadmanagement.domain.repository;

import com.example.threadmanagement.domain.repository.interfaces.IReceiverThreadRepository;
import com.example.threadmanagement.exception.ThreadManagementException;
import com.example.threadmanagement.exception.ThreadNotFoundException;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ReceiverThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.mapper.interfaces.IReceiverThreadMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReceiverThreadRepositoryTest {

    @Mock
    private IReceiverThreadRepository iReceiverThreadRepository;

    @Mock
    private IReceiverThreadMapper iReceiverThreadMapper;

    private ReceiverThreadRepository receiverThreadRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        receiverThreadRepository = new ReceiverThreadRepository(iReceiverThreadRepository, iReceiverThreadMapper);
    }

    @Test
    void createReceiverThreadsWithList_Success() {
        // Arrange
        List<ReceiverThreadDto> dtoList = Arrays.asList(new ReceiverThreadDto(), new ReceiverThreadDto());
        List<ReceiverThreadEntity> entityList = Arrays.asList(new ReceiverThreadEntity(), new ReceiverThreadEntity());
        when(iReceiverThreadMapper.toEntityList(dtoList)).thenReturn(entityList);
        when(iReceiverThreadRepository.saveAll(entityList)).thenReturn(entityList);

        // Act
        Boolean result = receiverThreadRepository.createReceiverThreadsWithList(dtoList);

        // Assert
        assertTrue(result);
        verify(iReceiverThreadMapper).toEntityList(dtoList);
        verify(iReceiverThreadRepository).saveAll(entityList);
    }

    @Test
    void createReceiverThreadsWithList_ThrowsException() {
        // Arrange
        List<ReceiverThreadDto> dtoList = Arrays.asList(new ReceiverThreadDto());
        when(iReceiverThreadMapper.toEntityList(any())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(ThreadManagementException.class,
                () -> receiverThreadRepository.createReceiverThreadsWithList(dtoList));
    }

    @Test
    void getReceiverThreadById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ReceiverThreadEntity entity = new ReceiverThreadEntity();
        ReceiverThreadDto dto = new ReceiverThreadDto();
        when(iReceiverThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iReceiverThreadMapper.toDto(entity)).thenReturn(dto);

        // Act
        Optional<ReceiverThreadDto> result = receiverThreadRepository.getReceiverThreadById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getReceiverThreadById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(iReceiverThreadRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<ReceiverThreadDto> result = receiverThreadRepository.getReceiverThreadById(id);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllReceiverThreads_Success() {
        // Arrange
        List<ReceiverThreadEntity> entities = Arrays.asList(new ReceiverThreadEntity());
        List<ReceiverThreadDto> dtos = Arrays.asList(new ReceiverThreadDto());
        when(iReceiverThreadRepository.findAll()).thenReturn(entities);
        when(iReceiverThreadMapper.toDtoList(entities)).thenReturn(dtos);

        // Act
        List<ReceiverThreadDto> result = receiverThreadRepository.getAllReceiverThreads();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(dtos, result);
    }

    @Test
    void getActiveReceiverThreads_Success() {
        // Arrange
        ReceiverThreadDto activeDto = new ReceiverThreadDto();
        activeDto.setState(ThreadState.RUNNING);
        ReceiverThreadDto inactiveDto = new ReceiverThreadDto();
        inactiveDto.setState(ThreadState.STOPPED);

        List<ReceiverThreadEntity> entities = Arrays.asList(new ReceiverThreadEntity(), new ReceiverThreadEntity());
        List<ReceiverThreadDto> dtos = Arrays.asList(activeDto, inactiveDto);

        when(iReceiverThreadRepository.findAll()).thenReturn(entities);
        when(iReceiverThreadMapper.toDtoList(entities)).thenReturn(dtos);

        // Act
        List<ReceiverThreadDto> result = receiverThreadRepository.getActiveReceiverThreads();

        // Assert
        assertEquals(1, result.size());
        assertEquals(ThreadState.RUNNING, result.get(0).getState());
    }

    @Test
    void deleteReceiverThreadById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(iReceiverThreadRepository).deleteById(id);

        // Act
        UUID result = receiverThreadRepository.deleteReceiverThreadById(id);

        // Assert
        assertEquals(id, result);
        verify(iReceiverThreadRepository).deleteById(id);
    }

    @Test
    void deleteReceiverThreadById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new IllegalArgumentException()).when(iReceiverThreadRepository).deleteById(id);

        // Act & Assert
        assertThrows(ThreadNotFoundException.class,
                () -> receiverThreadRepository.deleteReceiverThreadById(id));
    }

    @Test
    void updateReceiverThread_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ReceiverThreadDto dto = new ReceiverThreadDto();
        dto.setId(id);
        dto.setState(ThreadState.RUNNING);
        dto.setPriority(1);

        ReceiverThreadEntity entity = new ReceiverThreadEntity();
        when(iReceiverThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iReceiverThreadRepository.save(any(ReceiverThreadEntity.class))).thenReturn(entity);

        // Act
        ReceiverThreadDto result = receiverThreadRepository.updateReceiverThread(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void updateReceiverThread_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        ReceiverThreadDto dto = new ReceiverThreadDto();
        dto.setId(id);
        when(iReceiverThreadRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThreadNotFoundException.class,
                () -> receiverThreadRepository.updateReceiverThread(dto));
    }

    @Test
    void updateReceiverThreadPriority_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer priority = 2;
        ReceiverThreadEntity entity = new ReceiverThreadEntity();
        when(iReceiverThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iReceiverThreadRepository.save(any(ReceiverThreadEntity.class))).thenReturn(entity);

        // Act
        UUID result = receiverThreadRepository.updateReceiverThreadPriority(id, priority);

        // Assert
        assertEquals(id, result);
        assertEquals(priority, entity.getPriority());
    }

    @Test
    void updateReceiverThreadPriority_NullPriority() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(ThreadManagementException.class,
                () -> receiverThreadRepository.updateReceiverThreadPriority(id, null));
    }

    @Test
    void updateReceiverThreadState_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ThreadState newState = ThreadState.RUNNING;
        ReceiverThreadEntity entity = new ReceiverThreadEntity();
        when(iReceiverThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iReceiverThreadRepository.save(any(ReceiverThreadEntity.class))).thenReturn(entity);

        // Act
        UUID result = receiverThreadRepository.updateReceiverThreadState(id, newState);

        // Assert
        assertEquals(id, result);
        assertEquals(newState, entity.getState());
    }

    @Test
    void updateReceiverThreadState_NullState() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(ThreadManagementException.class,
                () -> receiverThreadRepository.updateReceiverThreadState(id, null));
    }
}