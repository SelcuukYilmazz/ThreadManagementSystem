package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.SenderThreadRepository;
import com.example.threadmanagement.exception.ThreadManagementException;
import com.example.threadmanagement.exception.ThreadNotFoundException;
import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class SenderThreadServiceTest {

    @Mock
    private SenderThreadRepository senderThreadRepository;

    @Mock
    private ExecutorService executorService;

    private BlockingQueue<String> sharedQueue;
    private SenderThreadService senderThreadService;
    private UUID threadId;

    @BeforeEach
    void setUp() {
        sharedQueue = new LinkedBlockingQueue<>();
        senderThreadService = new SenderThreadService(sharedQueue, executorService, senderThreadRepository);
        threadId = UUID.randomUUID();
    }

    @Test
    void createSenderThreadsWithAmount_ValidAmount_Success() {
        // Arrange
        Integer amount = 2;
        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(senderThreadRepository.createSenderThreadsWithList(anyList())).thenReturn(true);

        // Act
        List<SenderThreadDto> result = senderThreadService.createSenderThreadsWithAmount(amount);

        // Assert
        assertEquals(2, result.size());
        verify(senderThreadRepository).createSenderThreadsWithList(anyList());
        verify(executorService, times(2)).submit(any(Runnable.class));

        // Verify all threads are created with correct initial state
        for (SenderThreadDto thread : result) {
            assertNotNull(thread.getId());
            assertEquals(ThreadType.SENDER, thread.getType());
            assertEquals(ThreadState.RUNNING, thread.getState());
            assertEquals(Thread.NORM_PRIORITY, thread.getPriority());
        }
    }

    @Test
    void createSenderThreadsWithAmount_RepositoryError_ThrowsException() {
        // Arrange
        Integer amount = 2;
        when(senderThreadRepository.createSenderThreadsWithList(anyList()))
                .thenThrow(new ThreadManagementException("Database error", null));

        // Act & Assert
        assertThrows(ThreadManagementException.class, () ->
                senderThreadService.createSenderThreadsWithAmount(amount)
        );
    }

    @Test
    void createSenderThreadsWithAmount_NullAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                senderThreadService.createSenderThreadsWithAmount(null)
        );
    }

    @Test
    void updateSenderThread_ExistingThread_Success() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        SenderThreadDto inputDto = new SenderThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY);
        SenderThreadDto existingDto = new SenderThreadDto(threadId, ThreadType.RECEIVER, ThreadState.STOPPED, Thread.NORM_PRIORITY);

        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(senderThreadRepository.getSenderThreadById(threadId)).thenReturn(Optional.of(existingDto));
        when(senderThreadRepository.updateSenderThread(inputDto)).thenReturn(inputDto);

        // Act
        SenderThreadDto result = senderThreadService.updateSenderThread(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(ThreadState.RUNNING, result.getState());
        verify(executorService).submit(any(Runnable.class));
    }

    @Test
    void updateSenderThread_NonExistingThread_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        SenderThreadDto inputDto = new SenderThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY);
        when(senderThreadRepository.getSenderThreadById(threadId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                senderThreadService.updateSenderThread(inputDto)
        );
    }

    @Test
    void updateSenderThreadState_ExistingThread_Success() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        SenderThreadDto existingDto = new SenderThreadDto(threadId, ThreadType.RECEIVER, ThreadState.STOPPED, Thread.NORM_PRIORITY);

        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(senderThreadRepository.getSenderThreadById(threadId)).thenReturn(Optional.of(existingDto));
        when(senderThreadRepository.updateSenderThreadState(threadId, ThreadState.RUNNING)).thenReturn(threadId);

        // Act
        UUID response = senderThreadService.updateSenderThreadState(threadId, ThreadState.RUNNING);

        // Assert
        assertEquals(threadId, response);
        verify(executorService).submit(any(Runnable.class));
    }

    @Test
    void updateSenderThreadState_NullState_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                senderThreadService.updateSenderThreadState(threadId, null)
        );
    }

    @Test
    void updateSenderThreadState_ThreadNotFound_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        when(senderThreadRepository.getSenderThreadById(threadId))
                .thenThrow(new ThreadNotFoundException(threadId));

        // Act & Assert
        assertThrows(ThreadNotFoundException.class, () ->
                senderThreadService.updateSenderThreadState(threadId, ThreadState.RUNNING)
        );
    }

    @Test
    void updateSenderThreadPriority_ValidParameters_Success() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        Integer newPriority = Thread.MAX_PRIORITY;
        when(senderThreadRepository.updateSenderThreadPriority(threadId, newPriority)).thenReturn(threadId);

        // Act
        UUID result = senderThreadService.updateSenderThreadPriority(threadId, newPriority);

        // Assert
        assertEquals(threadId, result);
        verify(senderThreadRepository).updateSenderThreadPriority(threadId, newPriority);
    }

    @Test
    void updateSenderThreadPriority_NullPriority_ThrowsException() {
        // Arrange
        UUID threadId = UUID.randomUUID();
        when(senderThreadRepository.updateSenderThreadPriority(threadId, null))
                .thenThrow(new ThreadManagementException("Priority Can't Be Null", null));

        // Act & Assert
        assertThrows(ThreadManagementException.class, () ->
                senderThreadService.updateSenderThreadPriority(threadId, null)
        );
    }

    @Test
    void getActiveSenderThreads_ValidParameters_Success() {
        // Arrange
        List<SenderThreadDto> expectedThreads = Arrays.asList(
                new SenderThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY),
                new SenderThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY)
        );
        when(senderThreadRepository.getActiveSenderThreads()).thenReturn(expectedThreads);

        // Act
        List<SenderThreadDto> result = senderThreadService.getActiveSenderThreads();

        // Assert
        assertEquals(expectedThreads.size(), result.size());
        verify(senderThreadRepository).getActiveSenderThreads();
    }

    @Test
    void startSenderThreadsLifeCycle_ValidParameters_Success() {
        // Arrange
        List<SenderThreadDto> activeThreads = Arrays.asList(
                new SenderThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY),
                new SenderThreadDto(UUID.randomUUID(), ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY)
        );
        when(executorService.submit(any(Runnable.class))).thenReturn(mock(Future.class));
        when(senderThreadRepository.getActiveSenderThreads()).thenReturn(activeThreads);

        // Act
        Boolean response = senderThreadService.startSenderThreadsLifeCycle();

        // Assert
        assertTrue(response);
        verify(executorService, times(2)).submit(any(Runnable.class));
    }

    @Test
    void deleteAllSenderThreads_ValidParameters_Success() {
        // Arrange
        when(senderThreadRepository.deleteAllSenderThreads()).thenReturn(true);

        // Act
        Boolean result = senderThreadService.deleteAllSenderThreads();

        // Assert
        assertTrue(result);
        verify(senderThreadRepository).deleteAllSenderThreads();
    }

    @Test
    void createSenderThreadsWithAmount_WhenThreadStarted_ShouldAddDataToQueue() throws InterruptedException {
        // Arrange
        SenderThreadDto threadDto = new SenderThreadDto(threadId, ThreadType.SENDER, ThreadState.RUNNING, Thread.NORM_PRIORITY);
        CountDownLatch messageLatch = new CountDownLatch(1);

        when(senderThreadRepository.getSenderThreadById(any(UUID.class)))
                .thenReturn(Optional.of(threadDto));

        when(executorService.submit((Runnable) any()))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    CompletableFuture.runAsync(() -> {
                        runnable.run();
                        messageLatch.countDown();
                    });
                    return CompletableFuture.completedFuture(null);
                });

        // Act
        senderThreadService.createSenderThreadsWithAmount(1);

        // Assert
        messageLatch.await(5, TimeUnit.SECONDS);
        assertTrue(sharedQueue.peek() != null, "Queue should not be empty");
        String message = sharedQueue.poll();
        assertNotNull(message, "Message should not be null");
        assertTrue(message.contains("Data from sender"), "Message should contain sender data");
    }

    @Test
    void whenInterrupted_ShouldStopExecution() throws InterruptedException {
        // Arrange
        SenderThreadDto threadDto = new SenderThreadDto(threadId, ThreadType.SENDER, ThreadState.RUNNING, Thread.NORM_PRIORITY);

        CountDownLatch interruptedLatch = new CountDownLatch(1);
        @SuppressWarnings("unchecked")
        Future<?> mockFuture = mock(Future.class);

        when(executorService.submit((Runnable) any()))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    Thread thread = new Thread(() -> {
                        try {
                            runnable.run();
                        } finally {
                            interruptedLatch.countDown();
                        }
                    });
                    thread.start();
                    thread.interrupt();
                    return mockFuture;  // Return the mock Future instead of null
                });

        // Act
        senderThreadService.createSenderThreadsWithAmount(1);

        // Assert
        assertTrue(interruptedLatch.await(2, TimeUnit.SECONDS));
        assertTrue(sharedQueue.isEmpty());
        verify(mockFuture, never()).cancel(true);
    }
}