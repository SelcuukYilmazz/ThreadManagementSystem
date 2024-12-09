package com.example.threadmanagement.domain.repository;

import com.example.threadmanagement.domain.repository.interfaces.ISenderThreadRepository;
import com.example.threadmanagement.exception.ThreadManagementException;
import com.example.threadmanagement.exception.ThreadNotFoundException;
import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.SenderThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.mapper.interfaces.ISenderThreadMapper;
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

class SenderThreadRepositoryTest {

    @Mock
    private ISenderThreadRepository iSenderThreadRepository;

    @Mock
    private ISenderThreadMapper iSenderThreadMapper;

    private SenderThreadRepository senderThreadRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        senderThreadRepository = new SenderThreadRepository(iSenderThreadRepository, iSenderThreadMapper);
    }

    @Test
    void createSenderThreadsWithList_Success() {
        // Arrange
        List<SenderThreadDto> dtoList = Arrays.asList(new SenderThreadDto(), new SenderThreadDto());
        List<SenderThreadEntity> entityList = Arrays.asList(new SenderThreadEntity(), new SenderThreadEntity());
        when(iSenderThreadMapper.toEntityList(dtoList)).thenReturn(entityList);
        when(iSenderThreadRepository.saveAll(entityList)).thenReturn(entityList);

        // Act
        Boolean result = senderThreadRepository.createSenderThreadsWithList(dtoList);

        // Assert
        assertTrue(result);
        verify(iSenderThreadMapper).toEntityList(dtoList);
        verify(iSenderThreadRepository).saveAll(entityList);
    }

    @Test
    void createSenderThreadsWithList_ThrowsException() {
        // Arrange
        List<SenderThreadDto> dtoList = Arrays.asList(new SenderThreadDto());
        when(iSenderThreadMapper.toEntityList(any())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(ThreadManagementException.class,
                () -> senderThreadRepository.createSenderThreadsWithList(dtoList));
    }

    @Test
    void getSenderThreadById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        SenderThreadEntity entity = new SenderThreadEntity();
        SenderThreadDto dto = new SenderThreadDto();
        when(iSenderThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iSenderThreadMapper.toDto(entity)).thenReturn(dto);

        // Act
        Optional<SenderThreadDto> result = senderThreadRepository.getSenderThreadById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getSenderThreadById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(iSenderThreadRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<SenderThreadDto> result = senderThreadRepository.getSenderThreadById(id);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllSenderThreads_Success() {
        // Arrange
        List<SenderThreadEntity> entities = Arrays.asList(new SenderThreadEntity());
        List<SenderThreadDto> dtos = Arrays.asList(new SenderThreadDto());
        when(iSenderThreadRepository.findAll()).thenReturn(entities);
        when(iSenderThreadMapper.toDtoList(entities)).thenReturn(dtos);

        // Act
        List<SenderThreadDto> result = senderThreadRepository.getAllSenderThreads();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(dtos, result);
    }

    @Test
    void getActiveSenderThreads_Success() {
        // Arrange
        SenderThreadDto activeDto = new SenderThreadDto();
        activeDto.setState(ThreadState.RUNNING);
        SenderThreadDto inactiveDto = new SenderThreadDto();
        inactiveDto.setState(ThreadState.STOPPED);

        List<SenderThreadEntity> entities = Arrays.asList(new SenderThreadEntity(), new SenderThreadEntity());
        List<SenderThreadDto> dtos = Arrays.asList(activeDto, inactiveDto);

        when(iSenderThreadRepository.findAll()).thenReturn(entities);
        when(iSenderThreadMapper.toDtoList(entities)).thenReturn(dtos);

        // Act
        List<SenderThreadDto> result = senderThreadRepository.getActiveSenderThreads();

        // Assert
        assertEquals(1, result.size());
        assertEquals(ThreadState.RUNNING, result.get(0).getState());
    }

    @Test
    void deleteSenderThreadById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(iSenderThreadRepository).deleteById(id);

        // Act
        UUID result = senderThreadRepository.deleteSenderThreadById(id);

        // Assert
        assertEquals(id, result);
        verify(iSenderThreadRepository).deleteById(id);
    }

    @Test
    void deleteSenderThreadById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new IllegalArgumentException()).when(iSenderThreadRepository).deleteById(id);

        // Act & Assert
        assertThrows(ThreadNotFoundException.class,
                () -> senderThreadRepository.deleteSenderThreadById(id));
    }

    @Test
    void updateSenderThread_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        SenderThreadDto dto = new SenderThreadDto();
        dto.setId(id);
        dto.setState(ThreadState.RUNNING);
        dto.setPriority(1);

        SenderThreadEntity entity = new SenderThreadEntity();
        when(iSenderThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iSenderThreadRepository.save(any(SenderThreadEntity.class))).thenReturn(entity);

        // Act
        SenderThreadDto result = senderThreadRepository.updateSenderThread(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void updateSenderThread_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        SenderThreadDto dto = new SenderThreadDto();
        dto.setId(id);
        when(iSenderThreadRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThreadNotFoundException.class,
                () -> senderThreadRepository.updateSenderThread(dto));
    }

    @Test
    void updateSenderThreadPriority_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer priority = 2;
        SenderThreadEntity entity = new SenderThreadEntity();
        when(iSenderThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iSenderThreadRepository.save(any(SenderThreadEntity.class))).thenReturn(entity);

        // Act
        UUID result = senderThreadRepository.updateSenderThreadPriority(id, priority);

        // Assert
        assertEquals(id, result);
        assertEquals(priority, entity.getPriority());
    }

    @Test
    void updateSenderThreadPriority_NullPriority() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(ThreadManagementException.class,
                () -> senderThreadRepository.updateSenderThreadPriority(id, null));
    }

    @Test
    void updateSenderThreadState_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ThreadState newState = ThreadState.RUNNING;
        SenderThreadEntity entity = new SenderThreadEntity();
        when(iSenderThreadRepository.findById(id)).thenReturn(Optional.of(entity));
        when(iSenderThreadRepository.save(any(SenderThreadEntity.class))).thenReturn(entity);

        // Act
        UUID result = senderThreadRepository.updateSenderThreadState(id, newState);

        // Assert
        assertEquals(id, result);
        assertEquals(newState, entity.getState());
    }

    @Test
    void updateSenderThreadState_NullState() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(ThreadManagementException.class,
                () -> senderThreadRepository.updateSenderThreadState(id, null));
    }
}