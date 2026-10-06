package com.sportx.core.model;

public record AbandonedResult(
        String reason,
        int homeScore,
        int awayScore
) implements MatchResult {
    @Override
    public String getSummary() {
        return "Match Abandoned due to: %s (Current Score: %d - %d)".formatted(
                reason, homeScore, awayScore
        );
    }
}
