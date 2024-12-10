package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.interfaces.IReceiverThreadService;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReceiverThreadControllerTest {

    @Mock
    private IReceiverThreadService iReceiverThreadService;

    private ReceiverThreadController receiverThreadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        receiverThreadController = new ReceiverThreadController(iReceiverThreadService);
    }

    @Test
    void createReceiverThreadsWithAmount_ValidParameters_Success() {
        // Arrange
        int receiverAmount = 5;
        List<ReceiverThreadDto> expectedThreads = Arrays.asList(
                new ReceiverThreadDto(),
                new ReceiverThreadDto()
        );
        when(iReceiverThreadService.createReceiverThreadsWithAmount(receiverAmount)).thenReturn(expectedThreads);

        // Act
        ResponseEntity<List<ReceiverThreadDto>> response = receiverThreadController.createReceiverThreadsWithAmount(receiverAmount);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedThreads, response.getBody());
        verify(iReceiverThreadService).createReceiverThreadsWithAmount(receiverAmount);
    }

    @Test
    void startReceiverThreadsLifeCycle_Valid_Success() {
        // Arrange
        when(iReceiverThreadService.startReceiverThreadsLifeCycle()).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = receiverThreadController.startReceiverThreadsLifeCycle();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(iReceiverThreadService).startReceiverThreadsLifeCycle();
    }

    @Test
    void getActiveReceiverThreads_Valid_Success() {
        // Arrange
        List<ReceiverThreadDto> activeThreads = Arrays.asList(
                new ReceiverThreadDto(),
                new ReceiverThreadDto()
        );
        when(iReceiverThreadService.getActiveReceiverThreads()).thenReturn(activeThreads);

        // Act
        ResponseEntity<List<ReceiverThreadDto>> response = receiverThreadController.getActiveReceiverThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activeThreads, response.getBody());
        verify(iReceiverThreadService).getActiveReceiverThreads();
    }

    @Test
    void getAllReceiverThreads_Valid_Success() {
        // Arrange
        List<ReceiverThreadDto> allThreads = Arrays.asList(
                new ReceiverThreadDto(),
                new ReceiverThreadDto()
        );
        when(iReceiverThreadService.getAllReceiverThreads()).thenReturn(allThreads);

        // Act
        ResponseEntity<List<ReceiverThreadDto>> response = receiverThreadController.getAllReceiverThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(allThreads, response.getBody());
        verify(iReceiverThreadService).getAllReceiverThreads();
    }

    @Test
    void getPassiveReceiverThreads_Valid_Success() {
        // Arrange
        List<ReceiverThreadDto> passiveThreads = Arrays.asList(
                new ReceiverThreadDto(),
                new ReceiverThreadDto()
        );
        when(iReceiverThreadService.getPassiveReceiverThreads()).thenReturn(passiveThreads);

        // Act
        ResponseEntity<List<ReceiverThreadDto>> response = receiverThreadController.getPassiveReceiverThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(passiveThreads, response.getBody());
        verify(iReceiverThreadService).getPassiveReceiverThreads();
    }

    @Test
    void updateReceiverThread_ValidParameters_Success() {
        // Arrange
        ReceiverThreadDto threadDto = new ReceiverThreadDto();
        when(iReceiverThreadService.updateReceiverThread(any(ReceiverThreadDto.class))).thenReturn(threadDto);

        // Act
        ResponseEntity<ReceiverThreadDto> response = receiverThreadController.updateReceiverThread(threadDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(threadDto, response.getBody());
        verify(iReceiverThreadService).updateReceiverThread(threadDto);
    }

    @Test
    void updateReceiverThreadPriority_ValidParameters_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer priority = 5;
        when(iReceiverThreadService.updateReceiverThreadPriority(id, priority)).thenReturn(id);

        // Act
        ResponseEntity<UUID> response = receiverThreadController.updateReceiverThreadPriority(id, priority);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(iReceiverThreadService).updateReceiverThreadPriority(id, priority);
    }

    @Test
    void updateReceiverThreadState_ValidParameters_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ThreadState threadState = ThreadState.RUNNING;
        when(iReceiverThreadService.updateReceiverThreadState(id, threadState)).thenReturn(id);

        // Act
        ResponseEntity<UUID> response = receiverThreadController.updateReceiverThreadState(id, threadState);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(iReceiverThreadService).updateReceiverThreadState(id, threadState);
    }

    @Test
    void deleteReceiverThreadById_ValidParameters_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(iReceiverThreadService.deleteReceiverThreadById(id)).thenReturn(id);

        // Act
        ResponseEntity<UUID> response = receiverThreadController.deleteReceiverThreadById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(iReceiverThreadService).deleteReceiverThreadById(id);
    }

    @Test
    void deleteAllReceiverThreads_Valid_Success() {
        // Arrange
        when(iReceiverThreadService.deleteAllReceiverThreads()).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = receiverThreadController.deleteAllReceiverThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(iReceiverThreadService).deleteAllReceiverThreads();
    }
}