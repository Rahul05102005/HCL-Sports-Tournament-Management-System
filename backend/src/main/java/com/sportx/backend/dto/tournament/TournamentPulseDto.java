package com.sportx.backend.dto.tournament;

import com.sportx.backend.dto.match.MatchResponseDto;
import com.sportx.backend.dto.standing.StandingResponseDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentPulseDto {
    private Long tournamentId;
    private String tournamentName;
    private String sportName;
    private String status;
    private int totalTeams;
    private int totalFixtures;
    private int completedMatches;
    private int liveMatchesCount;
    private double completionPercentage;
    private StandingResponseDto leaderTeam;
    private List<MatchResponseDto> liveMatches;
    private List<MatchResponseDto> upcomingMatches;
    private List<MatchResponseDto> recentResults;
    private List<String> recentAnnouncements;
}
