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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired 
    public UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired 
    public JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register User
    public String registerUser(RegisterRequest registerRequest) {
        logger.info("Attempting to register user with username: {} and email: {}", registerRequest.getUsername(), registerRequest.getEmail());

        if (userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail()).isPresent()) {
            logger.warn("Registration failed: Username or Email already taken");
            throw new UserAlreadyExistsException("Username or Email already taken");
        }

        User user = new User(registerRequest.getName(), registerRequest.getUsername(), registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    logger.error("ROLE_USER not found in database");
                    return new RoleNotFoundException("ROLE_USER not found");
                });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        logger.info("User registered successfully: {}", registerRequest.getUsername());
        return "User registered successfully";
    }

    // Login User
    public String loginUser(LoginRequest loginRequest) {
        logger.info("Login attempt for username/email: {}", loginRequest.getUsernameOrEmail());

        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: Invalid credentials for username/email: {}", loginRequest.getUsernameOrEmail());
                    return new InvalidCredentialsException("Invalid username/email or password");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Incorrect password for user: {}", loginRequest.getUsernameOrEmail());
            throw new InvalidCredentialsException("Invalid username/email or password");
        }

        String token = jwtTokenProvider.generateToken(user);
        logger.info("Login successful for user: {} | JWT generated", user.getUsername());
        return token;
    }
}
