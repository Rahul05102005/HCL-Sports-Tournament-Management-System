package com.sportx.core.exceptions;

/**
 * Checked exception thrown when team squad fails eligibility criteria.
 */
public class SquadValidationException extends Exception {
    private final String teamName;
    private final int currentEligible;
    private final int minRequired;

    public SquadValidationException(String teamName, int currentEligible, int minRequired) {
        super("Team '%s' does not satisfy minimum squad eligibility: %d eligible players found, %d required".formatted(
                teamName, currentEligible, minRequired));
        this.teamName = teamName;
        this.currentEligible = currentEligible;
        this.minRequired = minRequired;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getCurrentEligible() {
        return currentEligible;
    }

    public int getMinRequired() {
        return minRequired;
    }
}
