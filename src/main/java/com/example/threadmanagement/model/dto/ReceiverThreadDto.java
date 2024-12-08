package com.example.threadmanagement.model.dto;

import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ReceiverThreadDto {
    private UUID id;
    private ThreadType type;
    private ThreadState state;
    private Integer priority;
}
