package com.sportx.core.scoring;

import com.sportx.core.model.MatchResult;

public class FootballScoringRule implements ScoringRule {
    @Override
    public String getSportCode() {
        return "FB-11";
    }

    @Override
    public int getWinPoints() {
        return 3;
    }

    @Override
    public int getDrawPoints() {
        return 1;
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
