package com.example.threadmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "thrd.ReceiverThreads")
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverThreadEntity {
    @Id
    private UUID id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ThreadType type;

    @Column(name = "threadState", nullable = false)
    @Enumerated(EnumType.STRING)
    private ThreadState state;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    public ReceiverThreadEntity(ThreadType type, ThreadState state, Integer priority)
    {
        this.type = type;
        this.state = state;
        this.priority = priority;
    }
}