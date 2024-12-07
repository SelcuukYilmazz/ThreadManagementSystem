package com.example.threadmanagement.application.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    public HomeController() {
    }
    // Root endpoint
    @GetMapping("/")
    public String home() {
        return "Application is running.";
    }
}