package com.sportx.core;

import com.sportx.core.domain.*;
import com.sportx.core.domain.enums.EventType;
import com.sportx.core.domain.enums.TournamentFormat;
import com.sportx.core.exceptions.InvalidTournamentException;
import com.sportx.core.exceptions.SquadValidationException;
import com.sportx.core.model.MatchResult;
import com.sportx.core.service.TournamentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interactive Console Demonstrator for HCL Core Java Checkpoint.
 *
 * Implements:
 *   Flow 1: Register Team -> Add Players with Unique Jersey Validation -> Validate Squad Eligibility
 *   Flow 2: Generate Fixtures (with Seed) -> Start Match -> Live Score Events -> Finish Match -> Update Standings & Tie-Breakers
 *
 * Completely in-memory, no Spring, no SQL database.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("  SportX – Smart Sports Tournament Management Platform (Core Java Checkpoint)  ");
        System.out.println("================================================================================");

        TournamentService tournamentService = new TournamentService();

        try {
            // ----------------------------------------------------------------
            // 0. SETUP SPORT & TOURNAMENT
            // ----------------------------------------------------------------
            System.out.println("\n[SETUP] Initializing Sport and Collegiate Tournament...");
            Sport football = new Sport(1L, "Football", "FB-11", 5, 16, 90, "GOALS");
            Tournament tournament = new Tournament(
                    1L,
                    "South Zone Inter-Collegiate Trophy 2026",
                    "SZIT-2026",
                    football,
                    101L,
                    TournamentFormat.ROUND_ROBIN,
                    LocalDate.now(),
                    LocalDate.now().plusDays(10),
                    4,
                    3,
                    1,
                    0
            );
            tournament = tournamentService.createTournament(tournament);
            System.out.println("✓ Tournament Created: " + tournament);

            // Venues & Referees Setup
            Venue stadium1 = new Venue(1L, tournament.getId(), "Kovai Main Arena", "North Ground", 1, 2000);
            Venue stadium2 = new Venue(2L, tournament.getId(), "Kongu Field", "South Ground", 2, 1500);

            Referee refArun = new Referee(1L, "Arun Prasath", "arun.ref@sportx.io", "+9194431001", "FIFA National", 12);
            Referee refShankar = new Referee(2L, "Shankar Mahadevan", "shankar.ref@sportx.io", "+9194431002", "Grade A", 8);

            // ================================================================
            // FLOW 1: REGISTER TEAM -> ADD PLAYERS -> VALIDATE SQUAD
            // ================================================================
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println("  FLOW 1: TEAM REGISTRATION & SQUAD ELIGIBILITY VALIDATION");
            System.out.println("--------------------------------------------------------------------------------");

            // 1. Register Team 1: BIT Sparks
            Team bitSparks = new Team(1L, tournament.getId(), "BIT Sparks", "BIT", "Bannari Amman Inst of Tech", 201L);
            bitSparks = tournamentService.registerTeam(tournament.getId(), bitSparks);
            System.out.println("Registered Team: " + bitSparks.getName());

            // Add players to Team 1
            Player p1 = new Player(1L, "Rahul Prasanna", "rahul@bitsathy.ac.in", "+919001", bitSparks.getId(), 10, "22CS101", 20, "Forward", true);
            Player p2 = new Player(2L, "Dinesh Karthik", "dinesh@bitsathy.ac.in", "+919002", bitSparks.getId(), 7, "22CS102", 21, "Midfielder", true);
            Player p3 = new Player(3L, "Ashwin Murthy", "ashwin@bitsathy.ac.in", "+919003", bitSparks.getId(), 4, "22EC201", 20, "Defender", true);
            Player p4 = new Player(4L, "Hari Prasad", "hari@bitsathy.ac.in", "+919004", bitSparks.getId(), 1, "22ME301", 21, "Goalkeeper", true);
            Player p5 = new Player(5L, "Vimal Raj", "vimal@bitsathy.ac.in", "+919005", bitSparks.getId(), 9, "22IT401", 20, "Forward", true);

            bitSparks.addPlayer(p1);
            bitSparks.addPlayer(p2);
            bitSparks.addPlayer(p3);
            bitSparks.addPlayer(p4);
            bitSparks.addPlayer(p5);
            System.out.println("✓ 5 players added to " + bitSparks.getName() + " with unique jerseys: " + bitSparks.getAssignedJerseyNumbers());

            // Validate Squad (Requires min 5 players)
            boolean bitEligible = tournamentService.validateSquadEligibility(bitSparks.getId(), 5);
            System.out.println("✓ Squad Eligibility Check for " + bitSparks.getName() + ": PASSED (" + bitSparks.getEligiblePlayerCount() + " eligible athletes)");

            // 2. Register Team 2: PSG Strikers
            Team psgStrikers = new Team(2L, tournament.getId(), "PSG Strikers", "PSG", "PSG College of Technology", 202L);
            psgStrikers = tournamentService.registerTeam(tournament.getId(), psgStrikers);
            Player q1 = new Player(6L, "Manoj Venkatesh", "manoj@psgtech.edu", "+919006", psgStrikers.getId(), 9, "22PSG01", 21, "Forward", true);
            Player q2 = new Player(7L, "Surya Prakash", "surya@psgtech.edu", "+919007", psgStrikers.getId(), 8, "22PSG02", 20, "Midfielder", true);
            Player q3 = new Player(8L, "Vijay Narayanan", "vijay@psgtech.edu", "+919008", psgStrikers.getId(), 5, "22PSG03", 22, "Defender", true);
            Player q4 = new Player(9L, "Kiran Raj", "kiran@psgtech.edu", "+919009", psgStrikers.getId(), 1, "22PSG04", 20, "Goalkeeper", true);
            Player q5 = new Player(10L, "Sanjay Nathan", "sanjay@psgtech.edu", "+919010", psgStrikers.getId(), 11, "22PSG05", 21, "Forward", true);
            psgStrikers.addPlayer(q1);
            psgStrikers.addPlayer(q2);
            psgStrikers.addPlayer(q3);
            psgStrikers.addPlayer(q4);
            psgStrikers.addPlayer(q5);
            tournamentService.validateSquadEligibility(psgStrikers.getId(), 5);
            System.out.println("✓ Squad Eligibility Check for " + psgStrikers.getName() + ": PASSED");

            // 3. Register Team 3: KEC Warriors
            Team kecWarriors = new Team(3L, tournament.getId(), "KEC Warriors", "KEC", "Kongu Engineering College", 203L);
            kecWarriors = tournamentService.registerTeam(tournament.getId(), kecWarriors);
            Player r1 = new Player(11L, "Gowtham S", "gowtham@kongu.ac.in", "+919011", kecWarriors.getId(), 10, "22KEC01", 20, "Forward", true);
            Player r2 = new Player(12L, "Arvind S", "arvind@kongu.ac.in", "+919012", kecWarriors.getId(), 6, "22KEC02", 21, "Midfielder", true);
            Player r3 = new Player(13L, "Deepak K", "deepak@kongu.ac.in", "+919013", kecWarriors.getId(), 3, "22KEC03", 21, "Defender", true);
            Player r4 = new Player(14L, "Naveen P", "naveen@kongu.ac.in", "+919014", kecWarriors.getId(), 1, "22KEC04", 20, "Goalkeeper", true);
            Player r5 = new Player(15L, "Pravin M", "pravin@kongu.ac.in", "+919015", kecWarriors.getId(), 7, "22KEC05", 22, "Forward", true);
            kecWarriors.addPlayer(r1);
            kecWarriors.addPlayer(r2);
            kecWarriors.addPlayer(r3);
            kecWarriors.addPlayer(r4);
            kecWarriors.addPlayer(r5);
            tournamentService.validateSquadEligibility(kecWarriors.getId(), 5);

            // 4. Register Team 4: CIT Dynamos
            Team citDynamos = new Team(4L, tournament.getId(), "CIT Dynamos", "CIT", "Coimbatore Inst of Technology", 204L);
            citDynamos = tournamentService.registerTeam(tournament.getId(), citDynamos);
            Player s1 = new Player(16L, "Naveen Chander", "naveen@cit.edu.in", "+919016", citDynamos.getId(), 9, "22CIT01", 20, "Forward", true);
            Player s2 = new Player(17L, "Sanjay Dutt", "sanjay@cit.edu.in", "+919017", citDynamos.getId(), 14, "22CIT02", 21, "Midfielder", true);
            Player s3 = new Player(18L, "Anand Kumar", "anand@cit.edu.in", "+919018", citDynamos.getId(), 4, "22CIT03", 22, "Defender", true);
            Player s4 = new Player(19L, "Siddiq Ali", "siddiq@cit.edu.in", "+919019", citDynamos.getId(), 1, "22CIT04", 20, "Goalkeeper", true);
            Player s5 = new Player(20L, "Karthik R", "karthik@cit.edu.in", "+919020", citDynamos.getId(), 11, "22CIT05", 21, "Forward", true);
            citDynamos.addPlayer(s1);
            citDynamos.addPlayer(s2);
            citDynamos.addPlayer(s3);
            citDynamos.addPlayer(s4);
            citDynamos.addPlayer(s5);
            tournamentService.validateSquadEligibility(citDynamos.getId(), 5);

            System.out.println("✓ Flow 1 Successfully Executed: 4 Teams registered with validated squads.");

            // ================================================================
            // FLOW 2: GENERATE FIXTURES -> RECORD MATCH -> UPDATE STANDINGS
            // ================================================================
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println("  FLOW 2: FIXTURE GENERATION, LIVE SCORING & AUTOMATIC STANDINGS");
            System.out.println("--------------------------------------------------------------------------------");

            // Generate Fixtures using Round Robin Strategy with reproducible seed
            List<Fixture> fixtures = tournamentService.generateFixtures(
                    tournament.getId(),
                    List.of(stadium1, stadium2),
                    List.of(refArun, refShankar)
            );

            System.out.println("Generated " + fixtures.size() + " Fixtures:");
            for (Fixture f : fixtures) {
                System.out.println("  " + f);
            }

            // Simulate Match #1: BIT Sparks vs PSG Strikers
            System.out.println("\n[MATCH 1] Kick-Off: BIT Sparks vs PSG Strikers");
            tournamentService.startMatch(1L, refArun);

            // Record Live In-Game Events
            System.out.println("Recording Live Match Events...");
            ScoreEvent e1 = tournamentService.addScoreEvent(1L, refArun, bitSparks, p1, EventType.GOAL, 18, 1, "Rahul finished clinical strike from edge of box");
            ScoreEvent e2 = tournamentService.addScoreEvent(1L, refArun, psgStrikers, q1, EventType.GOAL, 54, 1, "Manoj headed equalizer from corner");
            ScoreEvent e3 = tournamentService.addScoreEvent(1L, refArun, bitSparks, p2, EventType.GOAL, 82, 1, "Dinesh curled long-range winner");
            System.out.println("  " + e1);
            System.out.println("  " + e2);
            System.out.println("  " + e3);

            // Finish Match #1
            MatchResult result1 = tournamentService.finishMatch(1L, refArun, tournament);
            System.out.println("✓ Match 1 Finished! Result: " + result1.getSummary());

            // Simulate Match #2: KEC Warriors vs CIT Dynamos
            System.out.println("\n[MATCH 2] Kick-Off: KEC Warriors vs CIT Dynamos");
            tournamentService.startMatch(2L, refShankar);
            ScoreEvent e4 = tournamentService.addScoreEvent(2L, refShankar, kecWarriors, r1, EventType.GOAL, 32, 1, "Gowtham counter attack goal");
            ScoreEvent e5 = tournamentService.addScoreEvent(2L, refShankar, citDynamos, s1, EventType.GOAL, 68, 1, "Naveen penalty conversion");
            System.out.println("  " + e4);
            System.out.println("  " + e5);

            MatchResult result2 = tournamentService.finishMatch(2L, refShankar, tournament);
            System.out.println("✓ Match 2 Finished! Result: " + result2.getSummary());

            // Display Updated Standings Table
            System.out.println("\n================================================================================");
            System.out.println("  AUTOMATIC LEAGUE STANDINGS (Ranked by Tie-Breaker: PTS -> GD -> GF -> FP)    ");
            System.out.println("================================================================================");
            List<Standing> currentStandings = tournamentService.getStandings(tournament.getId());
            System.out.println("RK  TEAM            |  P |  W |  D |  L | GF | GA |  GD | PTS |  FP");
            System.out.println("--------------------------------------------------------------------------------");
            for (Standing s : currentStandings) {
                System.out.println(s);
            }
            System.out.println("--------------------------------------------------------------------------------");

            // Display Top Scorer via Streams
            System.out.println("\n[LEADERBOARD] Top Performers (Streams Aggregation):");
            tournamentService.getTopScorers(tournament.getId()).forEach(ts ->
                    System.out.println("  ⭐ " + ts.playerName() + " (" + ts.teamName() + ") - " + ts.goals() + " Goals")
            );

            // Modern Java: CompletableFuture Asynchronous Analytics
            System.out.println("\n[ASYNC] Dispatching CompletableFuture Analytics Task...");
            CompletableFuture<String> analyticsFuture = tournamentService.generateTournamentAnalyticsAsync(tournament.getId());
            // Await async analytics result
            String analyticsReport = analyticsFuture.join();
            System.out.println(analyticsReport);

            System.out.println("✓ Core Java Checkpoint Demonstration Completed Successfully!");

        } catch (InvalidTournamentException | SquadValidationException e) {
            System.err.println("Checked Business Exception caught: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
