package com.sportx.backend.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateRequest {

    @NotBlank(message = "Team name is required")
    private String name;

    private String shortName;

    @NotBlank(message = "College name is required")
    private String collegeName;

    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;

    private String logoUrl;
}
