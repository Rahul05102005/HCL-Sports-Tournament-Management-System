package com.sportx.backend.service;

import com.sportx.backend.dto.auth.*;
import com.sportx.backend.entity.RoleEntity;
import com.sportx.backend.entity.UserEntity;
import com.sportx.backend.exception.BadRequestException;
import com.sportx.backend.exception.ResourceNotFoundException;
import com.sportx.backend.repository.RoleRepository;
import com.sportx.backend.repository.UserRepository;
import com.sportx.backend.security.JwtTokenProvider;
import com.sportx.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(principal.getUsername());

        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(principal.getId())
                .username(principal.getUsername())
                .email(principal.getEmail())
                .fullName(userRepository.findById(principal.getId()).map(UserEntity::getFullName).orElse(principal.getUsername()))
                .role(principal.getAuthorities().iterator().next().getAuthority())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(userSummary)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        String requestedRole = request.getRole() != null ? request.getRole() : "ROLE_ORGANIZER";
        final String roleName = requestedRole.startsWith("ROLE_") ? requestedRole : "ROLE_" + requestedRole.toUpperCase();

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(roleName).build()));

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(role)
                .active(true)
                .build();

        user = userRepository.save(user);

        return login(new LoginRequest(request.getUsername(), request.getPassword()));
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        String email = request.getEmail();
        if ((email == null || email.isBlank()) && request.getCredential() != null && !request.getCredential().isBlank()) {
            try {
                String[] parts = request.getCredential().split("\\.");
                if (parts.length >= 2) {
                    byte[] decoded = java.util.Base64.getUrlDecoder().decode(parts[1]);
                    String payloadJson = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
                    com.fasterxml.jackson.databind.JsonNode rootNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(payloadJson);
                    if (rootNode.has("email")) {
                        email = rootNode.get("email").asText();
                    }
                    if (request.getName() == null && rootNode.has("name")) {
                        request.setName(rootNode.get("name").asText());
                    }
                    if (request.getGoogleId() == null && rootNode.has("sub")) {
                        request.setGoogleId(rootNode.get("sub").asText());
                    }
                    if (request.getAvatarUrl() == null && rootNode.has("picture")) {
                        request.setAvatarUrl(rootNode.get("picture").asText());
                    }
                }
            } catch (Exception ignored) {}
        }

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required for Google Sign-In");
        }

        email = email.trim().toLowerCase();
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            String baseUsername = email.contains("@") 
                    ? email.substring(0, email.indexOf('@')).replaceAll("[^a-zA-Z0-9_]", "_") 
                    : "user";
            String candidateUsername = baseUsername;
            int suffix = 1;
            while (userRepository.existsByUsername(candidateUsername)) {
                candidateUsername = baseUsername + suffix++;
            }

            RoleEntity playerRole = roleRepository.findByName("ROLE_PLAYER")
                    .orElseGet(() -> roleRepository.save(RoleEntity.builder().name("ROLE_PLAYER").build()));

            String fullName = (request.getName() != null && !request.getName().isBlank()) 
                    ? request.getName().trim() 
                    : baseUsername;

            user = UserEntity.builder()
                    .username(candidateUsername)
                    .email(email)
                    .fullName(fullName)
                    .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                    .role(playerRole)
                    .active(true)
                    .build();

            user = userRepository.save(user);
        }

        String accessToken = tokenProvider.generateTokenFromUsername(user.getUsername(), user.getId(), user.getRole().getName());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(userSummary)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromJwt(request.getRefreshToken());
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        String newAccessToken = tokenProvider.generateTokenFromUsername(user.getUsername(), user.getId(), user.getRole().getName());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getUsername());

        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(userSummary)
                .build();
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User is not authenticated");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));

        return UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();
    }
}
