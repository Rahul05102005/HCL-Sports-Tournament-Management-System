package com.sportx.backend.dto.match;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixtureResponseDto {
    private Long id;
    private Long tournamentId;
    private String tournamentName;
    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;
    private Long venueId;
    private String venueName;
    private Long refereeId;
    private String refereeName;
    private LocalDateTime scheduledTime;
    private String roundName;
    private String status;
}
