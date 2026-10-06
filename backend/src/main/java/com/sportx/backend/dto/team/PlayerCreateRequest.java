package com.sportx.backend.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerCreateRequest {

    @NotBlank(message = "Player full name is required")
    private String fullName;

    private Integer jerseyNumber;
    private String position;
    private String studentRollNo;
    private String contactEmail;
    private Boolean isCaptain;
}
