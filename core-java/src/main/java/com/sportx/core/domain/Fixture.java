package com.sportx.core.domain;

import com.sportx.core.domain.enums.FixtureStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a generated tournament fixture.
 */
public class Fixture extends BaseEntity {
    private final Long tournamentId;
    private final int roundNumber;
    private final Team homeTeam;
    private final Team awayTeam;
    private Venue venue;
    private Referee referee;
    private LocalDateTime scheduledTime;
    private FixtureStatus status;

    public Fixture(Long id, Long tournamentId, int roundNumber,
                   Team homeTeam, Team awayTeam, Venue venue,
                   Referee referee, LocalDateTime scheduledTime) {
        super(id);
        Objects.requireNonNull(homeTeam, "Home team cannot be null");
        Objects.requireNonNull(awayTeam, "Away team cannot be null");
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("A team cannot play against itself");
        }
        this.tournamentId = tournamentId;
        this.roundNumber = roundNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.venue = venue;
        this.referee = referee;
        this.scheduledTime = scheduledTime != null ? scheduledTime : LocalDateTime.now();
        this.status = FixtureStatus.SCHEDULED;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Referee getReferee() {
        return referee;
    }

    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public FixtureStatus getStatus() {
        return status;
    }

    public void setStatus(FixtureStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "R%d: %s vs %s [%s @ %s, Ref: %s]".formatted(
                roundNumber,
                homeTeam.getShortName(),
                awayTeam.getShortName(),
                status,
                scheduledTime,
                referee != null ? referee.getName() : "TBD"
        );
    }
}
