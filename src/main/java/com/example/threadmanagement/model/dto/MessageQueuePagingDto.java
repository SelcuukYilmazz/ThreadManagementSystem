package com.example.threadmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation that creates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Creates a no-args constructor
@AllArgsConstructor // Creates a constructor with all fields
public class MessageQueuePagingDto {
    private int page = 0;    // default value
    private int size = 14;   // default value
}