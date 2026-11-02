package com.carrental.service.impl;

import com.carrental.dto.AuthResponse;
import com.carrental.dto.LoginRequest;
import com.carrental.dto.RegisterRequest;
import com.carrental.entity.User;
import com.carrental.enums.Role;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.exception.UnauthorizedException;
import com.carrental.repository.UserRepository;
import com.carrental.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for user authentication and management.
 * Demonstrates Interface implementation, Collections (ArrayList), Encapsulation and Password Hashing.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("An account with email " + request.getEmail() + " already exists.");
        }

        // Secure password hashing - never store plain text passwords!
        String encodedPassword = passwordEncoder.encode(request.getPassword().trim());

        User newUser = new User(
                request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getPhone().trim(),
                encodedPassword,
                Role.ROLE_USER
        );

        User savedUser = userRepository.save(newUser);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                savedUser.getDisplayRole(),
                "Registration successful! Welcome to Car Rental."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!user.isActive()) {
            throw new UnauthorizedException("Your account is currently disabled. Please contact support.");
        }

        // Verify password hash
        if (!passwordEncoder.matches(request.getPassword().trim(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getDisplayRole(),
                "Login successful!"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllCustomers() {
        // Demonstrating Collections framework usage
        List<User> customers = userRepository.findByRole(Role.ROLE_USER);
        return new ArrayList<>(customers);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCustomers() {
        return userRepository.countByRole(Role.ROLE_USER);
    }
}
