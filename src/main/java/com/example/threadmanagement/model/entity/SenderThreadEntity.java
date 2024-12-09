package com.example.threadmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Entity
@Table(name = "thrd.SenderThreads")
@NoArgsConstructor
@AllArgsConstructor
public class SenderThreadEntity {

    /**
     * Unique identifier for the sender thread.
     * Serves as the primary key in the database.
     */
    @Id
    private UUID id;

    /**
     * Type of the thread, stored as a string in the database.
     * Cannot be null and is mapped to ThreadType enum.
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ThreadType type;

    /**
     * Current state of the thread, stored as a string in the database.
     * Cannot be null and is mapped to ThreadState enum.
     */
    @Column(name = "threadState", nullable = false)
    @Enumerated(EnumType.STRING)
    private ThreadState state;

    /**
     * Priority level of the thread in the database.
     * Cannot be null, higher values indicate higher priority.
     */
    @Column(name = "priority", nullable = false)
    private Integer priority;

}