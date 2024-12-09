package com.example.threadmanagement.application.controller;

import com.example.threadmanagement.domain.service.interfaces.ISenderThreadService;
import com.example.threadmanagement.model.dto.SenderThreadDto;
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

class SenderThreadControllerTest {

    @Mock
    private ISenderThreadService iSenderThreadService;

    private SenderThreadController senderThreadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        senderThreadController = new SenderThreadController(iSenderThreadService);
    }

    @Test
    void createSenderThreadsWithAmount_Success() {
        // Arrange
        int senderAmount = 5;
        List<SenderThreadDto> expectedThreads = Arrays.asList(
                new SenderThreadDto(),
                new SenderThreadDto()
        );
        when(iSenderThreadService.createSenderThreadsWithAmount(senderAmount)).thenReturn(expectedThreads);

        // Act
        ResponseEntity<List<SenderThreadDto>> response = senderThreadController.createSenderThreadsWithAmount(senderAmount);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedThreads, response.getBody());
        verify(iSenderThreadService).createSenderThreadsWithAmount(senderAmount);
    }

    @Test
    void startSenderThreadsLifeCycle_Success() {
        // Arrange
        when(iSenderThreadService.startSenderThreadsLifeCycle()).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = senderThreadController.startSenderThreadsLifeCycle();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(iSenderThreadService).startSenderThreadsLifeCycle();
    }

    @Test
    void getActiveSenderThreads_Success() {
        // Arrange
        List<SenderThreadDto> activeThreads = Arrays.asList(
                new SenderThreadDto(),
                new SenderThreadDto()
        );
        when(iSenderThreadService.getActiveSenderThreads()).thenReturn(activeThreads);

        // Act
        ResponseEntity<List<SenderThreadDto>> response = senderThreadController.getActiveSenderThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activeThreads, response.getBody());
        verify(iSenderThreadService).getActiveSenderThreads();
    }

    @Test
    void getAllSenderThreads_Success() {
        // Arrange
        List<SenderThreadDto> allThreads = Arrays.asList(
                new SenderThreadDto(),
                new SenderThreadDto()
        );
        when(iSenderThreadService.getAllSenderThreads()).thenReturn(allThreads);

        // Act
        ResponseEntity<List<SenderThreadDto>> response = senderThreadController.getAllSenderThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(allThreads, response.getBody());
        verify(iSenderThreadService).getAllSenderThreads();
    }

    @Test
    void getPassiveSenderThreads_Success() {
        // Arrange
        List<SenderThreadDto> passiveThreads = Arrays.asList(
                new SenderThreadDto(),
                new SenderThreadDto()
        );
        when(iSenderThreadService.getPassiveSenderThreads()).thenReturn(passiveThreads);

        // Act
        ResponseEntity<List<SenderThreadDto>> response = senderThreadController.getPassiveSenderThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(passiveThreads, response.getBody());
        verify(iSenderThreadService).getPassiveSenderThreads();
    }

    @Test
    void updateSenderThread_Success() {
        // Arrange
        SenderThreadDto threadDto = new SenderThreadDto();
        when(iSenderThreadService.updateSenderThread(any(SenderThreadDto.class))).thenReturn(threadDto);

        // Act
        ResponseEntity<SenderThreadDto> response = senderThreadController.updateSenderThread(threadDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(threadDto, response.getBody());
        verify(iSenderThreadService).updateSenderThread(threadDto);
    }

    @Test
    void updateSenderThreadPriority_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer priority = 5;
        when(iSenderThreadService.updateSenderThreadPriority(id, priority)).thenReturn(id);

        // Act
        ResponseEntity<UUID> response = senderThreadController.updateSenderThreadPriority(id, priority);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(iSenderThreadService).updateSenderThreadPriority(id, priority);
    }

    @Test
    void updateSenderThreadState_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ThreadState threadState = ThreadState.RUNNING;
        when(iSenderThreadService.updateSenderThreadState(id, threadState)).thenReturn(id);

        // Act
        ResponseEntity<UUID> response = senderThreadController.updateSenderThreadState(id, threadState);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(iSenderThreadService).updateSenderThreadState(id, threadState);
    }

    @Test
    void deleteSenderThreadById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(iSenderThreadService.deleteSenderThreadById(id)).thenReturn(id);

        // Act
        ResponseEntity<UUID> response = senderThreadController.deleteSenderThreadById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(iSenderThreadService).deleteSenderThreadById(id);
    }

    @Test
    void deleteAllSenderThreads_Success() {
        // Arrange
        when(iSenderThreadService.deleteAllSenderThreads()).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = senderThreadController.deleteAllSenderThreads();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(iSenderThreadService).deleteAllSenderThreads();
    }
}