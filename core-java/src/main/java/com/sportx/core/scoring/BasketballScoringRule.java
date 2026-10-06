package com.sportx.core.scoring;

import com.sportx.core.model.MatchResult;

public class BasketballScoringRule implements ScoringRule {
    @Override
    public String getSportCode() {
        return "BB-5";
    }

    @Override
    public int getWinPoints() {
        return 2;
    }

    @Override
    public int getDrawPoints() {
        return 0; // Basketball games do not end in draws (overtimes)
    }

    @Override
    public int getLossPoints() {
        return 0;
    }

    @Override
    public int calculatePoints(MatchResult matchResult, boolean isWinner, boolean isDraw) {
        return isWinner ? getWinPoints() : getLossPoints();
    }
}
