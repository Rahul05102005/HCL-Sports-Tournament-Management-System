package com.sportx.core.fixture;

import com.sportx.core.domain.*;
import com.sportx.core.domain.enums.TournamentFormat;

import java.util.List;

/**
 * Strategy Pattern Interface for Fixture Generation.
 * Demonstrates: Polymorphism, Strategy Pattern, Liskov Substitution Principle (LSP).
 */
public interface FixtureGenerator {

    /**
     * Generates fixtures according to tournament rules and participating teams.
     */
    List<Fixture> generate(Tournament tournament, List<Team> teams, List<Venue> venues, List<Referee> referees);

    /**
     * Format supported by this generator.
     */
    TournamentFormat getSupportedFormat();
}
