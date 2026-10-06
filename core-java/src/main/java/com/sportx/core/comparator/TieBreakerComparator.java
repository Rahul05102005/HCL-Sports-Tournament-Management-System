package com.sportx.core.comparator;

import com.sportx.core.domain.Standing;

import java.util.Comparator;

/**
 * Configurable Comparator Chain for League Standings and Tie-Breaking.
 * Demonstrates: Comparator Rubric, Fluent Chaining, Configurable Business Rules.
 *
 * Tie-Breaker Sequence:
 *   1. Points (DESC)
 *   2. Goal Difference (DESC)
 *   3. Goals Scored / For (DESC)
 *   4. Fair Play Points (DESC)
 */
public class TieBreakerComparator implements Comparator<Standing> {

    private final Comparator<Standing> chainedComparator;

    public TieBreakerComparator() {
        this.chainedComparator = Comparator
                .comparingInt(Standing::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(Standing::getGoalDifference).reversed())
                .thenComparing(Comparator.comparingInt(Standing::getGoalsFor).reversed())
                .thenComparing(Comparator.comparingInt(Standing::getFairPlayPoints).reversed());
    }

    @Override
    public int compare(Standing s1, Standing s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        return chainedComparator.compare(s1, s2);
    }
}
