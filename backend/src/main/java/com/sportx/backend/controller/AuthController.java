package com.sportx.backend.controller;

import com.sportx.backend.dto.auth.*;
import com.sportx.backend.dto.common.ApiResponse;
import com.sportx.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login, registration, token refresh and profile lookup")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate with username/password and receive JWT access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/google")
    @Operation(summary = "Google Sign-In", description = "Authenticate or auto-provision user using Google credentials")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        AuthResponse response = authService.loginWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.ok("Google login successful", response));
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Register a new account (ORGANIZER, REFEREE, PLAYER, etc.)")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(ApiResponse.ok("Registration successful", response), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Access Token", description = "Obtain a fresh JWT access token using a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Retrieve profile details of the authenticated caller")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getCurrentUser(Authentication authentication) {
        UserSummaryDto user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(ApiResponse.ok("Current user profile retrieved", user));
    }
}
