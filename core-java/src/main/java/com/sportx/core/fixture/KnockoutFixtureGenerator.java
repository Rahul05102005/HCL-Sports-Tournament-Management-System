package com.sportx.core.fixture;

import com.sportx.core.domain.*;
import com.sportx.core.domain.enums.TournamentFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy: Single Elimination Knockout Fixture Generator.
 * Implements bracket seeding with round progression.
 */
public class KnockoutFixtureGenerator implements FixtureGenerator {

    @Override
    public TournamentFormat getSupportedFormat() {
        return TournamentFormat.KNOCKOUT;
    }

    @Override
    public List<Fixture> generate(Tournament tournament, List<Team> teams, List<Venue> venues, List<Referee> referees) {
        if (teams == null || teams.size() < 2) {
            throw new IllegalArgumentException("Knockout requires at least 2 teams");
        }

        List<Fixture> fixtures = new ArrayList<>();
        long fixtureIdSeq = 1;
        LocalDateTime scheduleCursor = tournament.getStartDate().atTime(10, 0);

        int n = teams.size();
        int matches = n / 2;

        for (int i = 0; i < matches; i++) {
            Team home = teams.get(i);
            Team away = teams.get(n - 1 - i);

            Venue venue = (venues != null && !venues.isEmpty()) ? venues.get(i % venues.size()) : null;
            Referee ref = (referees != null && !referees.isEmpty()) ? referees.get(i % referees.size()) : null;
            LocalDateTime slotTime = scheduleCursor.plusHours(i * 3L);

            Fixture fixture = new Fixture(
                    fixtureIdSeq++,
                    tournament.getId(),
                    1, // Round 1 (e.g. Quarterfinals / Round of 16)
                    home,
                    away,
                    venue,
                    ref,
                    slotTime
            );
            fixtures.add(fixture);
        }
        return fixtures;
    }
}
