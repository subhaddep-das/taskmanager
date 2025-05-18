package com.subhadeep_das.taskmanager.service;

import com.subhadeep_das.taskmanager.dto.LoginRequest;
import com.subhadeep_das.taskmanager.dto.RegisterRequest;
import com.subhadeep_das.taskmanager.entity.Role;
import com.subhadeep_das.taskmanager.entity.User;
import com.subhadeep_das.taskmanager.exception.InvalidCredentialsException;
import com.subhadeep_das.taskmanager.exception.RoleNotFoundException;
import com.subhadeep_das.taskmanager.exception.UserAlreadyExistsException;
import com.subhadeep_das.taskmanager.repository.RoleRepository;
import com.subhadeep_das.taskmanager.repository.UserRepository;
import com.subhadeep_das.taskmanager.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        authService = new AuthService();
        authService.userRepository = userRepository;
        authService.roleRepository = roleRepository;
        authService.jwtTokenProvider = jwtTokenProvider;
    }

    @Test
    void registerUser_shouldRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest("John", "john123", "john@email.com", "pass123");

        when(userRepository.findByUsernameOrEmail("john123", "john@email.com"))
                .thenReturn(Optional.empty());

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(userRole));

        String result = authService.registerUser(request);
        assertEquals("User registered successfully", result);

        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void registerUser_shouldThrowUserAlreadyExistsException() {
        RegisterRequest request = new RegisterRequest("John", "john123", "john@email.com", "pass123");
        when(userRepository.findByUsernameOrEmail("john123", "john@email.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(request));
    }

    @Test
    void registerUser_shouldThrowRoleNotFoundException() {
        RegisterRequest request = new RegisterRequest("John", "john123", "john@email.com", "pass123");
        when(userRepository.findByUsernameOrEmail("john123", "john@email.com"))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> authService.registerUser(request));
    }

    @Test
    void loginUser_shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("john123", "pass123");
        User user = new User("John", "john123", "john@email.com", new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("pass123"));

        when(userRepository.findByUsernameOrEmail("john123", "john123"))
                .thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(user)).thenReturn("fake-jwt-token");

        String token = authService.loginUser(request);
        assertEquals("fake-jwt-token", token);
    }

    @Test
    void loginUser_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        LoginRequest request = new LoginRequest("john123", "wrong");
        when(userRepository.findByUsernameOrEmail("john123", "john123")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(request));
    }

    @Test
    void loginUser_shouldThrowInvalidCredentialsException_whenPasswordInvalid() {
        LoginRequest request = new LoginRequest("john123", "wrongpass");
        User user = new User("John", "john123", "john@email.com", new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("correctpass"));
        when(userRepository.findByUsernameOrEmail("john123", "john123")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(request));
    }
}
