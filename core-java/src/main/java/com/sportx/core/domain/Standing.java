package com.sportx.core.domain;

import java.util.Objects;

/**
 * Domain entity representing a team's standing/record in a tournament table.
 * Demonstrates: Comparable rubric for natural ranking, Encapsulated state calculations.
 */
public class Standing extends BaseEntity implements Comparable<Standing> {
    private final Long tournamentId;
    private final Team team;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int points;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    private int fairPlayPoints;
    private int ranking;

    public Standing(Long id, Long tournamentId, Team team) {
        super(id);
        this.tournamentId = tournamentId;
        this.team = Objects.requireNonNull(team, "Team cannot be null");
        this.played = 0;
        this.won = 0;
        this.drawn = 0;
        this.lost = 0;
        this.points = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.goalDifference = 0;
        this.fairPlayPoints = 100;
        this.ranking = 1;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public Team getTeam() {
        return team;
    }

    public int getPlayed() {
        return played;
    }

    public int getWon() {
        return won;
    }

    public int getDrawn() {
        return drawn;
    }

    public int getLost() {
        return lost;
    }

    public int getPoints() {
        return points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public int getFairPlayPoints() {
        return fairPlayPoints;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public synchronized void recordMatchResult(int scored, int conceded, int pts, boolean isWin, boolean isDraw) {
        this.played++;
        this.goalsFor += scored;
        this.goalsAgainst += conceded;
        this.goalDifference = this.goalsFor - this.goalsAgainst;
        this.points += pts;

        if (isWin) {
            this.won++;
        } else if (isDraw) {
            this.drawn++;
        } else {
            this.lost++;
        }
    }

    public void deductFairPlayPoints(int points) {
        this.fairPlayPoints = Math.max(0, this.fairPlayPoints - points);
    }

    /**
     * Comparable rubric: Natural order sorting by Points (desc), Goal Difference (desc), Goals For (desc), Fair Play (desc).
     */
    @Override
    public int compareTo(Standing other) {
        if (other == null) return 1;
        int ptsDiff = Integer.compare(other.points, this.points);
        if (ptsDiff != 0) return ptsDiff;

        int gdDiff = Integer.compare(other.goalDifference, this.goalDifference);
        if (gdDiff != 0) return gdDiff;

        int gfDiff = Integer.compare(other.goalsFor, this.goalsFor);
        if (gfDiff != 0) return gfDiff;

        return Integer.compare(other.fairPlayPoints, this.fairPlayPoints);
    }

    @Override
    public String toString() {
        return String.format("%2d. %-15s | P:%2d | W:%2d | D:%2d | L:%2d | GF:%2d | GA:%2d | GD:%+3d | PTS:%3d | FP:%3d",
                ranking, team.getName(), played, won, drawn, lost, goalsFor, goalsAgainst, goalDifference, points, fairPlayPoints);
    }
}
