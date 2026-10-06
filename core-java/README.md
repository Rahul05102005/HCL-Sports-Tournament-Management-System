# SportX – Core Java Checkpoint Deliverable

This module demonstrates the **SportX** sports tournament domain using pure **Java 21** without Spring Boot or database frameworks, designed specifically to satisfy the HCL Core Java Assessment Rubric.

---

## Technical Rubric Implementation Mapping

| Rubric Area | Implementation in `core-java` | Code Location |
| :--- | :--- | :--- |
| **OOP & Encapsulation** | Immutable identity in `BaseEntity`, private fields, encapsulated state transitions (`startMatch`, `recordScoreEvent`, `finishMatch`). | `com.sportx.core.domain.*` |
| **Inheritance** | `Person` abstract class extended by `Player`, `Referee`, and `TeamManager`. Meaningful IS-A relationship. | `com.sportx.core.domain.Person` |
| **Polymorphism** | `FixtureGenerator` interface with interchangeable strategies: `RoundRobinFixtureGenerator`, `KnockoutFixtureGenerator`, `LeagueKnockoutFixtureGenerator`. | `com.sportx.core.fixture.*` |
| **Interfaces & Factory** | `ScoringRule` strategy interface and `ScoringRuleFactory` resolving sport-specific scoring (Football, Basketball, Cricket). | `com.sportx.core.scoring.*` |
| **Collections** | • **List:** Ordered players, fixtures, score events.<br>• **Set:** Enforces unique jersey numbers and player IDs.<br>• **Map:** Tournament standings and sport scoring rules.<br>• **Queue / PriorityQueue:** Live event queues and dynamic leaderboard ranking. | `com.sportx.core.service.TournamentService` |
| **Generics** | `Repository<T extends BaseEntity, ID>` with upper-bounded types, wildcards (`? extends T`, `? super T`), and generic transformation methods (`<R> List<R> mapAll(...)`). | `com.sportx.core.repository.*` |
| **Comparable & Comparator** | `Standing` implements `Comparable<Standing>` for natural ordering. `TieBreakerComparator` chains Points → Goal Difference → Goals For → Fair Play. | `com.sportx.core.comparator.*` |
| **Modern Java Records** | Immutable DTO records: `TournamentResponse`, `PlayerStatRecord`, `StandingRecord`. | `com.sportx.core.model.*` |
| **Sealed Classes** | `sealed interface MatchResult` permitting records `WinResult`, `DrawResult`, and `AbandonedResult` for exhaustive pattern matching. | `com.sportx.core.model.MatchResult` |
| **Streams & Aggregations** | FlatMapping score events, grouping by player, filtering, and sorting to generate tournament top-scorer leaderboards. | `TournamentService.getTopScorers` |
| **Modern java.time** | `LocalDate`, `LocalDateTime`, and `Duration` for rest period calculations and venue buffer scheduling. | `com.sportx.core.service.SchedulingService` |
| **CompletableFuture** | Asynchronous generation of tournament analytics summary without blocking main execution threads. | `TournamentService.generateTournamentAnalyticsAsync` |
| **Exception Handling** | Custom checked (`InvalidTournamentException`, `SquadValidationException`) and unchecked (`TeamRegistrationException`, `FixtureConflictException`, `MatchAccessDeniedException`). | `com.sportx.core.exceptions.*` |

---

## Demonstration Flows (Implemented in `Main.java`)

### Flow 1: Team Registration & Squad Eligibility Validation
1. Register Tournament and participating Collegiate Teams (`BIT Sparks`, `PSG Strikers`, `KEC Warriors`, `CIT Dynamos`).
2. Add student athletes with unique jersey number checks (enforced by `HashSet`).
3. Validate minimum squad requirements and age eligibility (throws `SquadValidationException` on deficiency).

### Flow 2: Fixture Generation, Live Match Scoring & Automatic Standings
1. Generate round-robin fixtures using deterministic seed (`42L`) ensuring full reproducibility.
2. Validate fixtures against scheduling clashes (detects team rest violations and venue double-bookings).
3. Referee authentication check to prevent IDOR access.
4. Record live match events (`GOAL`, `YELLOW_CARD`).
5. Conclude match and automatically update standings table with tie-breaker comparisons.
6. Compute top scorers via Java Streams and spawn asynchronous analytics via `CompletableFuture`.

---

## How to Build and Run

### 1. Compile and Execute Tests:
```bash
mvn clean test
```

### 2. Run the Interactive Console Application:
```bash
mvn compile exec:java -Dexec.mainClass="com.sportx.core.Main"
```
