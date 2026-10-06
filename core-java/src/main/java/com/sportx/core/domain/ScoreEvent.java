package com.sportx.core.domain;

import com.sportx.core.domain.enums.EventType;

import java.util.Objects;

/**
 * Domain entity representing an in-game discrete event (Goal, Card, Foul, etc.).
 */
public class ScoreEvent extends BaseEntity {
    private final Long matchId;
    private final Long teamId;
    private final Player player;
    private final EventType eventType;
    private final int minuteOffset;
    private final int pointsAwarded;
    private final String description;

    public ScoreEvent(Long id, Long matchId, Long teamId, Player player,
                      EventType eventType, int minuteOffset, int pointsAwarded, String description) {
        super(id);
        this.matchId = matchId;
        this.teamId = teamId;
        this.player = player;
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.minuteOffset = Math.max(0, minuteOffset);
        this.pointsAwarded = pointsAwarded;
        this.description = description != null ? description : "";
    }

    public Long getMatchId() {
        return matchId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public Player getPlayer() {
        return player;
    }

    public EventType getEventType() {
        return eventType;
    }

    public int getMinuteOffset() {
        return minuteOffset;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[%d'] %s by %s - %s (+%d pts)".formatted(
                minuteOffset,
                eventType,
                player != null ? player.getName() : "Team",
                description,
                pointsAwarded
        );
    }
}
