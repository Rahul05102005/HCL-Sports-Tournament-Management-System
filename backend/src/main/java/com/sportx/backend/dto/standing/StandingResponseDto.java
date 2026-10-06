package com.sportx.backend.dto.standing;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandingResponseDto {
    private Long id;
    private Long tournamentId;
    private Long teamId;
    private String teamName;
    private String shortName;
    private String collegeName;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int scoreFor;
    private int scoreAgainst;
    private int goalDifference;
    private int points;
    private String formGuide;
    private int rank;
}
