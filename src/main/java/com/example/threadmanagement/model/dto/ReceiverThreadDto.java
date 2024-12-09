package com.example.threadmanagement.model.dto;

import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiverThreadDto {

    /**
     * Unique identifier for the receiver thread.
     */
    private UUID id;

    /**
     * Type of the thread, specifically set to RECEIVER for this DTO.
     * Defined in the ThreadType enum.
     */
    private ThreadType type;

    /**
     * Current state of the thread (RUNNING or STOPPED).
     * Defined in the ThreadState enum.
     */
    private ThreadState state;

    /**
     * Priority level of the thread.
     * Higher values indicate higher priority for thread execution.
     */
    private Integer priority;
}
