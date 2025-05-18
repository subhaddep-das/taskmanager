package com.subhadeep_das.taskmanager.controller;

import com.subhadeep_das.taskmanager.dto.LoginRequest;
import com.subhadeep_das.taskmanager.dto.RegisterRequest;
import com.subhadeep_das.taskmanager.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
    	logger.info("Attempting to register user with email: {}", registerRequest.getEmail());
        String message = authService.registerUser(registerRequest);
        return ResponseEntity.ok(message);
    }

    // Login User
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Attempting login for username: {}", loginRequest.getUsernameOrEmail());
        return authService.loginUser(loginRequest);
    }
}
