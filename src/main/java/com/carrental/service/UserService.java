package com.carrental.service;

import com.carrental.dto.AuthResponse;
import com.carrental.dto.LoginRequest;
import com.carrental.dto.RegisterRequest;
import com.carrental.entity.User;

import java.util.List;

/**
 * Service interface demonstrating Abstraction in service layer.
 */
public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAllCustomers();
    long countCustomers();
}
