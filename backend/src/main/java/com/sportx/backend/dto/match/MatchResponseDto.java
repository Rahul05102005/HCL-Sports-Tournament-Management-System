package com.sportx.backend.dto.match;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponseDto {
    private Long id;
    private Long fixtureId;
    private Long tournamentId;
    private String tournamentName;
    private Long homeTeamId;
    private String homeTeamName;
    private String homeCollegeName;
    private Long awayTeamId;
    private String awayTeamName;
    private String awayCollegeName;
    private int homeScore;
    private int awayScore;
    private String matchStatus; // SCHEDULED, LIVE, HALF_TIME, COMPLETED, ABANDONED
    private Integer currentMinute;
    private LocalDateTime scheduledTime;
    private String venueName;
    private String venueLocation;
    private String refereeName;
    private String roundName;
    private Long winnerTeamId;
    private String winnerTeamName;
    private boolean isDraw;
    private String notes;
}
