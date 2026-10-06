package com.sportx.core.model;

/**
 * Modern Java Record representing aggregated player performance statistics.
 */
public record PlayerStatRecord(
        Long playerId,
        String playerName,
        String teamName,
        String position,
        int goals,
        int assists,
        int yellowCards,
        int redCards,
        int minutesPlayed
) {}
