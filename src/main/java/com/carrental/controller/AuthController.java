package com.carrental.controller;

import com.carrental.dto.ApiResponse;
import com.carrental.dto.AuthResponse;
import com.carrental.dto.LoginRequest;
import com.carrental.dto.RegisterRequest;
import com.carrental.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String SESSION_USER_KEY = "LOGGED_IN_USER";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request, 
                                                              HttpSession session) {
        AuthResponse response = userService.register(request);
        // Automatically log the user in after registration
        session.setAttribute(SESSION_USER_KEY, response);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, 
                                                           HttpSession session) {
        AuthResponse response = userService.login(request);
        session.setAttribute(SESSION_USER_KEY, response);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY);
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(HttpSession session) {
        AuthResponse user = (AuthResponse) session.getAttribute(SESSION_USER_KEY);
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Not authenticated", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Current user session", user));
    }
}
