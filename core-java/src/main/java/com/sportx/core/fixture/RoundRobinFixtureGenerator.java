package com.sportx.core.fixture;

import com.sportx.core.domain.*;
import com.sportx.core.domain.enums.TournamentFormat;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Concrete Strategy: Round Robin Fixture Generator.
 * Implements deterministic polygon rotation algorithm with optional seed reproducibility.
 */
public class RoundRobinFixtureGenerator implements FixtureGenerator {

    private final Long randomSeed;

    public RoundRobinFixtureGenerator() {
        this(null);
    }

    public RoundRobinFixtureGenerator(Long randomSeed) {
        this.randomSeed = randomSeed;
    }

    @Override
    public TournamentFormat getSupportedFormat() {
        return TournamentFormat.ROUND_ROBIN;
    }

    @Override
    public List<Fixture> generate(Tournament tournament, List<Team> teams, List<Venue> venues, List<Referee> referees) {
        if (teams == null || teams.size() < 2) {
            throw new IllegalArgumentException("Round-robin requires at least 2 teams");
        }

        List<Team> teamPool = new ArrayList<>(teams);
        if (randomSeed != null) {
            Collections.shuffle(teamPool, new Random(randomSeed));
        }

        // If odd number of teams, introduce a dummy bye team
        boolean hasBye = (teamPool.size() % 2 != 0);
        if (hasBye) {
            teamPool.add(new Team(999999L, tournament.getId(), "BYE", "BYE", "SportX System", null));
        }

        int totalTeams = teamPool.size();
        int totalRounds = totalTeams - 1;
        int matchesPerRound = totalTeams / 2;

        List<Fixture> fixtures = new ArrayList<>();
        long fixtureIdSeq = 1;
        LocalDateTime scheduleCursor = tournament.getStartDate().atTime(9, 0);

        for (int round = 0; round < totalRounds; round++) {
            LocalDateTime matchTime = scheduleCursor.plusDays(round * 2L);

            for (int matchIdx = 0; matchIdx < matchesPerRound; matchIdx++) {
                int homeIdx = (round + matchIdx) % (totalTeams - 1);
                int awayIdx = (totalTeams - 1 - matchIdx + round) % (totalTeams - 1);

                if (matchIdx == 0) {
                    awayIdx = totalTeams - 1;
                }

                Team home = teamPool.get(homeIdx);
                Team away = teamPool.get(awayIdx);

                // Skip bye pairings
                if ("BYE".equals(home.getShortName()) || "BYE".equals(away.getShortName())) {
                    continue;
                }

                // Alternate home and away on odd rounds for fairness
                if (round % 2 == 1 && matchIdx == 0) {
                    Team temp = home;
                    home = away;
                    away = temp;
                }

                Venue venue = (venues != null && !venues.isEmpty())
                        ? venues.get(matchIdx % venues.size())
                        : null;

                Referee ref = (referees != null && !referees.isEmpty())
                        ? referees.get((round + matchIdx) % referees.size())
                        : null;

                LocalDateTime slotTime = matchTime.plusHours(matchIdx * 2L + 1L);

                Fixture fixture = new Fixture(
                        fixtureIdSeq++,
                        tournament.getId(),
                        round + 1,
                        home,
                        away,
                        venue,
                        ref,
                        slotTime
                );
                fixtures.add(fixture);
            }
        }
        return fixtures;
    }
}
