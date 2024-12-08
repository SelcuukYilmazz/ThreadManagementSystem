package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.ReceiverThreadRepository;
import com.example.threadmanagement.exception.ThreadManagementException;
import com.example.threadmanagement.exception.ThreadNotFoundException;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiverThreadServiceTest {

    @Mock
    private ReceiverThreadRepository receiverThreadRepository;

    @Mock
    private ExecutorService executorService;

    private BlockingQueue<String> sharedQueue;
    private ReceiverThreadService receiverThreadService;

    @BeforeEach
    void setUp() {
        sharedQueue = new LinkedBlockingQueue<>();
        receiverThreadService = new ReceiverThreadService(sharedQueue, executorService, receiverThreadRepository);
    }

    @Test
    void createReceiverThreadsWithAmount_ValidAmount_Success() {
        // Arrange
        Integer amount = 2;
        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(receiverThreadRepository.createReceiverThreadsWithList(anyList())).thenReturn(true);

        // Act
        List<ReceiverThreadDto> result = receiverThreadService.createReceiverThreadsWithAmount(amount);

        // Assert
        assertEquals(2, result.size());
        verify(receiverThreadRepository).createReceiverThreadsWithList(anyList());
        verify(executorService, times(2)).submit(any(Runnable.class));

        // Verify all threads are created with correct initial state
        for (ReceiverThreadDto thread : result) {
            assertNotNull(thread.getId());
            assertEquals(ThreadType.RECEIVER, thread.getType());
            assertEquals(ThreadState.RUNNING, thread.getState());
            assertEquals(Thread.NORM_PRIORITY, thread.getPriority());
        }
    }

    @Test
    void createReceiverThreadsWithAmount_RepositoryError_ThrowsException() {
        // Arrange
        Integer amount = 2;
        when(receiverThreadRepository.createReceiverThreadsWithList(anyList()))
                .thenThrow(new ThreadManagementException("Database error", null));

        // Act & Assert
        assertThrows(ThreadManagementException.class, () ->
                receiverThreadService.createReceiverThreadsWithAmount(amount)
        );
    }

    @Test
    void createReceiverThreadsWithAmount_NullAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                receiverThreadService.createReceiverThreadsWithAmount(null)
        );
    }

    @Test
    void updateReceiverThread_ExistingThread_Success() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        ReceiverThreadDto inputDto = new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY);
        ReceiverThreadDto existingDto = new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.STOPPED, Thread.NORM_PRIORITY);

        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(receiverThreadRepository.getReceiverThreadById(threadId)).thenReturn(Optional.of(existingDto));
        when(receiverThreadRepository.updateReceiverThread(inputDto)).thenReturn(inputDto);

        // Act
        ReceiverThreadDto result = receiverThreadService.updateReceiverThread(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(ThreadState.RUNNING, result.getState());
        verify(executorService).submit(any(Runnable.class));
    }

    @Test
    void updateReceiverThread_NonExistingThread_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        ReceiverThreadDto inputDto = new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY);
        when(receiverThreadRepository.getReceiverThreadById(threadId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                receiverThreadService.updateReceiverThread(inputDto)
        );
    }

    @Test
    void updateReceiverThreadState_ExistingThread_Success() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        ReceiverThreadDto existingDto = new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.STOPPED, Thread.NORM_PRIORITY);

        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(receiverThreadRepository.getReceiverThreadById(threadId)).thenReturn(Optional.of(existingDto));
        when(receiverThreadRepository.updateReceiverThreadState(threadId, ThreadState.RUNNING)).thenReturn(threadId);

        // Act
        UUID response = receiverThreadService.updateReceiverThreadState(threadId, ThreadState.RUNNING);

        // Assert
        assertEquals(threadId, response);
        verify(executorService).submit(any(Runnable.class));
    }

    @Test
    void updateReceiverThreadState_NullState_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                receiverThreadService.updateReceiverThreadState(threadId, null)
        );
    }

    @Test
    void updateReceiverThreadState_ThreadNotFound_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        when(receiverThreadRepository.getReceiverThreadById(threadId))
                .thenThrow(new ThreadNotFoundException(threadId));

        // Act & Assert
        assertThrows(ThreadNotFoundException.class, () ->
                receiverThreadService.updateReceiverThreadState(threadId, ThreadState.RUNNING)
        );
    }

    @Test
    void updateReceiverThreadPriority_Success() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        Integer newPriority = Thread.MAX_PRIORITY;
        when(receiverThreadRepository.updateReceiverThreadPriority(threadId, newPriority)).thenReturn(threadId);

        // Act
        UUID result = receiverThreadService.updateReceiverThreadPriority(threadId, newPriority);

        // Assert
        assertEquals(threadId, result);
        verify(receiverThreadRepository).updateReceiverThreadPriority(threadId, newPriority);
    }

    @Test
    void updateReceiverThreadPriority_NullPriority_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        when(receiverThreadRepository.updateReceiverThreadPriority(threadId, null))
                .thenThrow(new ThreadManagementException("Priority Can't Be Null", null));

        // Act & Assert
        assertThrows(ThreadManagementException.class, () ->
                receiverThreadService.updateReceiverThreadPriority(threadId, null)
        );
    }

    @Test
    void getActiveReceiverThreads_Success() {
        // Arrange
        List<ReceiverThreadDto> expectedThreads = Arrays.asList(
                new ReceiverThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY),
                new ReceiverThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY)
        );
        when(receiverThreadRepository.getActiveReceiverThreads()).thenReturn(expectedThreads);

        // Act
        List<ReceiverThreadDto> result = receiverThreadService.getActiveReceiverThreads();

        // Assert
        assertEquals(expectedThreads.size(), result.size());
        verify(receiverThreadRepository).getActiveReceiverThreads();
    }

    @Test
    void startReceiverThreadsLifeCycle_Success() {
        // Arrange
        List<ReceiverThreadDto> activeThreads = Arrays.asList(
                new ReceiverThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY),
                new ReceiverThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY)
        );
        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(receiverThreadRepository.getActiveReceiverThreads()).thenReturn(activeThreads);

        // Act
        Boolean response = receiverThreadService.startReceiverThreadsLifeCycle();

        // Assert
        assertTrue(response);
        verify(executorService, times(2)).submit(any(Runnable.class));
    }

    @Test
    void deleteAllReceiverThreads_Success() {
        // Arrange
        when(receiverThreadRepository.deleteAllReceiverThreads()).thenReturn(true);

        // Act
        Boolean result = receiverThreadService.deleteAllReceiverThreads();

        // Assert
        assertTrue(result);
        verify(receiverThreadRepository).deleteAllReceiverThreads();
    }
}