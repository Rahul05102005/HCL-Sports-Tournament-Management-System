-- ============================================================================
-- SportX - Smart Sports Tournament Management Platform
-- Reporting View: tournament_summary_view
-- Target: MySQL 8.0+
-- ============================================================================

USE sportx_db;

DROP VIEW IF EXISTS tournament_summary_view;

CREATE VIEW tournament_summary_view AS
SELECT 
    t.id AS tournament_id,
    t.name AS tournament_name,
    t.code AS tournament_code,
    s.name AS sport_name,
    t.status AS tournament_status,
    t.start_date,
    t.end_date,
    t.venue_city,
    COUNT(DISTINCT tm.id) AS total_teams,
    COUNT(DISTINCT f.id) AS total_fixtures,
    COUNT(DISTINCT CASE WHEN m.status = 'COMPLETED' THEN m.id END) AS completed_matches,
    COUNT(DISTINCT CASE WHEN m.status = 'LIVE' THEN m.id END) AS live_matches,
    COALESCE(SUM(se.points_awarded), 0) AS total_points_scored,
    (
        SELECT tm_sub.name 
        FROM standings st_sub
        INNER JOIN teams tm_sub ON st_sub.team_id = tm_sub.id
        WHERE st_sub.tournament_id = t.id
        ORDER BY st_sub.points DESC, st_sub.goal_difference DESC, st_sub.goals_for DESC
        LIMIT 1
    ) AS leading_team,
    CONCAT(u.first_name, ' ', u.last_name) AS organiser_name
FROM tournaments t
INNER JOIN sports s ON t.sport_id = s.id
INNER JOIN users u ON t.organiser_id = u.id
LEFT JOIN teams tm ON t.id = tm.tournament_id
LEFT JOIN fixtures f ON t.id = f.tournament_id
LEFT JOIN matches m ON f.id = m.fixture_id
LEFT JOIN score_events se ON m.id = se.match_id
GROUP BY 
    t.id, t.name, t.code, s.name, t.status, t.start_date, t.end_date, t.venue_city, u.first_name, u.last_name;
