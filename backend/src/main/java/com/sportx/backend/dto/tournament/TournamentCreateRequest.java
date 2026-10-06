package com.sportx.backend.dto.tournament;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentCreateRequest {

    @NotBlank(message = "Tournament name is required")
    private String name;

    @NotNull(message = "Sport ID is required")
    private Long sportId;

    @NotBlank(message = "Format is required (ROUND_ROBIN, KNOCKOUT, HYBRID)")
    private String format;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Integer minTeams;
    private Integer maxTeams;
    private String description;
}
