package com.subhadeep_das.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhadeep_das.taskmanager.dto.LoginRequest;
import com.subhadeep_das.taskmanager.dto.RegisterRequest;
import com.subhadeep_das.taskmanager.security.JwtAuthenticationEntryPoint;
import com.subhadeep_das.taskmanager.security.JwtTokenProvider;
import com.subhadeep_das.taskmanager.service.AuthService;
import com.subhadeep_das.taskmanager.service.CustomUserDetailsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    
    @MockBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "john123", "john@email.com", "pass123");

        when(authService.registerUser(any())).thenReturn("User registered successfully");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void testLoginUser() throws Exception {
        LoginRequest request = new LoginRequest("john123", "pass123");

        when(authService.loginUser(any())).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-jwt-token"));
    }
}
