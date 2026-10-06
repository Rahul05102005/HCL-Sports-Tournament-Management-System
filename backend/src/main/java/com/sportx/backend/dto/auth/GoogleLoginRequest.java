package com.sportx.backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleLoginRequest {

    private String email;

    private String name;

    private String googleId;

    private String avatarUrl;

    private String credential;

    private String idToken;
}

