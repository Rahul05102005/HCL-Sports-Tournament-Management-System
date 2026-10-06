package com.sportx.core.model;

import com.sportx.core.domain.Team;

/**
 * Record representing a decisive win/loss outcome.
 * Demonstrates: Sealed class permitted record.
 */
public record WinResult(
        Team winner,
        Team loser,
        int homeScore,
        int awayScore,
        String winningGoalDetails
) implements MatchResult {
    @Override
    public String getSummary() {
        return "%s defeated %s (%d - %d). %s".formatted(
                winner.getName(), loser.getName(), homeScore, awayScore, winningGoalDetails
        );
    }
}
