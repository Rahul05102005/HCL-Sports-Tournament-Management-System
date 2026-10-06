package com.sportx.core.model;

import com.sportx.core.domain.Team;

/**
 * Record representing a drawn match outcome.
 */
public record DrawResult(
        Team homeTeam,
        Team awayTeam,
        int homeScore,
        int awayScore
) implements MatchResult {

    public DrawResult(Team homeTeam, Team awayTeam, int score) {
        this(homeTeam, awayTeam, score, score);
    }

    @Override
    public String getSummary() {
        return "Draw between %s and %s (%d - %d)".formatted(
                homeTeam.getName(), awayTeam.getName(), homeScore, awayScore
        );
    }
}
