package com.sportx.core.exceptions;

import java.time.LocalDateTime;

/**
 * Unchecked exception thrown when fixture scheduling detects venue, team, or referee clashes.
 * Demonstrates: Smart Scheduling conflict detection.
 */
public class FixtureConflictException extends RuntimeException {
    private final String conflictType;
    private final String conflictDetail;
    private final LocalDateTime conflictTime;

    public FixtureConflictException(String conflictType, String conflictDetail, LocalDateTime conflictTime) {
        super("Scheduling Conflict [%s]: %s at %s".formatted(conflictType, conflictDetail, conflictTime));
        this.conflictType = conflictType;
        this.conflictDetail = conflictDetail;
        this.conflictTime = conflictTime;
    }

    public String getConflictType() {
        return conflictType;
    }

    public String getConflictDetail() {
        return conflictDetail;
    }

    public LocalDateTime getConflictTime() {
        return conflictTime;
    }
}
