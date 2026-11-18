package com.cfs.RideTap.auth.controller;

import com.cfs.RideTap.auth.dto.request.LoginRequest;
import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.auth.dto.response.LoginResponse;
import com.cfs.RideTap.auth.dto.response.MessageResponse;
import com.cfs.RideTap.auth.dto.response.RegisterResponse;
import com.cfs.RideTap.auth.service.interfaces.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Bearer token is required");
        }

        String token = authorization.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }
}
