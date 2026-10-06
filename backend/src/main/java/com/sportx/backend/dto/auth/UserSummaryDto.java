package com.sportx.backend.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
}
