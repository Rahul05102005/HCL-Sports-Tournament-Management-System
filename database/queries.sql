-- ============================================================================
-- SportX - Smart Sports Tournament Management Platform
-- 10 Analytical & Business Queries (HCL SQL Rubric)
-- Covers: WHERE, IN, BETWEEN, LIKE, ORDER BY, LIMIT, INNER JOIN, LEFT JOIN,
--         RIGHT JOIN, UNION (FULL JOIN emulation), GROUP BY, HAVING, COUNT, SUM, AVG
-- ============================================================================

USE sportx_db;

-- ----------------------------------------------------------------------------
-- Query 1: Find tournaments currently ONGOING with sport details and organiser name
-- Rubric: WHERE, INNER JOIN
-- ----------------------------------------------------------------------------
SELECT 
    t.id AS tournament_id,
    t.name AS tournament_name,
    s.name AS sport_name,
    t.format,
    t.start_date,
    t.end_date,
    CONCAT(u.first_name, ' ', u.last_name) AS organiser_name
FROM tournaments t
INNER JOIN sports s ON t.sport_id = s.id
INNER JOIN users u ON t.organiser_id = u.id
WHERE t.status = 'ONGOING'
ORDER BY t.start_date ASC;

-- ----------------------------------------------------------------------------
-- Query 2: Find top teams by total wins across all tournaments
-- Rubric: GROUP BY, ORDER BY, LIMIT, AGGREGATION (SUM)
-- ----------------------------------------------------------------------------
SELECT 
    t.id AS team_id,
    t.name AS team_name,
    t.college_name,
    SUM(st.won) AS total_wins,
    SUM(st.points) AS total_points,
    SUM(st.goals_for) AS total_goals_scored
FROM teams t
INNER JOIN standings st ON t.id = st.team_id
GROUP BY t.id, t.name, t.college_name
ORDER BY total_wins DESC, total_points DESC
LIMIT 5;

-- ----------------------------------------------------------------------------
-- Query 3: Top 10 individual scoring players with goal/point counts and team details
-- Rubric: INNER JOIN, GROUP BY, ORDER BY, LIMIT, COUNT, SUM
-- ----------------------------------------------------------------------------
SELECT 
    p.id AS player_id,
    CONCAT(p.first_name, ' ', p.last_name) AS player_name,
    p.jersey_number,
    p.position,
    tm.name AS team_name,
    tm.college_name,
    COUNT(se.id) AS scoring_events_count,
    COALESCE(SUM(se.points_awarded), 0) AS total_points_scored
FROM players p
INNER JOIN teams tm ON p.team_id = tm.id
INNER JOIN score_events se ON p.id = se.player_id
WHERE se.event_type IN ('GOAL', 'POINT')
GROUP BY p.id, p.first_name, p.last_name, p.jersey_number, p.position, tm.name, tm.college_name
ORDER BY total_points_scored DESC, scoring_events_count DESC
LIMIT 10;

-- ----------------------------------------------------------------------------
-- Query 4: Venues with the highest number of assigned matches and capacity
-- Rubric: LEFT JOIN, GROUP BY, HAVING, COUNT, ORDER BY
-- ----------------------------------------------------------------------------
SELECT 
    v.id AS venue_id,
    v.name AS venue_name,
    v.location,
    v.capacity,
    COUNT(f.id) AS scheduled_match_count
FROM venues v
LEFT JOIN fixtures f ON v.id = f.venue_id
GROUP BY v.id, v.name, v.location, v.capacity
HAVING scheduled_match_count >= 1
ORDER BY scheduled_match_count DESC, v.capacity DESC;

-- ----------------------------------------------------------------------------
-- Query 5: Teams with more than 1 win and positive goal difference (Contenders)
-- Rubric: WHERE, HAVING, GROUP BY, Aggregations
-- ----------------------------------------------------------------------------
SELECT 
    t.name AS team_name,
    t.short_name,
    st.played,
    st.won,
    st.drawn,
    st.lost,
    st.goal_difference,
    st.points
FROM standings st
INNER JOIN teams t ON st.team_id = t.id
WHERE st.points >= 4
HAVING st.won >= 1 AND st.goal_difference > 0
ORDER BY st.points DESC, st.goal_difference DESC;

-- ----------------------------------------------------------------------------
-- Query 6: Upcoming matches for a particular team (e.g. BIT Sparks - ID: 1)
-- Rubric: OR, BETWEEN / Date Filtering, Multiple Joins
-- ----------------------------------------------------------------------------
SELECT 
    f.id AS fixture_id,
    t.name AS tournament_name,
    f.round_number,
    ht.name AS home_team,
    at.name AS away_team,
    v.name AS venue_name,
    f.scheduled_time,
    f.status
FROM fixtures f
INNER JOIN tournaments t ON f.tournament_id = t.id
INNER JOIN teams ht ON f.home_team_id = ht.id
INNER JOIN teams at ON f.away_team_id = at.id
LEFT JOIN venues v ON f.venue_id = v.id
WHERE (f.home_team_id = 1 OR f.away_team_id = 1)
  AND f.status IN ('SCHEDULED', 'IN_PROGRESS')
ORDER BY f.scheduled_time ASC;

-- ----------------------------------------------------------------------------
-- Query 7: Referees with the highest number of assigned matches and completion status
-- Rubric: RIGHT JOIN / INNER JOIN, GROUP BY, COUNT
-- ----------------------------------------------------------------------------
SELECT 
    u.id AS referee_id,
    CONCAT(u.first_name, ' ', u.last_name) AS referee_name,
    u.email,
    COUNT(ra.id) AS total_assignments,
    SUM(CASE WHEN ra.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_matches,
    SUM(CASE WHEN ra.status = 'ACCEPTED' THEN 1 ELSE 0 END) AS pending_matches
FROM users u
INNER JOIN referee_assignments ra ON u.id = ra.referee_id
WHERE u.role = 'REFEREE'
GROUP BY u.id, u.first_name, u.last_name, u.email
ORDER BY total_assignments DESC;

-- ----------------------------------------------------------------------------
-- Query 8: Tournaments having more than 5 registered teams with average points
-- Rubric: GROUP BY, HAVING, COUNT, AVG
-- ----------------------------------------------------------------------------
SELECT 
    trn.id AS tournament_id,
    trn.name AS tournament_name,
    COUNT(DISTINCT tm.id) AS registered_teams_count,
    ROUND(AVG(st.points), 2) AS average_team_points,
    SUM(st.goals_for) AS total_goals_scored
FROM tournaments trn
INNER JOIN teams tm ON trn.id = tm.tournament_id
LEFT JOIN standings st ON tm.id = st.team_id
GROUP BY trn.id, trn.name
HAVING registered_teams_count >= 5
ORDER BY registered_teams_count DESC;

-- ----------------------------------------------------------------------------
-- Query 9: Highest-scoring completed matches with team scores and goal timeline
-- Rubric: INNER JOIN, ORDER BY, LIMIT, Aggregation
-- ----------------------------------------------------------------------------
SELECT 
    m.id AS match_id,
    t.name AS tournament_name,
    ht.name AS home_team,
    m.home_score,
    at.name AS away_team,
    m.away_score,
    (m.home_score + m.away_score) AS total_match_goals,
    m.finished_at
FROM matches m
INNER JOIN fixtures f ON m.fixture_id = f.id
INNER JOIN tournaments t ON f.tournament_id = t.id
INNER JOIN teams ht ON f.home_team_id = ht.id
INNER JOIN teams at ON f.away_team_id = at.id
WHERE m.status = 'COMPLETED'
ORDER BY total_match_goals DESC
LIMIT 5;

-- ----------------------------------------------------------------------------
-- Query 10: Full outer join emulation between Teams and Matches to find match participation
-- Rubric: LEFT JOIN UNION RIGHT JOIN (emulates FULL OUTER JOIN in MySQL)
-- ----------------------------------------------------------------------------
SELECT 
    t.name AS team_name,
    f.id AS fixture_id,
    f.scheduled_time,
    'Home' AS side
FROM teams t
LEFT JOIN fixtures f ON t.id = f.home_team_id
WHERE t.tournament_id = 1

UNION

SELECT 
    t.name AS team_name,
    f.id AS fixture_id,
    f.scheduled_time,
    'Away' AS side
FROM teams t
RIGHT JOIN fixtures f ON t.id = f.away_team_id
WHERE t.tournament_id = 1
ORDER BY team_name, scheduled_time;
