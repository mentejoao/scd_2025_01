package com.messaging.notification.service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/app")
public class AppController {

    private static final String MESSAGE = "Notification Service API is running!";

    @GetMapping(path = "/health")
    @ResponseStatus(HttpStatus.OK)
    public String health() {
        return MESSAGE;
    }
}
