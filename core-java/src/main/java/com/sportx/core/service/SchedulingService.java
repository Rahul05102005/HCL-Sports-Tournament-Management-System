package com.sportx.core.service;

import com.sportx.core.domain.*;
import com.sportx.core.exceptions.FixtureConflictException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Smart Scheduling Service with Conflict Detection and Alternative Slot Suggestions.
 * Demonstrates: Modern java.time (Duration, LocalDateTime), Collections, Exception handling.
 */
public class SchedulingService {

    // Minimum required resting period between matches for the same team (3 hours)
    private static final Duration MIN_TEAM_REST = Duration.ofHours(3);
    // Minimum buffer period for venues between matches (2 hours)
    private static final Duration MIN_VENUE_BUFFER = Duration.ofHours(2);

    /**
     * Validates scheduling constraints for a proposed fixture against existing scheduled fixtures.
     * Throws FixtureConflictException with actionable alternatives if a clash is detected.
     */
    public void validateSchedule(Fixture proposedFixture, List<Fixture> existingFixtures) {
        LocalDateTime proposedTime = proposedFixture.getScheduledTime();
        Team home = proposedFixture.getHomeTeam();
        Team away = proposedFixture.getAwayTeam();
        Venue venue = proposedFixture.getVenue();
        Referee referee = proposedFixture.getReferee();

        for (Fixture existing : existingFixtures) {
            if (Objects.equals(existing.getId(), proposedFixture.getId())) continue;

            LocalDateTime existingTime = existing.getScheduledTime();
            Duration timeDiff = Duration.between(existingTime, proposedTime).abs();

            // 1. Team Conflict & Rest Period Check
            boolean teamClash = (existing.getHomeTeam().equals(home) || existing.getAwayTeam().equals(home)
                    || existing.getHomeTeam().equals(away) || existing.getAwayTeam().equals(away));

            if (teamClash && timeDiff.compareTo(MIN_TEAM_REST) < 0) {
                Team conflictingTeam = (existing.getHomeTeam().equals(home) || existing.getAwayTeam().equals(home)) ? home : away;
                List<LocalDateTime> alternatives = calculateSuggestedSlots(existingTime, MIN_TEAM_REST);
                throw new FixtureConflictException(
                        "Team Rest Violation",
                        "Team '%s' already has a match scheduled at %s. Minimum required rest period is %d hours. Suggested slots: %s"
                                .formatted(conflictingTeam.getName(), existingTime, MIN_TEAM_REST.toHours(), alternatives),
                        proposedTime
                );
            }

            // 2. Venue Conflict Check
            if (venue != null && existing.getVenue() != null && venue.equals(existing.getVenue())) {
                if (timeDiff.compareTo(MIN_VENUE_BUFFER) < 0) {
                    List<LocalDateTime> alternatives = calculateSuggestedSlots(existingTime, MIN_VENUE_BUFFER);
                    throw new FixtureConflictException(
                            "Venue Double-Booking",
                            "Venue '%s' is already booked for another match at %s. Suggested slots: %s"
                                    .formatted(venue.getName(), existingTime, alternatives),
                            proposedTime
                    );
                }
            }

            // 3. Referee Conflict Check
            if (referee != null && existing.getReferee() != null && referee.equals(existing.getReferee())) {
                if (timeDiff.compareTo(MIN_VENUE_BUFFER) < 0) {
                    List<LocalDateTime> alternatives = calculateSuggestedSlots(existingTime, MIN_VENUE_BUFFER);
                    throw new FixtureConflictException(
                            "Referee Double-Booking",
                            "Referee '%s' is already assigned to a match at %s. Suggested slots: %s"
                                    .formatted(referee.getName(), existingTime, alternatives),
                            proposedTime
                    );
                }
            }
        }
    }

    private List<LocalDateTime> calculateSuggestedSlots(LocalDateTime clashTime, Duration requiredBuffer) {
        return List.of(
                clashTime.plus(requiredBuffer),
                clashTime.plus(requiredBuffer).plusHours(1),
                clashTime.plus(requiredBuffer).plusHours(2)
        );
    }
}
