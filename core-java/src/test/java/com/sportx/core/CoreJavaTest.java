package com.sportx.core;

import com.sportx.core.comparator.TieBreakerComparator;
import com.sportx.core.domain.*;
import com.sportx.core.domain.enums.EventType;
import com.sportx.core.domain.enums.TournamentFormat;
import com.sportx.core.exceptions.*;
import com.sportx.core.fixture.RoundRobinFixtureGenerator;
import com.sportx.core.model.*;
import com.sportx.core.repository.InMemoryRepository;
import com.sportx.core.scoring.FootballScoringRule;
import com.sportx.core.scoring.ScoringRuleFactory;
import com.sportx.core.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SportX Core Java Checkpoint Tests")
class CoreJavaTest {

    private TournamentService tournamentService;
    private Tournament testTournament;

    @BeforeEach
    void setUp() throws InvalidTournamentException {
        tournamentService = new TournamentService();
        Sport football = new Sport(1L, "Football", "FB-11", 5, 16, 90, "GOALS");
        testTournament = new Tournament(
                1L, "Inter-Collegiate 2026", "IC-2026", football, 101L,
                TournamentFormat.ROUND_ROBIN, LocalDate.now(), LocalDate.now().plusDays(5),
                4, 3, 1, 0
        );
        tournamentService.createTournament(testTournament);
    }

    @Test
    @DisplayName("OOP & Inheritance: Person hierarchy preserves polymorphic role and properties")
    void testPersonInheritance() {
        Person player = new Player(1L, "Rahul", "rahul@test.edu", "+9198", 1L, 10, "R01", 20, "Forward", true);
        Person referee = new Referee(2L, "Arun", "arun@test.edu", "+9199", "FIFA", 5);
        Person manager = new TeamManager(3L, "Karthik", "karthik@test.edu", "+9197", "PSG Tech", 1L);

        assertEquals("Student Athlete", player.getRoleTitle());
        assertEquals("Official Referee", referee.getRoleTitle());
        assertEquals("Team Manager / Coach", manager.getRoleTitle());
        assertTrue(player instanceof BaseEntity);
    }

    @Test
    @DisplayName("Collections Rubric: Set prevents duplicate jersey numbers in same team")
    void testDuplicateJerseyThrowsException() {
        Team team = new Team(1L, 1L, "BIT Sparks", "BIT", "Bannari Amman", 101L);
        Player p1 = new Player(1L, "Rahul", "r1@bit.edu", "+91", 1L, 10, "R01", 20, "Forward", true);
        Player p2 = new Player(2L, "Dinesh", "r2@bit.edu", "+91", 1L, 10, "R02", 21, "Midfielder", true);

        team.addPlayer(p1);
        assertThrows(TeamRegistrationException.class, () -> team.addPlayer(p2),
                "Adding duplicate jersey number must throw TeamRegistrationException");
    }

    @Test
    @DisplayName("Checked Exception: SquadValidationException thrown when squad has insufficient eligible players")
    void testSquadValidationFailure() {
        Team team = new Team(1L, 1L, "BIT Sparks", "BIT", "Bannari Amman", 101L);
        tournamentService.registerTeam(1L, team);
        Player p1 = new Player(1L, "Rahul", "r1@bit.edu", "+91", 1L, 10, "R01", 20, "Forward", true);
        team.addPlayer(p1);

        assertThrows(SquadValidationException.class, () -> tournamentService.validateSquadEligibility(1L, 5));
    }

    @Test
    @DisplayName("Strategy Pattern: Round Robin Generator with seed produces reproducible fixture sequence")
    void testRoundRobinSeedReproducibility() {
        Team t1 = new Team(1L, 1L, "Team A", "TA", "College A", 1L);
        Team t2 = new Team(2L, 1L, "Team B", "TB", "College B", 2L);
        Team t3 = new Team(3L, 1L, "Team C", "TC", "College C", 3L);
        Team t4 = new Team(4L, 1L, "Team D", "TD", "College D", 4L);
        List<Team> teams = List.of(t1, t2, t3, t4);

        RoundRobinFixtureGenerator gen1 = new RoundRobinFixtureGenerator(42L);
        RoundRobinFixtureGenerator gen2 = new RoundRobinFixtureGenerator(42L);

        List<Fixture> f1 = gen1.generate(testTournament, teams, List.of(), List.of());
        List<Fixture> f2 = gen2.generate(testTournament, teams, List.of(), List.of());

        assertEquals(f1.size(), f2.size());
        for (int i = 0; i < f1.size(); i++) {
            assertEquals(f1.get(i).getHomeTeam().getName(), f2.get(i).getHomeTeam().getName());
            assertEquals(f1.get(i).getAwayTeam().getName(), f2.get(i).getAwayTeam().getName());
        }
    }

    @Test
    @DisplayName("Factory & Open/Closed: ScoringRuleFactory resolves sport scoring rules dynamically")
    void testScoringRuleFactory() {
        assertEquals("FB-11", ScoringRuleFactory.getRule("FB-11").getSportCode());
        assertEquals("BB-5", ScoringRuleFactory.getRule("BB-5").getSportCode());
        assertEquals(3, new FootballScoringRule().getWinPoints());
    }

    @Test
    @DisplayName("Sealed Classes: MatchResult pattern matching on Win, Draw, and Abandoned")
    void testSealedMatchResult() {
        Team t1 = new Team(1L, 1L, "Team A", "TA", "Col A", 1L);
        Team t2 = new Team(2L, 1L, "Team B", "TB", "Col B", 2L);

        MatchResult win = new WinResult(t1, t2, 2, 1, "Stoppage time goal");
        MatchResult draw = new DrawResult(t1, t2, 2);
        MatchResult abandoned = new AbandonedResult("Rain", 0, 0);

        String winDesc = switch (win) {
            case WinResult w -> "Winner is " + w.winner().getName();
            case DrawResult d -> "Draw";
            case AbandonedResult a -> "Abandoned";
        };
        assertEquals("Winner is Team A", winDesc);
        assertTrue(draw instanceof MatchResult);
        assertTrue(abandoned instanceof MatchResult);
    }

    @Test
    @DisplayName("Comparator Rubric: TieBreakerComparator properly orders standings by Points, GD, and Goals")
    void testTieBreakerComparator() {
        Team t1 = new Team(1L, 1L, "BIT", "BIT", "College", 1L);
        Team t2 = new Team(2L, 1L, "PSG", "PSG", "College", 2L);

        Standing s1 = new Standing(1L, 1L, t1); // 3 pts, GD +2
        s1.recordMatchResult(3, 1, 3, true, false);

        Standing s2 = new Standing(2L, 1L, t2); // 3 pts, GD +1
        s2.recordMatchResult(2, 1, 3, true, false);

        TieBreakerComparator comparator = new TieBreakerComparator();
        assertTrue(comparator.compare(s1, s2) < 0, "s1 has better Goal Difference, should rank ahead of s2");
    }

    @Test
    @DisplayName("Generics Rubric: Generic Repository handles bounded entities, wildcards, and transformations")
    void testGenericRepository() {
        InMemoryRepository<Team> repo = new InMemoryRepository<>();
        Team t1 = new Team(null, 1L, "BIT", "BIT", "College 1", 1L);
        Team t2 = new Team(null, 1L, "PSG", "PSG", "College 2", 2L);

        repo.saveAll(List.of(t1, t2));
        assertEquals(2, repo.count());

        List<String> names = repo.mapAll(Team::getName);
        assertEquals(List.of("BIT", "PSG"), names);
    }

    @Test
    @DisplayName("CompletableFuture Rubric: Asynchronous Analytics execution returns complete report")
    void testAsyncAnalytics() {
        Team t1 = new Team(1L, 1L, "BIT", "BIT", "College", 1L);
        tournamentService.registerTeam(1L, t1);

        CompletableFuture<String> future = tournamentService.generateTournamentAnalyticsAsync(1L);
        assertNotNull(future);
        String report = future.join();
        assertTrue(report.contains("TOURNAMENT ANALYTICS SUMMARY"));
        assertTrue(report.contains("Inter-Collegiate 2026"));
    }
}
