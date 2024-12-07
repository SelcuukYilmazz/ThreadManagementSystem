package com.example.threadmanagement.domain.service;

import com.example.threadmanagement.model.entity.ThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiverThreadService implements Runnable {

    @Override
    public void run() {

    }
}