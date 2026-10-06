package com.sportx.core.model;

import java.time.LocalDate;

/**
 * Modern Java Record demonstrating immutable DTOs for API/Service responses.
 */
public record TournamentResponse(
        Long id,
        String name,
        String code,
        String sportName,
        String format,
        LocalDate startDate,
        LocalDate endDate,
        int teamCount,
        int maxTeams
) {
    public TournamentResponse {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Record name cannot be blank");
        }
    }
}
