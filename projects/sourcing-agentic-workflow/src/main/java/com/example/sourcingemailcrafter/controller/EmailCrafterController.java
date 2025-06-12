package com.example.sourcingemailcrafter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sourcingemailcrafter.model.EmailCrafterInput;
import com.example.sourcingemailcrafter.model.EmailCrafterOutput;
import com.example.sourcingemailcrafter.service.EmailCrafterService;

@RestController
@RequestMapping("/api/v1/email")
public class EmailCrafterController {

    private final EmailCrafterService emailCrafterService;

    @Autowired
    public EmailCrafterController(EmailCrafterService emailCrafterService) {
        this.emailCrafterService = emailCrafterService;
    }

    @PostMapping("/craft")
    public ResponseEntity<EmailCrafterOutput> craftEmail(@RequestBody EmailCrafterInput input) {
        EmailCrafterOutput output = emailCrafterService.craftEmail(input);
        return ResponseEntity.ok(output);
    }
} 