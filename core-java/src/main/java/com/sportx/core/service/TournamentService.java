package com.sportx.core.service;

import com.sportx.core.comparator.TieBreakerComparator;
import com.sportx.core.domain.*;
import com.sportx.core.domain.enums.EventType;
import com.sportx.core.domain.enums.FixtureStatus;
import com.sportx.core.domain.enums.MatchStatus;
import com.sportx.core.exceptions.*;
import com.sportx.core.fixture.FixtureGenerator;
import com.sportx.core.fixture.RoundRobinFixtureGenerator;
import com.sportx.core.model.*;
import com.sportx.core.repository.InMemoryRepository;
import com.sportx.core.repository.Repository;
import com.sportx.core.scoring.ScoringRule;
import com.sportx.core.scoring.ScoringRuleFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Core Service orchestrating tournament lifecycle, fixtures, live scoring, and standings.
 * Demonstrates: Collections Rubric (List, Set, Map, Queue), Streams, Optional, CompletableFuture.
 */
public class TournamentService {

    // Repositories demonstrating generic repository pattern
    private final Repository<Tournament, Long> tournamentRepo = new InMemoryRepository<>();
    private final Repository<Team, Long> teamRepo = new InMemoryRepository<>();
    private final Repository<Fixture, Long> fixtureRepo = new InMemoryRepository<>();
    private final Repository<Match, Long> matchRepo = new InMemoryRepository<>();
    private final Repository<Standing, Long> standingRepo = new InMemoryRepository<>();

    // Queue rubric: Concurrent Queue for processing live match notifications / asynchronous event dispatch
    private final Queue<String> eventQueue = new ConcurrentLinkedQueue<>();

    // Map rubric: Fast Map lookup for sport-specific scoring strategies
    private final Map<String, ScoringRule> scoringRuleMap = new HashMap<>(ScoringRuleFactory.getRegisteredRules());

    // PriorityQueue rubric: Dynamic priority queue for instant leaderboard ranking using TieBreakerComparator
    private final PriorityQueue<Standing> standingsRankingQueue = new PriorityQueue<>(new TieBreakerComparator());

    private final SchedulingService schedulingService = new SchedulingService();

    // ------------------------------------------------------------------------
    // Tournament Management
    // ------------------------------------------------------------------------
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepo.save(tournament);
    }

    public Optional<Tournament> findTournamentById(Long id) {
        return tournamentRepo.findById(id);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepo.findAll();
    }

    // ------------------------------------------------------------------------
    // Team & Squad Validation (Flow 1)
    // ------------------------------------------------------------------------
    public Team registerTeam(Long tournamentId, Team team) {
        Tournament tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament", tournamentId));

        tournament.registerTeam(team);
        Team savedTeam = teamRepo.save(team);

        // Initialize team standing
        Standing standing = new Standing(standingRepo.count() + 1, tournamentId, savedTeam);
        standingRepo.save(standing);
        eventQueue.offer("Team registered: " + team.getName() + " in Tournament " + tournament.getName());

        return savedTeam;
    }

    /**
     * Validates squad eligibility checking min required players and age criteria.
     * Demonstrates: Checked SquadValidationException.
     */
    public boolean validateSquadEligibility(Long teamId, int minPlayersRequired) throws SquadValidationException {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team", teamId));

        int eligibleCount = (int) team.getEligiblePlayerCount();
        if (eligibleCount < minPlayersRequired) {
            throw new SquadValidationException(team.getName(), eligibleCount, minPlayersRequired);
        }
        return true;
    }

    // ------------------------------------------------------------------------
    // Fixture Generation & Scheduling
    // ------------------------------------------------------------------------
    public List<Fixture> generateFixtures(Long tournamentId, List<Venue> venues, List<Referee> referees) {
        Tournament tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament", tournamentId));

        List<Team> teams = tournament.getRegisteredTeams();
        if (teams.size() < 2) {
            throw new IllegalStateException("At least 2 teams are required to generate fixtures");
        }

        // Polymorphic strategy generator
        FixtureGenerator generator = new RoundRobinFixtureGenerator(42L); // reproducible seed
        List<Fixture> fixtures = generator.generate(tournament, teams, venues, referees);

        // Validate fixtures against schedule conflicts
        List<Fixture> verifiedFixtures = new ArrayList<>();
        for (Fixture f : fixtures) {
            schedulingService.validateSchedule(f, verifiedFixtures);
            Fixture savedFixture = fixtureRepo.save(f);
            verifiedFixtures.add(savedFixture);

            // Initialize corresponding match entity
            Match match = new Match(savedFixture.getId(), savedFixture, savedFixture.getReferee());
            matchRepo.save(match);
        }

        tournament.setFixtures(verifiedFixtures);
        return verifiedFixtures;
    }

    // ------------------------------------------------------------------------
    // Match Operations & Live Scoring (Flow 2)
    // ------------------------------------------------------------------------
    public Match startMatch(Long matchId, Referee referee) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match", matchId));
        match.startMatch(referee);
        eventQueue.offer("Match #%d started by Referee %s".formatted(matchId, referee.getName()));
        return matchRepo.save(match);
    }

    public ScoreEvent addScoreEvent(Long matchId, Referee referee, Team team, Player player,
                                    EventType eventType, int minute, int points, String desc) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match", matchId));

        ScoreEvent event = match.recordScoreEvent(referee, team, player, eventType, minute, points, desc);
        eventQueue.offer("Score Event [%d']: %s for %s".formatted(minute, eventType, team.getName()));
        matchRepo.save(match);
        return event;
    }

    /**
     * Completes match and automatically recalculates standings and player stats.
     */
    public MatchResult finishMatch(Long matchId, Referee referee, Tournament tournament) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match", matchId));

        match.finishMatch(referee);
        matchRepo.save(match);

        // Determine result using sealed class hierarchy
        Team home = match.getFixture().getHomeTeam();
        Team away = match.getFixture().getAwayTeam();
        int homeScore = match.getHomeScore();
        int awayScore = match.getAwayScore();

        ScoringRule rule = scoringRuleMap.getOrDefault(tournament.getSport().getCode(), ScoringRuleFactory.getRule("FB-11"));

        MatchResult result;
        if (homeScore > awayScore) {
            result = new WinResult(home, away, homeScore, awayScore, "Decisive home victory");
            updateStandings(tournament.getId(), home, away, homeScore, awayScore, rule.getWinPoints(), rule.getLossPoints(), true, false);
        } else if (awayScore > homeScore) {
            result = new WinResult(away, home, awayScore, homeScore, "Away triumph");
            updateStandings(tournament.getId(), away, home, awayScore, homeScore, rule.getWinPoints(), rule.getLossPoints(), true, false);
        } else {
            result = new DrawResult(home, away, homeScore);
            updateStandings(tournament.getId(), home, away, homeScore, awayScore, rule.getDrawPoints(), rule.getDrawPoints(), false, true);
        }

        recalculateRankings(tournament.getId());
        eventQueue.offer("Match #%d finished. %s".formatted(matchId, result.getSummary()));
        return result;
    }

    private void updateStandings(Long tournamentId, Team team1, Team team2,
                                 int score1, int score2, int pts1, int pts2,
                                 boolean isDecisive, boolean isDraw) {
        List<Standing> standings = standingRepo.findAll(s -> Objects.equals(s.getTournamentId(), tournamentId));

        Standing s1 = standings.stream()
                .filter(s -> s.getTeam().equals(team1))
                .findFirst()
                .orElseGet(() -> standingRepo.save(new Standing(null, tournamentId, team1)));

        Standing s2 = standings.stream()
                .filter(s -> s.getTeam().equals(team2))
                .findFirst()
                .orElseGet(() -> standingRepo.save(new Standing(null, tournamentId, team2)));

        if (isDecisive) {
            s1.recordMatchResult(score1, score2, pts1, true, false);
            s2.recordMatchResult(score2, score1, pts2, false, false);
        } else if (isDraw) {
            s1.recordMatchResult(score1, score2, pts1, false, true);
            s2.recordMatchResult(score2, score1, pts2, false, true);
        }

        standingRepo.save(s1);
        standingRepo.save(s2);
    }

    public synchronized List<Standing> recalculateRankings(Long tournamentId) {
        List<Standing> standings = standingRepo.findAll(s -> Objects.equals(s.getTournamentId(), tournamentId));

        // Use PriorityQueue to sort by TieBreakerComparator
        standingsRankingQueue.clear();
        standingsRankingQueue.addAll(standings);

        List<Standing> rankedList = new ArrayList<>();
        int rank = 1;
        while (!standingsRankingQueue.isEmpty()) {
            Standing s = standingsRankingQueue.poll();
            s.setRanking(rank++);
            standingRepo.save(s);
            rankedList.add(s);
        }
        return rankedList;
    }

    public List<Standing> getStandings(Long tournamentId) {
        return standingRepo.findAll(s -> Objects.equals(s.getTournamentId(), tournamentId)).stream()
                .sorted(new TieBreakerComparator())
                .toList();
    }

    // ------------------------------------------------------------------------
    // Streams Rubric: Player Leaderboards & Analytics
    // ------------------------------------------------------------------------
    public List<PlayerStatRecord> getTopScorers(Long tournamentId) {
        List<Match> matches = matchRepo.findAll(m -> Objects.equals(m.getFixture().getTournamentId(), tournamentId));

        // Flatten all score events across matches using Streams
        Map<Player, List<ScoreEvent>> playerEvents = matches.stream()
                .flatMap(m -> m.getScoreEvents().stream())
                .filter(se -> se.getPlayer() != null)
                .collect(Collectors.groupingBy(ScoreEvent::getPlayer));

        return playerEvents.entrySet().stream()
                .map(entry -> {
                    Player p = entry.getKey();
                    List<ScoreEvent> events = entry.getValue();
                    int goals = (int) events.stream().filter(e -> e.getEventType() == EventType.GOAL || e.getEventType() == EventType.POINT).count();
                    int yellowCards = (int) events.stream().filter(e -> e.getEventType() == EventType.YELLOW_CARD).count();
                    int redCards = (int) events.stream().filter(e -> e.getEventType() == EventType.RED_CARD).count();

                    return new PlayerStatRecord(
                            p.getId(),
                            p.getName(),
                            "Team #" + p.getTeamId(),
                            p.getPosition(),
                            goals,
                            0,
                            yellowCards,
                            redCards,
                            events.size() * 15
                    );
                })
                .sorted(Comparator.comparingInt(PlayerStatRecord::goals).reversed())
                .limit(10)
                .toList();
    }

    // ------------------------------------------------------------------------
    // Modern Java: CompletableFuture for Asynchronous Analytics Generation
    // ------------------------------------------------------------------------
    public CompletableFuture<String> generateTournamentAnalyticsAsync(Long tournamentId) {
        return CompletableFuture.supplyAsync(() -> {
            Tournament tournament = tournamentRepo.findById(tournamentId)
                    .orElseThrow(() -> new EntityNotFoundException("Tournament", tournamentId));

            List<Standing> standings = getStandings(tournamentId);
            long totalGoals = standings.stream().mapToInt(Standing::getGoalsFor).sum();
            long totalMatches = standings.stream().mapToInt(Standing::getPlayed).sum() / 2;
            double avgGoalsPerMatch = totalMatches > 0 ? (double) totalGoals / totalMatches : 0.0;

            String leaderName = standings.isEmpty() ? "None" : standings.get(0).getTeam().getName();

            return """
                    === TOURNAMENT ANALYTICS SUMMARY ===
                    Tournament: %s (%s)
                    Total Teams: %d
                    Total Matches Completed: %d
                    Total Goals Scored: %d
                    Average Scoring Rate: %.2f goals/match
                    Current Table Leader: %s
                    """.formatted(
                    tournament.getName(), tournament.getSport().getName(),
                    standings.size(), totalMatches, totalGoals, avgGoalsPerMatch, leaderName
            );
        });
    }

    public Queue<String> getEventQueue() {
        return eventQueue;
    }
}
