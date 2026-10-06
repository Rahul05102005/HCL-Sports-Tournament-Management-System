package com.sportx.backend.dto.match;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixtureCreateRequest {

    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;

    @NotNull(message = "Home team ID is required")
    private Long homeTeamId;

    @NotNull(message = "Away team ID is required")
    private Long awayTeamId;

    private Long venueId;
    private Long refereeId;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;

    private String roundName;
}
