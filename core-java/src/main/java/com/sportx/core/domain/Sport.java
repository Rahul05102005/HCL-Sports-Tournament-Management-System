package com.sportx.core.domain;

/**
 * Domain entity representing a sport discipline with rules configuration.
 */
public class Sport extends BaseEntity {
    private final String name;
    private final String code;
    private final int minPlayersPerTeam;
    private final int maxPlayersPerTeam;
    private final int matchDurationMinutes;
    private final String scoringType;

    public Sport(Long id, String name, String code, int minPlayersPerTeam,
                 int maxPlayersPerTeam, int matchDurationMinutes, String scoringType) {
        super(id);
        if (minPlayersPerTeam <= 0 || maxPlayersPerTeam < minPlayersPerTeam) {
            throw new IllegalArgumentException("Invalid player limits for sport");
        }
        this.name = name;
        this.code = code;
        this.minPlayersPerTeam = minPlayersPerTeam;
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        this.matchDurationMinutes = matchDurationMinutes;
        this.scoringType = scoringType != null ? scoringType : "GOALS";
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getMinPlayersPerTeam() {
        return minPlayersPerTeam;
    }

    public int getMaxPlayersPerTeam() {
        return maxPlayersPerTeam;
    }

    public int getMatchDurationMinutes() {
        return matchDurationMinutes;
    }

    public String getScoringType() {
        return scoringType;
    }

    @Override
    public String toString() {
        return "%s (%s) [Squad: %d-%d, Duration: %dm]".formatted(
                name, code, minPlayersPerTeam, maxPlayersPerTeam, matchDurationMinutes
        );
    }
}
