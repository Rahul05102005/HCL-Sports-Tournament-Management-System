package com.sportx.core.scoring;

import com.sportx.core.model.MatchResult;

/**
 * Strategy Interface for Sport-Specific Point Allocation Rules.
 * Demonstrates: Open/Closed Principle (OCP), Factory pattern consumer.
 */
public interface ScoringRule {
    String getSportCode();
    int getWinPoints();
    int getDrawPoints();
    int getLossPoints();
    int calculatePoints(MatchResult matchResult, boolean isWinner, boolean isDraw);
}
