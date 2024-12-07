package com.example.threadmanagement.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class ThreadRequestDto {
    @Min(1)
    @Max(20)
    private int senderCount;

    @Min(1)
    @Max(20)
    private int receiverCount;
}