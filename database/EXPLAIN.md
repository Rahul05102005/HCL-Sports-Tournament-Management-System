# SportX – Database Index Optimization & EXPLAIN Plan Analysis

This document provides architectural justifications and `EXPLAIN` query execution analysis for the performance indexes in the SportX database.

---

## 1. Index Portfolio & Architectural Justification

| Index Name | Table | Columns | Type | Justification / Benefiting Queries |
| :--- | :--- | :--- | :--- | :--- |
| `idx_fixture_tournament_date` | `fixtures` | `(tournament_id, scheduled_time)` | B-Tree Composite | Rapidly filters fixtures by tournament and sorts them chronologically. Eliminates `Using filesort`. |
| `idx_match_status` | `matches` | `(status)` | B-Tree | Used by Tournament Pulse and Live Dashboards to query matches in state `LIVE` or `UPCOMING` without full table scan. |
| `idx_player_team` | `players` | `(team_id, jersey_number)` | B-Tree Composite | Fast squad roster retrieval, jersey uniqueness validation, and eligibility checks. |
| `idx_score_event_match` | `score_events` | `(match_id, minute_offset)` | B-Tree Composite | Powers the chronological live timeline and Recharts **Match Momentum** curve. Eliminates filesort on minute ordering. |
| `idx_standings_tournament_rank` | `standings` | `(tournament_id, points DESC, goal_difference DESC, goals_for DESC)` | B-Tree Composite | Supports instant leaderboard evaluation and tie-breaker sorting without sorting overhead. |
| `idx_users_email_role` | `users` | `(email, role)` | B-Tree Composite | Critical for Spring Security authentication and RBAC claims validation on every incoming HTTP request. |

---

## 2. EXPLAIN Plan Demonstrations

### Query 1: Chronological Fixture Retrieval by Tournament
```sql
EXPLAIN SELECT * FROM fixtures 
WHERE tournament_id = 1 
ORDER BY scheduled_time ASC;
```

#### Without Index:
```text
+----+-------------+----------+------------+------+---------------+------+---------+------+------+----------+----------------+
| id | select_type | table    | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra          |
+----+-------------+----------+------------+------+---------------+------+---------+------+------+----------+----------------+
|  1 | SIMPLE      | fixtures | NULL       | ALL  | NULL          | NULL | NULL    | NULL | 1000 |    10.00 | Using filesort |
+----+-------------+----------+------------+------+---------------+------+---------+------+------+----------+----------------+
```
*Issue:* Full table scan (`type: ALL`) with an expensive in-memory/disk sort (`Using filesort`).

#### With Index `idx_fixture_tournament_date`:
```text
+----+-------------+----------+------------+------+-----------------------------+-----------------------------+---------+-------+------+----------+-------+
| id | select_type | table    | partitions | type | possible_keys               | key                         | key_len | ref   | rows | filtered | Extra |
+----+-------------+----------+------------+------+-----------------------------+-----------------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | fixtures | NULL       | ref  | idx_fixture_tournament_date | idx_fixture_tournament_date | 8       | const |   16 |   100.00 | NULL  |
+----+-------------+----------+------------+------+-----------------------------+-----------------------------+---------+-------+------+----------+-------+
```
*Improvement:* `type: ref`, directly scanning only the relevant 16 index leaf pages, `filesort` eliminated.

---

### Query 2: Match Momentum & Event Timeline Retrieval
```sql
EXPLAIN SELECT * FROM score_events 
WHERE match_id = 9 
ORDER BY minute_offset ASC;
```

#### With Index `idx_score_event_match`:
```text
+----+-------------+--------------+------------+------+-----------------------+-----------------------+---------+-------+------+----------+-------+
| id | select_type | table        | partitions | type | possible_keys         | key                   | key_len | ref   | rows | filtered | Extra |
+----+-------------+--------------+------------+------+-----------------------+-----------------------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | score_events | NULL       | ref  | idx_score_event_match | idx_score_event_match | 8       | const |    4 |   100.00 | NULL  |
+----+-------------+--------------+------------+------+-----------------------+-----------------------+---------+-------+------+----------+-------+
```
*Result:* Zero filesort. Event stream is retrieved directly in chronological sequence, guaranteeing microsecond response times for live WebSocket/polling clients.

---

## 3. Transaction ACID Guarantees & Isolation Levels

### ACID Properties in SportX Match Completion:
1. **Atomicity:** When a referee completes Match #9, either all changes (Match status, Fixture status, winning Score Event, Home Team Standings, Away Team Standings) are committed together, or none are.
2. **Consistency:** Database CHECK constraints (e.g. `played = won + drawn + lost`, `goal_difference = goals_for - goals_against`) ensure table integrity is never violated.
3. **Isolation:** Concurrent reads of the standings table during score updates will observe a consistent snapshot depending on the isolation level.
4. **Durability:** Once `COMMIT` executes, all state changes are written to InnoDB redo logs and persisted to disk.

### Transaction Isolation Levels:
* **READ UNCOMMITTED:** Allows dirty reads. Never used in SportX.
* **READ COMMITTED:** Protects against dirty reads. Suitable for high-concurrency read-heavy reporting.
* **REPEATABLE READ (MySQL InnoDB Default):** Protects against dirty reads and non-repeatable reads using Multi-Version Concurrency Control (MVCC) and gap locks to prevent phantom reads.
* **SERIALIZABLE:** Strict locking on all accessed ranges. Enforced in sensitive scheduling operations to prevent double-booking venues or referees.
