package com.sportx.core.scoring;

import com.sportx.core.model.MatchResult;

public class CricketScoringRule implements ScoringRule {
    @Override
    public String getSportCode() {
        return "CRIC-T20";
    }

    @Override
    public int getWinPoints() {
        return 2;
    }

    @Override
    public int getDrawPoints() {
        return 1; // No result / tie points
    }

    @Override
    public int getLossPoints() {
        return 0;
    }

    @Override
    public int calculatePoints(MatchResult matchResult, boolean isWinner, boolean isDraw) {
        if (isWinner) return getWinPoints();
        if (isDraw) return getDrawPoints();
        return getLossPoints();
    }
}
