package com.sportx.core.domain;

import com.sportx.core.domain.enums.EventType;
import com.sportx.core.domain.enums.FixtureStatus;
import com.sportx.core.domain.enums.MatchStatus;
import com.sportx.core.exceptions.MatchAccessDeniedException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Domain entity representing an active, live, or completed sports match.
 * Demonstrates: Encapsulation, State transitions, Access control (IDOR prevention), Collections.
 */
public class Match extends BaseEntity {
    private final Fixture fixture;
    private MatchStatus status;
    private int homeScore;
    private int awayScore;
    private int currentMinute;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Team winner;
    private Referee referee;
    private final List<ScoreEvent> scoreEvents = new ArrayList<>();

    public Match(Long id, Fixture fixture, Referee referee) {
        super(id);
        this.fixture = Objects.requireNonNull(fixture, "Fixture cannot be null");
        this.referee = referee;
        this.status = MatchStatus.UPCOMING;
        this.homeScore = 0;
        this.awayScore = 0;
        this.currentMinute = 0;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public int getCurrentMinute() {
        return currentMinute;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public Optional<Team> getWinner() {
        return Optional.ofNullable(winner);
    }

    public Referee getReferee() {
        return referee;
    }

    public List<ScoreEvent> getScoreEvents() {
        return Collections.unmodifiableList(scoreEvents);
    }

    /**
     * Start match transition with referee authentication.
     */
    public synchronized void startMatch(Referee officiatingReferee) {
        validateRefereeOwnership(officiatingReferee);
        if (status != MatchStatus.UPCOMING) {
            throw new IllegalStateException("Match cannot be started from status: " + status);
        }
        this.status = MatchStatus.LIVE;
        this.startedAt = LocalDateTime.now();
        this.fixture.setStatus(FixtureStatus.IN_PROGRESS);
    }

    /**
     * Add score event with referee ownership verification.
     */
    public synchronized ScoreEvent recordScoreEvent(Referee actor, Team team, Player player,
                                                    EventType eventType, int minuteOffset,
                                                    int pointsAwarded, String description) {
        validateRefereeOwnership(actor);
        if (status != MatchStatus.LIVE) {
            throw new IllegalStateException("Score events can only be recorded during a LIVE match");
        }

        this.currentMinute = Math.max(this.currentMinute, minuteOffset);
        if (eventType == EventType.GOAL || eventType == EventType.POINT || eventType == EventType.RUN) {
            if (team.equals(fixture.getHomeTeam())) {
                this.homeScore += pointsAwarded;
            } else if (team.equals(fixture.getAwayTeam())) {
                this.awayScore += pointsAwarded;
            }
        }

        ScoreEvent event = new ScoreEvent(
                (long) (scoreEvents.size() + 1),
                this.getId(),
                team.getId(),
                player,
                eventType,
                minuteOffset,
                pointsAwarded,
                description
        );
        this.scoreEvents.add(event);
        return event;
    }

    /**
     * Finalize match with referee ownership check and winner determination.
     */
    public synchronized void finishMatch(Referee actor) {
        validateRefereeOwnership(actor);
        if (status != MatchStatus.LIVE) {
            throw new IllegalStateException("Cannot finish match that is not currently LIVE");
        }
        this.status = MatchStatus.COMPLETED;
        this.finishedAt = LocalDateTime.now();
        this.currentMinute = 90;
        this.fixture.setStatus(FixtureStatus.COMPLETED);

        if (homeScore > awayScore) {
            this.winner = fixture.getHomeTeam();
        } else if (awayScore > homeScore) {
            this.winner = fixture.getAwayTeam();
        } else {
            this.winner = null; // Draw
        }

        if (this.referee != null) {
            this.referee.incrementMatchesOfficiated();
        }
    }

    private void validateRefereeOwnership(Referee actor) {
        if (this.referee != null && actor != null && !Objects.equals(this.referee.getId(), actor.getId())) {
            throw new MatchAccessDeniedException(
                    "Referee '%s' (ID: %d) is not authorized to officiate Match #%d assigned to '%s' (ID: %d)".formatted(
                            actor.getName(), actor.getId(), getId(), referee.getName(), referee.getId()
                    )
            );
        }
    }

    @Override
    public String toString() {
        return "%s %d - %d %s [%s, Min: %d']".formatted(
                fixture.getHomeTeam().getShortName(),
                homeScore,
                awayScore,
                fixture.getAwayTeam().getShortName(),
                status,
                currentMinute
        );
    }
}
