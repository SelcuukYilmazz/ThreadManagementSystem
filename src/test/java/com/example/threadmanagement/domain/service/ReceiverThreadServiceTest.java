package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.domain.repository.ReceiverThreadRepository;
import com.example.threadmanagement.domain.repository.ReceiverThreadRepository;
import com.example.threadmanagement.exception.ThreadManagementException;
import com.example.threadmanagement.exception.ThreadNotFoundException;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.dto.ReceiverThreadDto;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiverThreadServiceTest {

    @Mock
    private ReceiverThreadRepository receiverThreadRepository;

    @Mock
    private ExecutorService executorService;

    private BlockingQueue<String> sharedQueue;
    private ReceiverThreadService receiverThreadService;
    private UUID threadId;

    @BeforeEach
    void setUp() {
        sharedQueue = new LinkedBlockingQueue<>();
        receiverThreadService = new ReceiverThreadService(sharedQueue, executorService, receiverThreadRepository);
        threadId = UUID.randomUUID();
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
            assertEquals(ThreadType.SENDER, thread.getType());
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
    void updateReceiverThreadPriority_ValidParameters_Success() {
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
    void getActiveReceiverThreads_ValidParameters_Success() {
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
    void startReceiverThreadsLifeCycle_ValidParameters_Success() {
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
    void deleteAllReceiverThreads_ValidParameters_Success() {
        // Arrange
        when(receiverThreadRepository.deleteAllReceiverThreads()).thenReturn(true);

        // Act
        Boolean result = receiverThreadService.deleteAllReceiverThreads();

        // Assert
        assertTrue(result);
        verify(receiverThreadRepository).deleteAllReceiverThreads();
    }


    @Test
    void whenThreadStarted_ShouldConsumeDataFromQueue() throws InterruptedException {
        // Arrange
        ReceiverThreadDto threadDto = new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY);
        CountDownLatch consumptionLatch = new CountDownLatch(1);

        lenient().when(receiverThreadRepository.getReceiverThreadById(any(UUID.class)))
                .thenReturn(Optional.of(threadDto));

        when(executorService.submit(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    CompletableFuture.runAsync(() -> {
                        try {
                            // Add test data to queue
                            sharedQueue.put("Test Data");
                            runnable.run();
                            consumptionLatch.countDown();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    return CompletableFuture.completedFuture(null);
                });

        // Act
        receiverThreadService.createReceiverThreadsWithAmount(1);

        // Assert
        assertTrue(consumptionLatch.await(5, TimeUnit.SECONDS), "Data consumption timed out");
        assertTrue(sharedQueue.isEmpty(), "Queue should be empty after consumption");
    }

    @Test
    void whenThreadStopped_ShouldStopConsumingData() throws InterruptedException {
        // Arrange
        AtomicBoolean threadStopped = new AtomicBoolean(false);
        CountDownLatch stopLatch = new CountDownLatch(1);

        ReceiverThreadDto runningThread = new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY);
        ReceiverThreadDto stoppedThread = new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.STOPPED, Thread.NORM_PRIORITY);

        lenient().when(receiverThreadRepository.getReceiverThreadById(any(UUID.class)))
                .thenAnswer(invocation -> {
                    if (threadStopped.get()) {
                        return Optional.of(stoppedThread);
                    }
                    return Optional.of(runningThread);
                });

        Future<?> mockFuture = mock(Future.class);
        when(mockFuture.cancel(true)).thenReturn(true);

        when(executorService.submit(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    CompletableFuture.runAsync(() -> {
                        try {
                            runnable.run();
                        } finally {
                            stopLatch.countDown();
                        }
                    });
                    return mockFuture;
                });

        // Act
        receiverThreadService.createReceiverThreadsWithAmount(1);
        Thread.sleep(100);
        threadStopped.set(true);

        // Assert
        assertTrue(stopLatch.await(5, TimeUnit.SECONDS), "Thread did not stop within timeout");
        verify(mockFuture, timeout(2000)).cancel(true);
    }

    @Test
    void whenThreadPriorityChanged_ShouldUpdateThreadPriority() throws InterruptedException {
        // Arrange
        CountDownLatch priorityLatch = new CountDownLatch(1);
        AtomicInteger threadPriority = new AtomicInteger(Thread.NORM_PRIORITY);

        lenient().when(receiverThreadRepository.getReceiverThreadById(any(UUID.class)))
                .thenAnswer(invocation -> Optional.of(
                        new ReceiverThreadDto(
                                threadId,
                                ThreadType.RECEIVER,
                                ThreadState.RUNNING,
                                threadPriority.get()
                        )
                ));

        lenient().when(receiverThreadRepository.updateReceiverThreadPriority(any(UUID.class), anyInt()))
                .thenAnswer(invocation -> {
                    threadPriority.set(invocation.getArgument(1));
                    return invocation.getArgument(0);
                });

        when(executorService.submit(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    CompletableFuture.runAsync(() -> {
                        try {
                            runnable.run();
                        } finally {
                            if (threadPriority.get() == Thread.MAX_PRIORITY) {
                                priorityLatch.countDown();
                            }
                        }
                    });
                    return CompletableFuture.completedFuture(null);
                });

        // Act
        receiverThreadService.createReceiverThreadsWithAmount(1);
        Thread.sleep(100);
        receiverThreadService.updateReceiverThreadPriority(threadId, Thread.MAX_PRIORITY);

        // Assert
        assertTrue(priorityLatch.await(5, TimeUnit.SECONDS),
                "Priority change was not detected within timeout period");
        assertEquals(Thread.MAX_PRIORITY, threadPriority.get(),
                "Thread priority was not updated to MAX_PRIORITY");
    }

    @Test
    void whenThreadDeleted_ShouldStopExecution() throws InterruptedException {
        // Arrange
        CountDownLatch deleteLatch = new CountDownLatch(1);
        AtomicBoolean threadDeleted = new AtomicBoolean(false);

        lenient().when(receiverThreadRepository.getReceiverThreadById(any(UUID.class)))
                .thenAnswer(invocation -> {
                    if (threadDeleted.get()) {
                        return Optional.empty();
                    }
                    return Optional.of(new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY));
                });

        lenient().when(receiverThreadRepository.deleteReceiverThreadById(any(UUID.class)))
                .thenAnswer(invocation -> {
                    threadDeleted.set(true);
                    return invocation.getArgument(0);
                });

        Future<?> mockFuture = mock(Future.class);
        when(mockFuture.cancel(true)).thenReturn(true);

        when(executorService.submit(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    CompletableFuture.runAsync(() -> {
                        try {
                            runnable.run();
                        } finally {
                            deleteLatch.countDown();
                        }
                    });
                    return mockFuture;
                });

        // Act
        receiverThreadService.createReceiverThreadsWithAmount(1);
        Thread.sleep(100);
        receiverThreadService.deleteReceiverThreadById(threadId);

        // Assert
        assertTrue(deleteLatch.await(5, TimeUnit.SECONDS), "Thread deletion timed out");
        verify(mockFuture, timeout(2000)).cancel(true);
        assertTrue(threadDeleted.get(), "Thread was not deleted");
    }

    @Test
    void whenInterrupted_ShouldStopExecution() throws InterruptedException {
        // Arrange
        CountDownLatch interruptLatch = new CountDownLatch(1);

        lenient().when(receiverThreadRepository.getReceiverThreadById(any(UUID.class)))
                .thenReturn(Optional.of(new ReceiverThreadDto(threadId, ThreadType.RECEIVER, ThreadState.RUNNING, Thread.NORM_PRIORITY)));

        when(executorService.submit(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    Thread thread = new Thread(() -> {
                        try {
                            runnable.run();
                        } finally {
                            interruptLatch.countDown();
                        }
                    });
                    thread.start();
                    thread.interrupt();
                    return CompletableFuture.completedFuture(null);
                });

        // Act
        receiverThreadService.createReceiverThreadsWithAmount(1);

        // Assert
        assertTrue(interruptLatch.await(5, TimeUnit.SECONDS), "Thread interruption timed out");
        assertTrue(sharedQueue.isEmpty(), "Queue should be empty after thread interruption");
    }
}