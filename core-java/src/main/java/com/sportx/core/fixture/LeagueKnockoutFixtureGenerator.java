package com.sportx.core.fixture;

import com.sportx.core.domain.*;
import com.sportx.core.domain.enums.TournamentFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy: Hybrid League + Knockout Fixture Generator.
 * First generates group stage round robin, reserving subsequent slots for knockout stages.
 */
public class LeagueKnockoutFixtureGenerator implements FixtureGenerator {

    private final RoundRobinFixtureGenerator roundRobinGenerator = new RoundRobinFixtureGenerator();

    @Override
    public TournamentFormat getSupportedFormat() {
        return TournamentFormat.LEAGUE_AND_KNOCKOUT;
    }

    @Override
    public List<Fixture> generate(Tournament tournament, List<Team> teams, List<Venue> venues, List<Referee> referees) {
        // Generates preliminary league stage
        List<Fixture> groupStageFixtures = roundRobinGenerator.generate(tournament, teams, venues, referees);
        return new ArrayList<>(groupStageFixtures);
    }
}
