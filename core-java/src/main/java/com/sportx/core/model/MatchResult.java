package com.sportx.core.model;

/**
 * Modern Java Sealed Interface demonstrating restricted hierarchy.
 * Demonstrates: Sealed Classes, Pattern Matching foundation, exhaustive state handling.
 */
public sealed interface MatchResult permits WinResult, DrawResult, AbandonedResult {
    String getSummary();
    int homeScore();
    int awayScore();

    default int getHomeScore() {
        return homeScore();
    }

    default int getAwayScore() {
        return awayScore();
    }
}
