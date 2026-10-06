package com.sportx.core.model;

/**
 * Modern Java Record representing standings table row for presentation.
 */
public record StandingRecord(
        int rank,
        Long teamId,
        String teamName,
        String shortName,
        int played,
        int won,
        int drawn,
        int lost,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        int points,
        int fairPlayPoints
) {}
